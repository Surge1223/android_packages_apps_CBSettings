package com.cheekybastards.ftw.utils;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HeaderViewListAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.cheekybastards.ftw.R;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import android.support.v7.app.*;
import android.support.v7.internal.widget.ActionBarOverlayLayout;



public class BuildPropEditor extends AppCompatActivity  implements OnItemClickListener {
    public final String TAG = this.getClass().getSimpleName();
    private ListView mListView;
    private String tempFile;
    private boolean refreshList;

	private Toolbar toolbar;

    ListView getListView() {
        if (mListView == null) {
            mListView = (ListView) findViewById(android.R.id.list);
        }
        return mListView;
    }

    protected ListAdapter getListAdapter() {
        ListAdapter adapter = getListView().getAdapter();
        if (adapter instanceof HeaderViewListAdapter) {
            return ((HeaderViewListAdapter)adapter).getWrappedAdapter();
        } else {
            return adapter;
        }}

    protected void setListAdapter(ListAdapter adapter) {
        getListView().setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settingstoolbar, root, false);
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

 //   private Context mContext;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //mContext = this;
      //  setTheme(Prefs.getTheme(mContext));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_prop);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        mListView = getListView();
        mListView.setTextFilterEnabled(true);

        createTempFile();
        populateList();
    }

    @Override
    public void onDestroy() {
    	super.onDestroy();
    }

    @Override
    public void onResume() {
    	super.onResume();

    	// TODO: This isn't working.
    	if (refreshList) {
    		// Something was added, better refresh
    		populateList();
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	getMenuInflater().inflate(R.menu.main_prop, menu);
        MenuItem showDrawerIcon = menu.findItem(R.id.action_show_drawer_icon);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {

            case R.id.add_menu:
    			refreshList = true;
    			showEdit(null, null);
                return true;
    		case R.id.backup_menu:
    			backup();
                return true;
    		case R.id.restore_menu:
    			restore();
                return true;


        }

        return super.onOptionsItemSelected(item);
    }

    public void populateList() {
    	final Properties prop = new Properties();
        File file = new File(tempFile);
    	try {
    		prop.load(new FileInputStream(file));
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
		}

    	final String[] pTitle = prop.keySet().toArray(new String[0]);
    	final List<String> pDesc = new ArrayList<String>();
    	for (int i = 0; i < pTitle.length; i++) {
    		pDesc.add(prop.getProperty(pTitle[i]));
    	}

    	ArrayList<Map<String, String>> list = buildData(pTitle, pDesc);
		String[] from = { "title", "description" };
		int[] to = { R.id.prop_title, R.id.prop_desc };

		SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.list_item, from, to);
		setListAdapter(adapter);

        mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				showEdit(pTitle[position], prop.getProperty(pTitle[position]));
			}
        });
    }

    private ArrayList<Map<String, String>> buildData(String[] t, List<String> d) {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

		for (int i = 0; i < t.length; ++i) {
			list.add(putData(t[i], d.get(i)));
		}

		return list;
	}

    private HashMap<String, String> putData(String title, String description) {
		HashMap<String, String> item = new HashMap<String, String>();

		item.put("title", title);
		item.put("description", description);

		return item;
	}

    public void showEdit(String name, String key) {
    	Intent intent = new Intent(BuildPropEditor.this, PropEdit.class);

    	intent.putExtra("name", name);
    	intent.putExtra("key", key);

    	startActivity(intent);
    }

    private void backup() {
    	Process process = null;
        DataOutputStream os = null;

        try {
            process = Runtime.getRuntime().exec("su");
	        os = new DataOutputStream(process.getOutputStream());
	        os.writeBytes("mount -o remount,rw  /system\n");
	        os.writeBytes("cp -f /system/build.prop " + Environment.getExternalStorageDirectory().getAbsolutePath() + "/build.prop.bak\n");
	        os.writeBytes("exit\n");
	        os.flush();
	        process.waitFor();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            	Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    	Toast.makeText(getApplicationContext(), "build.prop Backup at " + Environment.getExternalStorageDirectory().getAbsolutePath() + "/build.prop.bak", Toast.LENGTH_SHORT).show();
    }

    private void restore() {
    	Process process = null;
        DataOutputStream os = null;

        try {
            process = Runtime.getRuntime().exec("su");
	        os = new DataOutputStream(process.getOutputStream());
	        os.writeBytes("mount -o remount,rw  /system\n");
	        os.writeBytes("mv -f /system/build.prop /system/build.prop.bak\n");
	        os.writeBytes("busybox cp -f " + Environment.getExternalStorageDirectory().getAbsolutePath() + "/build.prop.bak /system/build.prop\n");
	        os.writeBytes("chmod 644 /system/build.prop\n");
	        //os.writeBytes("mount -o remount,ro -t yaffs2 /dev/block/mtdblock4 /system\n");
	        os.writeBytes("exit\n");
	        os.flush();
	        process.waitFor();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            	Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    	Toast.makeText(getApplicationContext(), "build.prop Restored from " + Environment.getExternalStorageDirectory().getAbsolutePath() + "/build.prop.bak", Toast.LENGTH_SHORT).show();
    }

    private void createTempFile() {
    	Process process = null;
        DataOutputStream os = null;

        try {
            process = Runtime.getRuntime().exec("su");
	        os = new DataOutputStream(process.getOutputStream());
	        os.writeBytes("mount -o remount,rw  /system\n");
	        os.writeBytes("cp -f /system/build.prop " + Environment.getExternalStorageDirectory().getAbsolutePath() + "/build.prop.tmp\n");
	        os.writeBytes("chmod 777 " + Environment.getExternalStorageDirectory().getAbsolutePath() + "/build.prop.tmp\n");
	        os.writeBytes("exit\n");
	        os.flush();
	        process.waitFor();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            	Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        tempFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/build.prop.tmp";
    }

    public boolean runRootCommand(String command) {
        Process process = null;
        DataOutputStream os = null;

        try {
            process = Runtime.getRuntime().exec("su");
	        os = new DataOutputStream(process.getOutputStream());
	        os.writeBytes(command+"\n");
	        os.writeBytes("exit\n");
	        os.flush();
	        process.waitFor();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            	Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }
}

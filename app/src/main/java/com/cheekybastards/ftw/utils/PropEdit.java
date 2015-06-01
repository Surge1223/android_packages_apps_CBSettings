package com.cheekybastards.ftw.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.cheekybastards.ftw.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

public class PropEdit extends ActionBarActivity {

	private EditText editName;
	private EditText editKey;
	protected boolean changesPending;
	private AlertDialog unsavedChangesDialog;
	private String tempFile;
	private String propReplaceFile;




	public final String TAG = this.getClass().getSimpleName();
	private Context mContext;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		mContext = this;
		// setTheme(Preferences.getTheme(mContext));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_prop);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		toolbar.setTitle(getResources().getString(R.string.app_name));
		setContentView(R.layout.edit_prop);
		tempFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/buildprop.tmp";
		propReplaceFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/propreplace.txt";
		setUpControls();
	}

	private void setUpControls() {
		editName = (EditText)findViewById(R.id.prop_name);
    	editKey = (EditText)findViewById(R.id.prop_key);

		String name = getIntent().getExtras().getString("name");
    	String key = getIntent().getExtras().getString("key");
    	
    	if (name != null) {
			editName.setText(name);
	    	editKey.setText(key);
    	}
    	
    	editName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changesPending = true;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO: Nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO: Nothing
            }
        });
    	
    	editKey.addTextChangedListener(new TextWatcher() {
    		@Override
    		public void onTextChanged(CharSequence s, int start, int before, int count) {
				changesPending = true;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO: Nothing
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO: Nothing
			}
		});
	}


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		getMenuInflater().inflate(R.menu.edit, menu);
		MenuItem showDrawerIcon = menu.findItem(R.id.action_show_drawer_icon);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
            case R.id.save_menu:
                final Properties prop = new Properties();

                try {
                    FileInputStream in = new FileInputStream(new File(tempFile));
                    prop.load(in);
                    in.close();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
                }

                prop.setProperty(editName.getText().toString(), editKey.getText().toString());

                try {
                    FileOutputStream out = new FileOutputStream(new File(tempFile));
                    prop.store(out, null);
                    out.close();

                    replaceInFile(new File(tempFile));
                    transferFileToSystem();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
                }

                finish();
                break;
            case R.id.discart_menu:
                cancel();
                return true;


        }

        return super.onOptionsItemSelected(item);
    }

	
	protected void cancel() {
		if (changesPending) {
			unsavedChangesDialog = new AlertDialog.Builder(this)
			.setTitle(R.string.unsaved_changes_title)
			.setMessage(R.string.unsaved_changes_message)
			.setPositiveButton(R.string.save, new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					final Properties prop = new Properties();
					
					try {
						FileInputStream in = new FileInputStream(new File(tempFile));
						prop.load(in);
						in.close();
					} catch (IOException e) {
						Toast.makeText(getApplicationContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
					}
					
					prop.setProperty(editName.getText().toString(), editKey.getText().toString());
					
			    	try {
			    		FileOutputStream out = new FileOutputStream(new File(tempFile));
			    		prop.store(out, null);
			    		out.close();
			    		
			    		replaceInFile(new File(tempFile));
			    		transferFileToSystem();
					} catch (IOException e) {
						Toast.makeText(getApplicationContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
					}
				}
			})
			.setNeutralButton(R.string.discard, new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			})
			.setNegativeButton(R.string.cancel, new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					unsavedChangesDialog.cancel();
				}
			}).create();
			unsavedChangesDialog.show();
		} else {
			finish();
		}
	}
	
	private void transferFileToSystem() {
    	Process process = null;
        DataOutputStream os = null;
        
        try {
            process = Runtime.getRuntime().exec("su");
	        os = new DataOutputStream(process.getOutputStream());
	        os.writeBytes("mount -o remount,rw -t yaffs2 /dev/block/mtdblock4 /system\n");
	        os.writeBytes("mv -f /system/build.prop /system/build.prop.bak\n");
	        os.writeBytes("busybox cp -f " + propReplaceFile + " /system/build.prop\n");
	        os.writeBytes("chmod 644 /system/build.prop\n");
	        //os.writeBytes("mount -o remount,ro -t yaffs2 /dev/block/mtdblock4 /system\n");
	        //os.writeBytes("rm " + propReplaceFile);
	        //os.writeBytes("rm " + tempFile);
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
        
    	Toast.makeText(getApplicationContext(), "Edit saved and a backup was made at " + Environment.getExternalStorageDirectory().getAbsolutePath() + "/build.prop.bak", Toast.LENGTH_SHORT).show();
    }
	
	private void replaceInFile(File file) throws IOException {
		File tmpFile = new File(propReplaceFile);
		FileWriter fw = new FileWriter(tmpFile);
		Reader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		
		while (br.ready()) {
			fw.write(br.readLine().replaceAll("\\\\", "") + "\n");
		}
		
		fw.close();
		br.close();
		fr.close();
	}
}

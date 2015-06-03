package com.cheekybastards.ftw;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.cheekybastards.ftw.adapter.NavDrawerListAdapter;
import com.cheekybastards.ftw.fragments.AboutFragment;
import com.cheekybastards.ftw.fragments.BackupFragment;
import com.cheekybastards.ftw.fragments.GeneralFragment;
import com.cheekybastards.ftw.fragments.RootFileFragment;
import com.cheekybastards.ftw.model.NavDrawerItem;
import com.cheekybastards.ftw.settings.About;
import com.cheekybastards.ftw.settings.Help;
import com.cheekybastards.ftw.settings.InstallDialogThread;
import com.cheekybastards.ftw.settings.Prefs;
import com.cheekybastards.ftw.utils.ExecuteAsRootBase;
import com.cheekybastards.ftw.utils.RunExec;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    final static public String INITIAL_SETUP = "initial_setup";
    final static public String PREFS_NAME = "current_theme";
    final static public String FIRST_RUN = "first_run";
    private static AlertDialog mAlertDialog;
    public final String TAG = this.getClass().getSimpleName();
    String su = new String();
    ProgressDialog mProgressDialog;
	// shell defines
    String shell = new String();
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ProgressDialog pDialog = null;
    private InstallDialogThread installDialogThread = null;
    private Fragment mSelectedFragment;
    private String[] mDrawerEntries;
    private SharedPreferences mPreferences;
    // nav drawer title
    private CharSequence mDrawerTitle;
    private Toolbar toolbar;
    // used to store app title
    private CharSequence mTitle;
    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

        protected Dialog onCreateDialog(int id) {
        // show disclaimer....
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.Theme));
        builder.setMessage(R.string.theme_prompt);
        builder.setCancelable(false);
        builder.setPositiveButton("Dark", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // and, if the user accept, you can execute something like this:
                // We need an Editor object to make preference changes.
                // All objects are from android.context.Context
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(PREFS_NAME, "2");
                editor.putBoolean(FIRST_RUN, true);
                editor.commit();
            }
        })
                .setNegativeButton("White", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //nm.cancel(R.notification.running); // cancel the NotificationManager (icon)
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREFS_NAME, "0");
                        editor.putBoolean(FIRST_RUN, true);
                        editor.commit();

                    }
                });
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("accepted", true);
        editor.apply();
        AlertDialog alert = builder.create();
        return alert;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        if (settings.getBoolean("root", false) != false) {
            shell = "su ";
            ExecuteAsRootBase.executecmd("chmod  777 /data/data/com.cheekybastards.ftw/cache/*");
        }
        else {
            shell = "sh ";
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        shell = "sh";
        su = "su";
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (settings.getBoolean("accepted", false) == false) {
            showDialog(0);
        } else


        if(!new File("/data/data/com.cheekybastards.ftw/cache/busybox").exists()) {
            copyFileOrDir("cache");
            CopyAssets();
            RunExec.Cmd(shell, "chmod -R 777 /data/data/com.cheekybastards.ftw/cache/*");
        }

        if(savedInstanceState==null) {
            (new Startup()).setContext(this).execute();

        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTitle = mDrawerTitle = getTitle();

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources()
			.obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items to array
        // About
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        // General
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        // Performance
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        // Backup/Restore Apps
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
        // Root File Explorer
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));


        // enabling action bar app icon and behaving it as toggle button
        navMenuIcons.recycle();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
										   navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);

        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
        }
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     **/
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new AboutFragment();
                break;
            case 1:
                fragment = new GeneralFragment ();
                break;
            case 2:
                fragment = new PerformanceFragment ();
                break;
            case 3:
                fragment = new BackupFragment ();
                break;
            case 4:
                fragment = new RootFileFragment ();
                break;

            default:
                break;
        }

        if (fragment != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
				.replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    private void copyFile(String filename) {
                AssetManager assetManager = this.getAssets();

                InputStream in = null;
                OutputStream out = null;
                try {
                    in = assetManager.open(filename);
                    String newFileName = "/data/data/" + this.getPackageName() + "/" + filename;
                    out = new FileOutputStream(newFileName);

                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                    in.close();
                    in = null;
                    out.flush();
                    out.close();
                    out = null;
                } catch (Exception e) {
                    Log.e("tag", e.getMessage());
                }

            }

        public void copyFileOrDir(String path) {
            AssetManager assetManager = this.getAssets();
            String assets[] = null;
            try {
                assets = assetManager.list(path);
                if (assets.length == 0) {
                    copyFile(path);
                } else {
                    String fullPath = "/data/data/" + this.getPackageName() + "/" + path;
                    File dir = new File(fullPath);
                    if (!dir.exists())
                        dir.mkdir();
                    for (int i = 0; i < assets.length; ++i) {
                        copyFileOrDir(path + "/" + assets[i]);
                    }
                }
            } catch (IOException ex) {
                Log.e("tag", "I/O Exception", ex);
            }
        }

        private void copyFile(InputStream in, OutputStream out) throws IOException {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        }

        private void CopyAssets() {
            AssetManager assetManager = getAssets();
            String[] files = null;

            try {
                files = assetManager.list("cache");

            } catch (IOException e) {
                Log.e("tag", e.getMessage());
            }

            for (String filename : files) {
                System.out.println("File name => " + filename);
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = assetManager.open("cache/" + filename);   // if files resides inside the "Files" directory itself
                    out = new FileOutputStream(Environment.getDataDirectory().toString() + "/data/com.cheekybastards.ftw/cache/" + filename);
                    copyFile(in, out);
                    in.close();
                    in = null;
                    out.flush();
                    out.close();
                    out = null;
                } catch (Exception e) {
                    Log.e("tag", e.getMessage());
                }
            }

        }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.START))
            mDrawerLayout.closeDrawer(Gravity.START);
        else
            super.onBackPressed();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem showDrawerIcon = menu.findItem(R.id.action_show_drawer_icon);
        showDrawerIcon.setChecked(isLauncherIconEnabled());
        return true;
    }

    /**
     * onPause is called when the activity is going to background.
     */

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * onResume is called when the activity is going to foreground.
     */

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
    }

    /** Method to exit application. Not essential, since the Android Back button
     * accomplishes the same task.
     */

    public void finishUp(){
        finish();
    }

    /** Handle events from the options menu in action bar and its overflow menu.  */

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);

        switch(item.getItemId()){

            case (R.id.help):
                // Actions for Help page
                Intent i = new Intent(MainActivity.this, Help.class);
                startActivity(i);
                return true;

            case(R.id.about):
                // Actions for About page
                Intent k = new Intent(MainActivity.this, About.class);
                startActivity(k);
                return true;

            case(R.id.prefs):
                // Actions for preferences page
                Intent j = new Intent(MainActivity.this, Prefs.class);
                startActivity(j);
                return true;

				// Exit: not really needed because back button serves same function,
				// but we include as illustration since some users may be more
				// comfortable with an explicit quit button.

            case (R.id.quit):
                finishUp();
                return true;

            case(R.id.action_show_drawer_icon): {
					boolean checked = item.isChecked();
					item.setChecked(!checked);
					setLauncherIconEnabled(!checked);
					return true;
				}
        }

        return super.onOptionsItemSelected(item);
    }

    // Method to check SharedPreferences and set the current theme

    /** Process clicks on the buttons */

    public boolean isLauncherIconEnabled() {
        PackageManager p = getPackageManager();
        int componentStatus = p.getComponentEnabledSetting(new ComponentName(this, LauncherActivity.class));
        return componentStatus != PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
    }

    public void setLauncherIconEnabled(boolean enabled) {
        PackageManager p = getPackageManager();
        int newState = enabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        p.setComponentEnabledSetting(new ComponentName(this, LauncherActivity.class), newState, PackageManager.DONT_KILL_APP);
    }

    private class Startup extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pDialog = null;
        private Context context = null;
        private boolean suAvailable = false;
        private Boolean rootCheck = false;

        public Startup setContext(Context context) {
            this.context = context;
            return this;
        }



        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Installing Cheeky requisites");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            pDialog.getCurrentFocus();
            installDialogThread = new InstallDialogThread();
            installDialogThread.packageCodePath = getPackageCodePath();
            installDialogThread.mAppRoot = getFilesDir();
            installDialogThread.LOGTAG = "Cheekybastards";
        }


        @Override
        protected Void doInBackground(Void... args) {
            // Let's check to see if SU is there...
            rootCheck = ExecuteAsRootBase.canRunRootCommands();
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            if (settings.getBoolean("accepted", true) == true) {
                return null;
            } else {
                copyFileOrDir("cache");
                CopyAssets();
                installDialogThread.start();
                return null;

            }


        }




        @Override
        protected void onPostExecute(Void result) {
            // output
            pDialog.dismiss();
            if (!rootCheck) {
                String str = "Root not available?!? Some functions may not work as intended!";
                Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
            } else {
                String yay = "Install succeeded";
                Toast.makeText(context, yay, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements
	ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }
}




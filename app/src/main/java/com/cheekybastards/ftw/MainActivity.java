package com.cheekybastards.ftw;

import android.app.*;
import android.os.*;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.cheekybastards.ftw.fragments.*;
import com.cheekybastards.ftw.adapter.*;
import com.cheekybastards.ftw.model.*;
import com.cheekybastards.ftw.settings.*;

import java.util.ArrayList;



public class MainActivity extends ActionBarActivity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle actionBarDrawerToggle;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

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

    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
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


    /** onPause is called when the activity is going to background. */

    @Override
    public void onPause() {
        super.onPause();
    }

    /** onResume is called when the activity is going to foreground. */

    /** Process clicks on the buttons */

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

    public void setLauncherIconEnabled(boolean enabled) {
        PackageManager p = getPackageManager();
        int newState = enabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        p.setComponentEnabledSetting(new ComponentName(this, LauncherActivity.class), newState, PackageManager.DONT_KILL_APP);
    }

    public boolean isLauncherIconEnabled() {
        PackageManager p = getPackageManager();
        int componentStatus = p.getComponentEnabledSetting(new ComponentName(this, LauncherActivity.class));
        return componentStatus != PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
    }
}




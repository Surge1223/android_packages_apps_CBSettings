package com.cheekybastards.ftw.settings;
import android.os.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import com.cheekybastards.ftw.*;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.content.*;
import android.app.*;
import android.widget.AdapterView.*;
import com.cheekybastards.ftw.settings.*;
public class PrefsActivity extends AppCompatActivity  implements OnItemClickListener {
    public final String TAG = this.getClass().getSimpleName();
    private ListView mListView;
    private String tempFile;
    private boolean refreshList;
	private SharedPreferences mPreferences;
    // nav drawer title
    private CharSequence mDrawerTitle;
    private Toolbar toolbar;

	private ActionBarDrawerToggle actionBarDrawerToggle;

	private CharSequence mTitle;
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
        super.onPostCreate(savedInstanceState);		// setTheme(Preferences.getTheme(mContext));
		setContentView(R.layout.pref_activity);
		    }
	
	//   private Context mContext;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //mContext = this;
		//  setTheme(Prefs.getTheme(mContext));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pref_activity);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
     //   setSupportActionBar(toolbar);

        mTitle = mDrawerTitle = getTitle();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        mListView = getListView();
        mListView.setTextFilterEnabled(true);
		Fragment fragment = new Prefs();
		
		if (fragment != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
				.replace(R.id.frame_container, fragment).commit();
	}
}
    @Override
    public void onDestroy() {
    	super.onDestroy();
    }

    @Override
    public void onResume() {
    	super.onResume();


    	}
    }
	
	
		

package com.cheekybastards.ftw.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.app.ListFragment;
import android.support.v7.widget.Toolbar;
import android.widget.*;
import android.app.*;
import android.view.*;
import java.util.*;
import android.content.res.*;
import android.support.v7.appcompat.*;

public class ListerFragment extends Fragment {
	public final String TAG = this.getClass().getSimpleName();
    private ListView mListView;
    private String tempFile;
	// private List<ListViewItem> mItems;        // ListView items list
	private boolean refreshList;

	private Toolbar toolbar;

    

	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize the items list
     
        Resources resources = getResources();
		}
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.notification_media_action, container, false);

        return rootView;
    }
	
	

	
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub

    }

    }

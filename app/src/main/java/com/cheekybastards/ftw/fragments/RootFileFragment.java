package com.cheekybastards.ftw.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cheekybastards.ftw.R;

public class RootFileFragment extends Fragment {
	
	public RootFileFragment(){}
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_rootexplorer, container, false);
         
        return rootView;
    }
}

package com.cheekybastards.ftw.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cheekybastards.ftw.R;

public class PerformanceFragment extends Fragment {
	
	public PerformanceFragment(){}
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_performance, container, false);
         
        return rootView;
    }
}

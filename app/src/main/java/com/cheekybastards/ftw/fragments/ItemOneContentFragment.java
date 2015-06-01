package com.cheekybastards.ftw.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

public class ItemOneContentFragment extends Fragment {

	private static final String ARG_POSITION = "position";
	private int position;
	
	public ItemOneContentFragment() {
		// TODO Auto-generated constructor stub
	}

	public static com.cheekybastards.ftw.fragments.ItemOneContentFragment newInstance(int position) {
		com.cheekybastards.ftw.fragments.ItemOneContentFragment f = new com.cheekybastards.ftw.fragments.ItemOneContentFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_POSITION, position);
		f.setArguments(args);
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		position = getArguments().getInt(ARG_POSITION);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		
		FrameLayout fl = new FrameLayout(getActivity());
		fl.setLayoutParams(params);
		
		final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
		
		TextView v = new TextView(getActivity());
		params.setMargins(margin, margin, margin, margin);
		v.setLayoutParams(params);
		v.setGravity(Gravity.CENTER);
		v.setText("Card " + (position + 1));
		
		fl.addView(v);
		return fl;
	}
}

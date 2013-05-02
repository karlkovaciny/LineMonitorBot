package com.kovaciny.linemonitorbot;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class SkidTimesFragment extends SectionFragment {
		@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.skid_times_fragment,
				container, false);
		
		return rootView;
	}
	
}
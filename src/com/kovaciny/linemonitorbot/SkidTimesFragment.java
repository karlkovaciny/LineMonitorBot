package com.kovaciny.linemonitorbot;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class SkidTimesFragment extends SectionFragment {
		@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.skid_times_fragment,
				container, false);
		
		Button btnSkidFinished = (Button) rootView.findViewById(R.id.btn_set_alarm);
		btnSkidFinished.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/*TimePickerDialog dlgAlarmSet = new TimePickerDialog(this, )
				TimePickerFragment fragAlarmSet = new TimePickerFragment.newInstance(mStackLevel);
			    fragAlarmSet.show(ft, "dialog");*/
				
				


			}
		});
		
		
		return rootView;
	}
	
	public void showTimePickerDialog(View v) {
	    TimePickerFragment newFragment = new TimePickerFragment();
	    TimePickerFragment f = newFragment;
	    
	    FragmentManager fm = newFragment.this.getActivity().getSupportFragmentManager();
	    newFragment.show(newFragment.this.getActivity().getSupportFragmentManager(), "timePicker");
	    /*FragmentManager fm = 
	    	    newFragment.this.getActivity().getSupportFragmentManager();
	    	d.show(fm, "dialog");*/
	}
	
	
}
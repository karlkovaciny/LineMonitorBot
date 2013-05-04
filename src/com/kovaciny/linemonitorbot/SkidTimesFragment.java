package com.kovaciny.linemonitorbot;

import android.app.DialogFragment;
import android.os.Bundle;
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
		
		Button btnSetAlarm = (Button) rootView.findViewById(R.id.btn_set_alarm);
		btnSetAlarm.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				showTimePickerDialog(v);
			}
		});
		
		
		return rootView;
	}
		
	public void showTimePickerDialog(View v) {
	    DialogFragment newFragment = new TimePickerFragment();
	    newFragment.show(getActivity().getFragmentManager(), "timePicker");
	}
}
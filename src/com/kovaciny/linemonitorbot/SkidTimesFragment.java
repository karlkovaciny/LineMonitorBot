package com.kovaciny.linemonitorbot;

import android.app.AlarmManager;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


public class SkidTimesFragment extends SectionFragment {
	private SkidFinishedBroadcastReceiver alarm;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_alarm_manager);
        alarm = new SkidFinishedBroadcastReceiver();
    }
    
    public void onetimeTimer(View view, Integer interval){
        
    	Context context = getActivity();
        if(alarm != null){
         alarm.setOnetimeTimer(context, interval);
        }else{
         Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
        }
    }
    
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
				onetimeTimer(v, 20*60*1000);
			}
		});
		
		
		return rootView;
	}
		


	public void showTimePickerDialog(View v) {
	    DialogFragment newFragment = new TimePickerFragment();
	    newFragment.show(getActivity().getFragmentManager(), "timePicker");
	}
}

package com.kovaciny.linemonitorbot;

import java.util.ArrayList;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class SkidTimesFragment extends SectionFragment {
	private SkidFinishedBroadcastReceiver mAlarmReceiver;
	private View mRootView;
	private float mSheetsPerMinute;
	private ArrayList<Skid> mSkidList;

	public float getSheetsPerMinute() {
		return mSheetsPerMinute;
	}

	public void setSheetsPerMinute(float mSheetsPerMinute) {
		this.mSheetsPerMinute = mSheetsPerMinute;
	}

	public ArrayList<Skid> getSkidList() {
		return mSkidList;
	}

	public void setSkidList(ArrayList<Skid> skidList) {
		this.mSkidList = skidList;
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_alarm_manager);
        mAlarmReceiver = new SkidFinishedBroadcastReceiver();
    }
        
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		mRootView = inflater.inflate(R.layout.skid_times_fragment,
				container, false);
		
		EditText sheetsPerMinute = (EditText) mRootView.findViewById(R.id.sheets_per_minute);
		sheetsPerMinute.setText("12");
		//this.setSheetsPerMinute( Float.parseFloat( sheetsPerMinute.getText().toString() ) );
		
		Button btnSetAlarm = (Button) mRootView.findViewById(R.id.btn_set_alarm);
		btnSetAlarm.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				showTimePickerDialog(v);
				onetimeTimer(v, 60*60*1000);
			}
		});
		
		
		return mRootView;
	}
		
public void onetimeTimer(View view, Integer interval){
        
    	Context context = getActivity();
        if(mAlarmReceiver != null){
         mAlarmReceiver.setOnetimeTimer(context, interval);
        }else{
         Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
        }
    }
    

	public void showTimePickerDialog(View v) {
	    DialogFragment newFragment = new TimePickerFragment();
	    newFragment.show(getActivity().getFragmentManager(), "timePicker");
	}

	@Override
	public void onPause() {
		//store persistent data
		super.onPause();
	}
}

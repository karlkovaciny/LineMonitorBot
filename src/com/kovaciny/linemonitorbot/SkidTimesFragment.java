package com.kovaciny.linemonitorbot;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.kovaciny.database.PrimexSQLiteOpenHelper;
import com.kovaciny.primexmodel.Product;
import com.kovaciny.primexmodel.Skid;
import com.kovaciny.primexmodel.WorkOrder;


public class SkidTimesFragment extends SectionFragment implements OnClickListener, OnEditorActionListener {
	private SkidFinishedBroadcastReceiver mAlarmReceiver;
	private View mRootView; //the ScrollView that holds all other views
	private double mSheetsPerMinute;
	private List<Skid<Product>> mSkidList;
	private EditText mEdit_sheetsPerMinute;
	private ImageButton mImageBtn_calcSheetsPerMinute;
	OnSheetsPerMinuteChangeListener mCallback;
	
    // Container Activity must implement this interface
    public interface OnSheetsPerMinuteChangeListener {
        public void onSheetsPerMinuteChanged(double sheetsPerMinute);
    }
        
	/* (non-Javadoc)
	 * @see android.widget.TextView.OnEditorActionListener#onEditorAction(android.widget.TextView, int, android.view.KeyEvent)
	 */
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent arg2) {
		if ( (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT) ){
			if (v.getId() == R.id.sheets_per_minute) {
				String sheetsPerMin = ((TextView)v).getText().toString();
				if (sheetsPerMin.length() != 0) {
					mCallback.onSheetsPerMinuteChanged(Double.valueOf(sheetsPerMin)); //the whole app needs to know when the sheets per minute change						
				}
			}
		}
		return false;
	}

	public double getSheetsPerMinute() {
		return mSheetsPerMinute;
	}

	public void setSheetsPerMinute(double mSheetsPerMinute) {
		this.mSheetsPerMinute = mSheetsPerMinute;
		this.mEdit_sheetsPerMinute.setText(String.valueOf(mSheetsPerMinute));
	}

	public List<Skid<Product>> getSkidList() {
		return mSkidList;
	}

	public void setSkidList(List<Skid<Product>> skidList) {
		this.mSkidList = skidList;
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
        mAlarmReceiver = new SkidFinishedBroadcastReceiver();      
    }
        
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
        
		// This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnSheetsPerMinuteChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		mRootView = inflater.inflate(R.layout.skid_times_fragment,
				container, false);
		
		mEdit_sheetsPerMinute = (EditText) mRootView.findViewById(R.id.sheets_per_minute);
		mEdit_sheetsPerMinute.setOnEditorActionListener(this);
		
		Button btnSetAlarm = (Button) mRootView.findViewById(R.id.btn_set_alarm);
		btnSetAlarm.setOnClickListener(this);
		
		mImageBtn_calcSheetsPerMinute = (ImageButton)mRootView.findViewById(R.id.calc_sheets_per_minute);
		mImageBtn_calcSheetsPerMinute.setOnClickListener(this);
		
		return mRootView;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case(R.id.btn_set_alarm):
			showTimePickerDialog(v);
			//onetimeTimer(v, 60*60*1000);
			break;
		case(R.id.calc_sheets_per_minute):
		    // Create the fragment and show it as a dialog.
			//TODO get all these args from the activty or model
			SheetsPerMinuteDialogFragment newFragment = new SheetsPerMinuteDialogFragment();
			Bundle args = new Bundle();
			args.putDouble("Gauge", .015d);
			args.putDouble("SheetWidth", 48d);
			args.putDouble("SheetLength", 96d);
			args.putDouble("LineSpeed", 48d);
			args.putDouble("SpeedFactor", 1d);
			args.putInt("SheetsOrRolls", SheetsPerMinuteDialogFragment.SHEETS_MODE);
			newFragment.setArguments(args);
		    newFragment.show(getActivity().getFragmentManager(), "SheetsPerMinuteDialog");
			break;
		}
	}


		
public void onetimeTimer(View view, Integer interval){
        
    	Context context = getActivity();
        if(mAlarmReceiver != null){
         mAlarmReceiver.setOnetimeTimer(context, interval);
        } else {
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
		ViewGroup vg = (ViewGroup)mRootView;
		vg.getChildCount();
		vg.indexOfChild(mRootView.findViewById(R.id.editText5));
		WorkOrder newWO = new WorkOrder(135678, 0);
		//dbHelper.updateWorkOrder(newWO); //TODO what if you update an unexisting? check rows returnedw
		super.onPause();
	}
}

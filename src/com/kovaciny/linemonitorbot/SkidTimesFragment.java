package com.kovaciny.linemonitorbot;

import java.beans.PropertyChangeEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import com.kovaciny.helperfunctions.HelperFunction;
import com.kovaciny.primexmodel.PrimexModel;
import com.kovaciny.primexmodel.Product;
import com.kovaciny.primexmodel.Skid;

public class SkidTimesFragment extends SectionFragment implements
		OnClickListener, OnEditorActionListener, View.OnFocusChangeListener, ViewEventResponder {
	private SkidFinishedBroadcastReceiver mAlarmReceiver;
	private List<Skid<Product>> mSkidList;
	private ImageButton mImageBtn_calcSheetsPerMinute;

	private List<View> mEditTextList;
	private EditText mEdit_sheetsPerMinute;
	private EditText mEdit_skidNumber;
	private EditText mEdit_currentCount;
	private EditText mEdit_totalSheetsPerSkid;
	private EditText mEdit_skidStartTime;
	private EditText mEdit_skidFinishTime;
	private EditText mEdit_numSkidsInJob;

	private TextView mTxt_timePerSkid;
	private TextView mLbl_productsPerMinute;
	private TextView mLbl_totalProducts;

	OnViewChangeListener mCallback;

	// Container Activity must implement this interface
	public interface OnViewChangeListener {
		public void onViewChange (int viewId, String userEntry);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.skid_times_fragment, container,
				false);

		mEditTextList = new ArrayList<View>();

		mEdit_sheetsPerMinute = (EditText) rootView
				.findViewById(R.id.edit_products_per_minute);
		mEditTextList.add(mEdit_sheetsPerMinute);
		mEdit_skidNumber = (EditText) rootView
				.findViewById(R.id.edit_skid_number);
		mEditTextList.add(mEdit_skidNumber);
		mEdit_currentCount = (EditText) rootView
				.findViewById(R.id.edit_current_count);
		mEditTextList.add(mEdit_currentCount);
		mEdit_totalSheetsPerSkid = (EditText) rootView
				.findViewById(R.id.edit_total_sheets_per_skid);
		mEditTextList.add(mEdit_totalSheetsPerSkid);
		mEdit_skidStartTime = (EditText) rootView
				.findViewById(R.id.edit_skid_start_time);
		mEditTextList.add(mEdit_skidStartTime);
		mEdit_skidFinishTime = (EditText) rootView
				.findViewById(R.id.edit_skid_finish_time);
		mEditTextList.add(mEdit_skidFinishTime);
		mEdit_numSkidsInJob = (EditText) rootView
				.findViewById(R.id.edit_num_skids_in_job);
		mEditTextList.add(mEdit_numSkidsInJob);

		for (View v : mEditTextList) {
			EditText etv = (EditText) v;
			etv.setOnEditorActionListener(this);
			etv.setOnFocusChangeListener(this);
		}

		this.mTxt_timePerSkid = (TextView) rootView
				.findViewById(R.id.txt_time_per_skid);
		mLbl_productsPerMinute = (TextView) rootView
				.findViewById(R.id.lbl_products_per_minute);
		mLbl_totalProducts = (TextView) rootView.findViewById(R.id.lbl_total_products);

		Button btnSetAlarm = (Button) rootView
				.findViewById(R.id.btn_set_alarm);
		btnSetAlarm.setOnClickListener(this);

		mImageBtn_calcSheetsPerMinute = (ImageButton) rootView
				.findViewById(R.id.calc_sheets_per_minute);
		mImageBtn_calcSheetsPerMinute.setOnClickListener(this);

		return rootView;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.TextView.OnEditorActionListener#onEditorAction(android
	 * .widget.TextView, int, android.view.KeyEvent)
	 */
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent arg2) {
		if ((actionId == EditorInfo.IME_ACTION_DONE)
				|| (actionId == EditorInfo.IME_ACTION_NEXT)) {
			processUserTextEntry(v);
		}
		return false;
	}

	
	/* (non-Javadoc)
	 * @see android.view.View.OnFocusChangeListener#onFocusChange(android.view.View, boolean)
	 */
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (!hasFocus) {
			EditText etv = (EditText)v;
			if ( mEditTextList.contains(etv) ) {
				processUserTextEntry(etv);
			}
		}
	}
	
	public void processUserTextEntry(TextView tv) {
		tv.setTextAppearance(getActivity(), R.style.user_entered);
		String userEntry = tv.getText().toString();
		//TODO: data validation
		if (userEntry.length() != 0) {
			mCallback.onViewChange(tv.getId(), userEntry);
		}
	}

	public void hideAll() {
		
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
			mCallback = (OnViewChangeListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnHeadlineSelectedListener");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case (R.id.btn_set_alarm):
			showTimePickerDialog(v);
			// onetimeTimer(v, 60*60*1000);
			break;
		case (R.id.calc_sheets_per_minute):
			((MainActivity)getActivity()).showSheetsPerMinuteDialog();
			break;
		}
	}

	public void onetimeTimer(View v, Integer interval) {

		Context context = getActivity();
		if (mAlarmReceiver != null) {
			mAlarmReceiver.setOnetimeTimer(context, interval);
		} else {
			Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
		}
	}

	public void showTimePickerDialog(View v) {
		DialogFragment newFragment = new TimePickerFragment();
		newFragment.show(getActivity().getFragmentManager(), "timePicker");
	}

	public void modelPropertyChange (PropertyChangeEvent event) {
		String propertyName = event.getPropertyName();
		Object newProperty = event.getNewValue();
		
		if (propertyName == PrimexModel.PRODUCT_CHANGE_EVENT) {
			String units = ((Product)newProperty).getUnits();
			StringBuilder capUnits = new StringBuilder(units);
			capUnits.setCharAt(0, Character.toUpperCase(units.charAt(0)));
			this.mLbl_productsPerMinute.setText(capUnits.toString() + " per minute");
			this.mLbl_totalProducts.setText("Total\n" + units);
			
		} else if (propertyName == PrimexModel.PRODUCTS_PER_MINUTE_CHANGE_EVENT) {
			this.mEdit_sheetsPerMinute.setText(String.valueOf(newProperty));
			
		} else if (propertyName == PrimexModel.CURRENT_SHEET_COUNT_CHANGE_EVENT) {
			
		} else if (propertyName == PrimexModel.SKID_FINISH_TIME_CHANGE_EVENT) {
			SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");
			String formattedTime = formatter.format((Date)newProperty);
			mEdit_skidFinishTime.setText(formattedTime);
			
			//set alarm 
			long alarmLeadTime = 3 * HelperFunction.ONE_MINUTE_IN_MILLIS; //TODO
			Date curDate = new Date();
			long timeNow = curDate.getTime();
			String formattedTimeNow = formatter.format(timeNow);
			//Toast.makeText(getActivity(), "time now is " + formattedTimeNow + String.valueOf(timeNow), Toast.LENGTH_SHORT).show();
			long timeThen = ((Date)newProperty).getTime();
			String formattedTimeThen = formatter.format(timeThen);
			//Toast.makeText(getActivity(), "time then is " + formattedTimeThen + String.valueOf(timeThen), Toast.LENGTH_SHORT).show();
			Long timeBetweenMinutes = (timeThen - timeNow) / HelperFunction.ONE_MINUTE_IN_MILLIS; 
			Long interval = timeThen - timeNow - alarmLeadTime;
//			Toast.makeText(getActivity(), "interval is " + String.valueOf(interval), Toast.LENGTH_SHORT).show();
			if (interval > 0) {
				onetimeTimer( mEdit_skidFinishTime, Integer.valueOf(interval.intValue()) );	
				
			}			
		} else if (propertyName == PrimexModel.SKID_START_TIME_CHANGE_EVENT) {
			SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");
			String formattedTime = formatter.format((Date)newProperty);
			mEdit_skidStartTime.setText(formattedTime);
			
		} else if (propertyName == PrimexModel.TOTAL_SHEET_COUNT_CHANGE_EVENT) {
			
		} else if (propertyName == PrimexModel.MINUTES_PER_SKID_CHANGE_EVENT) {
			this.mTxt_timePerSkid.setText(HelperFunction
					.formatMinutesAsHours((Long)newProperty));
		}
	}
}

package com.kovaciny.linemonitorbot;

import java.beans.PropertyChangeEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.kovaciny.helperfunctions.HelperFunction;
import com.kovaciny.primexmodel.PrimexModel;
import com.kovaciny.primexmodel.Product;
import com.kovaciny.primexmodel.Skid;
import com.kovaciny.primexmodel.WorkOrder;

public class SkidTimesFragment extends SectionFragment implements
		OnClickListener, OnEditorActionListener, View.OnFocusChangeListener, ViewEventResponder,
		OnItemSelectedListener {
	private SkidFinishedBroadcastReceiver mAlarmReceiver;
	private List<Skid<Product>> mSkidList;
	private List<Integer> mSkidNumbersList;
	private ImageButton mImgBtn_calcSheetsPerMinute;
	private Button mBtn_cancelAlarm;
	private Button mBtn_newSkid;
	private Button mBtn_calculateTimes;
	private Spinner mSpin_skidNumber;
	
	private List<View> mEditableGroup;
	
	private EditText mEdit_sheetsPerMinute;
	private EditText mEdit_currentCount;
	private EditText mEdit_totalSheetsPerSkid;
	private EditText mEdit_skidStartTime;
	private EditText mEdit_skidFinishTime;
	private EditText mEdit_numSkidsInJob;

	private TextView mTxt_timePerSkid;
	private TextView mLbl_productsPerMinute;
	private TextView mLbl_totalProducts;
	private TextView mTxt_jobFinishTime;
	private TextView mLbl_jobFinishTime;
	private TextView mLbl_timeToMaxson;
	private TextView mTxt_timeToMaxson;

	private long mMillisPerSkid;
	private int mSkidNumber;
	private String mJobFinishText;
	private String mTimeToMaxsonText;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mAlarmReceiver = new SkidFinishedBroadcastReceiver();
		
		SharedPreferences settings = this.getActivity().getPreferences(Context.MODE_PRIVATE);
		mMillisPerSkid = settings.getLong("millisPerSkid", 0);
		mJobFinishText = settings.getString("jobFinishText","");
		mTimeToMaxsonText = settings.getString("timeToMaxsonText", "");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.skid_times_fragment, container,
				false);

		mSpin_skidNumber = (Spinner) rootView.findViewById(R.id.spinner_skid_number);
		mSpin_skidNumber.setOnItemSelectedListener(this);
		
		//set up editTexts
		mEditableGroup = new ArrayList<View>(); //TODO make the finish times noneditable since they're not in the group.
		
		mEdit_sheetsPerMinute = (EditText) rootView
				.findViewById(R.id.edit_products_per_minute);
		mEditableGroup.add(mEdit_sheetsPerMinute);
		
		mEdit_numSkidsInJob = (EditText) rootView
				.findViewById(R.id.edit_num_skids_in_job);
		mEditableGroup.add(mEdit_numSkidsInJob); //needs to be added before skid number so skid number will be in range when checked
		
		mEdit_currentCount = (EditText) rootView
				.findViewById(R.id.edit_current_count);
		mEditableGroup.add(mEdit_currentCount);
		
		mEdit_totalSheetsPerSkid = (EditText) rootView
				.findViewById(R.id.edit_total_sheets_per_skid);
		mEditableGroup.add(mEdit_totalSheetsPerSkid);
		
		mEdit_skidStartTime = (EditText) rootView
				.findViewById(R.id.edit_skid_start_time);
		
		mEdit_skidFinishTime = (EditText) rootView
				.findViewById(R.id.edit_skid_finish_time);
		
		for (View v : mEditableGroup) {
			EditText etv = (EditText) v;
			etv.setOnEditorActionListener(this);
			etv.setOnFocusChangeListener(this);
		}

		//set up buttons
		mImgBtn_calcSheetsPerMinute = (ImageButton) rootView
				.findViewById(R.id.imgBtn_sheets_per_minute);
		mImgBtn_calcSheetsPerMinute.setOnClickListener(this);
		
		mBtn_newSkid = (Button) rootView.findViewById(R.id.btn_new_skid);
		mBtn_newSkid.setOnClickListener(this);
		
		mBtn_cancelAlarm = (Button) rootView.findViewById(R.id.btn_cancel_alarm);
		mBtn_cancelAlarm.setOnClickListener(this);
		
		mBtn_calculateTimes = (Button) rootView.findViewById(R.id.btn_calculate_times);
		mBtn_calculateTimes.setOnClickListener(this);
		
		//set up textviews
		mTxt_timePerSkid = (TextView) rootView
				.findViewById(R.id.txt_time_per_skid);
		if (mMillisPerSkid > 0) {
			this.mTxt_timePerSkid.setText(HelperFunction
					.formatMinutesAsHours(mMillisPerSkid / HelperFunction.ONE_MINUTE_IN_MILLIS));	
		}
		
		mLbl_productsPerMinute = (TextView) rootView
				.findViewById(R.id.lbl_products_per_minute);
		mLbl_totalProducts = (TextView) rootView.findViewById(R.id.lbl_total_products);


		
		mTxt_jobFinishTime = (TextView) rootView.findViewById(R.id.txt_job_finish_time);
		if (mJobFinishText != null) {
			mTxt_jobFinishTime.setText(mJobFinishText);
		}
		
		mTxt_timeToMaxson = (TextView) rootView.findViewById(R.id.txt_time_to_maxson);
		if (mTimeToMaxsonText != null) {
			mTxt_timeToMaxson.setText(mTimeToMaxsonText);
		}
		
		mLbl_jobFinishTime = (TextView) rootView.findViewById(R.id.lbl_job_finish_time);
			
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
		return false;
	}

	
	/* (non-Javadoc)
	 * @see android.view.View.OnFocusChangeListener#onFocusChange(android.view.View, boolean)
	 */
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		
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
	public void onItemSelected(AdapterView<?> parent, View arg1, int pos,
			long arg3) {
		String skidNumber = String.valueOf(parent.getItemAtPosition(pos));
		if ( (Integer.valueOf(skidNumber) - 1) != pos ) throw new RuntimeException("i just don't get it");
		((MainActivity)getActivity()).getModelDebug().changeSkid(Integer.valueOf(skidNumber));
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case (R.id.imgBtn_sheets_per_minute):
			((MainActivity)getActivity()).showSheetsPerMinuteDialog();
			break;
		case (R.id.btn_calculate_times):
			//supply default values and validate entries
			String numSkids = mEdit_numSkidsInJob.getText().toString();
			String skidNumber = mSpin_skidNumber.getSelectedItem().toString();
			if ( numSkids.equals("") || ( Integer.valueOf(skidNumber) > Integer.valueOf(numSkids)) ) {
				mEdit_numSkidsInJob.setText(skidNumber);
			}
			//Process the entries only if all the necessary ones are filled.
			Iterator<View> itr = mEditableGroup.iterator();
			boolean calculatable = true;
			while (itr.hasNext()) {
				EditText et = (EditText)itr.next();
				et.setTextAppearance(getActivity(), R.style.UserEntered);
				if ( et.getText().toString().trim().equals("") ) {
					et.setError(getString(R.string.error_empty_field));
					calculatable = false;
				}
			}
			if (calculatable) {
				submitSkidData();
			}
			break;
		case (R.id.btn_new_skid):
			int oldNumSkids = ((MainActivity)getActivity()).getModelDebug().getSelectedWorkOrder().getSkidsList().size();
			((MainActivity)getActivity()).getModelDebug().getSelectedWorkOrder().setNumberOfSkids(oldNumSkids + 1);
			Skid<Product> terrible = new Skid<Product> (null, Integer.valueOf(mEdit_totalSheetsPerSkid.getText().toString()), 0.0d, 1, null, null);
			terrible.setSkidNumber(oldNumSkids + 1);
			((MainActivity)getActivity()).getModelDebug().saveSkid(terrible);
			((MainActivity)getActivity()).getModelDebug().changeSkid(oldNumSkids + 1);
			populateSpinner();
			break;
		case (R.id.btn_cancel_alarm):
			Context context = getActivity();
			
			Vibrator vib = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
			vib.vibrate(new long[]{0,10},-1);
			vib.cancel();
			
			
			Intent intent = new Intent(context, SkidFinishedBroadcastReceiver.class);
	        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
	        pi.cancel();
	        break;
		}
	}

	private void submitSkidData() {
		Skid<Product> skid = new Skid<Product>(
				null, 
				Integer.valueOf(mEdit_totalSheetsPerSkid.getText().toString()), 
				0.0d, 
				1, 
				null, 
				null);
		skid.setCurrentItems(Integer.valueOf(mEdit_currentCount.getText().toString()));
		skid.setSkidNumber(Integer.valueOf(mSpin_skidNumber.getSelectedItem().toString()));
		PrimexModel modelDebug =((MainActivity)getActivity()).getModelDebug(); 
		modelDebug.saveSkid(skid);
		modelDebug.changeNumberOfSkids(Integer.valueOf(mEdit_numSkidsInJob.getText().toString()));
		modelDebug.changeSkid(skid.getSkidNumber());
		modelDebug.setProductsPerMinute(Double.valueOf(mEdit_sheetsPerMinute.getText().toString()));
	}
	public void onetimeTimer(View v, Integer interval) {

		Context context = getActivity();
		if (mAlarmReceiver != null) {
			mAlarmReceiver.setOnetimeTimer(context, interval);
		} else {
			Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void repeatingTimer(View v, long trigger, long interval) {

		Context context = getActivity();
		if (mAlarmReceiver != null) {
			mAlarmReceiver.setRepeatingTimer(context, trigger, interval);
		} else {
			Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
		}
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
			
		} else if (propertyName == PrimexModel.CURRENT_SKID_FINISH_TIME_CHANGE_EVENT) {
			//update finish time for this skid
			SimpleDateFormat formatter = new SimpleDateFormat("hh:mm", Locale.US);
			String formattedTime = formatter.format((Date)newProperty);
			mEdit_skidFinishTime.setText(formattedTime);
			
			//set alarm 
			long alarmLeadTime = (long) (1.5 * HelperFunction.ONE_MINUTE_IN_MILLIS); //TODO
			Date curDate = new Date();
			long timeNow = curDate.getTime();
			long timeThen = ((Date)newProperty).getTime();
			Long triggerAtMillis = timeThen - timeNow - alarmLeadTime;
			if (triggerAtMillis > 0) {
				repeatingTimer( mEdit_skidFinishTime, triggerAtMillis, mMillisPerSkid );				
			}			
		} else if (propertyName == PrimexModel.CURRENT_SKID_START_TIME_CHANGE_EVENT) {
			SimpleDateFormat formatter = new SimpleDateFormat("hh:mm", Locale.US);
			String formattedTime = formatter.format((Date)newProperty);
			mEdit_skidStartTime.setText(formattedTime);
			
		} else if (propertyName == PrimexModel.MINUTES_PER_SKID_CHANGE_EVENT) {
			this.mTxt_timePerSkid.setText(HelperFunction
					.formatMinutesAsHours((Long)newProperty));
			mMillisPerSkid = (Long)newProperty * HelperFunction.ONE_MINUTE_IN_MILLIS;
			
		} else if (propertyName == PrimexModel.NUMBER_OF_SKIDS_CHANGE_EVENT) {
			mEdit_numSkidsInJob.setText(String.valueOf(newProperty));
			populateSpinner();
			
		} else if (propertyName == PrimexModel.JOB_FINISH_TIME_CHANGE_EVENT) {
			Date finishTime = (Date)newProperty;
			SimpleDateFormat formatter = new SimpleDateFormat("h:mm a", Locale.US);
			mJobFinishText = formatter.format(finishTime);
			mTxt_jobFinishTime.setText(mJobFinishText);
			
		} else if (propertyName == PrimexModel.SKID_CHANGE_EVENT) {
			Skid<Product> skid = (Skid<Product>)newProperty;
			mSpin_skidNumber.setSelection(skid.getSkidNumber() - 1);
			mEdit_currentCount.setText(String.valueOf(skid.getCurrentItems()));
			mEdit_totalSheetsPerSkid.setText(String.valueOf(skid.getTotalItems()));	
			
		} else if (propertyName == PrimexModel.TIME_TO_MAXSON_CHANGE_EVENT) {
			Double timeToMaxson = (Double)newProperty;
			mTimeToMaxsonText = Math.round(timeToMaxson / HelperFunction.ONE_SECOND_IN_MILLIS) + " seconds";
			mTxt_timeToMaxson.setText(mTimeToMaxsonText);
			
		} else if (propertyName == PrimexModel.NEW_WORK_ORDER_EVENT) {
			
		} else if (propertyName == PrimexModel.SELECTED_WO_CHANGE_EVENT) {
			populateSpinner();
		}
	}

	private void populateSpinner() {
		List<Integer> skidNumbers = new ArrayList<Integer> ();
		skidNumbers =((MainActivity)getActivity()).getSkidNumbersDebug();
		if (!skidNumbers.equals(mSkidNumbersList)) {
			//prevents infinite refreshing when you set the adapter
			ArrayAdapter<Integer> skidNumAdapter = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_item, skidNumbers);
			skidNumAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mSpin_skidNumber.setAdapter(skidNumAdapter);	
		}
		
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		SharedPreferences settings = this.getActivity().getPreferences(Context.MODE_PRIVATE);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putLong("millisPerSkid", mMillisPerSkid);
	    editor.putString("jobFinishText", mJobFinishText);
	    editor.putString("timeToMaxsonText", mTimeToMaxsonText);
	    
	    // Commit the edits!
	    editor.commit();
		super.onPause();
	}
}

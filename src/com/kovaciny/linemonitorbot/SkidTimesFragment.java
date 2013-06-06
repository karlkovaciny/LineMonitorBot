package com.kovaciny.linemonitorbot;

import java.beans.PropertyChangeEvent;
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

import com.kovaciny.helperfunctions.HelperFunction;
import com.kovaciny.primexmodel.PrimexModel;
import com.kovaciny.primexmodel.Product;
import com.kovaciny.primexmodel.Skid;

public class SkidTimesFragment extends SectionFragment implements
		OnClickListener, OnEditorActionListener, ViewEventResponder {
	private SkidFinishedBroadcastReceiver mAlarmReceiver;
	private double mSheetsPerMinute;
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

	OnSheetsPerMinuteChangeListener mCallback;

	// Container Activity must implement this interface
	public interface OnSheetsPerMinuteChangeListener {
		public void onSheetsPerMinuteChanged(double sheetsPerMinute);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.skid_times_fragment, container,
				false);

		mEditTextList = new ArrayList<View>();

		mEdit_sheetsPerMinute = (EditText) rootView
				.findViewById(R.id.sheets_per_minute);
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
			((EditText) v).setOnEditorActionListener(this);
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
			v.setTextAppearance(getActivity(), R.style.user_entered);
			if (v.getId() == R.id.sheets_per_minute) {
				String sheetsPerMin = ((TextView) v).getText().toString();
				if (sheetsPerMin.length() != 0) {
					mCallback.onSheetsPerMinuteChanged(Double
							.valueOf(sheetsPerMin)); // the whole app needs to
														// know when the sheets
														// per minute change
				}
			}
			String currentCount = this.mEdit_currentCount.getText().toString();
			String totalCount = this.mEdit_totalSheetsPerSkid.getText()
					.toString();
			String spm = this.mEdit_sheetsPerMinute.getText().toString();
			if ((currentCount.length() > 0) && (totalCount.length() > 0)
					&& (spm.length() > 0)) {
				double minutesLeft = (Double.valueOf(totalCount) - Double
						.valueOf(currentCount)) / Double.valueOf(spm);
				this.mTxt_timePerSkid.setText(HelperFunction
						.formatMinutesAsHours(Math.round(minutesLeft)));
			}
		}
		return false;
	}

	public void hideAll() {
		
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

	public void onetimeTimer(View view, Integer interval) {

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
		Object oldProperty = event.getOldValue();
		Object newProperty = event.getNewValue();
		
		if (propertyName == PrimexModel.PRODUCT_CHANGE_EVENT) {
			if (newProperty == null) {
				mLbl_productsPerMinute.setText("no product"); //debug
			} else {
				String units = ((Product)newProperty).getUnits();
				StringBuilder capUnits = new StringBuilder(units);
				capUnits.setCharAt(0, Character.toUpperCase(units.charAt(0)));
				this.mLbl_productsPerMinute.setText(capUnits.toString() + " per minute");
				this.mLbl_totalProducts.setText("Total\n" + units);
			}
		}
	}
}

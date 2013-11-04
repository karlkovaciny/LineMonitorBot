package com.kovaciny.linemonitorbot;

import static com.kovaciny.linemonitorbot.MainActivity.DEBUG;

import java.beans.PropertyChangeEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kovaciny.helperfunctions.HelperFunction;
import com.kovaciny.primexmodel.PrimexModel;
import com.kovaciny.primexmodel.Product;
import com.kovaciny.primexmodel.Skid;
import com.kovaciny.primexmodel.WorkOrder;

public class SkidTimesFragment extends Fragment implements
		OnClickListener, ViewEventResponder	{
    private Button mBtn_enterProduct;
	private ImageButton mImgBtn_cancelAlarm;
	private Button mBtn_calculateTimes;
	private Button mBtn_goByHeight;
	private Button mBtn_rollMath;
	private ImageButton mBtn_skidNumberUp;
	private ImageButton mBtn_totalSkidsUp;
	private ImageButton mImgBtn_launchSkidsList;
	
	private EditText mEdit_currentSkidNumber;
	private EditText mEdit_currentCount;
	private EditText mEdit_totalCountPerSkid;
	private EditText mEdit_numSkidsInJob;

	private TextView mLbl_productType;
	private TextView mLbl_skidFinishTime;
	private TextView mTxt_skidFinishTime;
	private TextView mLbl_jobFinishTime;
	private TextView mTxt_jobFinishTime;
	private TextView mLbl_skidStartTime;
	private TextView mTxt_skidStartTime;
	private TextView mLbl_timePerSkid;

	private TextView mTxt_timePerSkid;
	private TextView mLbl_productsPerMinute;
	private TextView mTxt_productsPerMinute;
	private TextView mLbl_totalProducts;
	private TextView mLbl_products;
	private TextView mLbl_timeToMaxson;
	private TextView mTxt_timeToMaxson;
	
	private SkidFinishedBroadcastReceiver mAlarmReceiver;
	private List<Skid<Product>> mSkidList;
	private List<EditText> mEditableGroup;
	private List<TextView> mTimesDisplayList;
	private List<TextView> mTimesDisplayLabelsList;
	private long mMillisPerSkid;
	private String mProductUnits = DEFAULT_PRODUCT_UNITS;
	private String mProductGrouping = DEFAULT_PRODUCT_GROUPING;
	
	public static final int MAXIMUM_NUMBER_OF_SKIDS = 200;
	public static final String DEFAULT_PRODUCT_UNITS = "Sheets";
    public static final String DEFAULT_PRODUCT_GROUPING = "Skid";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mAlarmReceiver = new SkidFinishedBroadcastReceiver();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.skid_times_fragment, container,
				false);

		//set up editTexts		
		mEdit_numSkidsInJob = (EditText) rootView.findViewById(R.id.edit_num_skids_in_job);
		mEdit_currentSkidNumber = (EditText) rootView.findViewById(R.id.edit_skid_number);
		mEdit_currentCount = (EditText) rootView.findViewById(R.id.edit_current_count);
		mEdit_totalCountPerSkid = (EditText) rootView.findViewById(R.id.edit_total_sheets_per_skid);
		
		mEditableGroup = new ArrayList<EditText>(); 
		ViewGroup dataEntryGroup = (RelativeLayout) rootView.findViewById(R.id.rl_data_entry_group);
		for (int i = 0, n = dataEntryGroup.getChildCount(); i < n; i++) {
			View nextChild = dataEntryGroup.getChildAt(i);
			if (nextChild instanceof EditText) {
				mEditableGroup.add((EditText)nextChild);
			}
		}
		
		//set up buttons
		mBtn_skidNumberUp = (ImageButton) rootView.findViewById(R.id.btn_skid_number_up);
		mBtn_skidNumberUp.setOnClickListener(this);
		mBtn_skidNumberUp.setColorFilter(new LightingColorFilter(0xFF99DDFF, 0xFF0000FF));
		
		mBtn_totalSkidsUp = (ImageButton) rootView.findViewById(R.id.btn_total_skids_up);
		mBtn_totalSkidsUp.setOnClickListener(this);
		mBtn_totalSkidsUp.setColorFilter(new LightingColorFilter(0xFF99DDFF, 0xFF0000FF));
		
		mBtn_enterProduct = (Button) rootView
				.findViewById(R.id.btn_enter_product);
		mBtn_enterProduct.setOnClickListener(this);
		mBtn_enterProduct.getBackground().setColorFilter(new LightingColorFilter(0xFF99DDFF, 0xFF0000FF));
        		
		mImgBtn_cancelAlarm = (ImageButton) rootView.findViewById(R.id.imgbtn_cancel_alarm);
		mImgBtn_cancelAlarm.setOnClickListener(this);
	
		mImgBtn_launchSkidsList = (ImageButton) rootView.findViewById(R.id.imgbtn_launch_skids_list);
		mImgBtn_launchSkidsList.setOnClickListener((MainActivity)getActivity());
		mImgBtn_launchSkidsList.setVisibility(ImageButton.VISIBLE);
		
		mBtn_calculateTimes = (Button) rootView.findViewById(R.id.btn_get_times);
		mBtn_calculateTimes.setOnClickListener(this);
		mBtn_calculateTimes.getBackground().setColorFilter(new LightingColorFilter(0xFF99DDFF,0xFF0000FF));
		
		mBtn_goByHeight = (Button) rootView.findViewById(R.id.btn_go_by_height);
		mBtn_goByHeight.setOnClickListener(this);
		
		mBtn_rollMath = (Button) rootView.findViewById(R.id.btn_roll_math);
        mBtn_rollMath.setOnClickListener((MainActivity) getActivity());
		
		//set up textViews
		mTxt_timePerSkid = (TextView) rootView.findViewById(R.id.txt_time_per_skid);		
		mTxt_jobFinishTime = (TextView) rootView.findViewById(R.id.txt_job_finish_time);
		mTxt_timeToMaxson = (TextView) rootView.findViewById(R.id.txt_time_to_maxson);
		mTxt_productsPerMinute = (TextView) rootView.findViewById(R.id.txt_products_per_minute);
		mTxt_skidStartTime = (TextView) rootView.findViewById(R.id.txt_skid_start_time);
		mTxt_skidFinishTime = (TextView) rootView.findViewById(R.id.txt_skid_finish_time);
		mTimesDisplayList = Arrays.asList(new TextView[]{mTxt_timePerSkid, mTxt_jobFinishTime, mTxt_timeToMaxson,
				mTxt_productsPerMinute, mTxt_skidStartTime, mTxt_skidFinishTime});
		
		mLbl_skidFinishTime = (TextView) rootView.findViewById(R.id.lbl_skid_finish_time);
		mLbl_jobFinishTime = (TextView) rootView.findViewById(R.id.lbl_job_finish_time);
		mLbl_skidStartTime = (TextView) rootView.findViewById(R.id.lbl_skid_start_time);
		mLbl_timePerSkid = (TextView) rootView.findViewById(R.id.lbl_time_per_skid);		
		mLbl_productsPerMinute = (TextView) rootView.findViewById(R.id.lbl_products_per_minute);
		mLbl_timeToMaxson = (TextView) rootView.findViewById(R.id.lbl_time_to_maxson);
		mTimesDisplayLabelsList = Arrays.asList(new TextView[]
				{mLbl_skidFinishTime, mLbl_jobFinishTime, mLbl_skidStartTime, mLbl_timePerSkid, 
				mLbl_productsPerMinute, mLbl_timeToMaxson});

		mLbl_products = (TextView) rootView.findViewById(R.id.lbl_products);
		mLbl_productType = (TextView) rootView.findViewById(R.id.lbl_product_type);
		
		//restore saved state
		SharedPreferences settings = this.getActivity().getPreferences(Context.MODE_PRIVATE);
		boolean visible = settings.getBoolean("cancelAlarmVisible", false);
		String tps = settings.getString("timePerSkid", "");
		String ppm = settings.getString("productsPerMinute", "");
		String jft = settings.getString("jobFinishTime", "");
		String ttm = settings.getString("timeToMaxson", "");
		String sst = settings.getString("skidStartTime", "");
		String sft = settings.getString("skidFinishTime", "");
		if (visible) {
			mImgBtn_cancelAlarm.setVisibility(Button.VISIBLE);
		} else mImgBtn_cancelAlarm.setVisibility(Button.INVISIBLE);
		mTxt_timePerSkid.setText(tps);
		mTxt_productsPerMinute.setText(ppm);
		mTxt_jobFinishTime.setText(jft);
		mTxt_timeToMaxson.setText(ttm);
		mTxt_skidStartTime.setText(sst);
		mTxt_skidFinishTime.setText(sft);

		return rootView;
	}

	public List<Skid<Product>> getSkidList() {
		return mSkidList;
	}

	public void setSkidList(List<Skid<Product>> skidList) {
		this.mSkidList = skidList;
	}

	@Override
	public void onClick(View v) {
		DialogController dialogController = new DialogController((MainActivity)getActivity());
	    switch (v.getId()) {
	    case (R.id.btn_skid_number_up):
			HelperFunction.hideKeyboard(getActivity());
			HelperFunction.incrementEditText(mEdit_currentSkidNumber);
			if (Double.valueOf(mEdit_currentSkidNumber.getText().toString()) >
					Math.ceil(Double.valueOf(mEdit_numSkidsInJob.getText().toString()))) { 
				//it's OK for this to be higher than a fractional number of skids
				HelperFunction.incrementEditText(mEdit_numSkidsInJob); 
			}
			for (EditText ett : mEditableGroup) {
                ett.clearFocus();
            }
			break;
		case (R.id.btn_total_skids_up):
			HelperFunction.hideKeyboard(getActivity());
			HelperFunction.incrementEditText(mEdit_numSkidsInJob);
			mEdit_numSkidsInJob.clearFocus();
			for (EditText ett : mEditableGroup) {
                ett.clearFocus();
            }
			break;
		case (R.id.btn_enter_product):
			mBtn_enterProduct.setError(null);
			for (EditText et : mEditableGroup) {
				et.clearFocus();
			}
			dialogController.showEnterProductDialog();
			break;
		case (R.id.btn_go_by_height):
			int updatedTotalSheets = Skid.DEFAULT_SHEET_COUNT;
			if (mEdit_totalCountPerSkid.getText().length() > 0) {
				updatedTotalSheets = Integer.valueOf(mEdit_totalCountPerSkid.getText().toString());
			}
			dialogController.showGoByHeightDialog(updatedTotalSheets);
			break;
		case (R.id.imgbtn_cancel_alarm):
		    cancelAlarm();
		    Toast.makeText(getActivity(), getResources().getString(R.string.toast_alarm_canceled), Toast.LENGTH_SHORT).show();
		    break;
		case (R.id.btn_get_times):
			//supply default values
			if (mEdit_currentCount.getText().length() == 0) {
				mEdit_currentCount.setText("0");
			}
			
			if (mEdit_currentSkidNumber.getText().length() == 0) {
				mEdit_currentSkidNumber.setText("1");
			}
			String currentSkidNumText = mEdit_currentSkidNumber.getText().toString();
			
			if (mEdit_numSkidsInJob.getText().length() == 0 ) {
				mEdit_numSkidsInJob.setText(currentSkidNumText);
			}
			
			if (mEdit_numSkidsInJob.getText().toString() == "0") {
				mEdit_numSkidsInJob.setText("1");
			}
			
			if (mEdit_totalCountPerSkid.getText().length() == 0) {
				mEdit_totalCountPerSkid.setText("0"); 
			}
			
			Integer currentSkidNum = Integer.valueOf(currentSkidNumText);
			Double totalSkids = Double.valueOf(mEdit_numSkidsInJob.getText().toString());
			
			if (currentSkidNum > Math.ceil(totalSkids)) {
				mEdit_numSkidsInJob.setText(currentSkidNumText);
			}
			
			BigDecimal fractionalSkid = new BigDecimal(totalSkids).remainder(BigDecimal.ONE);
			if (fractionalSkid.compareTo(BigDecimal.ZERO) != 0) {
				int wholeSkids = (int) Math.ceil(totalSkids);
				if (currentSkidNum == wholeSkids) {
					BigDecimal fractionalSheetCount = fractionalSkid.multiply(new BigDecimal(mEdit_totalCountPerSkid.getText().toString()));
					String roundedString = String.valueOf(fractionalSheetCount.toBigInteger());
					mEdit_totalCountPerSkid.setText(roundedString);
					mEdit_numSkidsInJob.setText(String.valueOf(wholeSkids));
				}				  
			}
			
			//Process the entries only if all the necessary ones are filled.
			Iterator<EditText> itr = mEditableGroup.iterator();
			boolean validInputs = true;
			int currentCount = Integer.valueOf(mEdit_currentCount.getText().toString());
			int totalCount = Integer.valueOf(mEdit_totalCountPerSkid.getText().toString());
			
			if (currentCount > totalCount) {
				mEdit_currentCount.setError(getString(R.string.error_current_greater_than_total));
				validInputs = false;
			}
			
			if (totalCount == 0) {
				mEdit_totalCountPerSkid.setError(getString(R.string.error_need_nonzero));
				validInputs = false;
			} 
			
			if (Double.valueOf(mEdit_numSkidsInJob.getText().toString()) > MAXIMUM_NUMBER_OF_SKIDS) {
				mEdit_numSkidsInJob.setError(getString(R.string.error_over_maximum_skids).replace("%1", String.valueOf(MAXIMUM_NUMBER_OF_SKIDS)));
				validInputs = false;
			}
			
			while (itr.hasNext()) {
				EditText et = itr.next();
				if ( et.getText().toString().trim().equals("") ) {
					et.setError(getString(R.string.error_empty_field));
					validInputs = false;
				}
			}
			
			if (validInputs) {
				HelperFunction.hideKeyboard(getActivity());
				mBtn_enterProduct.setError(null);
				try {
					dialogController.updateSkidData(
							Integer.valueOf(mEdit_currentSkidNumber.getText().toString()),
							Integer.valueOf(mEdit_currentCount.getText().toString()),
							Integer.valueOf(mEdit_totalCountPerSkid.getText().toString()),
							Double.valueOf(mEdit_numSkidsInJob.getText().toString())
							);
				} catch (IllegalStateException e) {
					if (e.getCause().getMessage().equals(PrimexModel.ERROR_NO_PRODUCT_SELECTED)) {
						mBtn_enterProduct.setError(getString(R.string.prompt_need_product));
					} else {
						throw e;
					}
				}
					
				for (EditText ett : mEditableGroup) {
					ett.setError(null);
					ett.clearFocus();
				}
			}
			break;
		}
	}

	public void onetimeTimer(View v, long interval) {

		Context context = getActivity();
		if (mAlarmReceiver != null) {
			mAlarmReceiver.setOnetimeTimer(context, interval);
		} else {
			Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
		}
		mImgBtn_cancelAlarm.setVisibility(Button.VISIBLE);
	}
	
	public void repeatingTimer(View v, long trigger, long interval) {

		Context context = getActivity();
		if (mAlarmReceiver != null) {
			mAlarmReceiver.setRepeatingTimer(context, trigger, interval);
		} else {
			Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
		}
		mImgBtn_cancelAlarm.setVisibility(Button.VISIBLE);
	}

	public void cancelAlarm() {
		Context context = getActivity();
		
		Vibrator vib = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		vib.vibrate(new long[]{0,10},-1);
		vib.cancel();
		
		Intent intent = new Intent(context, SkidFinishedBroadcastReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        pi.cancel();
        
        mImgBtn_cancelAlarm.setVisibility(Button.INVISIBLE);
	}
	public void modelPropertyChange (PropertyChangeEvent event) {
		String propertyName = event.getPropertyName();
		Object newProperty = event.getNewValue();
		
		if (propertyName == PrimexModel.PRODUCT_CHANGE_EVENT) {
			if (newProperty == null) {
			    mProductUnits = DEFAULT_PRODUCT_UNITS;
			    mProductGrouping = DEFAULT_PRODUCT_GROUPING;
			    mBtn_goByHeight.setVisibility(Button.GONE);
			    mBtn_rollMath.setVisibility(Button.GONE);
			    mBtn_calculateTimes.setEnabled(false);
			    mBtn_enterProduct.getBackground().setColorFilter(new LightingColorFilter(0xFF99DDFF, 0xFF0000FF));
			    mBtn_enterProduct.setTextAppearance(getActivity(), R.style.Button);
			    mBtn_enterProduct.setText(getString(R.string.btn_enter_product_text));
			} else {
			    Product p = (Product)newProperty;
			    mProductUnits = HelperFunction.capitalizeFirstChar(p.getUnits());
			    mProductGrouping = HelperFunction.capitalizeFirstChar(p.getGrouping());
			    if (p.hasStacks()) {
			        mBtn_goByHeight.setVisibility(Button.VISIBLE);
			        mBtn_rollMath.setVisibility(Button.GONE);
			    } else {
			        mBtn_goByHeight.setVisibility(Button.GONE);
			        mBtn_rollMath.setVisibility(Button.VISIBLE);
			    }
			    mBtn_calculateTimes.setEnabled(true);
			    
			    mBtn_enterProduct.setText(p.getFormattedDimensions());
                mBtn_enterProduct.getBackground().clearColorFilter();
                mBtn_enterProduct.setTextAppearance(getActivity(), R.style.Button_Minor);
			}
			updateLabels();
			
		} else if (propertyName == PrimexModel.PRODUCTS_PER_MINUTE_CHANGE_EVENT) {
			if ( (newProperty == null) || ((Double)newProperty <= 0) ) {
				mTxt_productsPerMinute.setText("");
				mLbl_productsPerMinute.setVisibility(TextView.INVISIBLE);
			} else {
				mTxt_productsPerMinute.setText(String.valueOf(newProperty));
				mLbl_productsPerMinute.setVisibility(TextView.VISIBLE);
			}
			
		} else if (propertyName == PrimexModel.CURRENT_SKID_FINISH_TIME_CHANGE_EVENT) {
			if (newProperty == null) {
				mTxt_skidFinishTime.setText("");
				mLbl_skidFinishTime.setVisibility(TextView.INVISIBLE);
				mImgBtn_cancelAlarm.setVisibility(ImageButton.GONE);
			} else {
			    Date roundedTimeForDisplay = HelperFunction.toNearestWholeMinute((Date)newProperty);
			    SimpleDateFormat formatter;
			    formatter = new SimpleDateFormat("h:mm a", Locale.US); //drops seconds
			    String formattedTime = formatter.format(roundedTimeForDisplay);
				mTxt_skidFinishTime.setText(formattedTime);
				mLbl_skidFinishTime.setVisibility(TextView.VISIBLE);
				mImgBtn_cancelAlarm.setVisibility(ImageButton.VISIBLE);
				//set alarm 
				long alarmLeadTime = (long) (1.5 * HelperFunction.ONE_MINUTE_IN_MILLIS); //TODO
				Date curDate = new Date();
				long timeNow = curDate.getTime();
				long timeThen = ((Date)newProperty).getTime();
				Long triggerAtMillis = timeThen - timeNow - alarmLeadTime;
				if (triggerAtMillis > 0) {
					SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
					Boolean repeatPref = sharedPref.getBoolean("repeating_timer", false);
					if (repeatPref) {
						repeatingTimer( mTxt_skidFinishTime, triggerAtMillis, mMillisPerSkid );	
					} else {
						onetimeTimer(mTxt_skidFinishTime, triggerAtMillis);
					}
				}
			}
			
		} else if (propertyName == PrimexModel.JOB_FINISH_TIME_CHANGE_EVENT) {
			if (newProperty == null) {
				mTxt_jobFinishTime.setText("");
				mLbl_jobFinishTime.setVisibility(TextView.INVISIBLE);
				mImgBtn_launchSkidsList.setVisibility(ImageButton.INVISIBLE);
			} else {
			    Date roundedTimeForDisplay = HelperFunction.toNearestWholeMinute((Date)newProperty);
                SimpleDateFormat formatter3 = new SimpleDateFormat("h:mm a E", Locale.US);
				
				//"Pace time": Don't show the day of the week if it's before 6 am the next day. 
				Calendar finishDate = new GregorianCalendar(Locale.US);
				finishDate.setTime(roundedTimeForDisplay);
				Calendar today = Calendar.getInstance(Locale.US);
				today.add(Calendar.DAY_OF_MONTH, 1);
				today.set(Calendar.HOUR_OF_DAY, 6);
				today.set(Calendar.MINUTE, 0);
				if (finishDate.before(today)) {
					formatter3 = new SimpleDateFormat("h:mm a", Locale.US);
				}
				mTxt_jobFinishTime.setText(formatter3.format(roundedTimeForDisplay));
				mLbl_jobFinishTime.setVisibility(TextView.VISIBLE);
				mImgBtn_launchSkidsList.setVisibility(ImageButton.VISIBLE);
			}
			
		} else if (propertyName == PrimexModel.CURRENT_SKID_START_TIME_CHANGE_EVENT) {
			if (newProperty == null) {
				mTxt_skidStartTime.setText("");
				mLbl_skidStartTime.setVisibility(TextView.INVISIBLE);
			} else {
				SimpleDateFormat formatter2 = new SimpleDateFormat("h:mm a", Locale.US);
				String formattedTime2 = formatter2.format((Date)newProperty);
				mTxt_skidStartTime.setText(formattedTime2);
				mLbl_skidStartTime.setVisibility(TextView.VISIBLE);
			}
			
		} else if (propertyName == PrimexModel.MINUTES_PER_SKID_CHANGE_EVENT) {
			if ((newProperty == null) || ((Double)newProperty <= 0) ) {
				mTxt_timePerSkid.setText("");
				mLbl_timePerSkid.setVisibility(TextView.INVISIBLE);
			} else {
				long minutes = Math.round((Double)newProperty);
				mMillisPerSkid = minutes * HelperFunction.ONE_MINUTE_IN_MILLIS;
				
				mTxt_timePerSkid.setText( HelperFunction.formatMinutesAsHours(minutes) );
				mLbl_timePerSkid.setVisibility(TextView.VISIBLE);
			}
			
		} else if (propertyName == PrimexModel.NUMBER_OF_SKIDS_CHANGE_EVENT) {
			String number = new DecimalFormat("#.###").format(newProperty);
			mEdit_numSkidsInJob.setText(number);
			
		} else if (propertyName == PrimexModel.NUMBER_OF_TABLE_SKIDS_CHANGE_EVENT) {

		} else if (propertyName == PrimexModel.SKID_CHANGE_EVENT) {
			Skid<?> skid = (Skid<?>)newProperty; //TODO check if null, clear the fields
			mEdit_currentSkidNumber.setText(String.valueOf(skid.getSkidNumber()));
			mEdit_currentCount.setText(String.valueOf(skid.getCurrentItems()));
			mEdit_totalCountPerSkid.setText(String.valueOf(skid.getTotalItems()));
			
		} else if (propertyName == PrimexModel.SECONDS_TO_MAXSON_CHANGE_EVENT) {
			Long timeToMaxson = (Long)newProperty;
			if (!(timeToMaxson > 0l)) {
				mTxt_timeToMaxson.setText("");
				mLbl_timeToMaxson.setVisibility(TextView.INVISIBLE);
                mTxt_timeToMaxson.setVisibility(TextView.GONE);
			} else {
				mTxt_timeToMaxson.setText( HelperFunction.formatSecondsAsMinutes(timeToMaxson) );
                mLbl_timeToMaxson.setVisibility(TextView.VISIBLE);
                mTxt_timeToMaxson.setVisibility(TextView.VISIBLE);
			}
			
		} else if (propertyName == PrimexModel.NEW_WORK_ORDER_EVENT) {
			for (TextView tv : mTimesDisplayList) {
				tv.setText("");
			}
			for (TextView lbl : mTimesDisplayLabelsList) {
				lbl.setVisibility(TextView.INVISIBLE);
			}
			
		} else if (propertyName == PrimexModel.SELECTED_WO_CHANGE_EVENT) {
		    WorkOrder wo = (WorkOrder)newProperty;
			Skid<Product> skid = wo.getSelectedSkid();
			mEdit_currentSkidNumber.setText(String.valueOf(skid.getSkidNumber()));
			mBtn_goByHeight.setText(getString(R.string.btn_go_by_height_text));
		} 
	}

	private void updateLabels() {
		mLbl_productType.setText(mProductGrouping + ":");
		mLbl_productsPerMinute.setText(mProductUnits + " per minute");
		mLbl_products.setText(mProductUnits + ":");
		mLbl_skidFinishTime.setText(mProductGrouping + " finish");
		mLbl_skidStartTime.setText(mProductGrouping + " start");
		mLbl_timePerSkid.setText("Time per " + mProductGrouping.toLowerCase(Locale.getDefault()));
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		//save the user's current state
		super.onSaveInstanceState(outState);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		SharedPreferences settings = this.getActivity().getPreferences(Context.MODE_PRIVATE);
	    SharedPreferences.Editor editor = settings.edit();
		boolean visible = false;
		if (mImgBtn_cancelAlarm.getVisibility() == Button.VISIBLE) {
			visible = true;
		}
		editor.putBoolean("cancelAlarmVisible", visible);
		editor.putString("timePerSkid", mTxt_timePerSkid.getText().toString());
		editor.putString("productsPerMinute", mTxt_productsPerMinute.getText().toString());
		editor.putString("jobFinishTime", mTxt_jobFinishTime.getText().toString());
		editor.putString("timeToMaxson", mTxt_timeToMaxson.getText().toString());
		editor.putString("skidStartTime", mTxt_skidStartTime.getText().toString());
		editor.putString("skidFinishTime", mTxt_skidFinishTime.getText().toString());

	    // Commit the edits!
	    editor.commit();
		super.onPause();
	}
}

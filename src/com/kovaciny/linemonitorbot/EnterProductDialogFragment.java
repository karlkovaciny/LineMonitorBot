package com.kovaciny.linemonitorbot;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.TextView;

import com.kovaciny.primexmodel.Product;
import com.kovaciny.primexmodel.ProductionLine;

public class EnterProductDialogFragment extends DialogFragment implements OnClickListener {

	public EnterProductDialogFragment() {
		super();
	}

	 /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
  	public interface EnterProductDialogListener {
		public void onClickPositiveButton(DialogFragment d);
	}
  	
  	public static final String SHEETS_MODE = Product.SHEETS_TYPE;
  	public static final String ROLLS_MODE = Product.ROLLS_TYPE;

  	LinearLayout mContainerProductDetails;
  	LinearLayout mContainerSkidsOnTable;
  	ImageButton mBtn_addWeb;
  	ImageButton mBtn_subtractWeb;
  	EditText mEdit_gauge;
  	EditText mEdit_sheetWidth;
  	EditText mEdit_sheetLength;
  	EditText mEdit_lineSpeed;
  	EditText mEdit_differentialSpeed;
  	EditText mEdit_speedFactor;
  	ImageView mImg_numberOfWebs;
  	TextView mLbl_numberOfWebs;
  	TextView mLbl_sheetLength;
  	TextView mLbl_speedFactor;
  	TextView mLbl_differentialSpeed;
  	TextView mLbl_reminderNoDifferentialSpeed;
  	ImageButton mImgbtnSheetsOrRolls;
  	RadioGroup mRadioGroup_skidsOnTable;
  	RadioButton mRadioButton_oneSkid;
  	RadioButton mRadioButton_twoSkids;
  	private String mSheetsOrRollsState;
  	private String mSpeedControllerType = ProductionLine.SPEED_CONTROLLER_TYPE_NONE;
  	private Double mDifferentialRangeLow;
  	private Double mDifferentialRangeHigh;
  	private int mNumberOfWebs;
  	public static final int MAXIMUM_NUMBER_OF_WEBS = 5;
  	
    // Use this instance of the interface to deliver action events
    EnterProductDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (EnterProductDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement EnterProductDialogListener");
        }
    }

	
	/* (non-Javadoc)
	 * @see android.app.DialogFragment#onCreateDialog(android.os.Bundle)
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Set other dialog properties
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View rootView = inflater.inflate(R.layout.enter_product_dialog, null);
		builder.setView(rootView);
		mContainerSkidsOnTable = (LinearLayout) inflater.inflate(R.layout.container_skids_on_table, null);
		
		mContainerProductDetails = (LinearLayout) rootView.findViewById(R.id.container_product_details);
		mRadioGroup_skidsOnTable = (RadioGroup) mContainerSkidsOnTable.findViewById(R.id.radio_group_skids_on_table);
		
		mRadioButton_oneSkid = (RadioButton) mContainerSkidsOnTable.findViewById(R.id.radio_one_skid);
		mRadioButton_twoSkids = (RadioButton) mContainerSkidsOnTable.findViewById(R.id.radio_two_skids);
		
		mBtn_addWeb = (ImageButton) rootView.findViewById(R.id.btn_add_web);
		mBtn_addWeb.setOnClickListener(this);
		
		mBtn_subtractWeb = (ImageButton) rootView.findViewById(R.id.btn_subtract_web);
		mBtn_subtractWeb.setOnClickListener(this);
		
		mImg_numberOfWebs = (ImageView) rootView.findViewById(R.id.img_number_of_webs);
		mLbl_numberOfWebs = (TextView) rootView.findViewById(R.id.lbl_number_of_webs);
		mLbl_speedFactor = (TextView) rootView.findViewById(R.id.lbl_speed_factor);
		mLbl_differentialSpeed = (TextView) rootView.findViewById(R.id.lbl_differential_speed);
		mLbl_reminderNoDifferentialSpeed = (TextView) rootView.findViewById(R.id.lbl_reminder_no_differential_speed);
		mLbl_sheetLength = (TextView) rootView.findViewById(R.id.lbl_sheet_length);
		
		mEdit_gauge = (EditText) rootView.findViewById(R.id.edit_gauge);
		mEdit_sheetWidth = (EditText) rootView.findViewById(R.id.edit_sheet_width);
		mEdit_sheetLength = (EditText) rootView.findViewById(R.id.edit_sheet_length);
		mEdit_lineSpeed = (EditText) rootView.findViewById(R.id.edit_line_speed);
		mEdit_differentialSpeed = (EditText) rootView.findViewById(R.id.edit_differential_speed);
		mEdit_speedFactor = (EditText) rootView.findViewById(R.id.edit_speed_factor);
		mImgbtnSheetsOrRolls = (ImageButton) rootView.findViewById(R.id.imgbtn_sheets_or_rolls);
		
		markRequiredFields();
		
		SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);
		
		if (getArguments() != null) {
			mDifferentialRangeLow = getArguments().getDouble("DifferentialLowValue", 0d);
			mDifferentialRangeHigh = getArguments().getDouble("DifferentialHighValue", 0d);
			
			double gauge = getArguments().getDouble("Gauge", 0d);
			double width = getArguments().getDouble("SheetWidth", 0d);
			double length = getArguments().getDouble("SheetLength", 0d);
			double speed = getArguments().getDouble("LineSpeed", 0d);
			double diff = getArguments().getDouble("DifferentialSpeed", 0d);
			mSpeedControllerType = getArguments().getString("SpeedControllerType");
			mNumberOfWebs = getArguments().getInt("NumberOfWebs", 1);
			int numberOfSkids = getArguments().getInt("NumberOfSkids", 0);
			double factor = getArguments().getDouble("SpeedFactor", 0d);
			String prodtype = getArguments().getString("ProductType");
			if (prodtype == null) {
				prodtype = settings.getString("savedMode", "");
				if (prodtype.length() == 0) {
					prodtype = SHEETS_MODE;
				}
			}
						
			if (gauge > 0) mEdit_gauge.setText(String.valueOf(gauge));
			if (width > 0) mEdit_sheetWidth.setText(String.valueOf(width));
			if (length > 0) mEdit_sheetLength.setText(String.valueOf(length));
			if (speed > 0) mEdit_lineSpeed.setText(String.valueOf(speed));
			if (diff > 0) {
				mEdit_differentialSpeed.setText(String.valueOf(diff));
				} else {
					if (mSpeedControllerType.equals(ProductionLine.SPEED_CONTROLLER_TYPE_NONE)) {
						mEdit_differentialSpeed.setText("1"); //TODO just to not produce error on submit
					}
				}
			if ((mSpeedControllerType != null) && (mSpeedControllerType.equals(ProductionLine.SPEED_CONTROLLER_TYPE_NONE))) {
				mEdit_differentialSpeed.setVisibility(EditText.GONE);
				mLbl_differentialSpeed.setVisibility(EditText.GONE);
				mLbl_reminderNoDifferentialSpeed.setVisibility(EditText.VISIBLE);
			}
			
			if (factor > 0) mEdit_speedFactor.setText(String.valueOf(factor));
			setSheetsOrRollsState(prodtype);

			setNumberOfWebs(mNumberOfWebs);
			
			if (numberOfSkids == 1) mRadioButton_oneSkid.setChecked(true);
			if (numberOfSkids == 2) mRadioButton_twoSkids.setChecked(true);
			
			if (!MainActivity.DEBUG) {
				mLbl_speedFactor.setVisibility(TextView.GONE);
				mEdit_speedFactor.setVisibility(EditText.GONE);
			}
		} else setNumberOfWebs(1);
		
		mImgbtnSheetsOrRolls.setOnClickListener(this);
		
		// Add action buttons
		builder.setPositiveButton(R.string.calculate, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   //do nothing; override in onShowListener
		           }
		       });
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               // User cancelled the dialog
		        	   //hide keyboard
		        	   getActivity().getWindow().setSoftInputMode(
			        	         WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		           }
		       });
		
		// Create the AlertDialog and set an onClickListener for the positive button
		final AlertDialog alertDialog = builder.create();
		alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {

				Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				b.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						View parent = (View)v.getRootView();
						ViewGroup table = (ViewGroup)parent.findViewById(R.id.table_spm_dialog);
						boolean validInputs = true;
						if (table != null) {					
							for (int i = 0, n = table.getChildCount(); i < n; i++) {
								ViewGroup nextRow = (ViewGroup)table.getChildAt(i);
								if (nextRow instanceof TableRow) {
									for (int j = 0, m = nextRow.getChildCount(); j < m; j++) {
										View nextChild = nextRow.getChildAt(j);
										if (nextChild instanceof EditText) {
											EditText et = (EditText) nextChild;
											String text = et.getText().toString();
											if (text.length() == 0) {
												et.setError(getString(R.string.error_empty_field));
												validInputs = false;
											} else if (Double.valueOf(text) <= 0) {
												et.setError(getString(R.string.error_need_nonzero));
												validInputs = false;
											}
										}
									}
								}
							} 
						}
						EditText diffSpeed = (EditText) parent.findViewById(R.id.edit_differential_speed);
						if (diffSpeed.getText().length() != 0) {
							Double diffSpeedValue = Double.valueOf(diffSpeed.getText().toString());
							if (diffSpeedValue < mDifferentialRangeLow) {
								diffSpeed.setError(getString(R.string.error_differential_too_low) + String.valueOf(mDifferentialRangeLow));
								validInputs = false;
							} else if (diffSpeedValue > mDifferentialRangeHigh) {
								diffSpeed.setError(getString(R.string.error_differential_too_high) + String.valueOf(mDifferentialRangeHigh));
								validInputs = false;
							}
						}
						
						if (validInputs) {
							//Click processed, hide keyboard, save state, and dismiss dialog.
							getActivity().getWindow().setSoftInputMode(
									WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
							SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);
							SharedPreferences.Editor editor = settings.edit();
							editor.putString("savedMode", getSheetsOrRollsState()).commit();
							editor.commit();
							
							mListener.onClickPositiveButton(EnterProductDialogFragment.this);
							alertDialog.dismiss();						
						}
					}
				});
			}
		});
		alertDialog.show();
		alertDialog.getWindow().setLayout((int)getResources().getDimension(R.dimen.dialog_sheets_per_minute_width),
				(int)getResources().getDimension(R.dimen.dialog_sheets_per_minute_height)); //TODO make this expand to fit contents
		return alertDialog;
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgbtn_sheets_or_rolls:
			if (mSheetsOrRollsState.equals(SHEETS_MODE)) {
				setSheetsOrRollsState(ROLLS_MODE);
			} else if (mSheetsOrRollsState.equals(ROLLS_MODE)) {
				setSheetsOrRollsState(SHEETS_MODE);
			} else throw new RuntimeException ("unknown sheet or rolls state"); //debug
			break;
		case R.id.btn_subtract_web:
			setNumberOfWebs(mNumberOfWebs - 1);
			break;
		case R.id.btn_add_web:
			setNumberOfWebs(mNumberOfWebs + 1);
		}
		
	}
 
	private void setSheetsOrRollsState (String state) {
		if (state.equals(ROLLS_MODE) || state.equals(Product.ROLLSET_TYPE)) {
			this.mSheetsOrRollsState = ROLLS_MODE;
			mImgbtnSheetsOrRolls.setBackgroundResource(R.drawable.roll_slider120);
			this.mEdit_sheetLength.setText("12");
			this.mLbl_sheetLength.setEnabled(false);
			this.mEdit_sheetLength.setEnabled(false);
			this.mEdit_sheetWidth.setNextFocusDownId(R.id.edit_line_speed);
			mContainerProductDetails.removeView(mContainerSkidsOnTable);
		} else if (state.equals(SHEETS_MODE) || state.equals(Product.SHEETSET_TYPE)) {
			this.mSheetsOrRollsState = SHEETS_MODE;
			mImgbtnSheetsOrRolls.setBackgroundResource(R.drawable.sheet_slider120);
			this.mLbl_sheetLength.setEnabled(true);
			this.mEdit_sheetLength.setEnabled(true);
			this.mEdit_sheetWidth.setNextFocusDownId(R.id.edit_sheet_length);
			setNumberOfWebs(getNumberOfWebs()); //to trigger the check for added views TODO ugly
		}
	}
	
	private void markRequiredFields() {
		List<EditText> required = new ArrayList<EditText>();
		required.add(mEdit_differentialSpeed);
		required.add(mEdit_lineSpeed);
		required.add(mEdit_sheetLength);
		required.add(mEdit_sheetWidth);
		for (EditText et : required) {
			et.setHint(Html.fromHtml("<i>Required</i>"));
		}
	}
	
	private void setNumberOfWebs(int numWebs) {
		if ((numWebs < 1) || (numWebs > MAXIMUM_NUMBER_OF_WEBS)) throw new RuntimeException("Invalid number of webs: " + String.valueOf(numWebs));
		
		mNumberOfWebs = numWebs;
		
		if (numWebs == MAXIMUM_NUMBER_OF_WEBS) {
			mBtn_addWeb.setEnabled(false);
		} else mBtn_addWeb.setEnabled(true);
		
		if (numWebs == 1) {
			mBtn_subtractWeb.setEnabled(false);
		} else mBtn_subtractWeb.setEnabled(true);
		
		mLbl_numberOfWebs.setText(String.valueOf(numWebs) + "-up ");
		
		int resId = getResources().getIdentifier("ic_" + String.valueOf(numWebs) + "sheet", "drawable", getActivity().getPackageName());
		mImg_numberOfWebs.setImageDrawable(getResources().getDrawable(resId));
		
		if ((numWebs == 2) && (getSheetsOrRollsState() == SHEETS_MODE)) {
			if ( (mContainerSkidsOnTable == null) || (mContainerSkidsOnTable).getParent() == null) {
				mContainerProductDetails.addView(mContainerSkidsOnTable);
			}
		} else {
			mRadioButton_oneSkid.setChecked(true);
			mContainerProductDetails.removeView(mContainerSkidsOnTable);
		}
	}
	
	public int getNumberOfWebs() {
		return mNumberOfWebs;
	}

	public double getLineSpeedValue() {
		String s = mEdit_lineSpeed.getText().toString().trim();
		if (s.length() > 0) {	
			return Double.valueOf(s);
		} else return 0f;
	}
	public double getGauge() {
		String gauge = mEdit_gauge.getText().toString().trim();
		if (gauge.equals("")) {
			return 0;
		} else return Double.valueOf(gauge);
	}
	public double getSheetWidthValue() {
		String width = mEdit_sheetWidth.getText().toString().trim();
		if (width.equals("")) {
			return 0;
		} else return Double.valueOf(width);
	}
	public double getSheetLengthValue() {
		String length = mEdit_sheetLength.getText().toString().trim();
		if (length.equals("")) {
			return 0;
		} else return Double.valueOf(length);}
	
	public double getDifferentialSpeedValue() {
		String diff = mEdit_differentialSpeed.getText().toString().trim();
		if (diff.equals("")){
			return 0;
		} else return Double.valueOf(diff);
	}
	public double getSpeedFactorValue() {
		String factor = mEdit_speedFactor.getText().toString().trim();
		if (factor.equals("")) {
			return 0;
		} else return Double.valueOf(factor);
	}
	public String getSheetsOrRollsState() {
		return mSheetsOrRollsState;
	}
	public int getNumberOfSkids() {
		if (mRadioGroup_skidsOnTable.getCheckedRadioButtonId() == R.id.radio_two_skids) {
			return 2;
		} else return 1;
	}
}

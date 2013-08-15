package com.kovaciny.linemonitorbot;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.kovaciny.primexmodel.Product;

public class SheetsPerMinuteDialogFragment extends DialogFragment implements OnClickListener {

	public SheetsPerMinuteDialogFragment() {
		super();
	}

	 /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
  	public interface SheetsPerMinuteDialogListener {
		public void onClickPositiveButton(DialogFragment d);
	}
  	
  	public static final String SHEETS_MODE = Product.SHEETS_TYPE;
  	public static final String ROLLS_MODE = Product.ROLLS_TYPE;
  	
  	EditText mEdit_gauge;
  	EditText mEdit_sheetWidth;
  	EditText mEdit_sheetLength;
  	EditText mEdit_lineSpeed;
  	EditText mEdit_differentialSpeed;
  	EditText mEdit_speedFactor;
  	TextView mLbl_speedFactor;
  	TextView mLbl_differentialSpeed;
  	ImageButton mImgbtnSheetsOrRolls;
  	private String mSheetsOrRollsState = SHEETS_MODE;
  	
    // Use this instance of the interface to deliver action events
    SheetsPerMinuteDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (SheetsPerMinuteDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement SheetsPerMinuteDialogListener");
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
		View rootView = inflater.inflate(R.layout.dialog_sheets_per_minute, null);
		builder.setView(rootView);

		mLbl_speedFactor = (TextView) rootView.findViewById(R.id.lbl_speed_factor);
		mLbl_differentialSpeed = (TextView) rootView.findViewById(R.id.lbl_differential_speed);
		
		mEdit_gauge = (EditText) rootView.findViewById(R.id.edit_gauge);
		mEdit_sheetWidth = (EditText) rootView.findViewById(R.id.edit_sheet_width);
		mEdit_sheetLength = (EditText) rootView.findViewById(R.id.edit_sheet_length);
		mEdit_lineSpeed = (EditText) rootView.findViewById(R.id.edit_line_speed);
		mEdit_differentialSpeed = (EditText) rootView.findViewById(R.id.edit_differential_speed);
		mEdit_speedFactor = (EditText) rootView.findViewById(R.id.edit_speed_factor);
		mImgbtnSheetsOrRolls = (ImageButton) rootView.findViewById(R.id.imgbtn_sheets_or_rolls);
		
		if (getArguments() != null) {
			double gauge = getArguments().getDouble("Gauge", 0);
			double width = getArguments().getDouble("SheetWidth", 0);
			double length = getArguments().getDouble("SheetLength", 0);
			double speed = getArguments().getDouble("LineSpeed", 0);
			double diff = getArguments().getDouble("DifferentialSpeed", 0);
			double factor = getArguments().getDouble("SpeedFactor", 0);
			String prodtype = getArguments().getString("ProductType");
			boolean isLine12 = getArguments().getBoolean("IsLine12", false);
			
			if (gauge > 0) mEdit_gauge.setText(String.valueOf(gauge));
			if (width > 0) mEdit_sheetWidth.setText(String.valueOf(width));
			if (length > 0) mEdit_sheetLength.setText(String.valueOf(length));
			if (speed > 0) mEdit_lineSpeed.setText(String.valueOf(speed));
			if (diff > 0) mEdit_differentialSpeed.setText(String.valueOf(diff));
			if (factor > 0) mEdit_speedFactor.setText(String.valueOf(factor));
			if (prodtype != null) {
				mSheetsOrRollsState = prodtype;
				setSheetsOrRollsState(mSheetsOrRollsState);
			}
			if (isLine12) {
				mEdit_differentialSpeed.setEnabled(false);
				mLbl_differentialSpeed.setEnabled(false);
			}
			
			if (!MainActivity.DEBUG) {
				mLbl_speedFactor.setVisibility(TextView.GONE);
				mEdit_speedFactor.setVisibility(EditText.GONE);
			}
		
		}
		
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
						if (parent != null) {
							EditText edit_diffSpeed = (EditText) parent.findViewById(R.id.edit_differential_speed);
							String diffText = edit_diffSpeed.getText().toString();
							if (diffText.length() == 0) {
								edit_diffSpeed.setError(getString(R.string.error_empty_field));
							} else {
								double diffValue = Double.valueOf(diffText);
								if (diffValue > 80) {//TODO magic constant
									edit_diffSpeed.setText(String.valueOf(diffValue/100));
									Toast.makeText(getActivity(), getString(R.string.reminder_differential_format), Toast.LENGTH_LONG).show();		            			
								}
							}
						}

						//Click processed, hide keyboard and dismiss dialog.
						getActivity().getWindow().setSoftInputMode(
								WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
						mListener.onClickPositiveButton(SheetsPerMinuteDialogFragment.this);
						alertDialog.dismiss();						
					}
				});
			}
		});
		alertDialog.show();
		alertDialog.getWindow().setLayout(500,850); //TODO make this expand to fit contents
		return alertDialog;
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.imgbtn_sheets_or_rolls) {
			if (mSheetsOrRollsState.equals(SHEETS_MODE)) {
				setSheetsOrRollsState(ROLLS_MODE);
			} else if (mSheetsOrRollsState.equals(ROLLS_MODE)) {
				setSheetsOrRollsState(SHEETS_MODE);
			} else throw new RuntimeException ("unknown sheet or rolls state"); //debug
		}
	}
 
	private void setSheetsOrRollsState (String state) {
		if (state.equals(ROLLS_MODE)) {
			this.mSheetsOrRollsState = ROLLS_MODE;
			mImgbtnSheetsOrRolls.setBackgroundResource(R.drawable.roll_slider120);
			this.mEdit_sheetLength.setText("12");
			this.mEdit_sheetLength.setEnabled(false);
			this.mEdit_sheetWidth.setNextFocusDownId(R.id.edit_line_speed);
		} else if (state.equals(SHEETS_MODE)) {
			this.mSheetsOrRollsState = SHEETS_MODE;
			mImgbtnSheetsOrRolls.setBackgroundResource(R.drawable.sheet_slider120);
			this.mEdit_sheetLength.setEnabled(true);
			this.mEdit_sheetWidth.setNextFocusForwardId(R.id.edit_sheet_length);
		}
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
}

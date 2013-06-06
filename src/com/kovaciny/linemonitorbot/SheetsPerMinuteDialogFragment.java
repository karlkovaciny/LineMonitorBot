package com.kovaciny.linemonitorbot;

import com.kovaciny.primexmodel.Product;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

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
  	EditText mEdit_speedFactor;
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
		View rootView = inflater.inflate(R.layout.calculate_sheets_per_minute_dialog, null);
		builder.setView(rootView);

		mEdit_gauge = (EditText) rootView.findViewById(R.id.edit_gauge);
		mEdit_sheetWidth = (EditText) rootView.findViewById(R.id.edit_sheet_width);
		mEdit_sheetLength = (EditText) rootView.findViewById(R.id.edit_sheet_length);
		mEdit_lineSpeed = (EditText) rootView.findViewById(R.id.edit_line_speed);
		mEdit_speedFactor = (EditText) rootView.findViewById(R.id.edit_speed_factor);
		mImgbtnSheetsOrRolls = (ImageButton) rootView.findViewById(R.id.imgbtn_sheets_or_rolls);
	  	
		if (getArguments() != null) {
			mEdit_gauge.setText(String.valueOf(getArguments().getDouble("Gauge")));
			
			mEdit_sheetWidth.setText(String.valueOf(getArguments().getDouble("SheetWidth")));
			
			mEdit_sheetLength.setText(String.valueOf(getArguments().getDouble("SheetLength")));
		  	
			mEdit_lineSpeed.setText(String.valueOf(getArguments().getDouble("LineSpeed")));
			
		  	mEdit_speedFactor.setText(String.valueOf(getArguments().getDouble("SpeedFactor")));
		  	
		  	mSheetsOrRollsState = getArguments().getString("ProductType");
		  	setSheetsOrRollsState(mSheetsOrRollsState);
		  	Toast.makeText(getActivity(), "setting sheets or rolls state to " + mSheetsOrRollsState, Toast.LENGTH_SHORT).show();
		}
		
		mImgbtnSheetsOrRolls.setOnClickListener(this);
		
		// Add action buttons
		builder.setPositiveButton(R.string.calculate, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               mListener.onClickPositiveButton(SheetsPerMinuteDialogFragment.this);
		           }
		       });
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               // User cancelled the dialog
		           }
		       });
		
		// Create the AlertDialog
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
		alertDialog.getWindow().setLayout(450,700);
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
		} else if (state.equals(SHEETS_MODE)) {
			this.mSheetsOrRollsState = SHEETS_MODE;
			mImgbtnSheetsOrRolls.setBackgroundResource(R.drawable.sheet_slider120);
			this.mEdit_sheetLength.setEnabled(true);
		}
	}

	public double getLineSpeedValue() {
		String s = mEdit_lineSpeed.getText().toString();
		if (s.length() > 0) {	
			return Double.valueOf(s);
		} else return 0f;
	}
	public double getGauge() {
		return Double.valueOf(mEdit_gauge.getText().toString());
	}
	public double getSheetWidthValue() {
		return Double.valueOf(mEdit_sheetWidth.getText().toString());
	}
	public double getSheetLengthValue() {
		return Double.valueOf(mEdit_sheetLength.getText().toString());
	}
	public double getSpeedFactorValue() {
		return Double.valueOf(mEdit_speedFactor.getText().toString());
	}
	public String getSheetsOrRollsState() {
		return mSheetsOrRollsState;
	}
}

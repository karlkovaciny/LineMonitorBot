package com.kovaciny.linemonitorbot;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SheetsPerMinuteDialogFragment extends DialogFragment {

	public SheetsPerMinuteDialogFragment() {
		super();
		// TODO Auto-generated constructor stub
	}

	 /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
  	public interface SheetsPerMinuteDialogListener {
		public void onClickPositiveButton(DialogFragment d);
	}
  	
  	EditText mEdit_sheetLength;
  	EditText mEdit_lineSpeed;
  	EditText mEdit_speedFactor;
  	
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

		mEdit_sheetLength = (EditText) rootView.findViewById(R.id.edit_sheet_length);
		mEdit_sheetLength.setText(String.valueOf(getArguments().getDouble("SheetLength")));
	  	
		mEdit_lineSpeed = (EditText) rootView.findViewById(R.id.edit_line_speed);
		mEdit_lineSpeed.setText(String.valueOf(getArguments().getDouble("LineSpeed")));
		
	  	mEdit_speedFactor = (EditText) rootView.findViewById(R.id.edit_speed_factor);
	  	mEdit_speedFactor.setText(String.valueOf(getArguments().getDouble("SpeedFactor")));
	  	
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
		alertDialog.getWindow().setLayout(450,400);
		return alertDialog;
	}
	
	public double getLineSpeedValue() {
		String s = mEdit_lineSpeed.getText().toString();
		if (s.length() > 0) {	
			return Double.valueOf(s);
		} else return 0f;
	}
	public double getSheetLengthValue() {
		return Double.valueOf(mEdit_sheetLength.getText().toString());
	}
	public double getSpeedFactorValue() {
		return Double.valueOf(mEdit_speedFactor.getText().toString());
	}
}

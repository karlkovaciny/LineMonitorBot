package com.kovaciny.linemonitorbot;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;

public class GoByHeightDialogFragment extends DialogFragment {

	public GoByHeightDialogFragment() {
		super();
	}
	
	 /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
  	public interface GoByHeightDialogListener {
		public void onClickPositiveButton(DialogFragment d);
	}
  	
  	
  	EditText mEdit_finishedHeight;
  	EditText mEdit_averageGauge;
  	EditText mEdit_currentHeight;
  	EditText mEdit_sheetsPerSkid;
  	EditText mEdit_numberOfStacks;
  	
    // Use this instance of the interface to deliver action events
    GoByHeightDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (GoByHeightDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement GoByHeightDialogListener");
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
		View rootView = inflater.inflate(R.layout.go_by_height_dialog, null);
		builder.setView(rootView);
	
		mEdit_finishedHeight = (EditText) rootView.findViewById(R.id.edit_finished_height);
	  	mEdit_averageGauge = (EditText) rootView.findViewById(R.id.edit_average_gauge);
	  	mEdit_currentHeight = (EditText) rootView.findViewById(R.id.edit_current_height);
	  	mEdit_sheetsPerSkid = (EditText) rootView.findViewById(R.id.edit_sheets_per_skid);
	  	mEdit_numberOfStacks = (EditText) rootView.findViewById(R.id.edit_number_of_stacks);
	  	
		SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);
		
		if (getArguments() != null) {
			int sheetsPerSkid = getArguments().getInt("SheetsPerSkid", 0);
			double finishedHeight = getArguments().getDouble("FinishedHeight", 0);
			double averageGauge = getArguments().getDouble("AverageGauge", 0);
			int stacks = getArguments().getInt("StacksPerSkid", 1);
			mEdit_numberOfStacks.setText(String.valueOf(stacks));
			if (sheetsPerSkid != 0) {
				mEdit_sheetsPerSkid.setText(String.valueOf(sheetsPerSkid));
			}
			if (finishedHeight != 0) {
				mEdit_finishedHeight.setText(String.valueOf(finishedHeight));
			} else if (averageGauge != 0) {
				mEdit_averageGauge.setText(String.valueOf(averageGauge));
			}
			
		} 
		
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
						boolean validInputs = true;
						ViewGroup table = (ViewGroup)parent.findViewById(R.id.table_go_by_height_2);
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
						EditText etHeight = (EditText) parent.findViewById(R.id.edit_finished_height);
						EditText etGauge = (EditText) parent.findViewById(R.id.edit_average_gauge);
						String height = etHeight.getText().toString();
						String gauge = etGauge.getText().toString();
						if ((height.length() == 0) && (gauge.length() == 0)) {
							etHeight.setError(getString(R.string.error_need_at_least_one));
							etGauge.setError(getString(R.string.error_need_at_least_one));
							validInputs = false;
						}
						if ((height.length() > 0) && (gauge.length() > 0)) {
							etHeight.setError(getString(R.string.error_need_only_one));
							etGauge.setError(getString(R.string.error_need_only_one));
							validInputs = false;
						}
						if (validInputs) {
							//Click processed, hide keyboard, save state, and dismiss dialog.
							getActivity().getWindow().setSoftInputMode(
									WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
							SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);
							SharedPreferences.Editor editor = settings.edit();
							editor.commit();

							mListener.onClickPositiveButton(GoByHeightDialogFragment.this);
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

	public int getTotalSheets() {
		return Integer.valueOf(mEdit_sheetsPerSkid.getText().toString());
	}
	
	public double getAverageGauge() {
		if (mEdit_averageGauge.getText().length() > 0) {
			return Double.valueOf(mEdit_averageGauge.getText().toString());			
		} else return 0d;
	}
	
	public double getCurrentHeight() {
		return Double.valueOf(mEdit_currentHeight.getText().toString());
	}
	
	public double getFinishedHeight() {
		if (mEdit_finishedHeight.getText().length() > 0) {
			return Double.valueOf(mEdit_finishedHeight.getText().toString());
		} else return 0d;
	}
	
	public int getNumberOfStacks() {
		return Integer.valueOf(mEdit_numberOfStacks.getText().toString());
	}	
}

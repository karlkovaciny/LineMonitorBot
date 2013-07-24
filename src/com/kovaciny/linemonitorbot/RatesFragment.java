package com.kovaciny.linemonitorbot;

import java.beans.PropertyChangeEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kovaciny.primexmodel.Novatec;
import com.kovaciny.primexmodel.PrimexModel;
import com.kovaciny.primexmodel.Product;
import com.kovaciny.primexmodel.WorkOrder;

public class RatesFragment extends SectionFragment implements OnClickListener{
	EditText mEdit_grossWidth;
	TextView mTxt_edgeTrimPercent;
	EditText mEdit_sheetWeight;
	TextView mTxt_netPph;
	TextView mTxt_grossPph;
	EditText mEdit_novatecSetpoint;
	Button mBtn_calculateColorPercent;
	TextView mTxt_colorPercent;
	TextView mLbl_novatecSetpoint;
	private List<EditText> mEditableGroup = new ArrayList<EditText>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.rates_fragment, container, false);
		
		mEdit_grossWidth = (EditText)  rootView.findViewById(R.id.edit_gross_width);
		mEditableGroup.add(mEdit_grossWidth);
		mEdit_sheetWeight = (EditText) rootView.findViewById(R.id.edit_sheet_weight); 
		mEditableGroup.add(mEdit_sheetWeight);
		mEdit_novatecSetpoint = (EditText) rootView.findViewById(R.id.edit_novatec_setpoint);
		mEditableGroup.add(mEdit_novatecSetpoint);
		
		mTxt_edgeTrimPercent = (TextView) rootView.findViewById(R.id.txt_edge_trim_percent);
		mTxt_netPph = (TextView) rootView.findViewById(R.id.txt_net_pph);
		mTxt_grossPph = (TextView) rootView.findViewById(R.id.txt_gross_pph);
		mTxt_colorPercent = (TextView) rootView.findViewById(R.id.txt_color_percent);	
		
		mLbl_novatecSetpoint = (TextView) rootView.findViewById(R.id.lbl_novatec_setpoint);
		
		mBtn_calculateColorPercent = (Button) rootView.findViewById(R.id.btn_calculate_rates);
		mBtn_calculateColorPercent.setOnClickListener(this);
		return rootView;
	}
		
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_calculate_rates) {
			boolean validInputs = true;
			for (EditText et : mEditableGroup) {
				String value = et.getText().toString();
				if (value.equals("")) {
					et.setError(getString(R.string.error_empty_field));
					validInputs = false;
				} else et.setError(null);
			}
			if (validInputs) {
				Double grossWidth = Double.valueOf(mEdit_grossWidth.getText().toString());
				Double sheetWeight = Double.valueOf(mEdit_sheetWeight.getText().toString());
				Double novaSetpoint = Double.valueOf(mEdit_novatecSetpoint.getText().toString());
				((MainActivity)getActivity()).hideKeyboard();
				try {
					((MainActivity)getActivity()).updateRatesData(grossWidth, sheetWeight, novaSetpoint);
				} catch (IllegalStateException e) {
					String cause = e.getCause().getMessage();
					if (cause.equals(PrimexModel.ERROR_NET_LESS_THAN_GROSS)) {
						mEdit_grossWidth.setError("Gross width must be higher than sheet width");
					} else if (cause.equals(PrimexModel.ERROR_NO_PRODUCT_SELECTED)){
						Toast.makeText(getActivity(), R.string.prompt_need_product, Toast.LENGTH_LONG).show();
						((MainActivity)getActivity()).showSheetsPerMinuteDialog();
					} else {
						throw e;
					}
				}
			}
		}
	}

	public void modelPropertyChange (PropertyChangeEvent event) {
		String propertyName = event.getPropertyName();
		Object newProperty = event.getNewValue();
		if (propertyName == PrimexModel.PRODUCT_CHANGE_EVENT) {
			Product p = (Product)newProperty;
			Double sheetWeight = p.getUnitWeight();
			if (sheetWeight <= 0) {
				mEdit_sheetWeight.setText("");
			} else {
				String swdisp = new DecimalFormat("#0.00").format(sheetWeight);
				mEdit_sheetWeight.setText(swdisp);
			}
			
		} else if (propertyName == PrimexModel.SELECTED_WO_CHANGE_EVENT) {
			
		} else if (propertyName == PrimexModel.SELECTED_LINE_CHANGE_EVENT){
			
		} else if (propertyName == PrimexModel.EDGE_TRIM_RATIO_CHANGE_EVENT) {
			Double etPercent = (Double)newProperty * 100d;
			if (etPercent <= 0) {
				mTxt_edgeTrimPercent.setText("");
			} else {
				String etdisp = new DecimalFormat("#0.0").format(etPercent);
				etdisp += "%";
				mTxt_edgeTrimPercent.setText(etdisp);
			}
			
		} else if (propertyName == PrimexModel.NET_PPH_CHANGE_EVENT) {
			Double netPph = (Double)newProperty;
			if (netPph <= 0) {
				mTxt_netPph.setText("");
			} else {
				long netPphdisp = Math.round(netPph);
				mTxt_netPph.setText(String.valueOf(netPphdisp));
			}
			
		} else if (propertyName == PrimexModel.GROSS_PPH_CHANGE_EVENT) {
			Double grossPph = (Double)newProperty;
			if (grossPph <= 0) {
				mTxt_grossPph.setText("");
			} else {
				long grossPphdisp = Math.round(grossPph);
				mTxt_grossPph.setText(String.valueOf(grossPphdisp));
			}
			
		} else if (propertyName == PrimexModel.GROSS_WIDTH_CHANGE_EVENT) {
			Double grossWidth = (Double)newProperty;
			if (grossWidth <= 0) {
				mEdit_grossWidth.setText("");
			} else {
				String gwdisp = new DecimalFormat("#0.0").format(grossWidth);
				mEdit_grossWidth.setText(gwdisp);
			}
			
		} else if (propertyName == PrimexModel.COLOR_PERCENT_CHANGE_EVENT) {
			double concPercent = (Double)newProperty;
			if (concPercent <= 0) {
				mTxt_colorPercent.setText("");
			} else {
				String concdisp = new DecimalFormat("#0.0").format(concPercent*100); //TODO use round instead?
				concdisp += "%";
				mTxt_colorPercent.setText(concdisp);	
			}
		} 
		
		else if (propertyName == PrimexModel.NOVATEC_CHANGE_EVENT) {
			Novatec n = (Novatec)newProperty;
			String label = getString(R.string.label_novatec_setpoint);
			if (n.getLetdownRatio() != 1) {
				 label += " (" + String.valueOf(n.getLetdownRatio()) + "x)";
			}
			mLbl_novatecSetpoint.setText(label); 
			mEdit_novatecSetpoint.setText(String.valueOf(n.getControllerSetpoint()));
			
		}
	}	
}

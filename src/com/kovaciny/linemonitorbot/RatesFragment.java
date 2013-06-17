package com.kovaciny.linemonitorbot;

import java.text.DecimalFormat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kovaciny.primexmodel.Novatec;

public class RatesFragment extends SectionFragment implements OnClickListener{
	EditText mEdit_grossWidth;
	TextView mTxt_edgeTrimPercent;
	EditText mEdit_sheetWeight;
	TextView mTxt_netPph;
	TextView mTxt_grossPph;
	EditText mEdit_novatecSetpoint;
	Button mBtn_calculateColorPercent;
	TextView mTxt_colorPercent;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.rates_fragment, container, false);
		
		mEdit_grossWidth = (EditText)  rootView.findViewById(R.id.edit_gross_width);
		mTxt_edgeTrimPercent = (TextView) rootView.findViewById(R.id.txt_edge_trim_percent);
		mEdit_sheetWeight = (EditText) rootView.findViewById(R.id.edit_sheet_weight); 
		mTxt_netPph = (TextView) rootView.findViewById(R.id.txt_net_pph);
		mTxt_grossPph = (TextView) rootView.findViewById(R.id.txt_gross_pph);
		mEdit_novatecSetpoint = (EditText) rootView.findViewById(R.id.edit_novatec_setpoint);
		mBtn_calculateColorPercent = (Button) rootView.findViewById(R.id.btn_calculate_rates);
		mTxt_colorPercent = (TextView) rootView.findViewById(R.id.txt_color_percent);	
		
		mBtn_calculateColorPercent.setOnClickListener(this);
			
			
		return rootView;
	}
		
	@Override
	public void onClick(View v) {
		double netWidth = ((MainActivity)getActivity()).getModelDebug().getSelectedWorkOrder().getProduct().getWidth();
		double grossWidth = Double.valueOf(mEdit_grossWidth.getText().toString());
		double etPercent = (grossWidth - netWidth) / grossWidth;
		String etdisp = new DecimalFormat("#.0").format(etPercent*100); //use round instead
		etdisp += "%";
		mTxt_edgeTrimPercent.setText(etdisp);
		
		double productsPerMinute = ((MainActivity)getActivity()).getModelDebug().getProductsPerMinute();
		double netPph = Double.valueOf(mEdit_sheetWeight.getText().toString()) * productsPerMinute * 60;
		long netPphdisp = Math.round(netPph);
		mTxt_netPph.setText(String.valueOf(netPphdisp));
		Double grossPph = netPph/(1-etPercent);
		long grossPphdisp = Math.round(grossPph);
		mTxt_grossPph.setText(String.valueOf(grossPphdisp));
		
		double setpt = Double.valueOf(mEdit_novatecSetpoint.getText().toString());
		Novatec mNovatec = new Novatec(50,setpt,1); 
		double concpct = mNovatec.getRate()/grossPph;
		String concdisp = new DecimalFormat("#.0").format(concpct*100); //use round instead
		concdisp += "%";
		mTxt_colorPercent.setText(concdisp);
	
	}

}

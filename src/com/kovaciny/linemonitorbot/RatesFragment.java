package com.kovaciny.linemonitorbot;

import java.text.DecimalFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RatesFragment extends SectionFragment {
	Button junkbuttonconc;
	TextView junktextview;
	TextView junkrate;
	EditText junkedit;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.rates_fragment,
				container, false);
		junkbuttonconc = (Button) rootView.findViewById(R.id.buttonCalculateColorPercent);
		junktextview = (TextView) rootView.findViewById(R.id.textView3);
		junkrate = (TextView) rootView.findViewById(R.id.textView2);
		junkedit = (EditText) rootView.findViewById(R.id.editText1);
		 junkedit.setText("1");
		junkbuttonconc.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				float setpt = Float.valueOf(junkedit.getText().toString());
				Novatec mNovatec = new Novatec(50,setpt,1);
				float grosspph = Float.valueOf(junkrate.getText().toString());
				float concpct = mNovatec.getRate()/grosspph;
				String concdisp = new DecimalFormat("#.00").format(concpct*100); //use round instead
				concdisp += "%";
				junktextview.setText(concdisp);
				
			}
		});
		return rootView;
	}
		
}
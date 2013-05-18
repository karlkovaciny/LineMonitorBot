package com.kovaciny.linemonitorbot;

import java.text.DecimalFormat;

import com.kovaciny.primexmodel.Novatec;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
		junkedit = (EditText) rootView.findViewById(R.id.skid_number);
		 junkedit.setText("1");
		 junkedit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus == true) {
					junkedit.setError("got focus");
				}
				else junkedit.setError("lost focus");
				
			}
		});
		
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
		
		//activate fragment
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        DrainingFragment fragment = new DrainingFragment();        
        fragmentTransaction.add(R.id.FragmentContainer, fragment);
        fragmentTransaction.commit();
		return rootView;
	}
		
}
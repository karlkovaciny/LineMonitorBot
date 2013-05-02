package com.kovaciny.linemonitorbot;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class DrainingFragment extends SectionFragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.draining_fragment,
				container, false);
		
		final ListView testList = (ListView) rootView.findViewById(R.id.listView1);
		final Button myButton = (Button)rootView.findViewById(R.id.button1);
		myButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			    myButton.setText("You click...");
			    // SLEEP 2 SECONDS HERE ...
			    Handler handler = new Handler(); 
			    handler.postDelayed(new Runnable() { 
			         public void run() { 
			              myButton.setText("and I respond."); 
			         } 
			    }, 2000); 
				
			}
		});
		
		String testStrings[] = {"one","two","three", "four","five","six","seven","eight","nine","ten"};
		
		
		ArrayAdapter<String> testAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, testStrings);
		testList.setAdapter(testAdapter);
		
	    // SLEEP 2 SECONDS HERE ...
	    Handler handler = new Handler(); 
	    handler.postDelayed(new Runnable() { 
	         public void run() { 
	        		Integer testArray[] = {1,2,3,4,5,6,7,8,9,10,11,12};
	        		ArrayAdapter<Integer> secondAdapter = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_list_item_1, testArray);
	        		testList.setAdapter(secondAdapter);
	         } 
	    }, 2000); 
	    
		return rootView;
	}
}

package com.kovaciny.linemonitorbot;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.kovaciny.database.DatabaseViewer;
import com.kovaciny.database.PrimexDatabaseSchema;

public class DrainingFragment extends SectionFragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.draining_fragment,
				container, false);
		
		final ListView testList = (ListView) rootView.findViewById(R.id.listView1);
		
		//String testStrings[] = {"one","two","three", "four","five","six"};
		/*Product p = ((MainActivity)getActivity()).getModel().getSelectedLine().getProduct();
		String testStrings[]= {"Selected Product", String.valueOf(p.getGauge()), String.valueOf(p.getLength()), String.valueOf(p.getWidth()), String.valueOf(p.getUnit()), String.valueOf(p.getLineNumber())};
		*/
		
		DatabaseViewer dbviewer = new DatabaseViewer(getActivity());
		ArrayList<ArrayList<String>> abcd = dbviewer.selectRecordsFromDBList(PrimexDatabaseSchema.Products.TABLE_NAME, new String[]{"*"}, null, null, null, null, null);
		ArrayList<String> results = new ArrayList<String>();
		ArrayList<ArrayList<String>> efgh = dbviewer.selectRecordsFromDBList(PrimexDatabaseSchema.ProductTypes.TABLE_NAME, new String[]{"*"}, null, null, null, null, null);
		ArrayList<String> efgresults = new ArrayList<String>();
		
		//String testStrings[] = {firstRow.get(0)};
		for (ArrayList<String> efgrow : efgh) {
			results.add("Product Type");
			results.addAll(efgrow);
		}
		for (ArrayList<String> abcdrow : abcd) {
			results.add("Product");
			results.addAll(abcdrow);
		}
		
		ArrayAdapter<String> testAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, results);
		testList.setAdapter(testAdapter);

		/*
	    // SLEEP 2 SECONDS HERE ...
	    Handler handler = new Handler(); 
	    handler.postDelayed(new Runnable() { 
	         public void run() { 
	        		Integer testArray[] = {1,2,3,4,5,6,7,8,9,10,11,12};
	        		
	        		Context cont = getActivity();
	        		if (cont != null) {
	        			ArrayAdapter<Integer> secondAdapter = new ArrayAdapter<Integer>(cont, android.R.layout.simple_list_item_1, testArray);
		        		testList.setAdapter(secondAdapter);
	        		}
	         } 
	    }, 2000); 
	    */
		
		return rootView;
	}
}

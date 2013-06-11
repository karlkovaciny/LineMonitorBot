package com.kovaciny.linemonitorbot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.kovaciny.database.DatabaseViewer;
import com.kovaciny.database.PrimexDatabaseSchema;

public class DrainingFragment extends SectionFragment {
	
	private DatabaseViewer mDbViewer;
	private ListView mTableViewer;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.draining_fragment,
				container, false);
		
		mTableViewer = (ListView) rootView.findViewById(R.id.listView1);
		
		//String testStrings[] = {"one","two","three", "four","five","six"};
		/*Product p = ((MainActivity)getActivity()).getModel().getSelectedLine().getProduct();
		String testStrings[]= {"Selected Product", String.valueOf(p.getGauge()), String.valueOf(p.getLength()), String.valueOf(p.getWidth()), String.valueOf(p.getUnit()), String.valueOf(p.getLineNumber())};
		*/
		
		mDbViewer = new DatabaseViewer(getActivity());
		
		String[] toShow = new String[]{	PrimexDatabaseSchema.WorkOrders.TABLE_NAME
		};
		displayTables(toShow);
		/*ArrayList<ArrayList<String>> abcd = mDbViewer.selectRecordsFromDBList(PrimexDatabaseSchema.Products.TABLE_NAME, new String[]{"*"}, null, null, null, null, null);
		ArrayList<String> results = new ArrayList<String>();
		ArrayList<ArrayList<String>> efgh = mDbViewer.selectRecordsFromDBList(PrimexDatabaseSchema.ProductTypes.TABLE_NAME, new String[]{"*"}, null, null, null, null, null);
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
		mTableViewer.setAdapter(testAdapter);*/

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
	
	protected void displayTables(String[] tableNames) {
		ArrayList<String> results = new ArrayList<String>();
		
		for (String tableName : tableNames) {
			List<String> columnNames = mDbViewer.getColumnNames(tableName);
			
			
			ArrayList<ArrayList<String>> records =	
					mDbViewer.selectRecordsFromDBList(tableName, new String[]{"*"}, null, null, null, null, null);
			
			
			for (ArrayList<String> record : records) {
				results.add(tableName.toUpperCase());
				Iterator<String> columnNamesItr = columnNames.iterator();
				Iterator<String> recordItr = record.iterator();
				while (columnNamesItr.hasNext()) {
					results.add(columnNamesItr.next());
					results.add(recordItr.next());
				}
				//results.addAll(record);
			}
		}
			
		ArrayAdapter<String> testAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, results);
		mTableViewer.setAdapter(testAdapter);
	}
}

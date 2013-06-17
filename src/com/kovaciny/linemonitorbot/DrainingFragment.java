package com.kovaciny.linemonitorbot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.kovaciny.database.DatabaseViewer;
import com.kovaciny.database.PrimexDatabaseSchema;

public class DrainingFragment extends SectionFragment implements OnItemSelectedListener {
	
	private DatabaseViewer mDbViewer;
	private ListView mTableViewer;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.draining_fragment,
				container, false);
		
		mTableViewer = (ListView) rootView.findViewById(R.id.listView1);
		
		mDbViewer = new DatabaseViewer(getActivity());

		//Set up a spinner to let you choose which table to view.
		List<String> tables = mDbViewer.getTableNames();
		tables.remove("android_metadata");
		ArrayAdapter<String> tablesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, tables);
		tablesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner tableNameSpinner = (Spinner) rootView.findViewById(R.id.table_name_spinner);
		tableNameSpinner.setAdapter(tablesAdapter);
		tableNameSpinner.setOnItemSelectedListener(this);
		
		return rootView;
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View arg1, int pos,
			long arg3) {
		String item = String.valueOf(parent.getItemAtPosition(pos));
		displayTable(item);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
	}

	protected void displayTable(String tableName) {
		List<String> results = new ArrayList<String>();
		
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
		}
					
		ArrayAdapter<String> testAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, results);
		mTableViewer.setAdapter(testAdapter);
	}
}

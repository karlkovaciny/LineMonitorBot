package com.kovaciny.linemonitorbot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.kovaciny.database.PrimexSQLiteOpenHelper;
import com.kovaciny.helperfunctions.HelperFunction;
import com.kovaciny.primexmodel.Product;
import com.kovaciny.primexmodel.Skid;
import com.kovaciny.primexmodel.WorkOrder;

public class SkidsListFragment extends Fragment {

	private PrimexSQLiteOpenHelper mDbHelper;
	private ListView mListView_skidsList;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
    
		View rootView = inflater.inflate(R.layout.skids_list_header_row, container, false);
		
		int woNumber = getArguments().getInt("woNumber");
		mDbHelper = new PrimexSQLiteOpenHelper(getActivity());
		List<Skid<Product>> skidsList = new ArrayList<Skid<Product>>();
		skidsList = mDbHelper.getSkidList(woNumber);
		
        // create the grid item mapping
        String[] from = new String[] {"skid_number", "item_count", "finish_time"};
        int[] to = new int[] { R.id.skids_list_column_skid_number, R.id.skids_list_column_item_count, R.id.skids_list_column_finish_time};
 
        // prepare the list of all records
        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
        for(int i = 0; i < skidsList.size(); i++){
            HashMap<String, String> map = new HashMap<String, String>();
            Skid<Product> skid = skidsList.get(i);
            map.put("skid_number", String.valueOf(skid.getSkidNumber()));
            map.put("item_count", String.valueOf(skid.getTotalItems()));
            
            if (skid.getFinishTime() != null) {
            	Date roundedTimeForDisplay = HelperFunction.toNearestWholeMinute(skid.getFinishTime());
            	SimpleDateFormat formatter3 = new SimpleDateFormat("h:mm a E", Locale.US);
            	
            	//"Pace time": Don't show the day of the week if it's before 6 am the next day. 
            	Calendar finishDate = new GregorianCalendar(Locale.US);
            	finishDate.setTime(roundedTimeForDisplay);
            	Calendar today = Calendar.getInstance(Locale.US);
            	today.add(Calendar.DAY_OF_MONTH, 1);
            	today.set(Calendar.HOUR_OF_DAY, 6);
            	today.set(Calendar.MINUTE, 0);
            	if (finishDate.before(today)) {
            		formatter3 = new SimpleDateFormat("h:mm a", Locale.US);
            	}
            	map.put("finish_time", formatter3.format(roundedTimeForDisplay));
            } else {
            	map.put("finish_time", "n/a");
            }
            fillMaps.add(map);
        }
 
        // identify the currently selected skid
        WorkOrder wo = mDbHelper.getWorkOrder(woNumber);
        int currentSkidNumber = wo.getSelectedSkid().getSkidNumber();
        // fill in the grid_item layout
        ColorCodedAdapter adapter = new ColorCodedAdapter(getActivity(), fillMaps, R.layout.skids_list_item, from, to, currentSkidNumber - 1);
        //TODO should I have done that in constructor?
        mListView_skidsList = (ListView) rootView.findViewById(R.id.listview_skids_list);
        mListView_skidsList.setAdapter(adapter);
        mListView_skidsList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView_skidsList.setSelection(currentSkidNumber - 1);
        mListView_skidsList.setItemChecked(currentSkidNumber - 1, true);

        return rootView;
	}

}

package com.kovaciny.linemonitorbot;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ColorCodedAdapter extends SimpleAdapter {

	private static int TAN;
	private static int YELLOW;
	private static int LIGHT_BLUE;
	private List<HashMap<String, String>> mData;
	private int mCheckedItemPosition;
	
	public ColorCodedAdapter(Context context,
			List<HashMap<String, String>> data, int resource, String[] from,
			int[] to, int checkedItemPosition) {
		super(context, data, resource, from, to);
		mData = data;
		mCheckedItemPosition = checkedItemPosition;
		TAN = context.getResources().getColor(R.color.Tan);
		YELLOW = context.getResources().getColor(R.color.yellow);
		LIGHT_BLUE = context.getResources().getColor(R.color.LightBlue);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		String finishTime = mData.get(position).get("finish_time");
		if (finishTime.equals("n/a") || (position > mCheckedItemPosition)) {
		    view.setAlpha(0.5f); //TODO use gray
		} else {
		    view.setAlpha(1.0f);
		}
		
//		if (position  == 47 ){
//			backgroundColorId = YELLOW;
//			view.setBackgroundColor(backgroundColorId);
//		} else if (position  > 47) {
//			view.setAlpha(.5f);
//		} else view.setAlpha(1f);

		return view;
	}

}

package com.kovaciny.linemonitorbot;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

public class ColorCodedAdapter extends SimpleAdapter {

	private static int TAN;
	private static int YELLOW;
	private static int LIGHT_BLUE;
	
	public ColorCodedAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
		
		TAN = context.getResources().getColor(R.color.Tan);
		YELLOW = context.getResources().getColor(R.color.yellow);
		LIGHT_BLUE = context.getResources().getColor(R.color.LightBlue);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		int backgroundColorId = 0;
//		if (position  == 47 ){
//			backgroundColorId = YELLOW;
//			view.setBackgroundColor(backgroundColorId);
//		} else if (position  > 47) {
//			view.setAlpha(.5f);
//		} else view.setAlpha(1f);

		return view;
	}

}

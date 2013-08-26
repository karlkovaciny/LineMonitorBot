package com.kovaciny.linemonitorbot;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MessageFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.message_fragment, container, false);
		TextView tv = (TextView) rootView.findViewById(R.id.txt_message);
	
		return rootView;
	}

}

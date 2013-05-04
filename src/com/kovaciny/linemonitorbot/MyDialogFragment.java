package com.kovaciny.linemonitorbot;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class MyDialogFragment extends DialogFragment {
    static String mMessage;
    
	static MyDialogFragment newInstance(String message) {
		mMessage = message;
        return new MyDialogFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dummy_dialog, container, false);
        TextView tv = (TextView) v.findViewById(R.id.dummy_dialog_text);
        tv.setText(mMessage);
        return v;
    }
}
package com.kovaciny.linemonitorbot;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class SelectLineFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	    View rootView = inflater.inflate(R.layout.select_line_fragment, container, false);
	    ImageView arrow = (ImageView) rootView.findViewById(R.id.img_tutorial_arrow);
	    TextView label = (TextView) rootView.findViewById(R.id.lbl_select_line_tutorial_prompt);
	    TextView hint = (TextView) rootView.findViewById(R.id.lbl_select_line_reminder);
	    
	    hint.setText((Html.fromHtml("<i>Select a line&#160;</i>")));
	    if (getArguments() != null) {
            boolean showTutorial = getArguments().getBoolean("ShowTutorial", false);
            if (showTutorial) {
                arrow.setVisibility(ImageView.VISIBLE);
                label.setVisibility(TextView.VISIBLE);
                hint.setVisibility(TextView.GONE);
            }
        }
		return rootView;
	}
}

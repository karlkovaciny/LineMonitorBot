package com.kovaciny.linemonitorbot;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A dummy fragment representing a section of the app, but that simply
 * displays dummy text.
 */
public class SectionFragment extends Fragment {
	int mSectionNum;
	
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	public static final String ARG_SECTION_NUMBER = "section_number";
	
	/**
     * Create a new instance of SectionFragment, providing "sectionnum"
     * as an argument.
     */
    static SectionFragment newInstance(int sectionnum) {
        SectionFragment f = new SectionFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionnum);
        f.setArguments(args);

        return f;
    }
	
	
	public SectionFragment() {
	}

	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if ( getArguments() != null ) {
			mSectionNum = getArguments().getInt(ARG_SECTION_NUMBER, 1000);
		}
		else mSectionNum = 1000;
	}
			
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_main_dummy,
				container, false);
		//TextView dummyTextView = (TextView) rootView
				//.findViewById(R.id.section_label);
		//dummyTextView.setText(Integer.toString(getArguments().getInt(
				//ARG_SECTION_NUMBER)));
		//dummyTextView.setText(Integer.toString(mSectionNum));
		//dummyTextView.append("more sissies");
		return rootView;
	}
}

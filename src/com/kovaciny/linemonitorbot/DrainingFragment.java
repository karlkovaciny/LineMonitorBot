package com.kovaciny.linemonitorbot;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class DrainingFragment extends Fragment {

    private Button mBtn_selfDestruct;
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.draining_fragment, container, false);
		mBtn_selfDestruct = (Button) rootView.findViewById(R.id.btn_self_destruct);
		mBtn_selfDestruct.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                throw new IllegalStateException("You pushed the self-destruct button! You fiend!");
            }
        });
		
		return rootView;
	}
}

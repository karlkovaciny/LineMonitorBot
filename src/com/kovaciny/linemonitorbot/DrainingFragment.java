package com.kovaciny.linemonitorbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.kovaciny.helperfunctions.MutuallyExclusiveViewSet;
import com.kovaciny.primexmodel.DrainingController;

public class DrainingFragment extends Fragment implements View.OnClickListener {

    private Button mBtn_selfDestruct;
    private Button mBtn_getDrainTimes;
    
    LinearLayout mContainerDrainingFragment;
    private List<EditText> mEditableGroup = new ArrayList<EditText>();
    private EditText mEdit_grossRate;

    private DrainingController mDrainingController;
    
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
		
		mBtn_getDrainTimes = (Button) rootView.findViewById(R.id.btn_get_drain_times);
		mBtn_getDrainTimes.setOnClickListener(this);
		
		mContainerDrainingFragment = (LinearLayout) rootView.findViewById(R.id.container_draining_fragment);
		mEdit_grossRate = (EditText) rootView.findViewById(R.id.edit_gross_rate);
		mEditableGroup.add(mEdit_grossRate);

		return rootView;
	}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_drain_times:
                for (EditText et : mEditableGroup) {
                    et.clearFocus();
                    et.setError(null);
                }
                if (this.validateInputs()) {
                    
                }
                break;
        }
    }
    
    private boolean validateInputs() {
        boolean validInputs = true;
        if (mEdit_grossRate.getText().length() == 0) {
            mEdit_grossRate.setError(getString(R.string.error_empty_field));
            validInputs = false;
        } else if (mEdit_grossRate.getText().toString().equals("0")) {
            mEdit_grossRate.setError(getString(R.string.error_need_nonzero));
            validInputs = false;
        }
        return validInputs;
    }
}

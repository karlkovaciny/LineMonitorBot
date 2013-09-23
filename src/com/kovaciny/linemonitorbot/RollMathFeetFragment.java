package com.kovaciny.linemonitorbot;

import java.text.DecimalFormat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kovaciny.helperfunctions.HelperFunction;
import com.kovaciny.primexmodel.PrimexModel;

public class RollMathFeetFragment extends Fragment implements View.OnClickListener {

    Button mBtn_getFeet;

    EditText mEdit_orderedGauge;
    EditText mEdit_targetDiameter;
    
    TextView mTxt_linearFeet;
    
    int mLinearFeet;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);
        mLinearFeet = settings.getInt("RollMath.linearFeet", 0);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.roll_math_feet_fragment, container, false);
        
        mBtn_getFeet = (Button) rootView.findViewById(R.id.btn_get_feet);
        mBtn_getFeet.setOnClickListener(this);
        
        mEdit_targetDiameter = (EditText) rootView.findViewById(R.id.edit_target_diameter);
        mEdit_orderedGauge = (EditText) rootView.findViewById(R.id.edit_ordered_gauge);
                
        mTxt_linearFeet = (TextView) rootView.findViewById(R.id.txt_linear_feet);
                
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_get_feet) {
            if (validateInputs()) {
                HelperFunction.hideKeyboard(getActivity());
                
                
                double targetDiameter = Double.valueOf(mEdit_targetDiameter.getText().toString());
                double orderedGauge = Double.valueOf(mEdit_orderedGauge.getText().toString());
                int coreType = ((RollMathActivity)getActivity()).getCoreType();
                double linearFeet = Math.max(0d, 
                        ((RollMathActivity)getActivity()).calculateLinearFeet(coreType, targetDiameter / 2d, orderedGauge));
                String linearFeetDisplay = new DecimalFormat("####0").format(linearFeet) + " feet";
                mTxt_linearFeet.setText(String.valueOf(linearFeetDisplay));
                
                SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putFloat("RollMath.orderedGauge", (float) orderedGauge);
                editor.putFloat("RollMath.diameter", (float) targetDiameter);
                editor.commit();
            }
        }
    }

    private boolean validateInputs() {        
        boolean validInputs = true;
        if (mEdit_targetDiameter.getText().length() == 0) {
            mEdit_targetDiameter.setError(getString(R.string.error_empty_field));
            validInputs = false;
        }
        if (mEdit_orderedGauge.getText().length() == 0) {
            mEdit_orderedGauge.setError(getString(R.string.error_empty_field));
            validInputs = false;
        }
        
        String averageGauge = mEdit_orderedGauge.getText().toString();
        //auto-convert if user enters gauge as a whole number instead of a decimal
        if (!averageGauge.equals("")) {
            double gaugeValue = Double.valueOf(averageGauge);
            if (gaugeValue > PrimexModel.MAXIMUM_POSSIBLE_GAUGE) {
                gaugeValue /= 1000;
                mEdit_orderedGauge.setText(String.valueOf(gaugeValue));
            }
        }

        return validInputs;
    }


    
    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

}

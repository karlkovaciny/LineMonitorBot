package com.kovaciny.linemonitorbot;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.kovaciny.helperfunctions.HelperFunction;
import com.kovaciny.primexmodel.PrimexModel;
import com.kovaciny.primexmodel.Roll;

public class RollMathFeetFragment extends Fragment implements View.OnClickListener {

    Button mBtn_getFeet;

    EditText mEdit_linearFeet;
    EditText mEdit_orderedGauge;
    
    TextView mTxt_rollDiameter;
    
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
        
        mEdit_orderedGauge = (EditText) rootView.findViewById(R.id.edit_ordered_gauge);
        mEdit_linearFeet = (EditText) rootView.findViewById(R.id.edit_linear_feet);
        if (mLinearFeet > 0) {
            mEdit_linearFeet.setText(String.valueOf(mLinearFeet));
        }

        
        mTxt_rollDiameter = (TextView) rootView.findViewById(R.id.txt_roll_diameter);
                
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_get_feet) {
            if (validateInputs()) {
                double linearInches = Double.valueOf(mEdit_linearFeet.getText().toString()) * HelperFunction.INCHES_PER_FOOT;
                double orderedGauge = Double.valueOf(mEdit_orderedGauge.getText().toString());
                HelperFunction.hideKeyboard(getActivity());
            }
        }
    }

    private boolean validateInputs() {        
        boolean validInputs = true;
        if (mEdit_orderedGauge.getText().length() == 0) {
            mEdit_orderedGauge.setError(getString(R.string.error_empty_field));
            validInputs = false;
        }
        if (mEdit_linearFeet.getText().length() == 0) {
            mEdit_linearFeet.setError(getString(R.string.error_empty_field));
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

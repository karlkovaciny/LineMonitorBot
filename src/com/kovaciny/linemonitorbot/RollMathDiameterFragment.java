package com.kovaciny.linemonitorbot;

import java.text.DecimalFormat;

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

public class RollMathDiameterFragment extends Fragment implements View.OnClickListener {

    Button mBtn_getDiameter;
    
    EditText mEdit_linearFeet;
    EditText mEdit_orderedGauge;
    EditText mEdit_grossWeight;
    EditText mEdit_materialDensity;
    EditText mEdit_width;
    
    TextView mTxt_rollDiameter;
    
    double mWidth;
    int mLinearFeet;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);
        mLinearFeet = settings.getInt("RollMath.linearFeet", 0);
        mWidth = Double.valueOf(settings.getFloat("RollMath.width", 0f));
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.roll_math_diameter_fragment, container, false);
        
        mBtn_getDiameter = (Button) rootView.findViewById(R.id.btn_get_diameter);
        mBtn_getDiameter.setOnClickListener(this);
        
        mEdit_orderedGauge = (EditText) rootView.findViewById(R.id.edit_ordered_gauge);
        mEdit_linearFeet = (EditText) rootView.findViewById(R.id.edit_linear_feet);
        if (mLinearFeet > 0) {
            mEdit_linearFeet.setText(String.valueOf(mLinearFeet));
        }
        mEdit_grossWeight = (EditText) rootView.findViewById(R.id.edit_gross_weight);
        mEdit_materialDensity = (EditText) rootView.findViewById(R.id.edit_material_density);
        mEdit_width = (EditText) rootView.findViewById(R.id.edit_width);
        if (mWidth > 0) {
        	mEdit_width.setText(String.valueOf(mWidth));
        }
        mTxt_rollDiameter = (TextView) rootView.findViewById(R.id.txt_roll_diameter);
                
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_get_diameter) {
            if (validateInputs()) {
                HelperFunction.hideKeyboard(getActivity());
                
                SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                
                double diameter;
                
                if (mEdit_orderedGauge.getText().length() > 0) { //they filled in column 1
                    int linearFeet = Integer.valueOf(mEdit_linearFeet.getText().toString());
                    double orderedGauge = Double.valueOf(mEdit_orderedGauge.getText().toString());
                    diameter = ((RollMathActivity)getActivity())
                            .calculateRollDiameter(((RollMathActivity)getActivity()).getCoreType(), linearFeet, orderedGauge);
                    editor.putFloat("RollMath.orderedGauge", (float) orderedGauge);
                    editor.putInt("RollMath.linearFeet", linearFeet);
                } else {
                    double rollWidth = Double.valueOf(mEdit_width.getText().toString());
                    double grossWeight = Double.valueOf(mEdit_grossWeight.getText().toString());
                    double materialDensity = Double.valueOf(mEdit_materialDensity.getText().toString());
                    diameter = ((RollMathActivity)getActivity())
                            .calculateRollDiameter(((RollMathActivity)getActivity()).getCoreType(), rollWidth, grossWeight, materialDensity);
                    editor.putFloat("RollMath.width", (float) rollWidth);
                    editor.putFloat("RollMath.grossWeight", (float) grossWeight);
                    editor.putFloat("RollMath.materialDensity", (float) materialDensity);
                }
                
                SpannableStringBuilder diameterSb = new SpannableStringBuilder();
                diameterSb.append(HelperFunction.formatDecimalAsProperFraction(diameter, 8d))
                .append("\"");
                mTxt_rollDiameter.setText(diameterSb);

                editor.putFloat("RollMath.diameter", (float) diameter);
                editor.commit();
            }
        }
    }

    private boolean validateInputs() {        
        boolean validInputs = true;
        
        //establish that the user filled in at least one of the required columns
        if ((mEdit_orderedGauge.getText().length() == 0) && (mEdit_grossWeight.getText().length() == 0)) {
            mEdit_orderedGauge.setError(getString(R.string.error_need_at_least_one));
            mEdit_grossWeight.setError(getString(R.string.error_need_at_least_one));
            validInputs = false;
        } 
        if ((mEdit_orderedGauge.getText().length() > 0) && 
                (mEdit_grossWeight.length() > 0) &&
                (Double.valueOf(mEdit_orderedGauge.getText().toString()) > 0d)) {
            mEdit_orderedGauge.setError(getString(R.string.error_need_only_one));
            mEdit_grossWeight.setError(getString(R.string.error_need_only_one));
            validInputs = false;
        }
        
        //Process only the column they wanted
        if (validInputs) {
           if (mEdit_orderedGauge.getText().length() > 0) {
               if (mEdit_linearFeet.getText().length() == 0) {
                   mEdit_linearFeet.setError(getString(R.string.error_empty_field));
                   validInputs = false;
               }   
               //auto-convert if user enters gauge as a whole number instead of a decimal, then format as gauge
               double gaugeValue = Double.valueOf(mEdit_orderedGauge.getText().toString());
               if (gaugeValue > PrimexModel.MAXIMUM_POSSIBLE_GAUGE) {
                   gaugeValue /= 1000;
               }
               String threeDecimals = new DecimalFormat("#.000").format(gaugeValue);
               mEdit_orderedGauge.setText(threeDecimals);
           } else {
               if (mEdit_materialDensity.getText().length() == 0) {
                   mEdit_materialDensity.setError(getString(R.string.error_empty_field));
                   validInputs = false;
               }

               if (mEdit_width.getText().length() == 0) {
                   mEdit_width.setError(getString(R.string.error_empty_field));
                   validInputs = false;
               }
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

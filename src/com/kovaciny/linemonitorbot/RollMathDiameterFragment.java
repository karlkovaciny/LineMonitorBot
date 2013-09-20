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
    CheckBox mChk_heavyWall;

    EditText mEdit_linearFeet;
    EditText mEdit_orderedGauge;
    EditText mEdit_grossWeight;
    EditText mEdit_materialDensity;
    EditText mEdit_width;
    
    RadioGroup mRadioGroup_coreSize;
    RadioButton mRadio_r3;
    RadioButton mRadio_r6;
    RadioButton mRadio_r8;
    
    TextView mTxt_rollDiameter;
    TextView mTxt_coreWeight;
    
    int mCoreType;
    int mLinearFeet;
    double mWidth;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);
        mCoreType = settings.getInt("RollMath.coreType", Roll.CORE_TYPE_R8);
        mLinearFeet = settings.getInt("RollMath.linearFeet", 0);
        mWidth = Double.valueOf(settings.getFloat("RollMath.width", 0f));
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.roll_math_diameter_fragment, container, false);
        
        mBtn_getDiameter = (Button) rootView.findViewById(R.id.btn_get_diameter);
        mBtn_getDiameter.setOnClickListener(this);
        
        mChk_heavyWall = (CheckBox) rootView.findViewById(R.id.chk_heavy_wall);

        mRadioGroup_coreSize = (RadioGroup) rootView.findViewById(R.id.radio_group_core_size);
        mRadio_r3 = (RadioButton) rootView.findViewById(R.id.radio_r3);
        mRadio_r6 = (RadioButton) rootView.findViewById(R.id.radio_r6);
        mRadio_r8 = (RadioButton) rootView.findViewById(R.id.radio_r8);
        setCoreType(mCoreType);
        mRadioGroup_coreSize.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_r3:
                        mChk_heavyWall.setEnabled(true);
                        break;
                    case R.id.radio_r6:
                        mChk_heavyWall.setEnabled(true);
                        break;
                    case R.id.radio_r8:
                        mChk_heavyWall.setEnabled(false);
                        break;
                    default:
                        mRadioGroup_coreSize.clearCheck();
                }
                
            }
        });

        mEdit_orderedGauge = (EditText) rootView.findViewById(R.id.edit_ordered_gauge);
        mEdit_linearFeet = (EditText) rootView.findViewById(R.id.edit_linear_feet);
        if (mLinearFeet > 0) {
            mEdit_linearFeet.setText(String.valueOf(mLinearFeet));
        }
        mEdit_grossWeight = (EditText) rootView.findViewById(R.id.edit_gross_weight);
        mEdit_materialDensity = (EditText) rootView.findViewById(R.id.edit_material_density);
        mEdit_width = (EditText) rootView.findViewById(R.id.edit_width);
        mTxt_coreWeight = (TextView) rootView.findViewById(R.id.txt_core_weight);
        if (mWidth > 0) {
            mEdit_width.setText(String.valueOf(mWidth));
            double coreWeight = Roll.getCoreWeight(getCoreType(), mWidth);
            //round to nearest 0.5
            coreWeight = Math.round(coreWeight * 2d) / 2d;
            String coreWeightDisplay = new DecimalFormat("0.#").format(coreWeight) + " lbs";
            mTxt_coreWeight.setText(coreWeightDisplay);            
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
                            .calculateRollDiameter(getCoreType(), linearFeet, orderedGauge);
                    editor.putFloat("RollMath.orderedGauge", (float) orderedGauge);
                    editor.putInt("RollMath.linearFeet", linearFeet);
                } else {
                    double rollWidth = Double.valueOf(mEdit_width.getText().toString());
                    double grossWeight = Double.valueOf(mEdit_grossWeight.getText().toString());
                    double materialDensity = Double.valueOf(mEdit_materialDensity.getText().toString());
                    diameter = ((RollMathActivity)getActivity())
                            .calculateRollDiameter(getCoreType(), rollWidth, grossWeight, materialDensity);
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



    private void setCoreType(int coreType) {
        switch(coreType){
            case Roll.CORE_TYPE_R3:
            case Roll.CORE_TYPE_R3_HEAVY:
                mRadio_r3.setChecked(true);
                break;
            case Roll.CORE_TYPE_R6:
            case Roll.CORE_TYPE_R6_HEAVY:
                mRadio_r6.setChecked(true);
                break;
            case Roll.CORE_TYPE_R8:
                mRadio_r8.setChecked(true);
                break;
            default:
                mRadio_r3.setChecked(true);
        }
    }
    
    private int getCoreType() {
        boolean heavyWalled = mChk_heavyWall.isChecked();
        switch (mRadioGroup_coreSize.getCheckedRadioButtonId()) {
            case R.id.radio_r3:
                if (heavyWalled) return Roll.CORE_TYPE_R3_HEAVY;
                else return Roll.CORE_TYPE_R3;
            case R.id.radio_r6:
                mChk_heavyWall.setEnabled(true);
                if (heavyWalled) return Roll.CORE_TYPE_R6_HEAVY;
                else return Roll.CORE_TYPE_R6;
            case R.id.radio_r8:
                mChk_heavyWall.setEnabled(false);
                return Roll.CORE_TYPE_R8;
            default:
                mRadioGroup_coreSize.clearCheck();
                return 0;
        }
    }
    
    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

}

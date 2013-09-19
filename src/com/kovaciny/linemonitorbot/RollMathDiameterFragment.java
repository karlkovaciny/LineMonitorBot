package com.kovaciny.linemonitorbot;

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
    
    RadioGroup mRadioGroup_coreSize;
    RadioButton mRadio_r3;
    RadioButton mRadio_r6;
    RadioButton mRadio_r8;
    
    TextView mTxt_rollDiameter;
    
    int mCoreType;
    int mLinearFeet;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        mCoreType = getArguments().getInt("coreType", Roll.CORE_TYPE_R8);
        mLinearFeet = getArguments().getInt("linearFeet", 0);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.roll_math_diameter_fragment, container, false);
        
        mBtn_getDiameter = (Button) rootView.findViewById(R.id.btn_get_diameter);
        mBtn_getDiameter.setOnClickListener(this);
        
        mChk_heavyWall = (CheckBox) rootView.findViewById(R.id.chk_heavy_wall);

        mEdit_orderedGauge = (EditText) rootView.findViewById(R.id.edit_ordered_gauge);
        mEdit_linearFeet = (EditText) rootView.findViewById(R.id.edit_linear_feet);
        if (mLinearFeet > 0) {
            mEdit_linearFeet.setText(String.valueOf(mLinearFeet));
        }

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
        
        mTxt_rollDiameter = (TextView) rootView.findViewById(R.id.txt_roll_diameter);
                
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_get_diameter) {
            if (validateInputs()) {
                double linearInches = Double.valueOf(mEdit_linearFeet.getText().toString()) * HelperFunction.INCHES_PER_FOOT;
                double orderedGauge = Double.valueOf(mEdit_orderedGauge.getText().toString());
                double diameter = ((RollMathActivity)getActivity()).calculateRollDiameter(getCoreType(), linearInches, orderedGauge);
                SpannableStringBuilder diameterSb = new SpannableStringBuilder();
                diameterSb.append(HelperFunction.formatDecimalAsProperFraction(diameter, 8d))
                    .append("\"");
                mTxt_rollDiameter.setText(diameterSb);
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

package com.kovaciny.linemonitorbot;

import java.text.DecimalFormat;
import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kovaciny.helperfunctions.HelperFunction;
import com.kovaciny.helperfunctions.MutuallyExclusiveViewSet;
import com.kovaciny.primexmodel.PrimexModel;

public class RollMathFeetFragment extends Fragment implements View.OnClickListener {

    Button mBtn_getFeet;

    EditText mEdit_orderedGauge;
    EditText mEdit_targetDiameter;
    EditText mEdit_grossWeight;
    EditText mEdit_footWeight;
    
    LinearLayout mContainer_feetInputs1;
    LinearLayout mContainer_feetInputs2;

    MutuallyExclusiveViewSet<ViewGroup> mMutuallyExclusiveViewSet;
    
    TextView mTxt_linearFeet;
    
    int mLinearFeet;
    double mFootWeight;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);
        mLinearFeet = settings.getInt("RollMath.linearFeet", 0);
        mFootWeight = settings.getFloat("RollMath.footWeight", 0);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.roll_math_feet_fragment, container, false);
        
        mBtn_getFeet = (Button) rootView.findViewById(R.id.btn_get_feet);
        mBtn_getFeet.setOnClickListener(this);
        
        mEdit_targetDiameter = (EditText) rootView.findViewById(R.id.edit_target_diameter_2);
        mEdit_orderedGauge = (EditText) rootView.findViewById(R.id.edit_ordered_gauge_2);
        mEdit_grossWeight = (EditText) rootView.findViewById(R.id.edit_gross_weight_2);
        mEdit_footWeight = (EditText) rootView.findViewById(R.id.edit_foot_weight_2);
        
        mContainer_feetInputs1 = (LinearLayout) rootView.findViewById(R.id.container_feet_inputs_1);
        mContainer_feetInputs2 = (LinearLayout) rootView.findViewById(R.id.container_feet_inputs_2);
        
        mTxt_linearFeet = (TextView) rootView.findViewById(R.id.txt_linear_feet);
        
        HashMap<ViewGroup, EditText> containerToRequiredFieldMap = new HashMap<ViewGroup, EditText>();
        containerToRequiredFieldMap.put(mContainer_feetInputs1, mEdit_targetDiameter);
        containerToRequiredFieldMap.put(mContainer_feetInputs2, mEdit_grossWeight);
        
        mMutuallyExclusiveViewSet = 
                new MutuallyExclusiveViewSet<ViewGroup>(
                        getActivity(), containerToRequiredFieldMap, R.drawable.selector_viewgroup_exclusive);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_get_feet) {
            if (validateInputs()) {
                HelperFunction.hideKeyboard(getActivity());
                
                ViewGroup selectedGroup = (ViewGroup) getView().findViewById(mMutuallyExclusiveViewSet.getValidGroupId());
                if (selectedGroup.findFocus() != null) {
                    selectedGroup.findFocus().clearFocus();
                }
                
                SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                double linearFeet = 0d;
                if (selectedGroup.getId() == R.id.container_feet_inputs_1) {
                    double targetDiameter = Double.valueOf(mEdit_targetDiameter.getText().toString());
                    double orderedGauge = Double.valueOf(mEdit_orderedGauge.getText().toString());
                    int coreType = ((RollMathActivity)getActivity()).getCoreType();
                    linearFeet = Math.max(0d, 
                            ((RollMathActivity)getActivity()).calculateLinearFeet(coreType, targetDiameter / 2d, orderedGauge));
                    editor.putFloat("RollMath.orderedGauge", (float) orderedGauge);
                    editor.putFloat("RollMath.diameter", (float) targetDiameter);
                } else if (selectedGroup.getId() == R.id.container_feet_inputs_2) {
                    int grossWeight = Integer.valueOf(mEdit_grossWeight.getText().toString());
                    double footWeight = Double.valueOf(mEdit_footWeight.getText().toString());
                    linearFeet = ((RollMathActivity)getActivity())
                            .calculateLinearFeet(grossWeight, footWeight);
                    
                    editor.putFloat("RollMath.footWeight", (float) footWeight);
                }
                String linearFeetDisplay = new DecimalFormat("####0").format(linearFeet) + " feet";
                mTxt_linearFeet.setText(String.valueOf(linearFeetDisplay));
                
                editor.commit();
            }
        }
    }

    private boolean validateInputs() {        
        boolean validInputs = true;
        int selectedGroup = mMutuallyExclusiveViewSet.getValidGroupId();
        if (selectedGroup == 0) {
            validInputs = false;
        } else {
            LinearLayout validGroup = (LinearLayout) getView().findViewById(mMutuallyExclusiveViewSet.getValidGroupId());
            if (validGroup == mContainer_feetInputs1) {
                if (mEdit_orderedGauge.getText().length() == 0) {
                    mEdit_orderedGauge.setError(getString(R.string.error_empty_field));
                    validInputs = false;
                } else {
                    String averageGauge = mEdit_orderedGauge.getText().toString();
                    //auto-convert if user enters gauge as a whole number instead of a decimal
                    if (!averageGauge.equals("")) {
                        double gaugeValue = Double.valueOf(averageGauge);
                        if (gaugeValue > PrimexModel.MAXIMUM_POSSIBLE_GAUGE) {
                            gaugeValue /= 1000;
                            mEdit_orderedGauge.setText(String.valueOf(gaugeValue));
                        }
                    }
                }
            } else if (validGroup == mContainer_feetInputs2) {
                if (mEdit_footWeight.getText().length() == 0) {
                    mEdit_footWeight.setError(getString(R.string.error_empty_field));
                    validInputs = false;
                }
            } else {
                validInputs = false;
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

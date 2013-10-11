package com.kovaciny.linemonitorbot;

import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kovaciny.helperfunctions.HelperFunction;
import com.kovaciny.helperfunctions.MutuallyExclusiveViewSet;

public class RollMathWeightFragment extends Fragment implements View.OnClickListener {

    Button mBtn_getWeight;
    
    EditText mEdit_targetDiameter;
    EditText mEdit_materialDensity;
    EditText mEdit_linearFeet;
    EditText mEdit_footWeight;
    EditText mEdit_width;
    
    LinearLayout mContainer_weightInputs1;
    LinearLayout mContainer_weightInputs2;
    
    MutuallyExclusiveViewSet<ViewGroup> mMutuallyExclusiveViewSet;
    TextView mTxt_rollWeight;
    
    int mLinearFeet;
    double mFootWeight;
    double mWidth;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);
        mLinearFeet = settings.getInt("RollMath.linearFeet", 0);
        mFootWeight = settings.getFloat("RollMath.footWeight", 0);
        mWidth = settings.getFloat("RollMath.width", 0);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.roll_math_weight_fragment, container, false);
        
        mBtn_getWeight = (Button) rootView.findViewById(R.id.btn_get_weight);
        mBtn_getWeight.setOnClickListener(this);
        
        mEdit_targetDiameter = (EditText) rootView.findViewById(R.id.edit_target_diameter);
        mEdit_targetDiameter.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //they are editing column 2, so they must not want the one that's prefilled
                      mEdit_linearFeet.setText(null);
                  }
            }
        });
        mEdit_materialDensity = (EditText) rootView.findViewById(R.id.edit_material_density);
        mEdit_width = (EditText) rootView.findViewById(R.id.edit_width);
        if (mWidth > 0) {
            mEdit_width.setText(String.valueOf(mWidth));
        }
        mEdit_linearFeet = (EditText) rootView.findViewById(R.id.edit_linear_feet);
        if (mLinearFeet > 0) {
            mEdit_linearFeet.setText(String.valueOf(mLinearFeet));
        }
        mEdit_footWeight = (EditText) rootView.findViewById(R.id.edit_foot_weight);
        if (mFootWeight > 0) {
            mEdit_footWeight.setText(String.valueOf(mFootWeight));
        }

        mContainer_weightInputs1 = (LinearLayout) rootView.findViewById(R.id.container_weight_inputs_1);
        mContainer_weightInputs2 = (LinearLayout) rootView.findViewById(R.id.container_weight_inputs_2);
         
        mTxt_rollWeight = (TextView) rootView.findViewById(R.id.txt_roll_weight);
                
        HashMap<ViewGroup, EditText> containerToRequiredFieldMap = new HashMap<ViewGroup, EditText>();
        containerToRequiredFieldMap.put(mContainer_weightInputs1, mEdit_footWeight);
        containerToRequiredFieldMap.put(mContainer_weightInputs2, mEdit_targetDiameter);
        
        mMutuallyExclusiveViewSet = 
                new MutuallyExclusiveViewSet<ViewGroup>(
                        getActivity(), containerToRequiredFieldMap, R.drawable.selector_viewgroup_exclusive);

        
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_get_weight) {
            if (validateInputs()) {
                HelperFunction.hideKeyboard(getActivity());
                
                ViewGroup selectedGroup = (ViewGroup) getView().findViewById(mMutuallyExclusiveViewSet.getValidGroupId());
                if (selectedGroup.findFocus() != null) {
                    selectedGroup.findFocus().clearFocus();
                }
                
                SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                
                double netWeight = 0d;
                if (selectedGroup.getId() == R.id.container_weight_inputs_2) {
                    double rollWidth = Double.valueOf(mEdit_width.getText().toString());
                    double targetDiameter = Double.valueOf(mEdit_targetDiameter.getText().toString());
                    double materialDensity = Double.valueOf(mEdit_materialDensity.getText().toString());
                    netWeight = ((RollMathActivity)getActivity())
                            .calculateRollNetWeight(
                                    ((RollMathActivity)getActivity()).getCoreType(), 
                                    rollWidth, targetDiameter, materialDensity);
                    editor.putFloat("RollMath.width", (float) rollWidth);
                    editor.putFloat("RollMath.grossWeight", (float) targetDiameter);
                    editor.putFloat("RollMath.materialDensity", (float) materialDensity);
                } else if (selectedGroup.getId() == R.id.container_weight_inputs_1) {
                    int linearFeet = Integer.valueOf(mEdit_linearFeet.getText().toString());
                    double footWeight = Double.valueOf(mEdit_footWeight.getText().toString());
                    netWeight = ((RollMathActivity)getActivity())
                            .calculateRollNetWeight(linearFeet, footWeight);
                    editor.putFloat("RollMath.footWeight", (float) footWeight);
                    editor.putInt("RollMath.linearFeet", linearFeet);
                }
                
                int roundedNet = (int) Math.max(netWeight, 0d);
                SpannableStringBuilder weightSb = new SpannableStringBuilder();
                weightSb.append(String.valueOf(roundedNet));
                SpannableString poundSign = new SpannableString("# net");
                poundSign.setSpan(new RelativeSizeSpan(HelperFunction.RELATIVE_SIZE_SMALLER), 0, poundSign.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                weightSb.append(poundSign);
                mTxt_rollWeight.setText(weightSb);
                
                editor.putFloat("RollMath.diameter", (float) netWeight);
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
            LinearLayout validGroup = (LinearLayout) getView().findViewById(selectedGroup);
            if (validGroup == mContainer_weightInputs1) {
                if (mEdit_linearFeet.getText().length() == 0) {
                    mEdit_linearFeet.setError(getString(R.string.error_empty_field));
                    validInputs = false;
                }   
            } else if (validGroup == mContainer_weightInputs2) {
                if (mEdit_materialDensity.getText().length() == 0) {
                    mEdit_materialDensity.setError(getString(R.string.error_empty_field));
                    validInputs = false;
                }
                
                if (mEdit_width.getText().length() == 0) {
                    mEdit_width.setError(getString(R.string.error_empty_field));
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

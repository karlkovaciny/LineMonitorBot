package com.kovaciny.linemonitorbot;

import java.text.DecimalFormat;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.kovaciny.primexmodel.Roll;

public class CoreTypeFragment extends Fragment {

	OnCoreTypeChangedListener mCallback;

	// Container Activity must implement this interface
	public interface OnCoreTypeChangedListener {
		public void onCoreTypeChanged(int checkedRadio, boolean heavyWallChecked);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		 try {
	        mCallback = (OnCoreTypeChangedListener) activity;
	        } catch (ClassCastException e) {
	            throw new ClassCastException(activity.toString()
	                    + " must implement OnCoreTypeChangedListener");
	        }

	}
	
	CheckBox mChk_heavyWall;

	RadioGroup mRadioGroup_coreSize;
	RadioButton mRadio_r3;
	RadioButton mRadio_r6;
	RadioButton mRadio_r8;

	TextView mTxt_coreWeight;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.core_type_fragment,
				container, false);

		mChk_heavyWall = (CheckBox) rootView.findViewById(R.id.chk_heavy_wall);
		mRadioGroup_coreSize = (RadioGroup) rootView
		        .findViewById(R.id.radio_group_core_size);
		mRadio_r3 = (RadioButton) rootView.findViewById(R.id.radio_r3);
		mRadio_r6 = (RadioButton) rootView.findViewById(R.id.radio_r6);
		mRadio_r8 = (RadioButton) rootView.findViewById(R.id.radio_r8);
		
		mTxt_coreWeight = (TextView) rootView.findViewById(R.id.txt_core_weight);
		
		initializeViews(((RollMathActivity)getActivity()).getCoreType(), ((RollMathActivity)getActivity()).getCoreWeight());
		
		mChk_heavyWall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mCallback.onCoreTypeChanged(mRadioGroup_coreSize.getCheckedRadioButtonId(), isChecked);
			}
		});
		
		mRadioGroup_coreSize
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

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
							mChk_heavyWall.setChecked(false);
							break;
						default:
							mRadioGroup_coreSize.clearCheck();
						}
						mCallback.onCoreTypeChanged(checkedId, mChk_heavyWall.isChecked());
					}
				});


		return rootView;
	}

	public void onCoreWeightChanged(double weight) {
		// round to nearest 0.5
		weight = Math.round(weight * 2d) / 2d;
		String coreWeightDisplay = new DecimalFormat("0.#").format(weight)
				+ " lbs";
		mTxt_coreWeight.setText(coreWeightDisplay);
	}

	public void initializeViews(int coreType, double weight) {
		switch (coreType) {
		case Roll.CORE_TYPE_R3:
		    mRadio_r3.setChecked(true);
		    mChk_heavyWall.setEnabled(true);
		    mChk_heavyWall.setChecked(false);
		    break;
		case Roll.CORE_TYPE_R3_HEAVY:
		    mRadio_r3.setChecked(true);
		    mChk_heavyWall.setEnabled(true);
		    mChk_heavyWall.setChecked(true);
		    break;
		case Roll.CORE_TYPE_R6:
		    mRadio_r6.setChecked(true);
		    mChk_heavyWall.setEnabled(true);
		    mChk_heavyWall.setChecked(false);
		    break;
		case Roll.CORE_TYPE_R6_HEAVY:
		    mRadio_r6.setChecked(true);
		    mChk_heavyWall.setEnabled(true);
		    mChk_heavyWall.setChecked(true);
			break;
		case Roll.CORE_TYPE_R8:
		    mRadio_r8.setChecked(true);
		    mChk_heavyWall.setEnabled(false);
		    mChk_heavyWall.setChecked(false);
			break;
		default:
			mRadio_r3.setChecked(true);
		}
		onCoreWeightChanged(weight);
	}
}

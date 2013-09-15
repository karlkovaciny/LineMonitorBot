package com.kovaciny.linemonitorbot;

import java.beans.PropertyChangeEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.kovaciny.helperfunctions.HelperFunction;
import com.kovaciny.primexmodel.Novatec;
import com.kovaciny.primexmodel.PrimexModel;
import com.kovaciny.primexmodel.Product;

public class RatesFragment extends Fragment implements OnClickListener, ViewEventResponder {
	EditText mEdit_grossWidth;
	TextView mTxt_edgeTrimPercent;
	TextView mLbl_sheetWeight;
	EditText mEdit_sheetWeight;
	TextView mTxt_netPph;
	TextView mTxt_grossPph;
	EditText mEdit_novatecSetpoint;
	EditText mEdit_tenSecondLetdownGrams;
	Button mBtn_calculateRates;
	Button mBtn_enterProduct;
	TextView mTxt_colorPercent;
	TextView mLbl_novatecSetpoint;
	private List<EditText> mEditableGroup = new ArrayList<EditText>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.rates_fragment, container, false);
		
		mEdit_grossWidth = (EditText)  rootView.findViewById(R.id.edit_gross_width);
		mEditableGroup.add(mEdit_grossWidth);
		mEdit_sheetWeight = (EditText) rootView.findViewById(R.id.edit_sheet_weight); 
		mEditableGroup.add(mEdit_sheetWeight);
		mEdit_novatecSetpoint = (EditText) rootView.findViewById(R.id.edit_novatec_setpoint);
		mEditableGroup.add(mEdit_novatecSetpoint);
		mEdit_tenSecondLetdownGrams = (EditText) rootView.findViewById(R.id.edit_ten_second_letdown_grams);
		mEditableGroup.add(mEdit_tenSecondLetdownGrams);
		
		mTxt_edgeTrimPercent = (TextView) rootView.findViewById(R.id.txt_edge_trim_percent);
		mLbl_sheetWeight = (TextView) rootView.findViewById(R.id.lbl_sheet_weight);
		mTxt_netPph = (TextView) rootView.findViewById(R.id.txt_net_pph);
		mTxt_grossPph = (TextView) rootView.findViewById(R.id.txt_gross_pph);
		mTxt_colorPercent = (TextView) rootView.findViewById(R.id.txt_color_percent);	

		//restore saved state
		SharedPreferences settings = this.getActivity().getPreferences(Context.MODE_PRIVATE);
		String etpct = settings.getString("edgeTrimPercent", "");
		String net = settings.getString("netPph", "");
		String gross = settings.getString("grossPph", "");
		String colorpct = settings.getString("colorPercent", "");
		mTxt_edgeTrimPercent.setText(etpct);
		mTxt_netPph.setText(net);
		mTxt_grossPph.setText(gross);
		mTxt_colorPercent.setText(colorpct);
		
		mLbl_novatecSetpoint = (TextView) rootView.findViewById(R.id.lbl_novatec_setpoint);
		
		mBtn_calculateRates = (Button) rootView.findViewById(R.id.btn_calculate_rates);
		mBtn_calculateRates.setOnClickListener(this);
		mBtn_calculateRates.getBackground().setColorFilter(new LightingColorFilter(0xFF99DDFF, 0xFF0000FF));
		
		mBtn_enterProduct = (Button) rootView.findViewById(R.id.btn_enter_product_rates_frag);
		mBtn_enterProduct.setOnClickListener(this);
		mBtn_enterProduct.getBackground().setColorFilter(new LightingColorFilter(0xFF99DDFF, 0xFF0000FF));
		
		return rootView;
	}
		
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case (R.id.btn_enter_product_rates_frag):
		    for (EditText et : mEditableGroup) {
                et.clearFocus();
            }
		    ((MainActivity)getActivity()).showEnterProductDialog();
		    break;
		
		case (R.id.btn_calculate_rates):
            for (EditText et : mEditableGroup) {
                et.setError(null);
            }
		    boolean validInputs = true;
		    
		    if (mEdit_grossWidth.getText().length() == 0) {
                mEdit_grossWidth.setError(getString(R.string.error_empty_field));
                validInputs = false;
            }
            if (mEdit_sheetWeight.getText().length() == 0) {
                mEdit_sheetWeight.setError(getString(R.string.error_empty_field));
                validInputs = false;
            }
    		if ((mEdit_novatecSetpoint.getText().length() == 0) && (mEdit_tenSecondLetdownGrams.getText().length() == 0)) {
    		    mEdit_novatecSetpoint.setError(getString(R.string.error_need_at_least_one));
    		    mEdit_tenSecondLetdownGrams.setError(getString(R.string.error_need_at_least_one));
    		    validInputs = false;
    		} 
    		if ((mEdit_novatecSetpoint.getText().length() > 0) && 
    		        (mEdit_tenSecondLetdownGrams.length() > 0) &&
    		        (Double.valueOf(mEdit_novatecSetpoint.getText().toString()) > 0d)) {
    		    mEdit_novatecSetpoint.setError(getString(R.string.error_need_only_one));
    		    mEdit_tenSecondLetdownGrams.setError(getString(R.string.error_need_only_one));
    		    validInputs = false;
    		}
			
			if (validInputs) {
				Double grossWidth = Double.valueOf(mEdit_grossWidth.getText().toString());
				Double sheetWeight = Double.valueOf(mEdit_sheetWeight.getText().toString());
				Double novaSetpoint = 0d; 
				if (mEdit_novatecSetpoint.getText().length() > 0) {
				    novaSetpoint = Double.valueOf(mEdit_novatecSetpoint.getText().toString());
				}
				Double letdownGrams = 0d;
				if (mEdit_tenSecondLetdownGrams.getText().length() > 0) {
				    letdownGrams = Double.valueOf(mEdit_tenSecondLetdownGrams.getText().toString());
				}
				((MainActivity)getActivity()).hideKeyboard();
				try {
					((MainActivity)getActivity()).updateRatesData(grossWidth, sheetWeight, novaSetpoint, letdownGrams);
				} catch (IllegalStateException e) {
					String cause = e.getCause().getMessage();
					if (cause.equals(PrimexModel.ERROR_NET_LESS_THAN_GROSS)) {
						mEdit_grossWidth.setError(getString(R.string.error_net_less_than_gross));
					} else if (cause.equals(PrimexModel.ERROR_NO_PRODUCT_SELECTED) || 
							cause.equals(PrimexModel.ERROR_NO_PPM_VALUE)){
						Toast.makeText(getActivity(), R.string.prompt_need_product, Toast.LENGTH_LONG).show();
						((MainActivity)getActivity()).showEnterProductDialog();
					} else {
						throw e;
					}
				}
				for (EditText et : mEditableGroup) {
				    et.clearFocus();
				}
			}
			break;
		
		}
	}

	public void modelPropertyChange (PropertyChangeEvent event) {
		String propertyName = event.getPropertyName();
		Object newProperty = event.getNewValue();
		if (propertyName == PrimexModel.PRODUCT_CHANGE_EVENT) {
		    if (newProperty == null) {
		        mLbl_sheetWeight.setText(getString(R.string.default_sheet_weight_label));
		        mEdit_sheetWeight.setText(getString(R.string.default_sheet_weight));
		        mBtn_enterProduct.setText(getString(R.string.btn_enter_product_text));
		        mBtn_calculateRates.setEnabled(false);

		        //Reset enter product button
		        mBtn_enterProduct.getBackground().setColorFilter(new LightingColorFilter(0xFF99DDFF, 0xFF0000FF));
                mBtn_enterProduct.setTextAppearance(getActivity(), R.style.Button);
                mBtn_enterProduct.setText(getString(R.string.btn_enter_product_text));
		    } else {
		        Product p = (Product)newProperty;
		        mLbl_sheetWeight.setText(HelperFunction.capitalizeFirstChar(p.getUnit()) + " weight");
		        Double sheetWeight = p.getUnitWeight() / p.getNumberOfWebs();
		        if (sheetWeight <= 0) {
		            mEdit_sheetWeight.setText(getString(R.string.default_sheet_weight));
		        } else {
		            String swdisp = new DecimalFormat("#0.###").format(sheetWeight);
		            mEdit_sheetWeight.setText(swdisp);
		        }
		        mBtn_calculateRates.setEnabled(true);
		      
		        //Set button text to display product dimensions
                if (p.getUnits().equals("sheets") || p.getUnits().equals("cuts")) {
                    SpannableStringBuilder productDimensions = new SpannableStringBuilder();
                    productDimensions
                         .append(HelperFunction.formatDecimalAsProperFraction( p.getWidth()/p.getNumberOfWebs() ))
                        .append(" x ")
                        .append(HelperFunction.formatDecimalAsProperFraction(p.getLength()));
                    mBtn_enterProduct.setText(productDimensions);
                    mBtn_enterProduct.getBackground().clearColorFilter();
                    mBtn_enterProduct.setTextAppearance(getActivity(), R.style.Button_Minor);
                } else { //TODO it should work for R3 and R6 too, reset the button for now
                    mBtn_enterProduct.getBackground().setColorFilter(new LightingColorFilter(0xFF99DDFF, 0xFF0000FF));
                    mBtn_enterProduct.setTextAppearance(getActivity(), R.style.Button);
                    mBtn_enterProduct.setText(getString(R.string.btn_enter_product_text));
                }
		    }
			
		} else if (propertyName == PrimexModel.SELECTED_WO_CHANGE_EVENT) {
		    Animation fadeOutFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out_fade_in);
            ViewSwitcher viewSwitcher = (ViewSwitcher) getView().findViewById(R.id.view_switcher_rates_fragment);
            viewSwitcher.setAnimation(fadeOutFadeIn);
            viewSwitcher.showNext();
		} else if (propertyName == PrimexModel.SELECTED_LINE_CHANGE_EVENT){
			
		} else if (propertyName == PrimexModel.EDGE_TRIM_RATIO_CHANGE_EVENT) {
			Double etPercent = (Double)newProperty * 100d;
			if (etPercent <= 0) {
				mTxt_edgeTrimPercent.setText("");
			} else {
				String etdisp = new DecimalFormat("#0.0").format(etPercent);
				etdisp += "%";
				mTxt_edgeTrimPercent.setText(etdisp);
			}
			
		} else if (propertyName == PrimexModel.NET_PPH_CHANGE_EVENT) {
			Double netPph = (Double)newProperty;
			if (netPph <= 0) {
				mTxt_netPph.setText("");
			} else {
				long netPphdisp = Math.round(netPph);
				mTxt_netPph.setText(String.valueOf(netPphdisp));
			}
			
		} else if (propertyName == PrimexModel.GROSS_PPH_CHANGE_EVENT) {
			Double grossPph = (Double)newProperty;
			if (grossPph <= 0) {
				mTxt_grossPph.setText("");
			} else {
				long grossPphdisp = Math.round(grossPph);
				mTxt_grossPph.setText(String.valueOf(grossPphdisp));
			}
			
		} else if (propertyName == PrimexModel.GROSS_WIDTH_CHANGE_EVENT) {
			Double grossWidth = (Double)newProperty;
			if (grossWidth <= 0) {
				mEdit_grossWidth.setText("");
			} else {
				String gwdisp = new DecimalFormat("#0.###").format(grossWidth);
				mEdit_grossWidth.setText(gwdisp);
			}
			
		} else if (propertyName == PrimexModel.COLOR_PERCENT_CHANGE_EVENT) {
			double concPercent = (Double)newProperty;
			if (concPercent <= 0d) {
				mTxt_colorPercent.setText("");
			} else {
				String concdisp = new DecimalFormat("#0.0").format(concPercent*100); //TODO use round instead?
				concdisp += "%";
				mTxt_colorPercent.setText(concdisp);	
			}
			
		} else if (propertyName == PrimexModel.TEN_SECOND_LETDOWN_CHANGE_EVENT) {
		    double letdownGrams = (Double) newProperty;
		    if (letdownGrams <= 0d) {
		        mEdit_tenSecondLetdownGrams.setText("");
		    } else {
		        String gramsDisp = new DecimalFormat("#0.####").format(letdownGrams);
		        mEdit_tenSecondLetdownGrams.setText(gramsDisp);
		    }
		    
		} else if (propertyName == PrimexModel.NOVATEC_CHANGE_EVENT) {
			Novatec n = (Novatec)newProperty;
			SpannableStringBuilder labelSb = new SpannableStringBuilder(getString(R.string.label_novatec_setpoint));
			if (n.getScrewSizeFactor() != 1) {
			    SpannableString screwSizeIndicator = 
			            new SpannableString("\n(" + String.valueOf(n.getScrewSizeFactor()) + "x screw)");
			    screwSizeIndicator.setSpan(new RelativeSizeSpan(HelperFunction.RELATIVE_SIZE_SMALLER),
			            0, 
			            screwSizeIndicator.length(), 
			            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			    labelSb.append(screwSizeIndicator);
			}
			mLbl_novatecSetpoint.setText(labelSb); 
			mEdit_novatecSetpoint.setText(String.valueOf(n.getSetpoint()));
			
		}
	}

	@Override
	public void onPause() {
		SharedPreferences settings = this.getActivity().getPreferences(Context.MODE_PRIVATE);
	    SharedPreferences.Editor editor = settings.edit();

	    editor.putString("edgeTrimPercent", mTxt_edgeTrimPercent.getText().toString());
		editor.putString("netPph", mTxt_netPph.getText().toString());
		editor.putString("grossPph", mTxt_grossPph.getText().toString());
		editor.putString("colorPercent", mTxt_colorPercent.getText().toString());

	    // Commit the edits!
	    editor.commit();
		super.onPause();
	}	
}

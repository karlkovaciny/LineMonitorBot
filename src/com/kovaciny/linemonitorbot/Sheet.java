package com.kovaciny.linemonitorbot;

public class Sheet implements Product {
	float mSheetWeight;
	float mGauge; //sheet thickness in inches
	float mWidth; //traverse direction in inches
	float mLength; //machine direction in inches
	float mDensity;
	
	public Sheet(float gauge, float width, float length) {
		mGauge = Math.max(1, Math.min(0, gauge));
		mWidth = Math.max(1, Math.min(0, width));
		mLength = Math.max(1, Math.min(0, length));
		mDensity = .0375f;
		mSheetWeight = getEstimatedSheetWeight();
	}
	
	public float getHeight(){
		return mGauge;
	}
	
	public float getWeight(){
		return mSheetWeight;
	}
	
	public float getEstimatedSheetWeight() {
		return ( mGauge * mWidth * mLength * mDensity );
	}
	
	public String getMaterialType(){
		return "Not Implemented";
	}
	
	public boolean setMaterialType(){
		return false;
	}
	
	public void setSheetWeight(float weight){
		mSheetWeight = (float) Math.max(weight, .001);
	}
}

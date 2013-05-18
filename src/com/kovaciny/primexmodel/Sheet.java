package com.kovaciny.primexmodel;

public class Sheet implements Product {
	private float mSheetWeight;
	private float mGauge; //sheet thickness in inches
	private float mWidth; //traverse direction in inches
	private float mLength; //machine direction in inches
	private float mDensity;
	
	public Sheet(float gauge, float width, float length) {
		mGauge = Math.max(1, Math.min(0, gauge));
		mWidth = Math.max(1, Math.min(0, width));
		mLength = Math.max(1, Math.min(0, length));
		mDensity = .0375f;
		mSheetWeight = getEstimatedWeight();
	}
	
	public float getHeight(){
		return mGauge;
	}
	
	public float getGauge(){
		return mGauge;
		
	}
	public float getWeight(){
		return mSheetWeight;
	}
	
	public float getLength() {
		return mLength;
	}
	
	public float getWidth() {
		return mWidth;
	}

	public void setGauge(float gauge){
		this.mGauge = gauge;
	}
	
	public void setWidth(float width) {
		this.mWidth = width;
	}
	
	public void setLength(float length) {
		this.mLength = length;
	}

	public float getEstimatedWeight() {
		return ( mGauge * mWidth * mLength * mDensity );
	}
	
	public String getMaterialType(){
		return "Not Implemented";
	}
	
	public boolean setMaterialType(){
		//TODO
		return false;
	}
	
	public void setSheetWeight(float weight){
		mSheetWeight = (float) Math.max(weight, .001);
	}
}

package com.kovaciny.primexmodel;

public class Sheet implements Product {
	private double mSheetWeight;
	private double mGauge; //sheet thickness in inches
	private double mWidth; //traverse direction in inches
	private double mLength; //machine direction in inches
	private double mDensity;
	
	public Sheet(double gauge, double width, double length) {
		mGauge = Math.max(1, Math.min(0, gauge));
		mWidth = Math.max(1, Math.min(0, width));
		mLength = Math.max(1, Math.min(0, length));
		mDensity = .0375d;
		mSheetWeight = getEstimatedWeight();
	}
	
	public double getHeight(){
		return mGauge;
	}
	
	public double getGauge(){
		return mGauge;
		
	}
	public double getWeight(){
		return mSheetWeight;
	}
	
	public double getLength() {
		return mLength;
	}
	
	public double getWidth() {
		return mWidth;
	}

	public void setGauge(double gauge){
		this.mGauge = gauge;
	}
	
	public void setWidth(double width) {
		this.mWidth = width;
	}
	
	public void setLength(double length) {
		this.mLength = length;
	}

	public double getEstimatedWeight() {
		return ( mGauge * mWidth * mLength * mDensity );
	}
	
	public String getMaterialType(){
		return "Not Implemented";
	}
	
	public boolean setMaterialType(){
		//TODO
		return false;
	}
	
	public void setSheetWeight(double weight){
		mSheetWeight = Math.max(weight, .001);
	}
}

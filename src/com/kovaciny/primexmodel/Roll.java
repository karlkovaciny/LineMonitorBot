package com.kovaciny.primexmodel;

public class Roll implements Product {
	private float mLinearFootWeight = 0;
	private int mLinearFeet = 0;
	private float mGauge; //sheet thickness in inches
	private float mWidth; //traverse direction in inches
	private float mDensity;
	
	public Roll(float gauge, float width, int linearFeet) {
		if ( (gauge < 0 || width < 0) ) {
			throw new IllegalArgumentException();
		}
		mGauge = gauge;
		mWidth = width;
		mLinearFeet = linearFeet;
		mDensity = .0375f;
		mLinearFootWeight = getEstimatedWeight();
	}
	
	public float getEstimatedWeight() {
		return ( mGauge * mWidth * mDensity * mLinearFeet);
	}
	public float getHeight() {
		//depends on eye-to-sky status
		//TODO
		return 0f;		
	}
	
	public float getWeight() {
		return mLinearFootWeight * mLinearFeet;
	}
	
	public float getLength() {
		return 12; //one linear foot
	}
	
	public float getWidth() {
		return mWidth;
	}
	public String getMaterialType() {
		return "Not implemented";
	}
	public boolean setMaterialType(){
		//TODO
		return false;
	}
}

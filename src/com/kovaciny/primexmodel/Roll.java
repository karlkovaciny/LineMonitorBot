package com.kovaciny.primexmodel;

public class Roll implements Product {
	private double mLinearFootWeight = 0;
	private int mLinearFeet = 0;
	private double mGauge; //sheet thickness in inches
	private double mWidth; //traverse direction in inches
	private double mDensity;
	
	public Roll(double gauge, double width, int linearFeet) {
		if ( (gauge < 0 || width < 0) ) {
			throw new IllegalArgumentException();
		}
		mGauge = gauge;
		mWidth = width;
		mLinearFeet = linearFeet;
		mDensity = .0375f;
		mLinearFootWeight = getEstimatedWeight();
	}
	
	public double getEstimatedWeight() {
		return ( mGauge * mWidth * mDensity * mLinearFeet);
	}
	public double getHeight() {
		//depends on eye-to-sky status
		//TODO
		return 0d;		
	}
	
	public double getWeight() {
		return mLinearFootWeight * mLinearFeet;
	}
	
	public double getLength() {
		return 12; //one linear foot
	}
	
	public double getWidth() {
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

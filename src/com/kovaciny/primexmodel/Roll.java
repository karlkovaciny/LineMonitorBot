package com.kovaciny.primexmodel;

public class Roll implements Product {
	private double mLinearFootWeight = 0;
	private int mLinearFeet = 0;
	private double mGauge; //sheet thickness in inches
	private double mWidth; //traverse direction in inches
	private double mLength = 12d; //one linear foot
	private double mDensity;
	private String mUnitSingular = "foot";
	private String mUnitPlural = "feet";
	public int mLineNumber;
	
	/*
	 * 
	 */
	public Roll(double gauge, double width, int linearFeet) {
		if ( (gauge < 0 || width < 0) ) {
			throw new IllegalArgumentException("invalid dimensions");
		}
		mGauge = gauge;
		mWidth = width;
		mLinearFeet = linearFeet;
		mDensity = .0375d;
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
	
	public double getGauge() {
		return mGauge;
	}
	public double getUnitWeight() {
		return mLinearFootWeight;
	}
	public void setUnitWeight(double linearFootWeight) {
		if (linearFootWeight < 0) {
			throw new IllegalArgumentException("negative weight");
		}
		mLinearFootWeight = linearFootWeight;
	}	
	public double getLength() {
		return 12; //one linear foot
	}
	public void setLength(double length) {
		if (length != 12) {
			throw new IllegalArgumentException("Sheet length for rolls must always be 12");
		}
	}
	public int getLinearFeet() {
		return mLinearFeet;
	}

	public void setLinearFeet(int linearFeet) {
		this.mLinearFeet = linearFeet;
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
	public String getUnit() {
		return this.mUnitSingular;
	}
	
	public String getUnits() {
		return this.mUnitPlural;
	}
	
	public String getType() {
		return Product.ROLLS_TYPE;
	}
}

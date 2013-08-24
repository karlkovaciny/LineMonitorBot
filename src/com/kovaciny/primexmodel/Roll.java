package com.kovaciny.primexmodel;

public class Roll implements Product {
	private double mLinearFootWeight = 0;
	private int mLinearFeet = 0;
	private double mGauge; //sheet thickness in inches
	private double mAverageGauge;
	private double mWidth; //traverse direction in inches
	private double mLength = 12d; //one linear foot
	private double mDensity;
	private static final String mUnitSingular = "foot";
	private static final String mUnitPlural = "feet";
	private static final String mGrouping = "roll";
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
	
	public void setGauge(double gauge){
		mGauge = gauge;
	}
	
	public double getAverageGauge() {
		return mAverageGauge;
	}
	public void setAverageGauge(double gauge) {
		mAverageGauge = gauge;
	}
	public boolean hasStacks() {
		return false;
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
	public void setWidth(double width) {
		if (width <= 0) throw new IllegalArgumentException("width must be positive");
		mWidth = width;
	}
	public String getMaterialType() {
		return "Not implemented";
	}
	public boolean setMaterialType(){
		//TODO
		return false;
	}
	public int getNumberOfWebs() {
		return 1;
	}
	public String getUnit() {
		return mUnitSingular;
	}
	
	public String getUnits() {
		return mUnitPlural;
	}
	
	public String getGrouping() {
		return mGrouping;
	}
	
	public String getType() {
		return Product.ROLLS_TYPE;
	}
	public double getDensity() {
		return mDensity;
	}

	public void setDensity(double density) {
		this.mDensity = density;
	}

	
	@Override
	public String toString() {
		return getType() + ": " + String.valueOf(mGauge) + " x " + String.valueOf(mWidth) + " x R8, " + 
				String.valueOf(getNumberOfWebs()) + " webs";
	}
}

package com.kovaciny.primexmodel;

public class Sheet implements Product {
	private double mSheetWeight;
	private double mGauge; //sheet thickness in inches
	private double mWidth; //traverse direction in inches
	private double mLength; //machine direction in inches
	private double mDensity;
	private static final String mUnitSingular = "sheet";
	private static final String mUnitPlural = "sheets";
	private static final String mGrouping = "skid";
	
	/*
	 * 
	 */
		public Sheet(double gauge, double width, double length) {
		if (!areValidDimensions(gauge, width, length)) {
			throw new IllegalArgumentException("invalid dimensions");
		}
		mGauge = gauge;
		mWidth = width;
		mLength = length;
		mDensity = .0375d;
	}
	
	private boolean areValidDimensions(double gauge, double width, double length) {
		if ( (gauge < 0 ) || (width < 0) || (length < 0) ) {
			return false;
		} else return true;
	}
	public double getHeight(){
		return mGauge;
	}
	
	public double getGauge(){
		return mGauge;
		
	}
	public double getUnitWeight(){
		return mSheetWeight;
	}
	public void setUnitWeight(double unitWeight) {
		if (unitWeight < 0) {
			throw new IllegalArgumentException("negative weight");
		}
		mSheetWeight = unitWeight;
	}
	
	public double getLength() {
		return mLength;
	}

	public void setLength(double length) {
		if (length < 0) {throw new IllegalArgumentException("Sheet length less than zero");}
		this.mLength = length;
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
		return Product.SHEETS_TYPE;
	}
	
	@Override
	public String toString() {
		return "Sheet: " + String.valueOf(mGauge) + " x " + String.valueOf(mWidth) + " x " + String.valueOf(mLength);
	}
}

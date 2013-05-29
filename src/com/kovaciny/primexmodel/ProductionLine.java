package com.kovaciny.primexmodel;

public class ProductionLine {
	private int mLineNumber;
	private int mLineLength; //in feet
	private int mDieWidth; //in inches
	private double mProductsPerMinute; //there is no setter, change line speed or length 
	private double mNetRate; //in lbs/hr
	private double mGrossRate; //in lbs/hr
	private double mSpeedFactor;
	private double mLineSpeedSetpoint;
	private String mSpeedControllerType;
	private String mTakeoffEquipmentType;
	private Product mProduct = null;
	
	public ProductionLine(int lineNumber, int lineLength, int dieWidth, String speedControllerType, String takeoffEquipmentType) {
		setLineNumber(lineNumber);
		setLineLength(lineLength);
		setDieWidth(dieWidth);
		setSpeedControllerType(speedControllerType);
		setTakeoffEquipmentType(takeoffEquipmentType);
	}
	


	public double getProductsPerMinute(){
		if (this.mSpeedControllerType == "Direct") {
			//TODO do something
		}
		mProductsPerMinute = 12 / mProduct.getLength() * this.mLineSpeedSetpoint * this.mSpeedFactor; 
		return mProductsPerMinute;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Line ");
		sb.append(
				getLineNumber()).append(", length ").append(getLineLength()).append(", die width ").append(getDieWidth())
				.append (", speed controller type: ").append(getSpeedControllerType()).append(", takeoff equipment: ")
				.append(getTakeoffEquipmentType()).append("\n").append(super.toString());
		return sb.toString();
	}
	

	
	/*
	 * Boilerplate from here on down
	 */
	public void setProduct(Product product) {
		this.mProduct = product;
	}
	public Product getProduct() {
		return mProduct;
	}
	public int getLineNumber() {
		return mLineNumber;
	}
	public void setLineNumber(int lineNumber) {
		this.mLineNumber = lineNumber;
	}
	public int getLineLength() {
		return mLineLength;
	}
	public void setSpeedFactor(double fudgeFactor){
		mSpeedFactor = fudgeFactor;
	}
	public double getSpeedFactor() {
		return this.mSpeedFactor;
	}
	public void setLineSpeedSetpoint(double setpoint) {
		this.mLineSpeedSetpoint = setpoint;
	}
	public double getLineSpeedSetpoint() {
		return this.mLineSpeedSetpoint;
	}
	public void setLineLength(int mLineLength) {
		this.mLineLength = mLineLength;
	}
	public int getDieWidth() {
		return mDieWidth;
	}
	public void setDieWidth(int mDieWidth) {
		this.mDieWidth = mDieWidth;
	}
	public String getSpeedControllerType() {
		return mSpeedControllerType;
	}
	public void setSpeedControllerType(String mSpeedControllerType) {
		this.mSpeedControllerType = mSpeedControllerType;
	}
	public String getTakeoffEquipmentType() {
		return mTakeoffEquipmentType;
	}
	public void setTakeoffEquipmentType(String takeOffEquipment) {
		this.mTakeoffEquipmentType = takeOffEquipment;
	}	
}

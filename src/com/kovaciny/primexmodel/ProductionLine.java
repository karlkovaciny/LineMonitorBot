package com.kovaciny.primexmodel;

public class ProductionLine {
	private int mLineNumber;
	private int mLineLength; //in feet
	private int mDieWidth; //in inches
	private double mNetRate; //in lbs/hr
	private double mGrossRate; //in lbs/hr
	private SpeedValues mSpeedValues;
	private String mSpeedControllerType;
	private String mTakeoffEquipmentType;
	private Product mProduct = null;
	
	public static final String SPEED_CONTROLLER_TYPE_DIRECT = "Direct";
	public static final String SPEED_CONTROLLER_TYPE_GEARED = "Geared";
	
	public ProductionLine(int lineNumber, int lineLength, int dieWidth, String speedControllerType, String takeoffEquipmentType) {
		setLineNumber(lineNumber);
		setLineLength(lineLength);
		setDieWidth(dieWidth);
		setSpeedControllerType(speedControllerType);
		setTakeoffEquipmentType(takeoffEquipmentType);
		//TODO default settings
		mSpeedValues = new SpeedValues(0,1,1);
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
	

	public double getLineSpeed() {
		return mSpeedValues.getProduct();		
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
	public SpeedValues getSpeedValues() {
		return mSpeedValues;
	}
	public void setSpeedValues(SpeedValues sv) {
		mSpeedValues = sv;
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

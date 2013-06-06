package com.kovaciny.primexmodel;

public class ProductionLine {
	private int mLineNumber;
	private int mLineLength; //in feet
	private int mDieWidth; //in inches
	private double mNetRate; //in lbs/hr
	private double mGrossRate; //in lbs/hr
	private double mSpeedFactor;
	private double mLineSpeedSetpoint;
	private double mPullRollSetpoint;
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
		//TODO default settingS
		mPullRollSetpoint = 1.0;
		mSpeedFactor = 1.0;
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
		if (mSpeedControllerType.equals(SPEED_CONTROLLER_TYPE_DIRECT)) {
			return mLineSpeedSetpoint * mSpeedFactor; 
		} else if (mSpeedControllerType.equals(SPEED_CONTROLLER_TYPE_GEARED)){
			return mLineSpeedSetpoint * mPullRollSetpoint * mSpeedFactor;
		} else {
			throw new RuntimeException ("speed controller type unknown");
		}
		
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

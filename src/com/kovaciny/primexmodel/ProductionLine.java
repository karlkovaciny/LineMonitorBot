package com.kovaciny.primexmodel;

import com.kovaciny.helperfunctions.HelperFunction;

public class ProductionLine {
	private int mLineNumber;
	private int mLineLength; //in feet
	private int mDieWidth; //in inches
	private double mWebWidth;
	private SpeedValues mSpeedValues;
	private String mSpeedControllerType;
	private String mTakeoffEquipmentType;
	private Novatec mNovatec;
	
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
		mNovatec = new Novatec(50,0,1); 
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
				.append(getTakeoffEquipmentType());
		sb.append(" (").append(super.toString()).append(")");
		sb.append("\n").append("Speed values: ").append(mSpeedValues.toString());
		return sb.toString();
	}
	
	public double getLineSpeed() {
		return mSpeedValues.getProduct();		
	}
	
	public long getSecondsToMaxson() {
		if (getLineSpeed() > 0) {
			return Math.round(getLineLength() / getLineSpeed() * HelperFunction.SECONDS_PER_MINUTE);
		} else throw new IllegalStateException(new Throwable(PrimexModel.ERROR_ZERO_LINE_SPEED));
	}
	
	/*
	 * Boilerplate from here on down
	 */
	
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
	public Novatec getNovatec() {
		return mNovatec;
	}
	public void setNovatec(Novatec novatec) {
		this.mNovatec = novatec;
	}
	public double getWebWidth() {
		return mWebWidth;
	}
	public void setWebWidth(double width) {
		this.mWebWidth = width;
	}
}

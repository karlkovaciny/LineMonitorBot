package com.kovaciny.primexmodel;

import android.util.Log;

import com.kovaciny.helperfunctions.HelperFunction;

public class ProductionLine {
	private int mLineNumber;
	private int mLineLength; //in feet
	private int mDieWidth; //in inches
	private double mWebWidth;
	private int mNumberOfWebs;
	private SpeedValues mSpeedValues;
	private double[] mDifferentialRange;
	private String mSpeedControllerType;
	private String mTakeoffEquipmentType;
	private Novatec mNovatec;
	
	public static final String SPEED_CONTROLLER_TYPE_GEARED = "ProductionLine.Geared";
	public static final String SPEED_CONTROLLER_TYPE_PERCENT = "ProductionLine.Percent";
	public static final String SPEED_CONTROLLER_TYPE_RATIO = "ProductionLine.Ratio";
	public static final String SPEED_CONTROLLER_TYPE_NONE = "ProductionLine.None";
	
	public ProductionLine(int lineNumber, int lineLength, int dieWidth, String speedControllerType, String takeoffEquipmentType) {
		setLineNumber(lineNumber);
		setLineLength(lineLength);
		setDieWidth(dieWidth);
		setSpeedControllerType(speedControllerType);
		setTakeoffEquipmentType(takeoffEquipmentType);
		//TODO default settings
		mSpeedValues = new SpeedValues(0,1,1);
		mNumberOfWebs = 1;
		mNovatec = new Novatec(); 
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
		if (mSpeedControllerType == SPEED_CONTROLLER_TYPE_NONE) {
			sv.differentialSpeed = 1;
		}
		mSpeedValues = sv;
		Log.v("ProductionLine", "just set speed values to " + sv.toString());
	}
	
	public double getLineSpeed() {
		return mSpeedValues.getProduct();		
	}
	public double getDifferentialRangeLow() {
		return mDifferentialRange[0];
	}
	public double getDifferentialRangeHigh() {
		return mDifferentialRange[1];
	}
	private void setDifferentialRange(double low, double high) {
		if (low > high) throw new IllegalArgumentException("low greater than high");
		if ((low < 0) || (high < 0)) throw new IllegalArgumentException("Negative differential range");
		mDifferentialRange = new double[] {low, high};
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
	public void setSpeedControllerType(String speedControllerType) {
		if (speedControllerType.equals(SPEED_CONTROLLER_TYPE_GEARED)) {
			setDifferentialRange(0d, 1.1d);
		} else if (speedControllerType.equals(SPEED_CONTROLLER_TYPE_PERCENT)) {
			setDifferentialRange(90d, 110d);
		} else if (speedControllerType.equals(SPEED_CONTROLLER_TYPE_RATIO)) {
			setDifferentialRange(.9d, 1.1d);
		} else if (speedControllerType.equals(SPEED_CONTROLLER_TYPE_NONE)) {
			setDifferentialRange(1d, 1d);
		} else throw new IllegalArgumentException("unknown speed controller type");
		this.mSpeedControllerType = speedControllerType;
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

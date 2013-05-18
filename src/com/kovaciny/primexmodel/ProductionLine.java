package com.kovaciny.primexmodel;

public class ProductionLine {
	private int mLineNumber;
	private int mLineLength; //in feet
	private int mDieWidth; //in inches
	private float mLineSpeed = 0; //in feet per minute
	private String mSpeedControllerType;
	private String mTakeoffEquipmentType;
	
	public ProductionLine(int lineNumber, int lineLength, int dieWidth, String speedControllerType, String takeoffEquipmentType) {
		setLineNumber(lineNumber);
		setLineLength(lineLength);
		setDieWidth(dieWidth);
		setSpeedControllerType(speedControllerType);
		setTakeoffEquipmentType(takeoffEquipmentType);
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
	public void setLineLength(int mLineLength) {
		this.mLineLength = mLineLength;
	}
	public int getDieWidth() {
		return mDieWidth;
	}
	public void setDieWidth(int mDieWidth) {
		this.mDieWidth = mDieWidth;
	}
	public float getLineSpeed(){
		return mLineSpeed;
	}
	public void setLineSpeed(float setpoint, float fudgeFactor) {
		if (this.getSpeedControllerType() == "Direct"){
			mLineSpeed = setpoint * fudgeFactor;		
		} else {
			mLineSpeed = setpoint * fudgeFactor;
		}
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
	
	public float getProductsPerMinute(Product product){
		return 12 / product.getLength() * this.getLineSpeed();  
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
	
	
	
}

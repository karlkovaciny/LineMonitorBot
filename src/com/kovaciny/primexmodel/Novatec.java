package com.kovaciny.primexmodel;

public class Novatec implements Hopper {
	double mCapacity; //in pounds of mReferenceMaterial
	/**
	 * percent of full capacity at which alarm sounds.  100 = no alarm.
	 * Not implemented yet.
	 */
	double mAlarmPercent; 
	Material mReferenceMaterial; 
	private double mControllerSetpoint;
	double mLbsContained;
	private double mLetdownRatio;
	private double mRate;
	Material mContents;
	
	public Novatec(double capacity, double setpoint, double letdownRatio) {
		if ((capacity < 0) || (setpoint < 0) || (letdownRatio < 0)) {
			throw new IllegalArgumentException("No negative numbers");
		}
		mCapacity = capacity;
		mControllerSetpoint = setpoint;
		mLetdownRatio = letdownRatio;
		mLbsContained = 0;
		mReferenceMaterial = new Material("105 Concentrate");
		mContents = mReferenceMaterial;
		mAlarmPercent = 100;
	}
	
	public Novatec() {
		this(0,0,1);
	}
	
	private void updateRate(){
		mRate = mControllerSetpoint * mLetdownRatio * mContents.getDensity()/mReferenceMaterial.getDensity();
	}
	public double getRate(){
		updateRate();
		return mRate;
	}
	
	public double setControllerSetpoint(double setpoint) {
		if (setpoint < 0) {
			throw new IllegalArgumentException("No negative numbers");
		}
		mControllerSetpoint = setpoint;
		updateRate();
		return mControllerSetpoint;
	}
	public double getControllerSetpoint() {
		return mControllerSetpoint;
	}
	
	public double estimateMinimumFullCapacity() {
		return mCapacity ; //need to change w/ new matl
	}
	
	public double estimateAlarmCapacity() {
		return mAlarmPercent * mCapacity;
	}
	
	public double getLbsContained() {
		return mLbsContained;
	}
	
	public double setLbsContained(double lbs) {
		if (lbs < 0) return 0;
		else return mLbsContained = lbs;
	}

	public double getLetdownRatio() {
		return mLetdownRatio;
	}

	public void setLetdownRatio(double letdownRatio) {
		this.mLetdownRatio = letdownRatio;
		updateRate();
	}

	public Material getContents() {
		return mContents;
	}

	public void setContents(Material mContents) {
		this.mContents = mContents;
		updateRate();
		updateCapacity();
	}
	
	private void updateCapacity() {
		mCapacity = mCapacity * mContents.getDensity()/mReferenceMaterial.getDensity();
	}
}

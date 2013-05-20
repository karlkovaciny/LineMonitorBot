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
	private double mScrewSize;
	private double mRate;
	Material mContents;
	
	public Novatec(double capacity, double setpoint, double screwSize) {
		mCapacity = Math.max(capacity, 0);
		mControllerSetpoint = Math.max(setpoint, 0);
		mScrewSize = Math.max(screwSize,0);
		mLbsContained = 0;
		mReferenceMaterial = new Material("105 Concentrate");
		mContents = mReferenceMaterial;
		mAlarmPercent = 100;
	}
	
	public Novatec() {
		this(0,0,1);
	}
	
	private void updateRate(){
		mRate = mControllerSetpoint * mScrewSize * mContents.getDensity()/mReferenceMaterial.getDensity();
	}
	public double getRate(){
		return mRate;
	}
	
	public double setControllerSetpoint(double setpoint) {
		setpoint = Math.max(setpoint,0);
		updateRate();
		return mControllerSetpoint = setpoint;
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

	public double getScrewSize() {
		return mScrewSize;
	}

	public void setScrewSize(double mScrewSize) {
		this.mScrewSize = mScrewSize;
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

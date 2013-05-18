package com.kovaciny.primexmodel;

public class Novatec implements Hopper {
	float mCapacity; //in pounds of mReferenceMaterial
	/**
	 * percent of full capacity at which alarm sounds.  100 = no alarm.
	 * Not implemented yet.
	 */
	float mAlarmPercent; 
	Material mReferenceMaterial; 
	private float mControllerSetpoint;
	float mLbsContained;
	private float mScrewSize;
	private float mRate;
	Material mContents;
	
	public Novatec(float capacity, float setpoint, float screwSize) {
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
	public float getRate(){
		return mRate;
	}
	
	public float setControllerSetpoint(float setpoint) {
		setpoint = Math.max(setpoint,0);
		updateRate();
		return mControllerSetpoint = setpoint;
	}
	public float getControllerSetpoint() {
		return mControllerSetpoint;
	}
	
	public float estimateMinimumFullCapacity() {
		return mCapacity ; //need to change w/ new matl
	}
	
	public float estimateAlarmCapacity() {
		return mAlarmPercent * mCapacity;
	}
	
	public float getLbsContained() {
		return mLbsContained;
	}
	
	public float setLbsContained(float lbs) {
		if (lbs < 0) return 0;
		else return mLbsContained = lbs;
	}

	public float getScrewSize() {
		return mScrewSize;
	}

	public void setScrewSize(float mScrewSize) {
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

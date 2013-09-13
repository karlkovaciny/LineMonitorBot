package com.kovaciny.primexmodel;

public class Novatec implements Hopper {
    public static double DEFAULT_VOLUME = 50d;
    public static double DEFAULT_SCREW_SIZE_FACTOR = 1d;
	double mCapacity; //in pounds of mReferenceMaterial
	double mAlarmVolume;
	Material mReferenceMaterial; 
	private double mControllerSetpoint;
	double mLbsContained;
	private double mScrewSizeFactor;
	private double mFeedRate;
	Material mContents;
	
	/*
	 * "0" for alarmVolume represents no alarm
	 */
	public Novatec(double volume, double alarmVolume, double setpoint, double screwSizeFactor) {
		if ((volume <= 0d) || (alarmVolume < 0d) || (setpoint < 0d) || (screwSizeFactor <= 0d)) {
			throw new IllegalArgumentException("No negative numbers");
		}
		mCapacity = volume;
		mAlarmVolume = alarmVolume;
		mControllerSetpoint = setpoint;
		mScrewSizeFactor = screwSizeFactor;
		mLbsContained = 0;
		mReferenceMaterial = new Material("105 Concentrate");
		mContents = mReferenceMaterial;
	}
	
	public Novatec() {
		this(DEFAULT_VOLUME,0,0,DEFAULT_SCREW_SIZE_FACTOR);
	}
	
	private void updateFeedRate(){
		mFeedRate = mControllerSetpoint * mScrewSizeFactor * mContents.getDensity()/mReferenceMaterial.getDensity();
	}
	public double getFeedRate(){
		updateFeedRate();
		return mFeedRate;
	}
	public void setFeedRate(double feedRate) {
	    this.mFeedRate = feedRate;
	}
	
	public double setSetpoint(double setpoint) {
		if (setpoint < 0) {
			throw new IllegalArgumentException("No negative numbers");
		}
		mControllerSetpoint = setpoint;
		updateFeedRate();
		return mControllerSetpoint;
	}
	public double getSetpoint() {
		return mControllerSetpoint;
	}
	
	public double getUsableVolume() {
		return mCapacity ; //need to change w/ new matl
	}
	
	public double getAlarmVolume() {
		return mAlarmVolume;
	}
	
	public double getLbsContained() {
		return mLbsContained;
	}
	
	public double setLbsContained(double lbs) {
		if (lbs < 0) return 0;
		else return mLbsContained = lbs;
	}

	public double getScrewSizeFactor() {
		return mScrewSizeFactor;
	}

	public void setScrewSizeFactor(double screwSizeFactor) {
		this.mScrewSizeFactor = screwSizeFactor;
		updateFeedRate();
	}

	public Material getContents() {
		return mContents;
	}

	public void setContents(Material mContents) {
		this.mContents = mContents;
		updateFeedRate();
		updateCapacity();
	}
	
	private void updateCapacity() {
		mCapacity = mCapacity * mContents.getDensity()/mReferenceMaterial.getDensity();
	}
	
	public boolean hasAlarm() {
	    return (mAlarmVolume > 0) ? true : false; 
	}
}

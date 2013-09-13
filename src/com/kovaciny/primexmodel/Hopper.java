package com.kovaciny.primexmodel;

public class Hopper {
    private double mFeedRate;
    private Material mContents;
    private double mVolume;
    private double mAlarmVolume;
    private double mSetpoint;
    public double mLbsContained;

    /*
     * "0" for alarmVolume represents no alarm
     */
    public Hopper(double volume, double alarmVolume, double setpoint) {
        if ((volume <= 0d) || (alarmVolume < 0d) || (setpoint < 0d)) {
            throw new IllegalArgumentException("No negative numbers");
        }
        mVolume = volume;
        mAlarmVolume = alarmVolume;
        mSetpoint = setpoint;
        mLbsContained = 0;
    }

    public double getUsableVolume() {
        return mVolume; //need to change w/ new matl
    }
    
    public double getAlarmVolume() {
        return mAlarmVolume;
    }	
	   
	public void setSetpoint(double setpoint) {
        if (setpoint < 0) {
            throw new IllegalArgumentException("No negative numbers");
        }
        mSetpoint = setpoint;
    }
    public double getSetpoint() {
        return mSetpoint;
    }
    
    public boolean hasAlarm() {
        return (mAlarmVolume > 0) ? true : false; 
    }
    
    public Material getContents() {
        return mContents;
    }

    public void setContents(Material mContents) {
        this.mContents = mContents;
    }
    
	public double getFeedRate(){ //measured in pounds per hour
        return mFeedRate;
    }
    public void setFeedRate(double feedRate) {
        this.mFeedRate = feedRate;
    }
    
    public double getLbsContained() {
        return mLbsContained;
    }
    
    public double setLbsContained(double lbs) {
        if (lbs < 0) return 0;
        else return mLbsContained = lbs;
    }
}

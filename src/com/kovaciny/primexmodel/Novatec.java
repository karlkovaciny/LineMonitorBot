package com.kovaciny.primexmodel;

public class Novatec extends Hopper {
    public static double DEFAULT_VOLUME = 50d;
    public static double DEFAULT_SCREW_SIZE_FACTOR = 1d;
	double mLbsContained;
	private double mScrewSizeFactor;
	
	/*
	 * "0" for alarmVolume represents no alarm
	 */
	public Novatec(double volume, double alarmVolume, double screwSizeFactor) {
		super(volume, alarmVolume);
	    if (screwSizeFactor <= 0d) {
			throw new IllegalArgumentException("Invalid screw size factor");
		}
	    mScrewSizeFactor = screwSizeFactor;
	}
	
	public Novatec() {
		this(DEFAULT_VOLUME,0, DEFAULT_SCREW_SIZE_FACTOR);
	}
	
	@Override
	public double getFeedRate(){ //measured in pounds per hour
        return getSetpoint() * mScrewSizeFactor;
    }

	@Override
	public void setSetpoint(double setpoint) {
		super.setSetpoint(setpoint);
	}
	
	public double getScrewSizeFactor() {
		return mScrewSizeFactor;
	}

	public void setScrewSizeFactor(double screwSizeFactor) {
		this.mScrewSizeFactor = screwSizeFactor;
	}
}

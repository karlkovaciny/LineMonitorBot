package com.kovaciny.primexmodel;

import java.util.HashMap;
import java.util.Map;

public class Novatec extends Hopper {
    public static double DEFAULT_VOLUME = 50d;
    public static double DEFAULT_SCREW_SIZE_FACTOR = 1d;
	double mLbsContained;
	private double mScrewSizeFactor;
	
	/*
	 * "0" for alarmVolume represents no alarm
	 */
	public Novatec(Map<Integer, Double> materialToSafeDrainTimeMap, 
            Map<Integer, Double> materialToEstimatedDrainTimeMap, double screwSizeFactor) {
//		super(materialToSafeDrainTimeMap, materialToEstimatedDrainTimeMap); 
	    super();//TODO call the real one
	    if (screwSizeFactor <= 0d) {
			throw new IllegalArgumentException("Invalid screw size factor");
		}
	    mScrewSizeFactor = screwSizeFactor;
	}
	
	public Novatec() {
		this(new HashMap<Integer,Double>(), new HashMap<Integer, Double>(), DEFAULT_SCREW_SIZE_FACTOR); //TODO delete
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

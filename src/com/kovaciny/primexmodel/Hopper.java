package com.kovaciny.primexmodel;

import android.annotation.SuppressLint;
import java.util.HashMap;
import java.util.Map;

@SuppressLint("UseSparseArrays")
public class Hopper {
    public static final double REFERENCE_GROSS_RATE = 1000d; //lbs/hr
    private double mFeedRate;
    private int mContentsType;
    private double mSetpoint;
    public double mLbsContained;
    private HashMap<Integer, Double> mMaterialToSafeDrainTimeMap;
    private HashMap<Integer, Double> mMaterialToEstimatedDrainTimeMap;

    public Hopper() {
        
    }
    /*
     * 
     */
    public Hopper(Map<Integer, Double> materialToSafeDrainTimeMap, 
                Map<Integer, Double> materialToEstimatedDrainTimeMap) {
        mMaterialToSafeDrainTimeMap = new HashMap<Integer, Double>(materialToSafeDrainTimeMap);
        mMaterialToEstimatedDrainTimeMap = new HashMap<Integer, Double>(materialToEstimatedDrainTimeMap);
        mSetpoint = 0;
        mLbsContained = 0;
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
    
    public int getContentsType() {
        return mContentsType;
    }

    public void setContents(int contentsType) {
        this.mContentsType = contentsType;
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
    
    public double getSafeDrainTime(double safetyMargin) {
        double relativeDrainRate = mFeedRate / REFERENCE_GROSS_RATE;
        double safeDrainTime;
        if (mMaterialToSafeDrainTimeMap.containsKey(mContentsType)) {
            safeDrainTime = mMaterialToSafeDrainTimeMap.get(mContentsType) * relativeDrainRate;
        } else {
            safeDrainTime = mMaterialToEstimatedDrainTimeMap.get(mContentsType) * relativeDrainRate;
        }
        return safeDrainTime;
    }
}

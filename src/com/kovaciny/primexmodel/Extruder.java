package com.kovaciny.primexmodel;

public class Extruder {
    private Hopper mFeedHopper;
    private double mOutputRate;
    
    public Extruder(Hopper feedHopper){
        this.mFeedHopper = feedHopper;
    }
    
    public void setOutputRate(double rate) {
        mFeedHopper.setFeedRate(rate);
        mOutputRate = rate;
    }
    
    public double getOutputRate() {
        return this.mOutputRate;
    }
}

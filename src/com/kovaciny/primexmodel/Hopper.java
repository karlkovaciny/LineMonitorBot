package com.kovaciny.primexmodel;

public interface Hopper {
	double setControllerSetpoint(double setpoint);
	double getControllerSetpoint();
	double estimateMinimumFullCapacity();
	/*
	 * Estimate the number of pounds left when the low alarm sounds.
	 */
	double estimateAlarmCapacity();
	double getLbsContained();
	double setLbsContained(double lbs);
	double getRate(); //measured in pounds per hour
}

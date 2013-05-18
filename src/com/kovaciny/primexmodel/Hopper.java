package com.kovaciny.primexmodel;

public interface Hopper {
	float setControllerSetpoint(float setpoint);
	float getControllerSetpoint();
	float estimateMinimumFullCapacity();
	/*
	 * Estimate the number of pounds left when the low alarm sounds.
	 */
	float estimateAlarmCapacity();
	float getLbsContained();
	float setLbsContained(float lbs);
	float getRate(); //measured in pounds per hour
}

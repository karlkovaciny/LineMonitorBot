package com.kovaciny.linemonitorbot;

interface Hopper {
	float setControllerSetpoint(float setpoint);
	float getControllerSetpoint();
	float estimateMinimumFullCapacity();
	float getLbsContained();
	float setLbsContained(float lbs);
	float getRate(); //measured in pounds per hour
}

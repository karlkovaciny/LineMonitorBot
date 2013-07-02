package com.kovaciny.primexmodel;

public class SpeedValues {
	public double lineSpeedSetpoint;
	public double differentialSpeed;
	public double speedFactor;
	
	public SpeedValues(double linespeed, double differential, double factor) {
		if (linespeed < 0) {throw new IllegalArgumentException("Line speed setpoint less than zero");}
		if (differential < 0) {throw new IllegalArgumentException("Differential setpoint less than zero");}
		if (factor < 0) {throw new IllegalArgumentException("Speed factor less than zero");}
		
		lineSpeedSetpoint = linespeed;
		differentialSpeed = differential;
		speedFactor = factor;
	}
	
	public double getProduct() {
		return lineSpeedSetpoint * differentialSpeed * speedFactor;
	}
}

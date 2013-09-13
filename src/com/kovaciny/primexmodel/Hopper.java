package com.kovaciny.primexmodel;

public interface Hopper {
	double setSetpoint(double setpoint);
	double getSetpoint();
	double getUsableVolume();
	double getAlarmVolume();
	boolean hasAlarm();
	Material getContents();
	double getFeedRate(); //measured in pounds per hour
	void setFeedRate(double feedRate);
	double getLbsContained();
	double setLbsContained(double lbs);
}

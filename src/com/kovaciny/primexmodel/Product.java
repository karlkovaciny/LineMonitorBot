package com.kovaciny.primexmodel;

public interface Product {
	double getHeight();
	double getGauge();
	double getWeight();
	double getWidth(); //in inches
	double getLength(); // in inches
	void setLength(double length);
	double getEstimatedWeight();
	String getMaterialType();
	boolean setMaterialType();
	String getUnit(); //singular
	String getUnits(); //plural
	String getType(); //TODO hope this is not bad polymorphism
	int getLineNumber(); //TODO this shouldn't be part of this object
	void setLineNumber(int ln); //TODO this shouldn't be part of this object
	
	
	public static String SHEETS_TYPE="Sheet";
	public static String ROLLS_TYPE="Roll";
}

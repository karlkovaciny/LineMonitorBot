package com.kovaciny.primexmodel;

public interface Product {
	double getHeight();
	double getGauge();
	double getUnitWeight(); //of one unit of the product (sheet or roll? TODO)
	void setUnitWeight(double unitWeight);
	double getWidth(); //in inches
	double getLength(); // in inches
	void setLength(double length);
	double getEstimatedWeight();
	String getMaterialType();
	boolean setMaterialType();
	String getUnit(); //singular
	String getUnits(); //plural
	String getType(); //TODO hope this is not bad polymorphism	
	
	public static String SHEETS_TYPE="Sheet";
	public static String ROLLS_TYPE="Roll";
}

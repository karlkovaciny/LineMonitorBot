package com.kovaciny.primexmodel;

import android.text.SpannableStringBuilder;

public interface Product {
	double getHeight();
	double getGauge();
	boolean hasStacks();
	void setGauge(double gauge);
	double getAverageGauge();
	void setAverageGauge(double gauge);
	double getUnitWeight(); //of one unit of the product (sheet or roll? TODO)
	void setUnitWeight(double unitWeight);
	double getWidth(); //in inches
	void setWidth(double width);
	double getLength(); // in inches
	void setLength(double length);
	int getNumberOfWebs();
	double getEstimatedWeight();
	String getMaterialType();
	boolean setMaterialType();
	SpannableStringBuilder getFormattedDimensions();
	String getUnit(); //singular
	String getUnits(); //plural
	String getGrouping(); //name for a collection of units
	String getType(); //TODO hope this is not bad polymorphism	
	
	public static String SHEETS_TYPE="Sheet";
	public static String SHEETSET_TYPE="Sheetset";
	public static String ROLLS_TYPE="Roll";
	public static String ROLLSET_TYPE="Rollset";
}

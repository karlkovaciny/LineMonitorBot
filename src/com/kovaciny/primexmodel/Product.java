package com.kovaciny.primexmodel;

public interface Product {
	double getHeight();
	double getWeight();
	double getWidth(); //in inches
	double getLength(); // in inches
	void setLength(double length);
	double getEstimatedWeight();
	String getMaterialType();
	boolean setMaterialType();
	
	public static int SHEETS_TYPE=0;
	public static int ROLLS_TYPE=1;
}

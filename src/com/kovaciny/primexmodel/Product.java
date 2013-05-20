package com.kovaciny.primexmodel;

public interface Product {
	double getHeight();
	double getWeight();
	double getWidth(); //in inches
	double getLength(); // in inches
	double getEstimatedWeight();
	String getMaterialType();
	boolean setMaterialType();
}

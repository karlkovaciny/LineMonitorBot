package com.kovaciny.primexmodel;

public interface Product {
	float getHeight();
	float getWeight();
	float getWidth(); //in inches
	float getLength(); // in inches
	float getEstimatedWeight();
	String getMaterialType();
	boolean setMaterialType();
}

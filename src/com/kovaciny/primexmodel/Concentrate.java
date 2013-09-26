package com.kovaciny.primexmodel;

/**
 * Currently not needed
 */
public class Concentrate extends Material {
	
	public Concentrate() {
		this(1, "Concentrate");
	}

	public Concentrate(double mDensity, String mName) {
		super(mDensity, mName, Material.CONCENTRATE_TYPE);
	}
}

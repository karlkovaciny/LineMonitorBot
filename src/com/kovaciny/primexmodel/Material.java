package com.kovaciny.primexmodel;

public class Material {
	private double mDensity;
	private String mName;
		
	public Material() {
		this(1f, "Material");
	}

	public Material(String name) {
		this(1f, name);
	}
	public Material(double mDensity, String mName) {
		super();
		this.mDensity = mDensity;
		this.mName = mName;
	}
		
	public double getDensity() {
		return mDensity;
	}

	public String getName() {
		return mName;
	}
}

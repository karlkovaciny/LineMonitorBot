package com.kovaciny.linemonitorbot;

public class Material {
	private float mDensity;
	private String mName;
		
	public Material() {
		this(1f, "Material");
	}

	public Material(String name) {
		this(1f, name);
	}
	public Material(float mDensity, String mName) {
		super();
		this.mDensity = mDensity;
		this.mName = mName;
	}
		
	public float getDensity() {
		return mDensity;
	}

	public String getName() {
		return mName;
	}
}

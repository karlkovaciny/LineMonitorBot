package com.kovaciny.primexmodel;

public class Material {
    public static final int EMPTY = -1;
	public static final int RESIN_TYPE = 0;
	public static final int REGRIND_TYPE = 1;
	public static final int RESIN_REGRIND_BLEND_30_TO_70_PERCENT = 11;
	public static final int IMPACT_MODIFIER_TYPE = 2;
	public static final int FILLER_TYPE_HM10_MAX = 3;
	public static final int FILLER_TYPE_HICAL = 4;
	public static final int CONCENTRATE_TYPE = 5;
	
	public static final String DEFAULT_MATERIAL_NAME = "Material";
	
    private double mDensity;
    private int mType;
	private String mName;
	
	public Material() {
		this(1f, DEFAULT_MATERIAL_NAME, RESIN_TYPE);
	}

	public Material(int type) {
	    this(1f, DEFAULT_MATERIAL_NAME, type);
	}
	
	public Material(String name, int type) {
		this(1f, name, type);
	}
	public Material(double mDensity, String mName, int type) {
		this.mType = type;
	    this.mDensity = mDensity;
		this.mName = mName;
	}
		
	public double getDensity() {
		return mDensity;
	}

	public String getName() {
		return mName;
	}
	
	public int getType() {
	    return this.mType;
	}
}

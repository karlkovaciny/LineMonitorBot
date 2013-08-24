package com.kovaciny.primexmodel;

public class Rollset extends Roll {
	private int mNumberOfWebs = 1;
	private static final String mUnitSingular = "foot";
	private static final String mUnitPlural = "feet";
	private static final String mGrouping = "rollset";
	
	public Rollset(double gauge, double width, int linearFeet, int numberOfWebs) {
		super(gauge, width, linearFeet);
		setNumberOfWebs(numberOfWebs);
	}

	@Override
	public String getUnit() {
		return mUnitSingular;
	}
	
	@Override
	public String getUnits() {
		return mUnitPlural;
	}

	@Override
	public String getGrouping() {
		return mGrouping;
	}

	public int getNumberOfWebs() {
		return mNumberOfWebs;
	}
	public void setNumberOfWebs(int numberOfWebs) {
		if (numberOfWebs <= 0) throw new IllegalArgumentException("number of webs must be positive");
		this.mNumberOfWebs = numberOfWebs;
	}
	public double getSingleWebWidth() {
		return super.getWidth(); //TODO huh?
	}
	
	@Override
	public String getType() {
		return Product.ROLLSET_TYPE;
	}
}

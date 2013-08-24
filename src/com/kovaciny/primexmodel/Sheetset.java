package com.kovaciny.primexmodel;

public class Sheetset extends Sheet {
	private int mNumberOfWebs = 1;
	private static final String mUnitSingular = "sheet";
	private static final String mUnitPlural = "cuts";
	private static final String mGrouping = "skid";

	public Sheetset(double gauge, double width, double length, int numberOfWebs) {
		super(gauge, width, length);
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

	@Override
	public String getType() {
		return Product.SHEETSET_TYPE;
	}

}

package com.kovaciny.primexmodel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Roll implements Product {
	private double mLinearFootWeight = 0;
	private int mLinearFeet = 0;
	private double mGauge; //sheet thickness in inches
	private double mAverageGauge;
	private double mWidth; //traverse direction in inches
	private double mDensity;
	private static final String mUnitSingular = "foot";
	private static final String mUnitPlural = "feet";
	private static final String mGrouping = "roll";
	private int mCoreType;
	
	public static final int CORE_TYPE_R3 = 3;
	public static final int CORE_TYPE_R6 = 6;
	public static final int CORE_TYPE_R8 = 8;
	public static final int CORE_TYPE_R3_HEAVY = 4;
	public static final int CORE_TYPE_R6_HEAVY = 7;
	
	public static final Map<Integer, Double> coreTypeToOutsideDiameterMap;
	static {
	    Map<Integer, Double> aMap = new HashMap<Integer, Double>();
	    aMap.put(CORE_TYPE_R3, 3.5);
	    aMap.put(CORE_TYPE_R6, 6.75);
	    aMap.put(CORE_TYPE_R8, 0d);
	    aMap.put(CORE_TYPE_R3_HEAVY, 4d);
	    aMap.put(CORE_TYPE_R6_HEAVY, 7d);
	    coreTypeToOutsideDiameterMap = Collections.unmodifiableMap(aMap);
	}
	
	public static final Map<Integer, String> coreTypeToDescriptionMap;
    static {
        Map<Integer, String> anotherMap = new HashMap<Integer, String>();
        anotherMap.put(CORE_TYPE_R3, "R3");
        anotherMap.put(CORE_TYPE_R6, "R6");
        anotherMap.put(CORE_TYPE_R8, "R8");
        anotherMap.put(CORE_TYPE_R3_HEAVY, "R3");
        anotherMap.put(CORE_TYPE_R6_HEAVY, "R6");
        coreTypeToDescriptionMap = Collections.unmodifiableMap(anotherMap);
    }

    public static final Map<Integer, Double> coreTypeToWeightPerInchMap;
    static {
        Map<Integer, Double> thirdMap = new HashMap<Integer, Double>();
        thirdMap.put(CORE_TYPE_R3, .0833333);
        thirdMap.put(CORE_TYPE_R6, .1666666);
        thirdMap.put(CORE_TYPE_R8, .1);
        thirdMap.put(CORE_TYPE_R3_HEAVY, .121212);
        thirdMap.put(CORE_TYPE_R6_HEAVY, .25);
        coreTypeToWeightPerInchMap = Collections.unmodifiableMap(thirdMap);
    }
    
    /*
     * 
     */
    public static double getCoreWeight (int coreType, double coreLength) {
       return coreTypeToWeightPerInchMap.get(coreType); 
    }
    
    public static double getCoreOutsideRadius(int coreType) {
        return coreTypeToOutsideDiameterMap.get(coreType) / 2d;
    }
    
    public static double getCoreArea(int coreType) {
        return Math.PI * Math.pow(getCoreOutsideRadius(coreType), 2d);
    }
	/*
	 * 
	 */
	public Roll(double gauge, double width, int linearFeet, int coreType) {
		if ( (gauge < 0 || width < 0) ) {
			throw new IllegalArgumentException("invalid dimensions");
		}
		mGauge = gauge;
		mWidth = width;
		mLinearFeet = linearFeet;
		mCoreType = coreType; 
		mDensity = .0375d;
	}
		
	public double getEstimatedWeight() {
		return ( mGauge * mWidth * mDensity * mLinearFeet);
	}
	public double getHeight() {
		//depends on eye-to-sky status
		//TODO
		return 0d;		
	}
	
	public double getGauge() {
		return mGauge;
	}
	
	public void setGauge(double gauge){
		mGauge = gauge;
	}
	
	public double getAverageGauge() {
		return mAverageGauge;
	}
	public void setAverageGauge(double gauge) {
		mAverageGauge = gauge;
	}
	public boolean hasStacks() {
		return false;
	}
	public double getUnitWeight() {
		return mLinearFootWeight;
	}
	public void setUnitWeight(double linearFootWeight) {
		if (linearFootWeight < 0) {
			throw new IllegalArgumentException("negative weight");
		}
		mLinearFootWeight = linearFootWeight;
	}	
	public double getLength() {
		return 12; //one linear foot
	}
	public void setLength(double length) {
		if (length != 12) {
			throw new IllegalArgumentException("Sheet length for rolls must always be 12");
		}
	}
	public int getLinearFeet() {
		return mLinearFeet;
	}

	public void setLinearFeet(int linearFeet) {
		this.mLinearFeet = linearFeet;
	}

	public double getWidth() {
		return mWidth;
	}
	public void setWidth(double width) {
		if (width <= 0) throw new IllegalArgumentException("width must be positive");
		mWidth = width;
	}
	public String getMaterialType() {
		return "Not implemented";
	}
	public boolean setMaterialType(){
		//TODO
		return false;
	}
	public int getNumberOfWebs() {
		return 1;
	}
	public int getCoreType() {
	    return mCoreType;
	}
	public void setCoreType(int coreType){
	    mCoreType = coreType;
	}
	public String getUnit() {
		return mUnitSingular;
	}
	
	public String getUnits() {
		return mUnitPlural;
	}
	
	public String getGrouping() {
		return mGrouping;
	}
	
	public String getType() {
		return Product.ROLLS_TYPE;
	}
	public double getDensity() {
		return mDensity;
	}

	public void setDensity(double density) {
		this.mDensity = density;
	}

	
	@Override
	public String toString() {
		return getType() + ": " + String.valueOf(mGauge) + " x " + String.valueOf(mWidth) + " x R8, " + 
				String.valueOf(getNumberOfWebs()) + " webs";
	}
}

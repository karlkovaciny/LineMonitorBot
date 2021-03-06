package com.kovaciny.primexmodel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.kovaciny.helperfunctions.HelperFunction;

public class Skid<E extends Product> implements Comparable<Skid<Product>>{

    public static final int DEFAULT_SHEET_COUNT = 1000;
    Pallet mPallet;
	Product mProductUnit;
	Integer mCurrentItems = 0;
	Integer mTotalItems = null;
	double mPackagingWeight;
	double mFinishedNetWeight;
	double mFinishedGrossWeight;
	double mMinutesPerSkid;
	double mStackHeight;
	int mSkidNumber = 0;
	int mNumberOfStacks;
	Date mStartTime;
	Date mFinishTime;

	/*
	 * 
	 */
	public Skid (Pallet p, int totalItems, double packagingWeight,
			int numStacks, Product product) {
		mPallet = p;
		mTotalItems = totalItems;
		mPackagingWeight = 0; // unimportant for now;
		mNumberOfStacks = numStacks;
		mProductUnit = product;
	}
	
	/*
	 * to see parameters
	 */
	public Skid(int currentItems, int totalItems, int numStacks) {
//		this.mSkidNumber = skidNumber;   The project will assign a skid number.
		this.mCurrentItems = currentItems;
		this.mTotalItems = totalItems;
		mPallet = null;
		mPackagingWeight = 0;
		mNumberOfStacks = numStacks;
	}

	public Skid(int totalItems, Product product) {
		this(new Pallet(), totalItems, 0d, 1, product);
	}

	public Skid() {
	    this(DEFAULT_SHEET_COUNT, null);
	}
	
	public double getEstimatedStackHeight() {
		return mTotalItems / mNumberOfStacks * mProductUnit.getHeight();
	}
	
	public double getFinishedStackHeight() {
		return mStackHeight;
	}

	public void setFinishedStackHeight(double stackHeight) {
		mStackHeight = stackHeight;
	}
	public double getFinishedNetWeight() {
		return mProductUnit.getUnitWeight() * mTotalItems;
	}

	public double getFinishedGrossWeight() {
		return mPallet.getWeight() + mProductUnit.getUnitWeight() * mTotalItems
				+ mPackagingWeight;
	}

	public Integer getCurrentItems() {
		return mCurrentItems;
	}

	public void setCurrentItems(int currentItems) {
		this.mCurrentItems = currentItems;
	}

	public Integer getTotalItems() {
		return mTotalItems;
	}

	public void setTotalItems(int totalItems) {
		this.mTotalItems = totalItems;
	}

	public int getNumberOfStacks() {
		return mNumberOfStacks;
	}

	public void setNumberOfStacks(int numberOfStacks) {
		this.mNumberOfStacks = numberOfStacks;
	}

	public Date getStartTime() {
		return mStartTime;
	}

	public void setStartTime(Date startTime) {
		this.mStartTime = startTime;
	}

	public Date calculateStartTime(double productsPerMinute) {
		double minutesElapsed = mCurrentItems / productsPerMinute;
		long millisElapsed = (long) (minutesElapsed * HelperFunction.ONE_MINUTE_IN_MILLIS);
		long timeNow = new Date().getTime();
		mStartTime = new Date( timeNow - millisElapsed);
		return mStartTime;
	}
	
	/*
	 * This version of the function is for a skid that's currently being produced.
	 */
	public Date calculateFinishTimeWhileRunning(double productsPerMinute) {
		Date currentDate = new Date();
		long timeNow = currentDate.getTime();
		
		if (mCurrentItems >= mTotalItems) {
			mFinishTime = currentDate;
		} else {
			double minutesLeft = (mTotalItems - mCurrentItems ) / productsPerMinute;
			long millisLeft = (long) (minutesLeft * HelperFunction.ONE_MINUTE_IN_MILLIS);
			mFinishTime = new Date( timeNow + millisLeft);
		}
		return mFinishTime;
	}
	
	/*
	 * This version of the function is for a skid that hasn't started yet.
	 */
	public Date calculateFinishTime(double productsPerMinute, Date startTime) {
		calculateMinutesPerSkid(productsPerMinute); //TODO maybe I shouldn't be able to use mMinutesPerSkid when it must have this called first
		long millisPerSkid = Math.round(mMinutesPerSkid * HelperFunction.ONE_MINUTE_IN_MILLIS);
		Date finishTime = new Date(startTime.getTime() + millisPerSkid);
		return finishTime;
	}
	
	public double calculateMinutesPerSkid(double productsPerMinute) {
		if (productsPerMinute <= 0) throw new IllegalArgumentException("products per minute not positive number");
		if ( mTotalItems == null) {
			throw new IllegalStateException("total items not set");
		}
		mMinutesPerSkid = mTotalItems / productsPerMinute;
		return mMinutesPerSkid;
	}
	
	public double getMinutesPerSkid() {
		return mMinutesPerSkid;
	}
	
	public void setMinutesPerSkid(double minutes) {
		mMinutesPerSkid = minutes;
	}

	public void setFinishTime(Date finishTime) {
		this.mFinishTime = finishTime;
	}

	public Date getFinishTime() {
		return mFinishTime;
	}
	
	public void setPackagingWeight(double packagingWeight) {
		this.mPackagingWeight = packagingWeight;
	}

	public int getSkidNumber() {
		return mSkidNumber;
	}

	public void setSkidNumber(int skidNumber) {
		if (!(skidNumber > 0)) throw new RuntimeException ("Should never assign a skid number " + String.valueOf(skidNumber) + "!");
		this.mSkidNumber = skidNumber;
	}

	public int getItemsPerStack() {
		return mTotalItems / mNumberOfStacks;
	}

	@Override
	public String toString() {
		String start = "n/a";
		String finish = "n/a";
		SimpleDateFormat formatter = new SimpleDateFormat("h:mm a", Locale.US);
		if (mStartTime != null) start = formatter.format(mStartTime);
		if (mFinishTime != null) finish = formatter.format(mFinishTime);
		return "Skid " + mSkidNumber + ": Start " + start + ", finish " + finish + ", current sheets " + 
				String.valueOf(mCurrentItems) + " of " + String.valueOf(mTotalItems) + ", " + 
				String.valueOf(mMinutesPerSkid) + " minutes per skid";
	}
	@Override
	public int compareTo(Skid<Product> arg0) {
		return this.mSkidNumber - arg0.getSkidNumber();
	}
}

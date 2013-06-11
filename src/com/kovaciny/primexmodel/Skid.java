package com.kovaciny.primexmodel;

import java.util.Date;

public class Skid<E extends Product> {
	Pallet mPallet;
	E mProductUnit;
	Integer mCurrentItems = null;
	Integer mTotalItems = null;
	double mPackagingWeight;
	double mFinishedNetWeight;
	double mFinishedGrossWeight;
	int mSkidNumber;
	int mNumberOfStacks;
	Date mStartTime;
	Date mFinishTime;

	public Skid(Pallet p, int totalItems, double packagingWeight,
			int numStacks, Date start, E product) {
		mPallet = p;
		mProductUnit = product;
		mCurrentItems = 0;
		mTotalItems = totalItems;
		mPackagingWeight = 0; // unimportant for now
		mFinishedNetWeight = getFinishedNetWeight();
		mFinishedGrossWeight = getFinishedGrossWeight();
		mNumberOfStacks = numStacks;
	}

	public Skid() {
	}

	public double getStackHeight() {
		return mTotalItems / mNumberOfStacks * mProductUnit.getHeight();
	}

	public double getFinishedNetWeight() {
		return mProductUnit.getWeight() * mTotalItems;
	}

	public double getFinishedGrossWeight() {
		return mPallet.getWeight() + mProductUnit.getWeight() * mTotalItems
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

	public int getmNumberOfStacks() {
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

	public Date getFinishTime() {
		return mFinishTime;
	}

	public void setFinishTime(Date finishTime) {
		this.mFinishTime = finishTime;
	}

	public void setPackagingWeight(double packagingWeight) {
		this.mPackagingWeight = packagingWeight;
	}

	public int getSkidNumber() {
		return mSkidNumber;
	}

	public void setSkidNumber(int skidNumber) {
		this.mSkidNumber = skidNumber;
	}
}

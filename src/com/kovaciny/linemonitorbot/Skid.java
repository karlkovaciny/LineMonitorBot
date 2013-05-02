package com.kovaciny.linemonitorbot;

import java.util.Date;

class Skid<E extends Product> {
	Pallet mPallet;
	E mProductUnit;
	int mCurrentItems;
	int mTotalItems;
	float mPackagingWeight;	
	float mFinishedNetWeight;
	float mFinishedGrossWeight;
	int mNumberOfStacks;	
	Date mStartTime;
	Date mFinishTime;
	
	
	Skid(Pallet p, int totalItems, float packagingWeight, int numStacks, Date start, E product){
		mPallet = p;
		mProductUnit = product;
		mCurrentItems = 0;
		mTotalItems = totalItems;
		mPackagingWeight = 0; //unimportant for now
		mFinishedNetWeight = getFinishedNetWeight();
		mFinishedGrossWeight = getFinishedGrossWeight();
		mNumberOfStacks = numStacks;
	}
	
	Skid() {
	}
	
	public float getStackHeight(){
			return mTotalItems / mNumberOfStacks * mProductUnit.getHeight();
	}
	
	public float getFinishedNetWeight(){
		return mProductUnit.getWeight() * mTotalItems;
	}
	
	public float getFinishedGrossWeight(){
		return mPallet.getWeight() + mProductUnit.getWeight() * mTotalItems + mPackagingWeight; 		
	}

	public int getmCurrentItems() {
		return mCurrentItems;
	}

	public void setmCurrentItems(int mCurrentItems) {
		this.mCurrentItems = mCurrentItems;
	}

	public int getmTotalItems() {
		return mTotalItems;
	}

	public void setmTotalItems(int mTotalItems) {
		this.mTotalItems = mTotalItems;
	}

	public int getmNumberOfStacks() {
		return mNumberOfStacks;
	}

	public void setmNumberOfStacks(int mNumberOfStacks) {
		this.mNumberOfStacks = mNumberOfStacks;
	}

	public Date getmStartTime() {
		return mStartTime;
	}

	public void setmStartTime(Date mStartTime) {
		this.mStartTime = mStartTime;
	}

	public Date getmFinishTime() {
		return mFinishTime;
	}

	public void setmFinishTime(Date mFinishTime) {
		this.mFinishTime = mFinishTime;
	}

	public void setmPackagingWeight(float mPackagingWeight) {
		this.mPackagingWeight = mPackagingWeight;
	}
}

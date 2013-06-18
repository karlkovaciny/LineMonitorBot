package com.kovaciny.primexmodel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WorkOrder {
	private int mWoNumber;
	private List<Skid<Product>> mSkidsList;
	private Product mProduct = null;
	private double mTotalProductsOrdered;
	private double mMaximumStackHeight; // in inches, not including pallet
	private int mSelectedSkidPosition = -1;
	private Date mFinishTime;
	
	static final double MAX_STACK_HEIGHT_OLEFINS = 24d;
	static final double MAX_STACK_HEIGHT_STYRENE = 30d;
	static final double MAX_WO_NUMBER = 999999d;
	
	public WorkOrder(int woNumber){
		setWoNumber(woNumber);
		mSkidsList = new ArrayList<Skid<Product>>();
		addSkid();
		mSelectedSkidPosition = 0;
	}
	
	public void setWoNumber(int woNumber) {
		if ((woNumber < 0) || (woNumber > MAX_WO_NUMBER)) {
			throw new IllegalArgumentException("invalid Work Order number");
		}
				this.mWoNumber = woNumber;
	}	
	
	public Date getFinishTime() {
		return mFinishTime;
	}
	
	/*
	 * rename
	 * This function updates all unfinished skids with projected finish times and returns the finish time for the job. 
	 */
	public Date calculateFinishTimes(double productsPerMinute) {
		if (mSkidsList.isEmpty()) {
			mFinishTime = null;
		} else {
			Skid<Product> currentSkid = mSkidsList.get(mSelectedSkidPosition);
			Date currentFinishTime = currentSkid.calculateFinishTimeWhileRunning(productsPerMinute);
			
			for (int i = mSelectedSkidPosition + 1; i < mSkidsList.size(); i++) {
				currentFinishTime = mSkidsList.get(i).calculateFinishTime(productsPerMinute, currentFinishTime);
			}
			mFinishTime = currentFinishTime;
		}
		return mFinishTime;
	}
	
	/*
	 * Appends a skid to the end of the list with start time equal to the last skid's finish time (now if none) 
	 * and returns the skid's position. Sets the skid's sheet count equal to the last one TODO
	 */
	public int addSkid () {
		int productsPerSkid = 0;
		if (hasSelectedSkid()) {
			productsPerSkid = mSkidsList.get(mSelectedSkidPosition).getTotalItems();
		}
		Skid<Product> newSkid = new Skid<Product>(productsPerSkid, mProduct);
		Date currentFinishTime = getFinishTime();
		newSkid.setStartTime(currentFinishTime);
		mSkidsList.add(newSkid);
		return mSkidsList.size() - 1;
	}
	
	/*
	 * Removes the last skid
	 */
	public void removeSkid() {
		mSkidsList.remove(mSkidsList.size()-1);
	}
	
	public boolean hasProduct() {
		return (mProduct == null) ? false : true;
	}
	
	public boolean hasSelectedSkid() {
		return (mSelectedSkidPosition == -1) ? false : true;
	}
		
	/*
	 * Boilerplate from here on down... if I remember to move any functions I update.
	 */
	
	public int getWoNumber() {
		return mWoNumber;
	}
	
	public List<Skid<Product>> getProductsList() {
		return mSkidsList;
	}

	public double getTotalProductsOrdered() {
		return mTotalProductsOrdered;
	}

	public void setTotalProductsOrdered(double ordered) {
		if (ordered < 0) throw new RuntimeException ("Negative products ordered");
		this.mTotalProductsOrdered = ordered;
	}

	public double getMaximumStackHeight() {
		return mMaximumStackHeight;
	}

	public void setMaximumStackHeight(double max) {
		if (max < 0) throw new RuntimeException ("Negative stack height");
		this.mMaximumStackHeight = max;
	}

	public int getNumberOfSkids() {
		return mSkidsList.size();
	}

	public void setNumberOfSkids(int num) {
		if (num < 0) throw new RuntimeException ("Negative number of skids");
		while ( getNumberOfSkids() > num) {
			removeSkid();
		}
		while (getNumberOfSkids() < num) {
			addSkid();
		}
	}

	public Skid<Product> getSelectedSkid() {
		return mSkidsList.get(mSelectedSkidPosition);
	}

	public Skid<Product> selectSkid(Integer skidNumber) {
		if (skidNumber < 0) throw new RuntimeException ("negative skid number");
		mSelectedSkidPosition = skidNumber - 1; //because we're storing this in an array... for now.
		return mSkidsList.get(mSelectedSkidPosition);
	}
	
	public void setProduct(Product product) {
		this.mProduct = product;
	}
	public Product getProduct() {
		return mProduct;
	}	
	
}

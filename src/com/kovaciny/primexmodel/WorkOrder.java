package com.kovaciny.primexmodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class WorkOrder {
	private int mWoNumber;
	private List<Skid<Product>> mSkidsList;
	private Product mProduct = null;
	private double mTotalProductsOrdered;
	private double mMaximumStackHeight; // in inches, not including pallet
	private double mNovatecSetpoint = 0;
	private int mSelectedSkidPosition = -1;
	private Date mFinishDate;
	
	static final double MAX_STACK_HEIGHT_OLEFINS = 24d;
	static final double MAX_STACK_HEIGHT_STYRENE = 30d;
	static final double MAX_WO_NUMBER = 999999d;
	
	public WorkOrder(int woNumber){
		setWoNumber(woNumber);
		mSkidsList = new ArrayList<Skid<Product>>();
		mSelectedSkidPosition = -1;
	}
	
	public void setWoNumber(int woNumber) {
		if ((woNumber < 0) || (woNumber > MAX_WO_NUMBER)) {
			throw new IllegalArgumentException("invalid Work Order number");
		}
				this.mWoNumber = woNumber;
	}	
	
	public Date getFinishDate() {
		return mFinishDate;
	}
	public void setFinishDate(Date finishDate) {
		mFinishDate = finishDate;
	}
	
	/*
	 * rename
	 * This function updates all unfinished skids with projected finish times and returns the finish time for the job. 
	 */
	public Date calculateFinishTimes(double productsPerMinute) {
		if (mSkidsList.isEmpty()) {
			mFinishDate = null;
		} else {
			Skid<Product> currentSkid = mSkidsList.get(mSelectedSkidPosition);
			Date currentFinishTime = currentSkid.calculateFinishTimeWhileRunning(productsPerMinute);
			
			for (int i = mSelectedSkidPosition + 1; i < mSkidsList.size(); i++) {
				currentFinishTime = mSkidsList.get(i).calculateFinishTime(productsPerMinute, currentFinishTime);
			}
			mFinishDate = currentFinishTime;
		}
		return mFinishDate;
	}
	
	/*
	 * Appends a skid to the end of the list, changing its skid number.. If the skid is null, set start time equal to the last skid's finish time (now if none)
	 * and set the skid's sheet count equal to the last one TODO
	 * Return the skid's position.
	 */
	public int addOrUpdateSkid (Skid<Product> newSkid) {
		if (newSkid == null) {
			int productsPerSkid = 0;
			if (hasSelectedSkid()) {
				productsPerSkid = mSkidsList.get(mSelectedSkidPosition).getTotalItems();
			}
			newSkid = new Skid<Product>(productsPerSkid, mProduct);
			Date currentFinishTime = getFinishDate();
			newSkid.setStartTime(currentFinishTime);
		}
		if (getSkidNumbers().contains(newSkid.getSkidNumber())) {
			mSkidsList.set(newSkid.getSkidNumber() - 1, newSkid);
		} else {
			mSkidsList.add(newSkid);
			newSkid.setSkidNumber(mSkidsList.size());	
		}		
		return newSkid.getSkidNumber() - 1;
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
	
	public List<Skid<Product>> getSkidsList() {
		return mSkidsList;
	}
	
	public void setSkidsList(List<Skid<Product>> list) {
		mSkidsList = list;
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

	public List<Integer> getSkidNumbers() {
		List<Integer> numbers = new ArrayList<Integer>();
		for (Skid<Product> skid : mSkidsList) {
			numbers.add(skid.getSkidNumber());
		}
		Collections.sort(numbers);
		return numbers;
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
			addOrUpdateSkid(null);
		}
		if (getNumberOfSkids() != num) throw new RuntimeException("something isn't right here");
	}

	public Skid<Product> getSelectedSkid() {
		return mSkidsList.get(mSelectedSkidPosition);
	}

	public Skid<Product> selectSkid(Integer skidNumber) {
		if ( (skidNumber > getNumberOfSkids()) || !(skidNumber > 0) ) {
			throw new IllegalArgumentException("tried to change to a skid number outside the range of skids -- " + String.valueOf(skidNumber));
		}
		mSelectedSkidPosition = skidNumber - 1; //because we're storing this in an array... for now.
		return mSkidsList.get(mSelectedSkidPosition);
	}
	
	public void setProduct(Product product) {
		this.mProduct = product;
	}
	public Product getProduct() {
		return mProduct;
	}	
	public double getNovatecSetpoint() {
		return mNovatecSetpoint;
	}
	public void setNovatecSetpoint(double novatecSetpoint) {
		this.mNovatecSetpoint = novatecSetpoint;
	}	
}

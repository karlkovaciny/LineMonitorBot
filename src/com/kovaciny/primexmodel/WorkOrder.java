package com.kovaciny.primexmodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.util.Log;

import com.kovaciny.helperfunctions.HelperFunction;

public class WorkOrder {
	private int mWoNumber;
	private List<Skid<Product>> mSkidsList;
	private Product mProduct = null;
	private double mTotalProductsOrdered;
	private double mMaximumStackHeight; // in inches, not including pallet
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
			double skidsRemaining = getNumberOfSkids() - currentSkid.getSkidNumber();
			long millisPerSkid = Math.round(currentSkid.calculateMinutesPerSkid(productsPerMinute) * HelperFunction.ONE_MINUTE_IN_MILLIS);
			long millisRemaining = (long) (skidsRemaining * millisPerSkid);
			mFinishDate = new Date(currentFinishTime.getTime() + millisRemaining);
		}
		return mFinishDate;
	}
	
	/*
	 * Appends a skid to the end of the list, changing its skid number.. If the skid is null, set start time equal to the last skid's finish time (now if none)
	 * and set the skid's sheet count equal to the last one TODO
	 * Return the skid.
	 */
	public Skid<Product> addOrUpdateSkid (Skid<Product> newSkid) {
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
			Log.v("WorkOrder.class", "Just added skid #" + String.valueOf(newSkid.getSkidNumber())); 
			newSkid.setSkidNumber(mSkidsList.size());	
		}		
		return newSkid;
	}
	
	/*
	 * Removes the last skid.
	 * Returns its skid number.
	 */
	public int removeLastSkid() {
		int skidNumber = mSkidsList.size();
		mSkidsList.remove(mSkidsList.size()-1);
		Log.v("WorkOrder.class", "deleting skid" + String.valueOf(skidNumber));
		return skidNumber;
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
	public double getNumberOfSkids() {
		if (mSkidsList.size() < 2 ) return mSkidsList.size();
		Skid<Product> penultSkid = mSkidsList.get(mSkidsList.size() - 2);
		double finalSkidRatio = getLastSkidSheetCount() / penultSkid.getTotalItems();
		if ((finalSkidRatio > .001) && (finalSkidRatio < 1)) { //ie, partial skid
			double totalSkids = mSkidsList.size() - 1 + finalSkidRatio;
			Log.v("WorkOrder.class", "returning number of skids: " + String.valueOf(totalSkids));
			return totalSkids;
		} else return mSkidsList.size();  
	}
	
	public double getLastSkidSheetCount() {
		if (mSkidsList.size() == 0) return 0;
		else return mSkidsList.get(mSkidsList.size() - 1).getTotalItems();
	}

	public Skid<Product> getSelectedSkid() {
		return mSkidsList.get(mSelectedSkidPosition);
	}

	public Skid<Product> selectSkid(Integer skidNumber) {
		if ( (skidNumber > Math.ceil(getNumberOfSkids())) || !(skidNumber > 0) ) {
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
	
	@Override
	public String toString() {
		String finish = (mFinishDate == null) ? "n/a" : mFinishDate.toString();
		return "W0" + mWoNumber + ": selected skid " + String.valueOf(mSelectedSkidPosition + 1) + " of " + 
				String.valueOf(getNumberOfSkids()) + ", finish time " + finish;
	}

	public void updateFutureSheetCounts(int totalCount) {
		Skid<Product> currentSkid = mSkidsList.get(mSelectedSkidPosition);
		double finalSkidRatio = getLastSkidSheetCount() / currentSkid.getTotalItems();
		if (finalSkidRatio > .99) {
			mSkidsList.get(mSkidsList.size() - 1).setTotalItems(totalCount);
		} else { //partial skid, set the sheet count to be the same fraction of the new sheet count
			mSkidsList.get(mSkidsList.size() - 1).setTotalItems((int)Math.round(finalSkidRatio * totalCount));
		}
		//update the rest of the skids
		for (int i = mSelectedSkidPosition; i < mSkidsList.size() - 1; i++) {
				mSkidsList.get(i).setTotalItems(totalCount);
		}
	}
}

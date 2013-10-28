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
			//When you're on skid #3 of 2.5, there are 0 skids left after this one.
			double skidsAfterThisOne = Math.max(0, getNumberOfSkids() - currentSkid.getSkidNumber()); 
			long millisPerSkid = Math.round(currentSkid.calculateMinutesPerSkid(productsPerMinute) * HelperFunction.ONE_MINUTE_IN_MILLIS);
			for (int i = 1, n = (int) skidsAfterThisOne + 1; i < n; i++) {
			    Skid<Product> futureSkid = mSkidsList.get(mSelectedSkidPosition + i);
			    Date futureFinishTime = new Date(currentFinishTime.getTime() + (i * millisPerSkid));
			    futureSkid.setFinishTime(futureFinishTime);
			}
			long millisRemaining = (long) (skidsAfterThisOne * millisPerSkid);
			mFinishDate = new Date(currentFinishTime.getTime() + millisRemaining);
			Skid<Product> finalSkid = mSkidsList.get(mSkidsList.size() - 1);
			finalSkid.setFinishTime(mFinishDate); //TODO because partial skids don't get set by the loop above
		}
		return mFinishDate;
	}
	
	/*
	 * Appends a skid to the end of the list, changing its skid number.
	 * If the skid is the first one added, select the skid to avoid having no selection.
	 * If the skid is null, set start time equal to the last skid's finish time (now if none)
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
//			Log.v("WorkOrder.class", "Just added skid #" + String.valueOf(newSkid.getSkidNumber())); 
			newSkid.setSkidNumber(mSkidsList.size());	
		}		
		if (mSkidsList.size() == 1) selectSkid(newSkid.getSkidNumber());
		return newSkid;
	}
	
	/*
	 * Removes the last skid.
	 * Returns its skid number.
	 */
	public int removeLastSkid() {
		int removedSkidNumber = mSkidsList.size();
		mSkidsList.remove(mSkidsList.size()-1);
		if (mSelectedSkidPosition == mSkidsList.size()) selectSkid(mSkidsList.size());
//		Log.v("WorkOrder.class", "deleting skid" + String.valueOf(skidNumber));
		return removedSkidNumber;
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
		double finalSkidRatio = Double.valueOf(getLastSkidSheetCount()) / Double.valueOf(penultSkid.getTotalItems());
		if ((finalSkidRatio > HelperFunction.EPSILON) && (finalSkidRatio < 1)) { //ie, partial skid
			double totalSkids = mSkidsList.size() - 1 + finalSkidRatio;
			return totalSkids;
		} else return mSkidsList.size();  
	}
	
	public int getLastSkidSheetCount() {
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

	public void updateSkidsList (int startingSkidNumber, int totalCount, double totalSkids) {
	    //Make sure there are skids up to and including startingSkidNumber
	    while (mSkidsList.size() < startingSkidNumber) {
	        addOrUpdateSkid(new Skid<Product>(totalCount, getProduct()));
	    }
	    
	    //Update or add skids till we reach the total skid count
	    int totalWholeSkids = (int)Math.floor(totalSkids);
	    for (int i = startingSkidNumber; i <= totalWholeSkids; i++) {
            Skid<Product> newSkid = new Skid<Product>(totalCount, getProduct());
            newSkid.setSkidNumber(i);
            addOrUpdateSkid(newSkid);
        }

	    //Check if the totalSkids argument included a fractional skid
	    int finalNumSkids = totalWholeSkids;
	    if ((totalSkids - Math.floor(totalSkids)) > HelperFunction.EPSILON) {
	        finalNumSkids++;
	        int fractionalSheetCount = (int)(Double.valueOf(totalCount) * (totalSkids % 1));
	        Skid<Product> newSkid = new Skid<Product> (fractionalSheetCount, getProduct());
	        newSkid.setSkidNumber((int)Math.ceil(totalSkids));
	        addOrUpdateSkid(newSkid);
	    }
	    
	    //Remove any extra skids
	    while (mSkidsList.size() > finalNumSkids) {
	        removeLastSkid();
	    }
	}
	
    @Override
    public String toString() {
        String finish = (mFinishDate == null) ? "n/a" : mFinishDate.toString();
        return "W0 " + mWoNumber + ": selected skid " + String.valueOf(mSelectedSkidPosition + 1) + " of " + 
                String.valueOf(getNumberOfSkids()) + ", finish time " + finish;
    }

}

package com.kovaciny.primexmodel;

import java.util.ArrayList;
import java.util.List;

public class WorkOrder {
	private int woNumber;
	private List<Skid<Product>> mSkidsList;
	private Skid<Product> mSelectedSkid;
	private Product mProduct = null;
	private double mTotalProductsOrdered;
	private double mMaximumStackHeight; // in inches, not including pallet
	private int mNumberOfSkids;
	
	static final double MAX_STACK_HEIGHT_OLEFINS = 24d;
	static final double MAX_STACK_HEIGHT_STYRENE = 30d;
	static final double MAX_WO_NUMBER = 999999d;
	
	public WorkOrder(int woNumber){
		setWoNumber(woNumber);
		mSkidsList = new ArrayList<Skid<Product>>();
	}
	
	public void setWoNumber(int woNumber) {
		if ((woNumber < 0) || (woNumber > MAX_WO_NUMBER)) {
			throw new IllegalArgumentException("invalid Work Order number");
		}
				this.woNumber = woNumber;
	}	
	
	public void addSkid (Skid<Product> skid) {
		mSkidsList.add(skid);
	}
	
	public boolean hasProduct() {
		return (mProduct == null) ? false : true;
	}
	
	public boolean hasSelectedSkid() {
		return (mSelectedSkid == null) ? false : true;
	}
		
	/*
	 * Boilerplate from here on down... if I remember to move any functions I update.
	 */
	
	public int getWoNumber() {
		return woNumber;
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
		return mNumberOfSkids;
	}

	public void setNumberOfSkids(int skids) {
		if (skids < 0) throw new RuntimeException ("Negative number of skids");
		this.mNumberOfSkids = skids;
	}

	public Skid<Product> getSelectedSkid() {
		return mSelectedSkid;
	}

	public void setSelectedSkid(Skid<Product> skid) {
		this.mSelectedSkid = skid;
	}
	
	public void setProduct(Product product) {
		this.mProduct = product;
	}
	public Product getProduct() {
		return mProduct;
	}	
	
}

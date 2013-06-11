package com.kovaciny.primexmodel;

import java.util.ArrayList;
import java.util.List;

public class WorkOrder {
	private int woNumber;
	private List<Skid<Product>> mSkidsList;
	private Skid<Product> mSelectedSkid;
	private int productsListPointer; //TODO this seems rubbish.
	
	public WorkOrder(int woNumber, int productsListPointer){
		setWoNumber(woNumber);
		setProductsListPointer(productsListPointer);
		mSkidsList = new ArrayList<Skid<Product>>();
	}
	
	public void setWoNumber(int woNumber) {
		if ((woNumber < 0)) {
			throw new IllegalArgumentException("invalid Work Order number");
		}
				this.woNumber = woNumber;
	}	
	
	public void addSkid (Skid<Product> skid) {
		mSkidsList.add(skid);
	}
	
	/*
	 * Boilerplate from here on down... if I remember to move any functions I update.
	 */
	
	public int getProductsListPointer() {
		return productsListPointer;
	}
	
	public void setProductsListPointer(int productsListPointer) {
		this.productsListPointer = productsListPointer;
	}
	
	public int getWoNumber() {
		return woNumber;
	}
	
	public List<Skid<Product>> getProductsList() {
		return mSkidsList;
	}
	
}

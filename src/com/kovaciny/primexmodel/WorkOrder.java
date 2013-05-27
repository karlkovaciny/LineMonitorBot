package com.kovaciny.primexmodel;

public class WorkOrder {
	private int woNumber;
	private Product[] productsList;
	private int productsListPointer; //TODO this seems rubbish.
	
	public WorkOrder(int woNumber, int productsListPointer){
		setWoNumber(woNumber);
		setProductsListPointer(productsListPointer);
	}
	
	public void setWoNumber(int woNumber) {
		if ((woNumber < 0)) {
			throw new IllegalArgumentException("invalid Work Order number");
		}
				this.woNumber = woNumber;
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
	
	public Product[] getProductsList() {
		return productsList;
	}
	
	public void setProductsList(Product[] productsList) {
		this.productsList = productsList;
	}
}

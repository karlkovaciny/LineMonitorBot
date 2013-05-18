package com.kovaciny.primexmodel;

public class WorkOrder {
	private int woNumber;
	private Product[] productsList;
	private int productsListPointer;
	
	public WorkOrder(int woNumber, int productsListPointer){
		setWoNumber(woNumber);
		setProductsListPointer(productsListPointer);
	}
	/**
	 * @return the productsListPointer
	 */
	public int getProductsListPointer() {
		return productsListPointer;
	}
	/**
	 * @param productsListPointer the productsListPointer to set
	 */
	public void setProductsListPointer(int productsListPointer) {
		this.productsListPointer = productsListPointer;
	}
	/**
	 * @return the woNumber
	 */
	public int getWoNumber() {
		return woNumber;
	}
	/**
	 * @param woNumber the woNumber to set
	 */
	public void setWoNumber(int woNumber) {
		if ((woNumber < 0)) {
			throw new IllegalArgumentException("invalid Work Order number");
		}
				this.woNumber = woNumber;
	}
	/**
	 * @return the productsList
	 */
	public Product[] getProductsList() {
		return productsList;
	}
	/**
	 * @param productsList the productsList to set
	 */
	public void setProductsList(Product[] productsList) {
		this.productsList = productsList;
	}
	
}

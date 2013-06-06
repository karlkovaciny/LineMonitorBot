package com.kovaciny.primexmodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.NoSuchElementException;

import android.content.Context;
import android.util.Log;

import com.kovaciny.database.PrimexDatabaseSchema;
import com.kovaciny.database.PrimexSQLiteOpenHelper;


public class PrimexModel {
	
	public PrimexModel(Context context) {
		mDbHelper = new PrimexSQLiteOpenHelper(context);
		mDbHelper.getWritableDatabase();
		mLineNumbersList = mDbHelper.getLineNumbers();
		if (mLineNumbersList.size() == 0) {
			throw new RuntimeException("database didn't find any lines");
		}
		mSelectedLine = mDbHelper.getLine(mLineNumbersList.get(0));
		mWoNumbersList = mDbHelper.getWoNumbers();
	}
	/*
	 * This section sets up notifying observers about changes.
	 */
	public static final String LINE_SPEED_CHANGE_EVENT = "PrimexModel.SPEED_CHANGE";
	public static final String SELECTED_LINE_CHANGE_EVENT = "PrimexModel.LINE_CHANGE";
	public static final String SELECTED_WO_CHANGE_EVENT = "PrimexModel.WO_CHANGE";
	public static final String NEW_WORK_ORDER_CHANGE_EVENT = "PrimexModel.NEW_WORK_ORDER"; 
	public static final String PRODUCT_CHANGE_EVENT = "PrimexModel.NEW_PRODUCT"; 
	public static final String PRODUCTS_PER_MINUTE_CHANGE_EVENT = "PrimexModel.PPM_CHANGE"; 
	
	public static final double INCHES_PER_FOOT = 12.0; 
		
	// Create PropertyChangeSupport to manage listeners and fire events.
	private final PropertyChangeSupport propChangeSupport = new PropertyChangeSupport(this);
	  
	// Provide delegating methods to add / remove listeners to / from the support class.  
	public void addPropertyChangeListener(PropertyChangeListener l) {
	    propChangeSupport.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
	    propChangeSupport.removePropertyChangeListener(l);
	}

	/*
	 * This section holds the different objects and their relation to each other, getters and setters.
	 */
	
	private List<Integer> mLineNumbersList;
	private List<Integer> mWoNumbersList;
	private ProductionLine mSelectedLine;
	private WorkOrder mSelectedWorkOrder;
	private List<Skid> skidsList;
	private Skid mSelectedSkid;
	private Product mSelectedProduct;
	private PrimexSQLiteOpenHelper mDbHelper;
	private double mProductsPerMinute;
	private double mNetRate;
	private double mGrossRate;
	
	/*
	 * also selects product currently
	 */
	public void setSelectedLine (Integer lineNumber) {
		Integer oldLine = -1;
		if (mLineNumbersList.contains(lineNumber)) {
			if (mSelectedLine != null) {
				oldLine = mSelectedLine.getLineNumber();
			}
			mSelectedLine = mDbHelper.getLine(lineNumber);
			Integer newLine = mSelectedLine.getLineNumber();
			
			setSelectedProductByLineNumber(lineNumber);
			propChangeSupport.firePropertyChange(SELECTED_LINE_CHANGE_EVENT, oldLine, newLine);
		
		} else if (lineNumber == null) {
			mSelectedLine = null;
			propChangeSupport.firePropertyChange(SELECTED_LINE_CHANGE_EVENT, oldLine, null);
		} else throw new NoSuchElementException("Line number not in the list of lines");
	}
	
	public void setSelectedWorkOrder(Integer woNumber) {
		Integer oldSelection = -1;
		if (mWoNumbersList.contains(woNumber)) {
			if (mSelectedWorkOrder != null) {
				oldSelection = mSelectedWorkOrder.getWoNumber();
			}
			mSelectedWorkOrder = mDbHelper.getWorkOrder(woNumber);
			Integer newSelection = mSelectedWorkOrder.getWoNumber();
			propChangeSupport.firePropertyChange(SELECTED_WO_CHANGE_EVENT, oldSelection, newSelection);
		} else if (woNumber == null) {
			mSelectedWorkOrder = null;
			propChangeSupport.firePropertyChange(SELECTED_WO_CHANGE_EVENT, oldSelection, null);
		} else throw new NoSuchElementException("Work order number not in the list of work orders");
	}

	public boolean addWorkOrder(WorkOrder newWo) {
		if (mDbHelper.addWorkOrder(newWo) != -1l) {
			mWoNumbersList.add(newWo.getWoNumber());
			propChangeSupport.firePropertyChange(NEW_WORK_ORDER_CHANGE_EVENT, null, newWo);
			return true;
		} else return false;
	}
	
	public void setCurrentLineSpeedSetpoint(double setpoint){
		if (setpoint < 0) {throw new IllegalArgumentException("Line speed setpoint less than zero");}
		
		double oldsetpoint = mSelectedLine.getLineSpeedSetpoint();
		mSelectedLine.setLineSpeedSetpoint(setpoint);
		mDbHelper.updateColumn(PrimexDatabaseSchema.ProductionLines.TABLE_NAME,
				PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_SPEED_SETPOINT,
				PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LINE_NUMBER + "=?",
				new String[]{String.valueOf(mSelectedLine.getLineNumber())},
				String.valueOf(setpoint));
		propChangeSupport.firePropertyChange(LINE_SPEED_CHANGE_EVENT, oldsetpoint, setpoint);
	}
	
	public void setCurrentProductLength(double length) {
		double oldLength = mSelectedLine.getProduct().getLength();
		mSelectedLine.getProduct().setLength(length);
		//TODO? propChangeSupport.firePropertyChange(PRODUCT_LENGTH_CHANGE_EVENT, oldLength, length);
	}
	
	public void setCurrentSpeedFactor (double speedFactor) {
		double oldFactor = mSelectedLine.getSpeedFactor();
		mSelectedLine.setSpeedFactor(speedFactor);
		//TODO propChange
	}
 
	public void setSelectedProduct(String type, double gauge, double width, double length) {
		//this function creates a new product of the specified dimensions and type. Then it updates the
		//database with that product and the current line number. blah blah debug
		Log.v("verbose", "setSelectedProduct is running");
		Product oldProduct = mSelectedProduct;
		if (type.equals(Product.SHEETS_TYPE)) {
			mSelectedProduct = new Sheet(gauge, width, length);
		} else if (type.equals(Product.ROLLS_TYPE)) {
			mSelectedProduct = new Roll(gauge, width, 0);
		} else throw new IllegalArgumentException("not a valid product type");
		Product newProduct = mSelectedProduct;
		mDbHelper.insertOrReplaceProduct(newProduct, getSelectedLine().getLineNumber());
		mSelectedLine.setProduct(newProduct);
		mSelectedProduct.setLineNumber(mSelectedLine.getLineNumber());
		calculateProductsPerMinute();
		this.propChangeSupport.firePropertyChange(PRODUCT_CHANGE_EVENT, oldProduct, newProduct);
	}
	
	public void setSelectedProduct(Product p) {
		if (p == null) {
			mSelectedProduct = null;
		} else {
			setSelectedProduct(p.getType(), p.getGauge(), p.getWidth(), p.getLength());
		}
	}
	
	/*
	 * This function is called whenever relevant properties change.
	 */
	protected void calculateProductsPerMinute() {
		setProductsPerMinute(INCHES_PER_FOOT / mSelectedProduct.getLength() * mSelectedLine.getLineSpeed());
	}
	
	public void setProductsPerMinute(double spm) {
		double oldSpm = mProductsPerMinute;
		mProductsPerMinute = spm; //TODO validity checking
		calculateRates();
		propChangeSupport.firePropertyChange(PRODUCTS_PER_MINUTE_CHANGE_EVENT, oldSpm, spm);
	}
	
	public double getProductsPerMinute() {
		return mProductsPerMinute;
	}
	
	/*
	 * This function is called whenever relevant properties change.
	 */
	protected void calculateRates() {
		if (hasSelectedProduct()) {
			mNetRate = mProductsPerMinute * mSelectedProduct.getWeight();
			//TODO gross rate
		}
		
	}
	
	public void setSelectedProductByLineNumber(int lineNumber) {
		Product newProduct = mDbHelper.getProduct(lineNumber);
		setSelectedProduct(newProduct);		
	}
	
	public Product getSelectedProduct() {
		return mSelectedProduct;
	}
	
	public void closeDb() {
		mDbHelper.close();
	}
	public double getCurrentLineSpeedSetpoint(){
		return mSelectedLine.getLineSpeedSetpoint();
	}
	public boolean hasSelectedLine() {
		if (mSelectedLine != null) {
			return true;
		} else return false;
	}
	public boolean hasSelectedWorkOrder(){
		if (mSelectedWorkOrder != null) {
			return true;
		} else return false;		
	}
	public boolean hasSelectedProduct() {
		if (mSelectedProduct != null) {
			return true;
		} else return false;
	}
	public ProductionLine getSelectedLine() {
		return mSelectedLine;
	}
	
	public WorkOrder getSelectedWorkOrder() {
		return mSelectedWorkOrder;
	}
	
	public List<Integer> getLineNumbers() {
		return mLineNumbersList;
	}
	
	public List<Integer> getWoNumbers() {
		return mDbHelper.getWoNumbers();
	}
	
	public int getHighestWoNumber() {
		return mDbHelper.getHighestWoNumber();
	}
	
	public void clearWoNumbers() {
		mDbHelper.clearWoNumbers();
		setSelectedWorkOrder(null);
	}
}

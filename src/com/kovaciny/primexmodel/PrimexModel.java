package com.kovaciny.primexmodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import android.content.Context;
import android.util.Log;

import com.kovaciny.database.PrimexDatabaseSchema;
import com.kovaciny.database.PrimexSQLiteOpenHelper;
import com.kovaciny.helperfunctions.HelperFunction;


public class PrimexModel {
	
	public PrimexModel(Context context) {
		mDbHelper = new PrimexSQLiteOpenHelper(context);
		mDbHelper.getWritableDatabase();
		mLineNumbersList = mDbHelper.getLineNumbers();
		if (mLineNumbersList.size() == 0) {
			throw new RuntimeException("database didn't find any lines");
		}
		mSelectedSkid = new Skid<Sheet>(); //TODO
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
	public static final String CURRENT_SHEET_COUNT_CHANGE_EVENT = "PrimexModel.SHEET_COUNT_CHANGE"; 
	public static final String TOTAL_SHEET_COUNT_CHANGE_EVENT = "PrimexModel.TOTAL_COUNT_CHANGE";
	public static final String SKID_FINISH_TIME_CHANGE_EVENT = "PrimexModel.FINISH_TIME_CHANGE"; 
	public static final String SKID_START_TIME_CHANGE_EVENT = "PrimexModel.START_TIME_CHANGE"; 
	public static final String MINUTES_PER_SKID_CHANGE_EVENT = "PrimexModel.SKID_TIME_CHANGE"; 
	 
	
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
	private Double mProductsPerMinute;
	private double mNetRate;
	private double mGrossRate;
	private long mMinutesPerSkid;
	
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
			
			setSelectedProductByWoNumber(woNumber);
			
			propChangeSupport.firePropertyChange(SELECTED_WO_CHANGE_EVENT, oldSelection, newSelection);
		} else if (woNumber == null) {
			mSelectedWorkOrder = null;
			propChangeSupport.firePropertyChange(SELECTED_WO_CHANGE_EVENT, oldSelection, null);
		} else throw new NoSuchElementException("Work order number not in the list of work orders, need to add it");
	}

	public boolean addWorkOrder(WorkOrder newWo) {
		if (mDbHelper.addWorkOrder(newWo) != -1l) {
			mWoNumbersList.add(newWo.getWoNumber());
			propChangeSupport.firePropertyChange(NEW_WORK_ORDER_CHANGE_EVENT, null, newWo);
			return true;
		} else return false;
	}
		
	public void setCurrentProductLength(double length) {
		double oldLength = mSelectedWorkOrder.getProduct().getLength();
		mSelectedWorkOrder.getProduct().setLength(length);
		//TODO? propChangeSupport.firePropertyChange(PRODUCT_LENGTH_CHANGE_EVENT, oldLength, length);
	}
	
	public void setCurrentSpeed (SpeedValues values) { 
		SpeedValues oldValues = mSelectedLine.getSpeedValues();
		mSelectedLine.setSpeedValues(values);
		String[] lineNum = new String[]{String.valueOf(mSelectedLine.getLineNumber())};
		mDbHelper.updateColumn(PrimexDatabaseSchema.ProductionLines.TABLE_NAME,
				PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_SPEED_SETPOINT,
				PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LINE_NUMBER + "=?",
				lineNum,
				String.valueOf(values.lineSpeedSetpoint));
		mDbHelper.updateColumn(PrimexDatabaseSchema.ProductionLines.TABLE_NAME,
				PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_DIFFERENTIAL_SPEED_SETPOINT,
				PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LINE_NUMBER + "=?",
				lineNum,
				String.valueOf(values.differentialSpeed));
		mDbHelper.updateColumn(PrimexDatabaseSchema.ProductionLines.TABLE_NAME,
				PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_SPEED_FACTOR,
				PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LINE_NUMBER + "=?",
				lineNum,
				String.valueOf(values.speedFactor));
		//TODO don't use three calls
		propChangeSupport.firePropertyChange(LINE_SPEED_CHANGE_EVENT, oldValues, values);
	}
 
	public void setSelectedProduct(String type, double gauge, double width, double length) {
		//this function creates a new product of the specified dimensions and type. Then it updates the
		//database with that product and the current line number. blah blah debug TODO
		Product oldProduct = mSelectedProduct;
		if (type.equals(Product.SHEETS_TYPE)) {
			mSelectedProduct = new Sheet(gauge, width, length);
		} else if (type.equals(Product.ROLLS_TYPE)) {
			mSelectedProduct = new Roll(gauge, width, 0);
		} else throw new IllegalArgumentException("not a valid product type");
		Product newProduct = mSelectedProduct;
		mDbHelper.insertOrReplaceProduct(newProduct, getSelectedWorkOrder().getWoNumber());
		mSelectedWorkOrder.setProduct(newProduct);
		calculateRates();
		calculateTimes();
		this.propChangeSupport.firePropertyChange(PRODUCT_CHANGE_EVENT, oldProduct, newProduct);
	}
	
	public void setSelectedProduct(Product p) {
		if (p == null) {
			mSelectedProduct = null;
		} else {
			setSelectedProduct(p.getType(), p.getGauge(), p.getWidth(), p.getLength());
		}
	}
		
	public void setSelectedProductByWoNumber(int woNumber) {
		Product newProduct = mDbHelper.getProduct(woNumber);
		setSelectedProduct(newProduct);	
	}
	
	public void setProductsPerMinute(double spm) {
		Double oldSpm = mProductsPerMinute;
		mProductsPerMinute = spm; //TODO this function shouldnt' be allowed! also validity checking. Also this maybe should be for direct setting only?
		calculateRates();
		calculateTimes();
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
			Double oldPpm = mProductsPerMinute; 
			mProductsPerMinute = INCHES_PER_FOOT / mSelectedProduct.getLength() * mSelectedLine.getLineSpeed();
			propChangeSupport.firePropertyChange(PRODUCTS_PER_MINUTE_CHANGE_EVENT, oldPpm, mProductsPerMinute);
			
			mNetRate = mProductsPerMinute * mSelectedProduct.getWeight();
			//TODO gross rate
		}		
	}
	
	public void setCurrentCount (Integer currentCount) {
		Integer oldCount = mSelectedSkid.getCurrentItems();
		mSelectedSkid.setCurrentItems(currentCount);
		calculateTimes();
		propChangeSupport.firePropertyChange(CURRENT_SHEET_COUNT_CHANGE_EVENT, oldCount, currentCount);
	}
	
	public void setTotalCount (Integer totalCount) {
		Integer oldCount = mSelectedSkid.getTotalItems();
		mSelectedSkid.setTotalItems(totalCount);
		calculateTimes();
		propChangeSupport.firePropertyChange(TOTAL_SHEET_COUNT_CHANGE_EVENT, oldCount, totalCount);
	}
	
	public void calculateTimes() {
		if (mSelectedSkid == null) throw new RuntimeException("Can't calc times without a skid");
		Integer totalItems = mSelectedSkid.getTotalItems();
		Integer currentItems = mSelectedSkid.getCurrentItems();
		if ( (totalItems != null) && (mProductsPerMinute != null) && (mProductsPerMinute != 0)) {
			//calculate total time per skid. 
			long oldMinutes = mMinutesPerSkid;
			mMinutesPerSkid = Math.round(totalItems / mProductsPerMinute);
			propChangeSupport.firePropertyChange(MINUTES_PER_SKID_CHANGE_EVENT, oldMinutes, mMinutesPerSkid);
			
			if (currentItems != null) { 
				//calculate skid start and finish time
				double minutesLeft = (totalItems - currentItems ) / mProductsPerMinute;
				double minutesElapsed = currentItems / mProductsPerMinute;
				long millisLeft = (long) (minutesLeft * HelperFunction.ONE_MINUTE_IN_MILLIS);
				long millisElapsed = (long) (minutesElapsed * HelperFunction.ONE_MINUTE_IN_MILLIS);
				Date currentDate = new Date();
				long t = currentDate.getTime();
				Date oldFinishTime = mSelectedSkid.getFinishTime();
				Date oldStartTime = mSelectedSkid.getStartTime();
				Date finishTime = new Date( t + (millisLeft));
				Date startTime = new Date( t - (millisElapsed));
				mSelectedSkid.setFinishTime( finishTime );
				mSelectedSkid.setStartTime( startTime );
				propChangeSupport.firePropertyChange(SKID_FINISH_TIME_CHANGE_EVENT, oldFinishTime, mSelectedSkid.getFinishTime());	
				propChangeSupport.firePropertyChange(SKID_START_TIME_CHANGE_EVENT, oldStartTime, mSelectedSkid.getStartTime());	
			}			
		}
		//TODO calc job finish times
	}

	public int getDatabaseVersion() {
		return mDbHelper.DATABASE_VERSION;
	}
	public Product getSelectedProduct() {
		return mSelectedProduct;
	}
	
	public void closeDb() {
		mDbHelper.close();
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

package com.kovaciny.primexmodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import android.content.Context;

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
	public static final String NUMBER_OF_SKIDS_CHANGE_EVENT = "PrimexModel.NUMBER_OF_SKIDS_CHANGE"; 
	 
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
	private List<Skid<? extends Product>> skidsList;
	private Skid<? extends Product> mSelectedSkid;
	private PrimexSQLiteOpenHelper mDbHelper;
	private Double mProductsPerMinute;
	private double mNetRate;
	private double mGrossRate;
	private long mMinutesPerSkid;
	private int mSkidsInJob;
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
			
			WorkOrder lookedUpWo = mDbHelper.getWorkOrder(woNumber);
			if (lookedUpWo == null) throw new RuntimeException("WorkOrder not found even though it is in woNumbersList");
			
			mSelectedWorkOrder = lookedUpWo;
			Integer newSelection = mSelectedWorkOrder.getWoNumber();
			
			Product p = mDbHelper.getProduct(woNumber);
			mSelectedWorkOrder.setProduct(p);
			
			propChangeSupport.firePropertyChange(SELECTED_WO_CHANGE_EVENT, oldSelection, newSelection);
		} else if (woNumber == null) {
			mSelectedWorkOrder = null;
			propChangeSupport.firePropertyChange(SELECTED_WO_CHANGE_EVENT, oldSelection, null);
		} else throw new NoSuchElementException("Work order number not in the list of work orders, need to add it");
	}
//TODO make it so there's not an "add product, add pallet, etc"
//imagine a delete work order, a list of work orders and you are at one index of it
//don't have a selected skid, it's just your place in the list of skids.
//
	protected void addProduct(Product p) {
		mDbHelper.insertOrReplaceProduct(p, mSelectedWorkOrder.getWoNumber());
	}
	
	public boolean addWorkOrder(WorkOrder newWo) {
		if (mDbHelper.addWorkOrder(newWo) != -1l) {
			mWoNumbersList.add(newWo.getWoNumber());
			propChangeSupport.firePropertyChange(NEW_WORK_ORDER_CHANGE_EVENT, null, newWo);
			return true;
		} else return false;
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
 
	public void changeProduct (Product p) {
		Product oldProduct = mSelectedWorkOrder.getProduct();
		mSelectedWorkOrder.setProduct(p);
		addProduct(p);
		calculateRates();
		calculateTimes();
		this.propChangeSupport.firePropertyChange(PRODUCT_CHANGE_EVENT, oldProduct, p);
	}
	
	public void saveProduct(Product p) {
		mDbHelper.insertOrReplaceProduct(p, getSelectedWorkOrder().getWoNumber());
	}
	
	/*
	 * Saves the selected line number and work order number.
	 */
	public void saveState() {
		if (hasSelectedLine()) {
			mDbHelper.updateColumn(PrimexDatabaseSchema.ModelState.TABLE_NAME, PrimexDatabaseSchema.ModelState.COLUMN_NAME_SELECTED_LINE, null, null, String.valueOf(getSelectedLine().getLineNumber()));
		} else {
			mDbHelper.updateColumn(PrimexDatabaseSchema.ModelState.TABLE_NAME, PrimexDatabaseSchema.ModelState.COLUMN_NAME_SELECTED_LINE, null, null, null);
		}
		
		if (hasSelectedWorkOrder()) {
			mDbHelper.updateColumn(PrimexDatabaseSchema.ModelState.TABLE_NAME, PrimexDatabaseSchema.ModelState.COLUMN_NAME_SELECTED_WORK_ORDER, null, null, String.valueOf(getSelectedWorkOrder().getWoNumber()));
		} else {
			mDbHelper.updateColumn(PrimexDatabaseSchema.ModelState.TABLE_NAME, PrimexDatabaseSchema.ModelState.COLUMN_NAME_SELECTED_WORK_ORDER, null, null, null);
		}
		
		if (hasSelectedProduct()){
			saveProduct(mSelectedWorkOrder.getProduct());
		}
	}

	public void loadState() {
		String lineNum = mDbHelper.getString(PrimexDatabaseSchema.ModelState.TABLE_NAME, PrimexDatabaseSchema.ModelState.COLUMN_NAME_SELECTED_LINE, null);
		String woNum = mDbHelper.getString(PrimexDatabaseSchema.ModelState.TABLE_NAME, PrimexDatabaseSchema.ModelState.COLUMN_NAME_SELECTED_WORK_ORDER, null);
		try {
			if ( (lineNum == null) || (woNum == null) ) {
				throw new IllegalStateException("either line number or WO number is null");
			}
		} catch (IllegalStateException e) { 
			setSelectedLine(18);
			addWorkOrder(new WorkOrder(18));
			setSelectedWorkOrder(18);
		}
		
		setSelectedLine(Integer.valueOf(lineNum));
		Integer woNumber = Integer.valueOf(woNum); 
		addWorkOrder(new WorkOrder(woNumber));
		setSelectedWorkOrder(woNumber);
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
			mProductsPerMinute = INCHES_PER_FOOT / mSelectedWorkOrder.getProduct().getLength() * mSelectedLine.getLineSpeed();
			propChangeSupport.firePropertyChange(PRODUCTS_PER_MINUTE_CHANGE_EVENT, oldPpm, mProductsPerMinute);
			
			mNetRate = mProductsPerMinute * mSelectedWorkOrder.getProduct().getWeight();
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
		return PrimexSQLiteOpenHelper.DATABASE_VERSION;
	}
	public void setNumSkidsDebug(int num) {
		int oldNum = mSkidsInJob;
		mSkidsInJob = num;
		propChangeSupport.firePropertyChange(NUMBER_OF_SKIDS_CHANGE_EVENT, oldNum, num);
		
	}
	public int getNumSkidsDebug() {
		return mSkidsInJob;
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
		return mSelectedWorkOrder.hasProduct();
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

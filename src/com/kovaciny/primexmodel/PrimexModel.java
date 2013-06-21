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
		mLineNumbersList = mDbHelper.getLineNumbers();
		if (mLineNumbersList.size() == 0) {
			throw new RuntimeException("database didn't find any lines");
		}
		mWoNumbersList = mDbHelper.getWoNumbers();
	}
	/*
	 * This section sets up notifying observers about changes.
	 */
	public static final String LINE_SPEED_CHANGE_EVENT = "PrimexModel.SPEED_CHANGE";
	public static final String SELECTED_LINE_CHANGE_EVENT = "PrimexModel.LINE_CHANGE";
	public static final String SELECTED_WO_CHANGE_EVENT = "PrimexModel.WO_CHANGE";
	public static final String NEW_WORK_ORDER_EVENT = "PrimexModel.NEW_WORK_ORDER"; 
	public static final String PRODUCT_CHANGE_EVENT = "PrimexModel.NEW_PRODUCT"; 
	public static final String PRODUCTS_PER_MINUTE_CHANGE_EVENT = "PrimexModel.PPM_CHANGE"; 
	public static final String CURRENT_SHEET_COUNT_CHANGE_EVENT = "PrimexModel.SHEET_COUNT_CHANGE"; 
	public static final String TOTAL_SHEET_COUNT_CHANGE_EVENT = "PrimexModel.TOTAL_COUNT_CHANGE";
	public static final String CURRENT_SKID_FINISH_TIME_CHANGE_EVENT = "PrimexModel.FINISH_TIME_CHANGE"; 
	public static final String CURRENT_SKID_START_TIME_CHANGE_EVENT = "PrimexModel.START_TIME_CHANGE"; 
	public static final String MINUTES_PER_SKID_CHANGE_EVENT = "PrimexModel.SKID_TIME_CHANGE"; 
	public static final String NUMBER_OF_SKIDS_CHANGE_EVENT = "PrimexModel.NUMBER_OF_SKIDS_CHANGE"; 
	public static final String JOB_FINISH_TIME_CHANGE_EVENT = "PrimexModel.JOB_FINISH_TIME_CHANGE"; 
	public static final String SKID_CHANGE_EVENT = "PrimexModel.SKID_CHANGE"; 
	 
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
	private Skid<Product> mSelectedSkid;
	private PrimexSQLiteOpenHelper mDbHelper;
	private Double mProductsPerMinute;
	private double mNetRate;
	private double mGrossRate;
	private long mMinutesPerSkid;

	public void setSelectedLine (Integer lineNumber) {
		if (lineNumber == null) throw new NullPointerException("need to select a line");
		if (!mLineNumbersList.contains(lineNumber)) throw new NoSuchElementException("Line number not in the list of lines");;
		
		int oldLineNumber = hasSelectedLine() ? mSelectedLine.getLineNumber() : -1;
		if (lineNumber != oldLineNumber) {
			mSelectedLine = mDbHelper.getLine(lineNumber);
			int associatedWoNumber = mDbHelper.getWoNumberByLine(lineNumber);
			if (associatedWoNumber > 0) {
				setSelectedWorkOrder(associatedWoNumber);	
			} else { //make sure a work order is selected
				int newWoNumber = mDbHelper.getHighestWoNumber() + 1;
				addWorkOrder(new WorkOrder(newWoNumber));
				setSelectedWorkOrder(newWoNumber);
			}
			propChangeSupport.firePropertyChange(SELECTED_LINE_CHANGE_EVENT, oldLineNumber, mSelectedLine.getLineNumber());	
		}
	}
	
	public void setSelectedWorkOrder(int woNumber) {
		if (!mWoNumbersList.contains(woNumber)) {
			throw new NoSuchElementException("Work order number not in the list of work orders, need to add it");
		}
		if (woNumber > 0) {
			int oldWoNumber = (mSelectedWorkOrder == null) ? 0 : mSelectedWorkOrder.getWoNumber();
					
			WorkOrder lookedUpWo = mDbHelper.getWorkOrder(woNumber);
			if (lookedUpWo == null) throw new RuntimeException("WorkOrder not found even though it is in woNumbersList");
			
			mSelectedWorkOrder = lookedUpWo;
			mDbHelper.updateLineWorkOrderLink(mSelectedLine.getLineNumber(), woNumber);
			mSelectedSkid = mSelectedWorkOrder.getSelectedSkid();
			
			Product p = mDbHelper.getProduct(woNumber);
			mSelectedWorkOrder.setProduct(p);
		
			propChangeSupport.firePropertyChange(SELECTED_WO_CHANGE_EVENT, oldWoNumber, mSelectedWorkOrder.getWoNumber());
		} else throw new IllegalArgumentException("Work order number must be positive");
	}
//TODO make it so there's not an "add product, add pallet, etc"
//imagine a delete work order, a list of work orders and you are at one index of it
//don't have a selected skid, it's just your place in the list of skids.
//
	protected void addProduct(Product p) {
		mDbHelper.insertOrReplaceProduct(p, mSelectedWorkOrder.getWoNumber());
	}
	
	public boolean addWorkOrder(WorkOrder newWo) {
		if (mDbHelper.insertOrReplaceWorkOrder(newWo) != -1l) {
			mWoNumbersList.add(newWo.getWoNumber());
			propChangeSupport.firePropertyChange(NEW_WORK_ORDER_EVENT, null, newWo);
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

	public void changeSkid(Integer skidNumber) {
		//TODO this function fires twice in a row
		Skid<Product> oldSkid = mSelectedSkid;
		if ( skidNumber > oldSkid.getSkidNumber()) {
			mSelectedSkid = getSelectedWorkOrder().selectSkid(skidNumber);
		
			propChangeSupport.firePropertyChange(CURRENT_SHEET_COUNT_CHANGE_EVENT, oldSkid.getCurrentItems(), mSelectedSkid.getCurrentItems());
			setTotalCount(oldSkid.getTotalItems()); //TODO this looks like a job for a makeskid() function.
		} else {
			getSelectedWorkOrder().selectSkid(skidNumber);	
		}
		propChangeSupport.firePropertyChange(SKID_CHANGE_EVENT, oldSkid, mSelectedSkid);
		calculateTimes();		
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
		String lineNum = mDbHelper.getFieldAsString(PrimexDatabaseSchema.ModelState.TABLE_NAME, PrimexDatabaseSchema.ModelState.COLUMN_NAME_SELECTED_LINE, null);
		try {
			if ( (lineNum == null)) {
				throw new IllegalStateException("line number is null");
			}
		} catch (IllegalStateException e) { 
			setSelectedLine(18);
			addWorkOrder(new WorkOrder(18));
			setSelectedWorkOrder(18);
		}
		setSelectedLine(Integer.valueOf(lineNum));
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
		if ( (mProductsPerMinute > 0) && (mSelectedSkid.getTotalItems() > 0) ) {
			//calculate total time per skid. 
			long oldMinutes = mSelectedSkid.getMinutesPerSkid();
			long newMinutes = mSelectedSkid.calculateMinutesPerSkid(mProductsPerMinute);
			propChangeSupport.firePropertyChange(MINUTES_PER_SKID_CHANGE_EVENT, oldMinutes, newMinutes);
			
			//calculate skid start and finish time
			Date oldStartTime = mSelectedSkid.getStartTime();
			Date newStartTime = mSelectedSkid.calculateStartTime(mProductsPerMinute);
			Date oldFinishTime = mSelectedSkid.getFinishTime();
			Date newFinishTime = mSelectedSkid.calculateFinishTimeWhileRunning(mProductsPerMinute);				
			propChangeSupport.firePropertyChange(CURRENT_SKID_START_TIME_CHANGE_EVENT, oldStartTime, newStartTime);
			propChangeSupport.firePropertyChange(CURRENT_SKID_FINISH_TIME_CHANGE_EVENT, oldFinishTime, newFinishTime);
			
			//calculate job finish times
			Date oldJobFinishTime = mSelectedWorkOrder.getFinishTime();
			Date newJobFinishTime = mSelectedWorkOrder.calculateFinishTimes(mProductsPerMinute);
			propChangeSupport.firePropertyChange(JOB_FINISH_TIME_CHANGE_EVENT, oldJobFinishTime, newJobFinishTime);
		}		

	}

	public int getDatabaseVersion() {
		return PrimexSQLiteOpenHelper.DATABASE_VERSION;
	}
	public void changeNumberOfSkids(int num) {
		int oldNum = mSelectedWorkOrder.getNumberOfSkids();
		mSelectedWorkOrder.setNumberOfSkids(num);
		calculateTimes(); //TODO necessary?
		propChangeSupport.firePropertyChange(NUMBER_OF_SKIDS_CHANGE_EVENT, oldNum, mSelectedWorkOrder.getNumberOfSkids());		
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
		mDbHelper.clearWorkOrders();		
	}
}

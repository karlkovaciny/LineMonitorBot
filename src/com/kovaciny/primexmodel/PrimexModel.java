package com.kovaciny.primexmodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
	public static final String CURRENT_SKID_FINISH_TIME_CHANGE_EVENT = "PrimexModel.FINISH_TIME_CHANGE"; 
	public static final String CURRENT_SKID_START_TIME_CHANGE_EVENT = "PrimexModel.START_TIME_CHANGE"; 
	public static final String MINUTES_PER_SKID_CHANGE_EVENT = "PrimexModel.SKID_TIME_CHANGE"; 
	public static final String NUMBER_OF_SKIDS_CHANGE_EVENT = "PrimexModel.NUMBER_OF_SKIDS_CHANGE"; 
	public static final String JOB_FINISH_TIME_CHANGE_EVENT = "PrimexModel.JOB_FINISH_TIME_CHANGE"; 
	public static final String SKID_CHANGE_EVENT = "PrimexModel.SKID_CHANGE"; 
	public static final String TIME_TO_MAXSON_CHANGE_EVENT = "PrimexModel.TIME_TO_MAXSON_CHANGE"; 
	public static final String NET_PPH_CHANGE_EVENT = "PrimexModel.NET_PPH_CHANGE"; 
	public static final String GROSS_PPH_CHANGE_EVENT = "PrimexModel.GROSS_PPH_CHANGE"; 
	public static final String GROSS_WIDTH_CHANGE_EVENT = "PrimexModel.GROSS_WIDTH_CHANGE"; //TODO not fired 
	public static final String COLOR_PERCENT_CHANGE_EVENT = "PrimexModel.NOVATEC_LETDOWN_CHANGE"; 
	public static final String EDGE_TRIM_RATIO_CHANGE_EVENT = "PrimexModel.EDGE_TRIM_PERCENT_CHANGE"; 
	 
	public static final String ERROR_NET_LESS_THAN_GROSS = "PrimexModel.Net width less than gross width";
	public static final String ERROR_NO_PRODUCT_SELECTED = "PrimexModel.No product selected";
	public static final String ERROR_NO_SKID_SELECTED = "PrimexModel.No skid selected";
		
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
	private double mMillisToMaxson;
	private double mNetPph; 
	private double mGrossPph;
	private double mNetWidth;
	private double mGrossWidth;
	private double mEdgeTrimRatio;
	private double mColorPercent;

	public void setSelectedLine (Integer lineNumber) {
		if (lineNumber == null) throw new NullPointerException("need to select a line");
		if (!mLineNumbersList.contains(lineNumber)) throw new NoSuchElementException("Line number not in the list of lines");;
		
		int oldLineNumber = hasSelectedLine() ? mSelectedLine.getLineNumber() : -1;
		if (lineNumber != oldLineNumber) {
			mSelectedLine = mDbHelper.getLine(lineNumber);
			int associatedWoNumber = mDbHelper.getWoNumberByLine(lineNumber);
			if (associatedWoNumber > 0) {
				setSelectedWorkOrder(associatedWoNumber);
				propChangeSupport.firePropertyChange(NUMBER_OF_SKIDS_CHANGE_EVENT, null, mSelectedWorkOrder.getNumberOfSkids()); //TODO load the whole state not just this
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
			WorkOrder oldWo = mSelectedWorkOrder;
					
			WorkOrder lookedUpWo = mDbHelper.getWorkOrder(woNumber);
			if (lookedUpWo == null) throw new RuntimeException("WorkOrder not found even though it is in woNumbersList");
			mSelectedWorkOrder = lookedUpWo;
			mDbHelper.updateLineWorkOrderLink(mSelectedLine.getLineNumber(), woNumber);
			changeSelectedSkid(mSelectedWorkOrder.getSkidsList().get(0).getSkidNumber());
			
			Product p = mDbHelper.getProduct(woNumber);
			mSelectedWorkOrder.setProduct(p);
			
			Date finishDate = mSelectedWorkOrder.getFinishDate();
			if (finishDate != null) {
				propChangeSupport.firePropertyChange(JOB_FINISH_TIME_CHANGE_EVENT, null, finishDate);
			}
			propChangeSupport.firePropertyChange(SELECTED_WO_CHANGE_EVENT, oldWo, mSelectedWorkOrder);
		} else throw new IllegalArgumentException("Work order number must be positive");
	}

	/*
	 * Returns the number of the newly added skid.
	 */
	public int addSkid (int currentCount, int totalCount) {
		int oldNumSkids = getSelectedWorkOrder().getSkidsList().size();
		getSelectedWorkOrder().setNumberOfSkids(oldNumSkids + 1);
		Skid<Product> newSkid = new Skid<Product>(currentCount, 
				totalCount,
				1);
		int newSkidNum = getSelectedWorkOrder().addOrUpdateSkid(newSkid);
		return newSkidNum;	
	}
	
	protected void addProduct(Product p) {
		mDbHelper.insertOrReplaceProduct(p, mSelectedWorkOrder.getWoNumber());
	}
	
	public boolean addWorkOrder(WorkOrder newWo) {
		if (mDbHelper.insertOrUpdateWorkOrder(newWo) != -1l) {
			mWoNumbersList.add(newWo.getWoNumber());
			if (newWo.getSkidsList().isEmpty()) { //Doing this after inserting the WO so that the WO will be in the table so I can look up its id. TODO stop exposing the skids list.
				Skid<Product> defaultSkid = new Skid<Product>(1000, null);
				newWo.addOrUpdateSkid(defaultSkid);
				mDbHelper.insertOrReplaceSkid(defaultSkid, newWo.getWoNumber());
				newWo.selectSkid(defaultSkid.getSkidNumber());
				mDbHelper.insertOrUpdateWorkOrder(newWo); //doing it twice to get the selection in there.
			}
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
 
	public void changeNovatecSetpoint (Double setpoint) {
		Novatec n = mSelectedLine.getNovatec();
		Double oldSetpoint = n.getControllerSetpoint();
		n.setControllerSetpoint(setpoint);
		mDbHelper.updateColumn(PrimexDatabaseSchema.Novatecs.TABLE_NAME, 
				PrimexDatabaseSchema.Novatecs.COLUMN_NAME_CURRENT_SETPOINT, 
				null, 
				null, 
				String.valueOf(setpoint));
	}
	public void changeProduct (Product p) {
		Product oldProduct = mSelectedWorkOrder.getProduct();
		mSelectedWorkOrder.setProduct(p);
		addProduct(p);
		mNetWidth = p.getWidth(); //TODO why is this necessary, and not double stack ready
		this.propChangeSupport.firePropertyChange(PRODUCT_CHANGE_EVENT, oldProduct, p);
	}
	
	public int changeSelectedSkid(Integer skidNumber) {
		//TODO this function fires twice in a row. Catch index out of bounds exceptions.
		//maybe this should call changeNumber of skids to make sure the skid exists you're changing to?
		//You should not have to check the WO has the right skids list to use this!
		Skid<Product> oldSkid = mSelectedSkid;
		if (mSelectedWorkOrder.getSkidsList().isEmpty()) {
			List<Skid<Product>> savedSkids = mDbHelper.getSkidList(mSelectedWorkOrder.getWoNumber());
			if (!savedSkids.isEmpty() ) {
				mSelectedWorkOrder.setSkidsList( savedSkids ); //TODO sync the class and db, prevent bug where calling this can reset skids list to size 1. Did this by adding isEmpty call
			}
		}
		mSelectedSkid = mSelectedWorkOrder.selectSkid(skidNumber);
		propChangeSupport.firePropertyChange(SKID_CHANGE_EVENT, oldSkid, mSelectedSkid);
		return skidNumber;
	}
	
	public void saveProduct(Product p) {
		mDbHelper.insertOrReplaceProduct(p, getSelectedWorkOrder().getWoNumber());
	}
	
	/*
	 * Convenience method for saving a skid from the currently selected work order. TODO the currently selected skid, in fact.
	 */
	public void saveSkid(Skid<Product> s) {
		mDbHelper.insertOrReplaceSkid(s, mSelectedWorkOrder.getWoNumber());
		mSelectedWorkOrder.getSkidsList().set(s.getSkidNumber() - 1, s);
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
		mProductsPerMinute = null; //spm; TODO this function shouldnt' be allowed! also validity checking. Also this maybe should be for direct setting only?
		propChangeSupport.firePropertyChange(PRODUCTS_PER_MINUTE_CHANGE_EVENT, oldSpm, spm);
	}
	
	public double getProductsPerMinute() {
		return mProductsPerMinute;
	}
	
	/*
	 * This function is called whenever relevant properties change.
	 * TODO should not need to remember to call it before times.
	 */
	public void calculateRates() {
		if (!hasSelectedProduct()) {
			throw new IllegalStateException(new Throwable(ERROR_NO_PRODUCT_SELECTED));
		}
		Double oldPpm = null; //mProductsPerMinute; 
		mProductsPerMinute = HelperFunction.INCHES_PER_FOOT / mSelectedWorkOrder.getProduct().getLength() * mSelectedLine.getLineSpeed();
		propChangeSupport.firePropertyChange(PRODUCTS_PER_MINUTE_CHANGE_EVENT, oldPpm, mProductsPerMinute);
		
		Double oldNet = null; //mNetPph;
		mNetPph = mProductsPerMinute * mSelectedWorkOrder.getProduct().getUnitWeight() * HelperFunction.MINUTES_PER_HOUR;
		propChangeSupport.firePropertyChange(NET_PPH_CHANGE_EVENT, oldNet, mNetPph);
		
		if (mGrossWidth > 0) {
			Double oldEt = null; //mEdgeTrimRatio;
			if (mNetWidth >= mGrossWidth) {
				throw new IllegalStateException(new Throwable(ERROR_NET_LESS_THAN_GROSS));
			}
			mEdgeTrimRatio = (mGrossWidth - mNetWidth) / mGrossWidth;
			propChangeSupport.firePropertyChange(EDGE_TRIM_RATIO_CHANGE_EVENT, oldEt, mEdgeTrimRatio);
			
			Double oldGross = null; //mGrossPph;
			mGrossPph = mNetPph / (1 - mEdgeTrimRatio);
			propChangeSupport.firePropertyChange(GROSS_PPH_CHANGE_EVENT, oldGross, mGrossPph);
			
			Double oldColorPercent = null; //mColorPercent;
			mColorPercent = getSelectedLine().getNovatec().getRate() / mGrossPph;
			propChangeSupport.firePropertyChange(COLOR_PERCENT_CHANGE_EVENT, oldColorPercent, mColorPercent);
		} 		
	}
	
	public void calculateTimes() {
		if (mSelectedSkid == null) throw new IllegalStateException(new Throwable("ERROR_NO_SELECTED_SKID"));
		if ( (mProductsPerMinute != null) && (mSelectedSkid.getTotalItems() > 0) ) { //TODO this function gets called way too much			
			//calculate total time per skid. 
			Double oldMinutes = null; //mSelectedSkid.getMinutesPerSkid();
			double newMinutes = mSelectedSkid.calculateMinutesPerSkid(mProductsPerMinute);
			propChangeSupport.firePropertyChange(MINUTES_PER_SKID_CHANGE_EVENT, oldMinutes, newMinutes);
			
			//calculate skid start and finish time
			Date oldStartTime = null; //mSelectedSkid.getStartTime(); 
			Date newStartTime = mSelectedSkid.calculateStartTime(mProductsPerMinute);
			Date oldFinishTime = mSelectedSkid.getFinishTime();
			Date newFinishTime = mSelectedSkid.calculateFinishTimeWhileRunning(mProductsPerMinute);			
			propChangeSupport.firePropertyChange(CURRENT_SKID_START_TIME_CHANGE_EVENT, oldStartTime, newStartTime);
			propChangeSupport.firePropertyChange(CURRENT_SKID_FINISH_TIME_CHANGE_EVENT, oldFinishTime, newFinishTime);
			
			//calculate job finish times
			Date oldJobFinishTime = null; //mSelectedWorkOrder.getFinishTime();
			Date newJobFinishTime = mSelectedWorkOrder.calculateFinishTimes(mProductsPerMinute);
			propChangeSupport.firePropertyChange(JOB_FINISH_TIME_CHANGE_EVENT, oldJobFinishTime, newJobFinishTime);
		}
		if (mSelectedLine.getLineSpeed() > 0) {
			double oldMillisToMaxson = mMillisToMaxson;
			mMillisToMaxson = mSelectedLine.getLineLength() / mSelectedLine.getLineSpeed() * HelperFunction.ONE_MINUTE_IN_MILLIS;
			propChangeSupport.firePropertyChange(TIME_TO_MAXSON_CHANGE_EVENT, oldMillisToMaxson, mMillisToMaxson);
		}
	}

	public int getDatabaseVersion() {
		return PrimexSQLiteOpenHelper.DATABASE_VERSION;
	}
	public void changeNumberOfSkids(int num) {
		int oldNum = mSelectedWorkOrder.getNumberOfSkids();
		mSelectedWorkOrder.setNumberOfSkids(num);
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

	public double getGrossWidth() {
		return mGrossWidth;
	}

	public void setGrossWidth(double grossWidth) {
		this.mGrossWidth = grossWidth;
	}
}

package com.kovaciny.primexmodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

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
		setNumberOfTableSkids(1);
	}
	
	public static final String LINE_SPEED_CHANGE_EVENT = "PrimexModel.SPEED_CHANGE";
	public static final String SELECTED_LINE_CHANGE_EVENT = "PrimexModel.LINE_CHANGE";
	public static final String SELECTED_WO_CHANGE_EVENT = "PrimexModel.WO_CHANGE";
	public static final String NEW_WORK_ORDER_EVENT = "PrimexModel.NEW_WORK_ORDER"; 
	public static final String PRODUCT_CHANGE_EVENT = "PrimexModel.PRODUCT_CHANGE"; 
	public static final String PRODUCTS_PER_MINUTE_CHANGE_EVENT = "PrimexModel.PPM_CHANGE"; 
	public static final String CURRENT_SKID_FINISH_TIME_CHANGE_EVENT = "PrimexModel.CURRENT_SKID_FINISH_TIME_CHANGE"; 
	public static final String CURRENT_SKID_START_TIME_CHANGE_EVENT = "PrimexModel.CURRENT_SKID_START_TIME_CHANGE"; 
	public static final String MINUTES_PER_SKID_CHANGE_EVENT = "PrimexModel.MINUTES_PER_SKID_CHANGE"; 
 	public static final String NUMBER_OF_SKIDS_CHANGE_EVENT = "PrimexModel.NUMBER_OF_SKIDS_CHANGE"; 
 	public static final String NUMBER_OF_TABLE_SKIDS_CHANGE_EVENT = "PrimexModel.NUMBER_OF_TABLE_SKIDS_CHANGE"; 
	public static final String JOB_FINISH_TIME_CHANGE_EVENT = "PrimexModel.JOB_FINISH_TIME_CHANGE"; 
	public static final String SKID_CHANGE_EVENT = "PrimexModel.SKID_CHANGE"; 
	public static final String SECONDS_TO_MAXSON_CHANGE_EVENT = "PrimexModel.SECONDS_TO_MAXSON_CHANGE"; 
	public static final String NET_PPH_CHANGE_EVENT = "PrimexModel.NET_PPH_CHANGE"; 
	public static final String GROSS_PPH_CHANGE_EVENT = "PrimexModel.GROSS_PPH_CHANGE";
	public static final String NUMBER_OF_WEBS_CHANGE_EVENT = "PrimexModel.NUMBER_OF_WEBS_CHANGE";
	public static final String GROSS_WIDTH_CHANGE_EVENT = "PrimexModel.GROSS_WIDTH_CHANGE"; //TODO not fired 
	public static final String COLOR_PERCENT_CHANGE_EVENT = "PrimexModel.NOVATEC_LETDOWN_CHANGE"; 
	public static final String EDGE_TRIM_RATIO_CHANGE_EVENT = "PrimexModel.EDGE_TRIM_PERCENT_CHANGE"; 
	public static final String NOVATEC_CHANGE_EVENT = "PrimexModel.NOVATEC_CHANGE"; 
	 
	public static final String ERROR_NET_LESS_THAN_GROSS = "PrimexModel.Net width less than gross width";
	public static final String ERROR_NO_LINE_SELECTED = "PrimexModel.No line selected";
	public static final String ERROR_NO_PRODUCT_SELECTED = "PrimexModel.No product selected";
	public static final String ERROR_NO_SKID_SELECTED = "PrimexModel.No skid selected";
	public static final String ERROR_NO_WORK_ORDER_SELECTED = "PrimexModel.No work order selected";
	public static final String ERROR_ZERO_LINE_SPEED = "PrimexModel.Dividing by zero line speed";
	public static final String ERROR_NO_PPM_VALUE = "PrimexModel.Products per minute is null";
	
	private List<Integer> mLineNumbersList;
	private List<Integer> mWoNumbersList;
	private ProductionLine mSelectedLine;
	private WorkOrder mSelectedWorkOrder;
	private Skid<Product> mSelectedSkid;
	private PrimexSQLiteOpenHelper mDbHelper;
	
	private double mColorPercent;
	private Date mCreateDate;
	private double mDifferentialSetpoint;
	private double mEdgeTrimRatio;
	private double mGrossPph;
	private double mLineSpeedSetpoint;
	private double mNetPph;
	private double mProductsPerMinute;
	private int mNumberOfTableSkids;
	
	/*
	 * Used to save speed changes until we're ready to fire them to the view.
	 */
	private boolean mSpeedChanged = false;
	private boolean mProductChanged = false;
	private boolean mSkidChanged = false;

	/*
	 * This section sets up notifying observers about changes.
	 */
		
	// Create PropertyChangeSupport to manage listeners and fire events.
	private final PropertyChangeSupport propChangeSupport = new PropertyChangeSupport(this);
	  
	// Provide delegating methods to add / remove listeners to / from the support class.  
	public void addPropertyChangeListener(PropertyChangeListener l) {
	    propChangeSupport.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
	    propChangeSupport.removePropertyChangeListener(l);
	}

	public void setSelectedLine (Integer lineNumber) {
		if (lineNumber == null) throw new NullPointerException("need to select a line");
		if (!mLineNumbersList.contains(lineNumber)) throw new NoSuchElementException("Line number not in the list of lines");
		int oldLineNumber = hasSelectedLine() ? mSelectedLine.getLineNumber() : -1;
		if (lineNumber != oldLineNumber) {
			ProductionLine lineToSelect = mDbHelper.getLine(lineNumber);
			setSelectedLine(lineToSelect);
		}
	}
	
	public void setSelectedLine (ProductionLine line) {
		if (hasSelectedLine()) saveSelectedLine();
		ProductionLine oldLine = mSelectedLine;
		
		mSelectedLine = line;
		mSelectedLine.setNovatec(mDbHelper.getNovatec(line.getLineNumber()));
		setDifferentialSetpoint(mSelectedLine.getSpeedValues().differentialSpeed);
		setLineSpeedSetpoint(mSelectedLine.getSpeedValues().lineSpeedSetpoint);

		propChangeSupport.firePropertyChange(SELECTED_LINE_CHANGE_EVENT, oldLine, mSelectedLine);
		propChangeSupport.firePropertyChange(NOVATEC_CHANGE_EVENT, null, mSelectedLine.getNovatec());
		propChangeSupport.firePropertyChange(GROSS_WIDTH_CHANGE_EVENT, null, mSelectedLine.getWebWidth());
		
		int associatedWoNumber = mDbHelper.getSelectedWoNumberByLine(line.getLineNumber());
		if (associatedWoNumber > 0) {
			setSelectedWorkOrder(associatedWoNumber);
		} else { //make sure a work order is selected
			int newWoNumber = mDbHelper.getHighestWoNumber() + 1;
			addWorkOrder(new WorkOrder(newWoNumber));
			setSelectedWorkOrder(newWoNumber);
		}
	}
	
	public void setSelectedWorkOrder(int woNumber) {
		if (!mWoNumbersList.contains(woNumber)) {
			throw new NoSuchElementException("Work order number not in the list of work orders, need to add it");
		}
		if (woNumber <= 0) throw new IllegalArgumentException("Work order number must be positive");
		
		if (hasSelectedWorkOrder()) saveState();
	
		mSelectedWorkOrder = mDbHelper.getWorkOrder(woNumber);
		mDbHelper.updateLineWorkOrderLink(mSelectedLine.getLineNumber(), woNumber);
		changeSelectedSkid(mSelectedWorkOrder.getSelectedSkid().getSkidNumber());
		
		Product p = mDbHelper.getProduct(woNumber);
		mSelectedWorkOrder.setProduct(p);
		if (p != null) propChangeSupport.firePropertyChange(PRODUCT_CHANGE_EVENT, null, p);
		
		loadState(woNumber);
		
		//Now that everything is loaded up, notify listeners of all the changes.
		propChangeSupport.firePropertyChange(JOB_FINISH_TIME_CHANGE_EVENT, null, mSelectedWorkOrder.getFinishDate());
		if (mSelectedSkid != null) {
			propChangeSupport.firePropertyChange(SKID_CHANGE_EVENT, null, mSelectedSkid);
			propChangeSupport.firePropertyChange(CURRENT_SKID_FINISH_TIME_CHANGE_EVENT, null, mSelectedSkid.getFinishTime());
			propChangeSupport.firePropertyChange(CURRENT_SKID_START_TIME_CHANGE_EVENT, null, mSelectedSkid.getStartTime());
			propChangeSupport.firePropertyChange(MINUTES_PER_SKID_CHANGE_EVENT, null, mSelectedSkid.getMinutesPerSkid());
			propChangeSupport.firePropertyChange(PRODUCTS_PER_MINUTE_CHANGE_EVENT, null, getProductsPerMinute()); 
			if (mSelectedLine.getLineSpeed() > 0) {
				propChangeSupport.firePropertyChange(SECONDS_TO_MAXSON_CHANGE_EVENT, null, mSelectedLine.getSecondsToMaxson());
			}
		}
		propChangeSupport.firePropertyChange(NUMBER_OF_SKIDS_CHANGE_EVENT, null, mSelectedWorkOrder.getNumberOfSkids());
		propChangeSupport.firePropertyChange(EDGE_TRIM_RATIO_CHANGE_EVENT, null, getEdgeTrimRatio()); 
		propChangeSupport.firePropertyChange(NET_PPH_CHANGE_EVENT, null, getNetPph()); 
		propChangeSupport.firePropertyChange(GROSS_PPH_CHANGE_EVENT, null, getGrossPph()); 
		propChangeSupport.firePropertyChange(COLOR_PERCENT_CHANGE_EVENT, null, getColorPercent()); 
		propChangeSupport.firePropertyChange(SELECTED_WO_CHANGE_EVENT, null, mSelectedWorkOrder);
		propChangeSupport.firePropertyChange(NUMBER_OF_TABLE_SKIDS_CHANGE_EVENT, null, getNumberOfTableSkids());
	}

	/*
	 * Returns the newly added skid.
	 */
	public Skid<Product> addSkid (int currentCount, int totalCount) { 	 		 
		Skid<Product> newSkid = new Skid<Product>(currentCount, 
				totalCount,
				1);
		getSelectedWorkOrder().addOrUpdateSkid(newSkid);
		return newSkid;	
	}
	
	protected void addProduct(Product p) {
		mDbHelper.insertOrReplaceProduct(p, mSelectedWorkOrder.getWoNumber());
	}
	
	public WorkOrder addWorkOrder() {
		//generate a work order number
		int newWoNumber = getHighestWoNumber() + 1;
		WorkOrder newWo = addWorkOrder(new WorkOrder(newWoNumber));
		return newWo;
	}
	
	public WorkOrder addWorkOrder(WorkOrder newWo) {
		mCreateDate = new Date();
		mDbHelper.insertOrUpdateWorkOrder(newWo);
		mWoNumbersList.add(newWo.getWoNumber());
		if (newWo.getSkidsList().isEmpty()) { //Doing this after inserting the WO so that the WO will be in the table so I can look up its id. TODO stop exposing the skids list.
			Skid<Product> defaultSkid = new Skid<Product>(1000, null);
			newWo.addOrUpdateSkid(defaultSkid);
			mDbHelper.insertOrReplaceSkid(defaultSkid, newWo.getWoNumber());
			newWo.selectSkid(defaultSkid.getSkidNumber());
			mDbHelper.insertOrUpdateWorkOrder(newWo); //doing it twice to get the selection in there.
		}
		propChangeSupport.firePropertyChange(NEW_WORK_ORDER_EVENT, null, newWo);
		return newWo;
	}
		
	public void setCurrentSpeed (SpeedValues values) { 
		mSelectedLine.setSpeedValues(values);
		setLineSpeedSetpoint(values.lineSpeedSetpoint);
		setDifferentialSetpoint(values.differentialSpeed);
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
		mSpeedChanged = true;
	}
 
	public void changeNovatecSetpoint (Double setpoint) {
		Novatec n = mSelectedLine.getNovatec();
		n.setControllerSetpoint(setpoint);
		mDbHelper.updateColumn(PrimexDatabaseSchema.Novatecs.TABLE_NAME, 
				PrimexDatabaseSchema.Novatecs.COLUMN_NAME_CURRENT_SETPOINT, 
				null, 
				null, 
				String.valueOf(setpoint));
	}
	public void changeProduct (Product p) {
		mSelectedWorkOrder.setProduct(p);
		addProduct(p);
		propChangeSupport.firePropertyChange(PRODUCT_CHANGE_EVENT, null, p);
		mProductChanged = true;
	}
	
	public int changeSelectedSkid(Integer skidNumber) {
		//TODO this function fires twice in a row. Catch index out of bounds exceptions.
		//maybe this should call changeNumber of skids to make sure the skid exists you're changing to?
		//You should not have to check the WO has the right skids list to use this!
		if (mSelectedWorkOrder.getSkidsList().isEmpty()) {
			List<Skid<Product>> savedSkids = mDbHelper.getSkidList(mSelectedWorkOrder.getWoNumber());
			if (!savedSkids.isEmpty() ) {
				mSelectedWorkOrder.setSkidsList( savedSkids ); //TODO sync the class and db, prevent bug where calling this can reset skids list to size 1. Did this by adding isEmpty call
			}
		}
		mSelectedSkid = mSelectedWorkOrder.selectSkid(skidNumber);
		mSkidChanged = true;
		return skidNumber;
	}
	
	public void saveProduct(Product p) {
		mDbHelper.insertOrReplaceProduct(p, getSelectedWorkOrder().getWoNumber());
	}
	
	public void saveSelectedLine() {
		mDbHelper.insertOrUpdateLine(mSelectedLine);
	}
	
	/*
	 * Convenience method for saving a skid from the currently selected work order. TODO the currently selected skid, in fact.
	 */
	public void saveSkid(Skid<Product> s) {
		mDbHelper.insertOrReplaceSkid(s, mSelectedWorkOrder.getWoNumber());
		mSelectedWorkOrder.getSkidsList().set(s.getSkidNumber() - 1, s);
	}
	
	
	/*
	 * Saves the selected line number and work order number, and other settings
	 */
	public void saveState() {
		if (!hasSelectedLine()) throw new IllegalStateException(new Throwable(ERROR_NO_LINE_SELECTED));
		if (!hasSelectedWorkOrder()) throw new IllegalStateException (new Throwable(ERROR_NO_WORK_ORDER_SELECTED));
    	if (mSelectedSkid != null) {
    		saveSkid(mSelectedSkid);
    		Log.v("saveState", "Saved state of selected skid");
    	}
    	if (hasSelectedProduct()){
    		saveProduct(mSelectedWorkOrder.getProduct());
    		Log.v("saveState", "Saved state of selected product.");
    	}
		mDbHelper.saveModelState(this);
	}

	/*
	 * Returns whether state was successfully loaded.
	 */
	public boolean loadState(int woNumber) {
		Cursor cursor = mDbHelper.loadModelState(woNumber);
		
		try {
			if (cursor.moveToFirst()) {
				int lineNumber = cursor.getInt(cursor.getColumnIndexOrThrow(PrimexDatabaseSchema.ModelState.COLUMN_NAME_SELECTED_LINE));
				long create = cursor.getLong(cursor.getColumnIndexOrThrow(PrimexDatabaseSchema.ModelState.COLUMN_NAME_CREATE_DATE));
		    	mCreateDate = new Date(create);
		    	mProductsPerMinute = cursor.getDouble(cursor.getColumnIndexOrThrow(PrimexDatabaseSchema.ModelState.COLUMN_NAME_PRODUCTS_PER_MINUTE));
		    	mEdgeTrimRatio = cursor.getDouble(cursor.getColumnIndexOrThrow(PrimexDatabaseSchema.ModelState.COLUMN_NAME_EDGE_TRIM_PERCENT));
		    	mNetPph = cursor.getDouble(cursor.getColumnIndexOrThrow(PrimexDatabaseSchema.ModelState.COLUMN_NAME_NET_PPH));
		    	mGrossPph = cursor.getDouble(cursor.getColumnIndexOrThrow(PrimexDatabaseSchema.ModelState.COLUMN_NAME_GROSS_PPH));
		    	mColorPercent= cursor.getDouble(cursor.getColumnIndexOrThrow(PrimexDatabaseSchema.ModelState.COLUMN_NAME_COLOR_PERCENT));
		    	mLineSpeedSetpoint = cursor.getDouble(cursor.getColumnIndexOrThrow(PrimexDatabaseSchema.ModelState.COLUMN_NAME_LINE_SPEED_SETPOINT));
		    	mDifferentialSetpoint = cursor.getDouble(cursor.getColumnIndexOrThrow(PrimexDatabaseSchema.ModelState.COLUMN_NAME_DIFFERENTIAL_SETPOINT));
		    	mNumberOfTableSkids = cursor.getInt(cursor.getColumnIndexOrThrow(PrimexDatabaseSchema.ModelState.COLUMN_NAME_NUMBER_OF_TABLE_SKIDS));
		    	return true;
		    } else return false;
	    } finally {
	    	if (cursor != null) cursor.close();
		}
	}
	
	/*
	 * This function is called whenever relevant properties change.
	 * TODO should not need to remember to call it before times.
	 */
	public void calculateRates() {
		if (!hasSelectedProduct()) {
			throw new IllegalStateException(new Throwable(ERROR_NO_PRODUCT_SELECTED));
		}
		
		if (mSpeedChanged) {
			propChangeSupport.firePropertyChange(LINE_SPEED_CHANGE_EVENT, null, mSelectedLine.getLineSpeed());
			mSpeedChanged = false;
		}
		if (mProductChanged) {
			this.propChangeSupport.firePropertyChange(PRODUCT_CHANGE_EVENT, null, mSelectedWorkOrder.getProduct());
			mProductChanged = false;
		}
		if (mSkidChanged) {
			this.propChangeSupport.firePropertyChange(SKID_CHANGE_EVENT, null, mSelectedSkid);
			mSkidChanged = false;
		}
		
		calculateProductsPerMinute();
		
		mNetPph = mProductsPerMinute * mSelectedWorkOrder.getProduct().getUnitWeight() * HelperFunction.MINUTES_PER_HOUR;
		propChangeSupport.firePropertyChange(NET_PPH_CHANGE_EVENT, null, mNetPph);
		
		double grossWidth = mSelectedLine.getWebWidth();
		if (grossWidth > 0) {
			Double oldEt = null; //mEdgeTrimRatio;
			double netWidth = getSelectedWorkOrder().getProduct().getWidth();
			if (netWidth >= grossWidth) {
				throw new IllegalStateException(new Throwable(ERROR_NET_LESS_THAN_GROSS));
			}
			mEdgeTrimRatio = (grossWidth - netWidth) / grossWidth;
			propChangeSupport.firePropertyChange(EDGE_TRIM_RATIO_CHANGE_EVENT, oldEt, mEdgeTrimRatio);
			
			if (mEdgeTrimRatio < 1) {
				mGrossPph = mNetPph / (1 - mEdgeTrimRatio);
				propChangeSupport.firePropertyChange(GROSS_PPH_CHANGE_EVENT, null, mGrossPph);
				
				if (mGrossPph > 0) {
					mColorPercent =  mSelectedLine.getNovatec().getRate() / mGrossPph;
					propChangeSupport.firePropertyChange(COLOR_PERCENT_CHANGE_EVENT, null, mColorPercent);
				}

			}
			
		} 		
	}
	
	public void calculateTimes() {
		if (mSelectedSkid == null) throw new IllegalStateException(new Throwable("ERROR_NO_SELECTED_SKID"));
		if (!hasSelectedProduct()) {
			throw new IllegalStateException(new Throwable(ERROR_NO_PRODUCT_SELECTED));
		}
		if (mSpeedChanged) {
			propChangeSupport.firePropertyChange(LINE_SPEED_CHANGE_EVENT, null, mSelectedLine.getLineSpeed());
			mSpeedChanged = false;
		}
		if (mProductChanged) {
			this.propChangeSupport.firePropertyChange(PRODUCT_CHANGE_EVENT, null, mSelectedWorkOrder.getProduct());
			mProductChanged = false;
		}
		if (mSkidChanged) {
			this.propChangeSupport.firePropertyChange(SKID_CHANGE_EVENT, null, mSelectedSkid);
			mSkidChanged = false;
		}
	
		calculateProductsPerMinute();
		
		if ( (mProductsPerMinute > 0 ) && (mSelectedSkid.getTotalItems() > 0) ) {			
			propChangeSupport.firePropertyChange(SECONDS_TO_MAXSON_CHANGE_EVENT, null, mSelectedLine.getSecondsToMaxson());
			
			//calculate total time per skid. 
			double minutes = mSelectedSkid.calculateMinutesPerSkid(mProductsPerMinute);
			minutes *= mNumberOfTableSkids;
			propChangeSupport.firePropertyChange(MINUTES_PER_SKID_CHANGE_EVENT, null, minutes);
			
			//calculate skid start and finish time
			Date newStartTime = mSelectedSkid.calculateStartTime(mProductsPerMinute);
			Date oldFinishTime = mSelectedSkid.getFinishTime();
			Date newFinishTime = mSelectedSkid.calculateFinishTimeWhileRunning(mProductsPerMinute);			
			propChangeSupport.firePropertyChange(CURRENT_SKID_START_TIME_CHANGE_EVENT, null, newStartTime);
			propChangeSupport.firePropertyChange(CURRENT_SKID_FINISH_TIME_CHANGE_EVENT, oldFinishTime, newFinishTime);
			
			//calculate job finish times
			Date oldJobFinishTime = null; //mSelectedWorkOrder.getFinishTime();
			Date newJobFinishTime = mSelectedWorkOrder.calculateFinishTimes(mProductsPerMinute);
			propChangeSupport.firePropertyChange(JOB_FINISH_TIME_CHANGE_EVENT, oldJobFinishTime, newJobFinishTime);
		}
	}
	
	private double calculateProductsPerMinute() {
		double productLength = mSelectedWorkOrder.getProduct().getLength();
		if (productLength < 0) throw new IllegalStateException(new Throwable(ERROR_NO_PPM_VALUE));

		double lineSpeed = mSelectedLine.getLineSpeed();
		mProductsPerMinute = HelperFunction.INCHES_PER_FOOT / productLength * lineSpeed;
		propChangeSupport.firePropertyChange(PRODUCTS_PER_MINUTE_CHANGE_EVENT, null, mProductsPerMinute);
		return mProductsPerMinute;
	}

	public int getDatabaseVersion() {
		return PrimexSQLiteOpenHelper.DATABASE_VERSION;
	}
	
	public void changeNumberOfSkids(double num) {
		if (num <= 0d) throw new IllegalArgumentException("Number of skids not positive");
		double totalSkids = Math.ceil(num);
		double fractionalSkid = num % 1.0; //TODO bigdecimal
		Skid<Product> currentSkid = mSelectedWorkOrder.getSelectedSkid();
		while (mSelectedWorkOrder.getNumberOfSkids() < totalSkids) {
			currentSkid = addSkid(0, mSelectedSkid.getTotalItems());
			mSelectedWorkOrder.addOrUpdateSkid(currentSkid);
			mDbHelper.insertOrReplaceSkid(currentSkid, mSelectedWorkOrder.getWoNumber());
		}
		while (mSelectedWorkOrder.getNumberOfSkids() > totalSkids) {
			int deletedSkidNo = mSelectedWorkOrder.removeLastSkid();
			mDbHelper.deleteSkid(mSelectedWorkOrder.getWoNumber(), deletedSkidNo);			
		}
		
		if (fractionalSkid > 0.001) {
			int fractionalSheetCount = (int) (mSelectedSkid.getTotalItems() * fractionalSkid); 
			mSelectedWorkOrder.removeLastSkid();
			Skid<Product> partialSkid = addSkid(0, fractionalSheetCount);
			mSelectedWorkOrder.addOrUpdateSkid(partialSkid);
			mDbHelper.insertOrReplaceSkid(partialSkid, mSelectedWorkOrder.getWoNumber());
		} else {
			//make sure the last skid has full sheet count if not fractional
			mSelectedWorkOrder.removeLastSkid();
			currentSkid = addSkid(0, mSelectedSkid.getTotalItems());
			mSelectedWorkOrder.addOrUpdateSkid(currentSkid);
			mDbHelper.insertOrReplaceSkid(currentSkid, mSelectedWorkOrder.getWoNumber());
		}
		propChangeSupport.firePropertyChange(NUMBER_OF_SKIDS_CHANGE_EVENT, null, mSelectedWorkOrder.getNumberOfSkids());		
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
		if (hasSelectedWorkOrder()) {
			return mSelectedWorkOrder.hasProduct();	
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

	public Date getLatestWoCreateDate() {
		return mDbHelper.getLatestWoCreateDate();
	}
	
	public List<Integer> getAllWoNumbersForLine(int lineNumber) {
		return mDbHelper.getAllWoNumbersForLine(lineNumber);
	}
	
	public int getHighestWoNumber() {
		return mDbHelper.getHighestWoNumber();
	}
	
	public void deleteWorkOrders() {
		mDbHelper.clearWorkOrders();		
	}

	public double getProductsPerMinute() {
		return mProductsPerMinute;
	}

	public void setProductsPerMinute(double productsPerMinute) {
		this.mProductsPerMinute = productsPerMinute;
	}

	public double getEdgeTrimRatio() {
		return mEdgeTrimRatio;
	}

	public void setEdgeTrimPercent(double edgeTrimPercent) {
		this.mEdgeTrimRatio = edgeTrimPercent;
	}

	public double getNetPph() {
		return mNetPph;
	}

	public void setNetPph(double netPph) {
		this.mNetPph = netPph;
	}

	public double getGrossPph() {
		return mGrossPph;
	}

	public void setGrossPph(double grossPph) {
		this.mGrossPph = grossPph;
	}

	public double getColorPercent() {
		return mColorPercent;
	}

	public void setColorPercent(double colorPercent) {
		this.mColorPercent = colorPercent;
	}

	public void setLineSpeedSetpoint(double setpoint) {
		mLineSpeedSetpoint = setpoint;
	}
	
	public double getLineSpeedSetpoint() {
		return mLineSpeedSetpoint;
	}

	public double getDifferentialSetpoint() {
		return mDifferentialSetpoint;
	}

	public void setDifferentialSetpoint(double differentialSetpoint) {
		this.mDifferentialSetpoint = differentialSetpoint;
	}
	
	public Date getCreateDate() {
		return mCreateDate;
	}
	
	public void setCreateDate(Date createDate) {
		this.mCreateDate = createDate;
	}	
	
	public int getNumberOfTableSkids() {
		return mNumberOfTableSkids;
	}

	public void setNumberOfTableSkids(int numberOfSkids) {
		if (numberOfSkids <= 0) throw new IllegalArgumentException("Number of skids must be positive");
		this.mNumberOfTableSkids = numberOfSkids;
		propChangeSupport.firePropertyChange(NUMBER_OF_TABLE_SKIDS_CHANGE_EVENT, null, numberOfSkids);
	}

	@Override
	public String toString() {
		return "Model. State: " + "\nRates: net " + mNetPph + 
				", gross " + mGrossPph + ", line speed setpoint " + String.valueOf(mLineSpeedSetpoint) + ", differential " +
				String.valueOf(mDifferentialSetpoint);
	}
}

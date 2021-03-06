package com.kovaciny.primexmodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Date;
import java.util.Iterator;
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
	public static final String TEN_SECOND_LETDOWN_CHANGE_EVENT = "PrimexModel.TEN_SECOND_LETDOWN_GRAMS_CHANGE"; 
	public static final String EDGE_TRIM_RATIO_CHANGE_EVENT = "PrimexModel.EDGE_TRIM_PERCENT_CHANGE"; 
	public static final String NOVATEC_CHANGE_EVENT = "PrimexModel.NOVATEC_CHANGE"; 
	 
	public static final String ERROR_NET_LESS_THAN_GROSS = "PrimexModel.Net width less than gross width";
	public static final String ERROR_NO_LINE_SELECTED = "PrimexModel.No line selected";
	public static final String ERROR_NO_PRODUCT_SELECTED = "PrimexModel.No product selected";
	public static final String ERROR_NO_SKID_SELECTED = "PrimexModel.No skid selected";
	public static final String ERROR_NO_WORK_ORDER_SELECTED = "PrimexModel.No work order selected";
	public static final String ERROR_ZERO_LINE_SPEED = "PrimexModel.Dividing by zero line speed";
	public static final String ERROR_NO_PPM_VALUE = "PrimexModel.Products per minute is null";
	
	public static final double MAXIMUM_POSSIBLE_GAUGE = .550d;
	private List<Integer> mLineNumbersList;
	private List<Integer> mWoNumbersList;
	private ProductionLine mSelectedLine;
	private WorkOrder mSelectedWorkOrder;
	private Skid<Product> mSelectedSkid;
	private PrimexSQLiteOpenHelper mDbHelper;
	
	private double mColorPercent;
	private double mTenSecondLetdownGrams;
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

	public void  setSelectedLine (Integer lineNumber) {
		if (!mLineNumbersList.contains(lineNumber)) throw new NoSuchElementException("Line number not in the list of lines");
		
		int oldLineNumber = hasSelectedLine() ? mSelectedLine.getLineNumber() : -1;
		if (lineNumber != oldLineNumber) {
			ProductionLine lineToSelect = mDbHelper.loadLine(lineNumber);
			setSelectedLine(lineToSelect);
		}
	}
	
	public void setSelectedLine (ProductionLine line) {
		if (hasSelectedLine()) saveSelectedLine();
		ProductionLine oldLine = mSelectedLine;
		
		mSelectedLine = line;
		mSelectedLine.setPrimaryNovatec(mDbHelper.loadNovatec(line.getLineNumber()));

		propChangeSupport.firePropertyChange(SELECTED_LINE_CHANGE_EVENT, oldLine, mSelectedLine);
		propChangeSupport.firePropertyChange(NOVATEC_CHANGE_EVENT, null, mSelectedLine.getPrimaryNovatec());
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
		
		if (hasSelectedWorkOrder()) {
			if (mSelectedWorkOrder.getWoNumber() == woNumber) {
				return;
			} else {
				saveState();
			}
		}
	
		mSelectedWorkOrder = mDbHelper.getWorkOrder(woNumber);
		mDbHelper.updateLineWorkOrderLink(mSelectedLine.getLineNumber(), woNumber);
		changeSelectedSkid(mSelectedWorkOrder.getSelectedSkid().getSkidNumber());
		
		if (!loadState(woNumber)) {
		    //initialize state variables
		    mCreateDate = new Date();
            mProductsPerMinute = 0d;
            mEdgeTrimRatio = 0d;
            mNetPph = 0d;
            mGrossPph = 0d;
            mColorPercent= 0d;
            mTenSecondLetdownGrams = 0d;
            setCurrentSpeed(mSelectedLine.getSpeedValues());
            mNumberOfTableSkids = 1;
		} else {
		    setCurrentSpeed(new SpeedValues(mLineSpeedSetpoint, mDifferentialSetpoint, mSelectedLine.getSpeedValues().speedFactor));
		}
		
		Product p = mDbHelper.getProduct(woNumber);
		changeProduct(p);
				
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
			} else {
				propChangeSupport.firePropertyChange(SECONDS_TO_MAXSON_CHANGE_EVENT, null, 0l); //so label will hide
			}
		}
		propChangeSupport.firePropertyChange(NUMBER_OF_SKIDS_CHANGE_EVENT, null, mSelectedWorkOrder.getNumberOfSkids());
		propChangeSupport.firePropertyChange(EDGE_TRIM_RATIO_CHANGE_EVENT, null, getEdgeTrimRatio()); 
		propChangeSupport.firePropertyChange(NET_PPH_CHANGE_EVENT, null, getNetPph()); 
		propChangeSupport.firePropertyChange(GROSS_PPH_CHANGE_EVENT, null, getGrossPph()); 
		propChangeSupport.firePropertyChange(COLOR_PERCENT_CHANGE_EVENT, null, getColorPercent()); 
		propChangeSupport.firePropertyChange(TEN_SECOND_LETDOWN_CHANGE_EVENT, null, getTenSecondLetdownGrams()); 
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
			Skid<Product> defaultSkid = new Skid<Product>();
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
 		mSpeedChanged = true;
	}
 
	public void changeNovatecSetpoint (Double setpoint) {
		Novatec n = mSelectedLine.getPrimaryNovatec();
		n.setSetpoint(setpoint);
		mDbHelper.updateColumn(PrimexDatabaseSchema.Novatecs.TABLE_NAME, 
				PrimexDatabaseSchema.Novatecs.COLUMN_NAME_CURRENT_SETPOINT, 
				null, 
				null, 
				String.valueOf(setpoint));
	}
	/*
	 * Dependency on setNumberOfTableSkids
	 */
	public void changeProduct (Product p) {
	    mSelectedWorkOrder.setProduct(p);
	    if (p != null) {
	        if (hasSelectedProduct() && p.getType().equals(String.valueOf(Product.SHEETSET_TYPE))) {
	            if (mNumberOfTableSkids == 2) {
	                ((Sheetset)p).setGrouping("skidset");
	            } else ((Sheetset)p).setGrouping("skid");
	        }
	        mDbHelper.insertOrReplaceProduct(p, mSelectedWorkOrder.getWoNumber());
		}

		propChangeSupport.firePropertyChange(PRODUCT_CHANGE_EVENT, null, p); //should fire if p is null
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
		this.propChangeSupport.firePropertyChange(SKID_CHANGE_EVENT, null, mSelectedSkid);
		return skidNumber;
	}
	
	public void saveProduct(Product p) {
		mDbHelper.insertOrReplaceProduct(p, getSelectedWorkOrder().getWoNumber());
	}
	
	public void saveSelectedLine() {
		mDbHelper.insertOrUpdateLine(mSelectedLine);
	}
	
	public void saveSelectedWorkOrder() {
	    mDbHelper.insertOrUpdateWorkOrder(getSelectedWorkOrder());
	}
	
	/*
	 * Convenience method for saving a skid from the currently selected work order. TODO the currently selected skid, in fact.
	 */
	public void saveSkid(Skid<Product> s) {
		mDbHelper.insertOrReplaceSkid(s, mSelectedWorkOrder.getWoNumber());
		mSelectedWorkOrder.getSkidsList().set(s.getSkidNumber() - 1, s);
		Log.v("saveState", "Saved " + s.toString());
	}	
	
	/*
	 * Saves the selected line number and work order number, and other settings
	 */
	public void saveState() {
		if (!hasSelectedLine()) throw new IllegalStateException(new Throwable(ERROR_NO_LINE_SELECTED));
		if (!hasSelectedWorkOrder()) throw new IllegalStateException (new Throwable(ERROR_NO_WORK_ORDER_SELECTED));
    	
		saveSelectedLine();
		saveSelectedWorkOrder();
        
		for (Iterator<Skid<Product>> itr = mSelectedWorkOrder.getSkidsList().iterator(); itr.hasNext(); ) {
    	    saveSkid(itr.next());
    	}
		if (hasSelectedProduct()){
    		saveProduct(mSelectedWorkOrder.getProduct());
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
				long create = cursor.getLong(cursor.getColumnIndexOrThrow(PrimexDatabaseSchema.ModelState.COLUMN_NAME_CREATE_DATE));
		    	mCreateDate = new Date(create);
		    	mProductsPerMinute = cursor.getDouble(cursor.getColumnIndexOrThrow(PrimexDatabaseSchema.ModelState.COLUMN_NAME_PRODUCTS_PER_MINUTE));
		    	mEdgeTrimRatio = cursor.getDouble(cursor.getColumnIndexOrThrow(PrimexDatabaseSchema.ModelState.COLUMN_NAME_EDGE_TRIM_PERCENT));
		    	mNetPph = cursor.getDouble(cursor.getColumnIndexOrThrow(PrimexDatabaseSchema.ModelState.COLUMN_NAME_NET_PPH));
		    	mGrossPph = cursor.getDouble(cursor.getColumnIndexOrThrow(PrimexDatabaseSchema.ModelState.COLUMN_NAME_GROSS_PPH));
		    	mColorPercent= cursor.getDouble(cursor.getColumnIndexOrThrow(PrimexDatabaseSchema.ModelState.COLUMN_NAME_COLOR_PERCENT));
		    	mTenSecondLetdownGrams = cursor.getDouble(cursor.getColumnIndexOrThrow(PrimexDatabaseSchema.ModelState.COLUMN_NAME_TEN_SECOND_LETDOWN_GRAMS));
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
	public void calculateRates(double tenSecondLetdownGrams) {
		mTenSecondLetdownGrams = tenSecondLetdownGrams;
		
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
				    double letdownLbsPerHour;
				    if (tenSecondLetdownGrams > 0) {
				        double gramsPerHour = tenSecondLetdownGrams * 6d * HelperFunction.MINUTES_PER_HOUR;
				        letdownLbsPerHour = gramsPerHour / HelperFunction.GRAMS_PER_POUND;
				    } else {
				        letdownLbsPerHour =  mSelectedLine.getPrimaryNovatec().getFeedRate();
				    }
				    mColorPercent = letdownLbsPerHour / mGrossPph;
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
		
		calculateProductsPerMinute();
		
		if ( (mProductsPerMinute > 0 ) && (mSelectedSkid.getTotalItems() > 0) ) {			
			propChangeSupport.firePropertyChange(SECONDS_TO_MAXSON_CHANGE_EVENT, null, mSelectedLine.getSecondsToMaxson());
			
			//calculate total time per skid. 
			double minutes = mSelectedSkid.calculateMinutesPerSkid(mProductsPerMinute);
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
	
	/*
	 * Irrespective of number of webs, as a rollset is one product with several webs.
	 */
	private double calculateProductsPerMinute() {
		double productLength = mSelectedWorkOrder.getProduct().getLength();
		if (productLength < 0) throw new IllegalStateException(new Throwable(ERROR_NO_PPM_VALUE));

		double lineSpeed = mSelectedLine.getLineSpeed();
		mProductsPerMinute = HelperFunction.INCHES_PER_FOOT / productLength * lineSpeed;
		propChangeSupport.firePropertyChange(PRODUCTS_PER_MINUTE_CHANGE_EVENT, null, mProductsPerMinute);
		return mProductsPerMinute;
	}

	public int calculateSheetsFromGauge(double currentHeight, double gauge) {
		return (int) Math.round(currentHeight / gauge);
	}
	
	public int calculateSheetsFromHeight(double currentHeight, double finishedHeight) {
		return (int) Math.round(currentHeight / finishedHeight * mSelectedSkid.getTotalItems()); //TODO may need to account for stacks
	}
	
	public int getDatabaseVersion() {
		return PrimexSQLiteOpenHelper.DATABASE_VERSION;
	}
	
	/*
	 * Dependent on the current skid number being right
	 */
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
	public boolean hasLineSpeed() {
	    return ( (mSelectedLine.getSpeedValues().lineSpeedSetpoint > 0) && 
	            (mSelectedLine.getSpeedValues().differentialSpeed > 0) ); 
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

	public double getTenSecondLetdownGrams() {
	    return mTenSecondLetdownGrams;
	}
	
	public void setTenSecondLetdownGrams(double grams) {
	    this.mTenSecondLetdownGrams = grams;
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

	private void setDifferentialSetpoint(double differentialSetpoint) {
		mDifferentialSetpoint = differentialSetpoint;
		Log.v("PrimexModel", "just set differential to " + String.valueOf(differentialSetpoint));
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

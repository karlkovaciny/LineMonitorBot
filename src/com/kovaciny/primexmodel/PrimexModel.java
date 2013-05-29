package com.kovaciny.primexmodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.NoSuchElementException;

import android.content.Context;

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
	
	public void changeCurrentLineSpeedSetpoint(double setpoint){
		if (setpoint < 0) {throw new IllegalArgumentException("Line speed setpoint less than zero");}
		
		double oldsetpoint = mSelectedLine.getLineSpeedSetpoint();
		mSelectedLine.setLineSpeedSetpoint(setpoint);
		propChangeSupport.firePropertyChange(LINE_SPEED_CHANGE_EVENT, oldsetpoint, setpoint);
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

package com.kovaciny.primexmodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

import android.content.Context;

import com.kovaciny.database.PrimexSQLiteOpenHelper;


public class PrimexModel {
	
	public PrimexModel(Context context) {
		mDbHelper = new PrimexSQLiteOpenHelper(context);
		mDbHelper.getWritableDatabase();
	}
	/*
	 * This section sets up notifying observers about changes.
	 */
	public static final String LINE_SPEED_CHANGE_EVENT = "PrimexModel.SPEED_CHANGE";
		
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
	
	private ArrayList<Integer> linesList;
	private ProductionLine mSelectedLine;
	private ArrayList<Skid> skidsList;
	private Skid mSelectedSkid;
	private Product mSelectedProduct;
	private PrimexSQLiteOpenHelper mDbHelper;
	
	public void setSelectedLine (Integer lineNumber) {
		if (linesList.contains(lineNumber)) {
			//mSelectedLine = lineNumber;
			//TODO: database call etc
		}
	}
	
	public void changeCurrentLineSpeed(double setpoint){
		if (mSelectedLine.getLineSpeed() != setpoint) {
			double oldspeed = mSelectedLine.getLineSpeed();
			double newspeed = mSelectedLine.setLineSpeed(setpoint, 1);
			propChangeSupport.firePropertyChange(LINE_SPEED_CHANGE_EVENT, oldspeed, newspeed);
		}
	}
	
	public double getCurrentLineSpeed(){
		return mSelectedLine.getLineSpeed();
	}
}

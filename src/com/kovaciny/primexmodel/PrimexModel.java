package com.kovaciny.primexmodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;


public class PrimexModel {
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
	
	private ArrayList<ProductionLine> linesList;
	private ProductionLine mSelectedLine;
	private ArrayList<Skid> skidsList;
	private Skid mSelectedSkid;
	private Product mSelectedProduct;
	
	
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

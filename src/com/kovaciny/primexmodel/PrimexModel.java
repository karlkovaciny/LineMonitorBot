package com.kovaciny.primexmodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


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
	
	
	private ProductionLine mPline = new ProductionLine(22,5,5,"blah","blah");
	
	public void changeCurrentLineSpeed(double setpoint){
		if (mPline.getLineSpeed() != setpoint) {
			double oldspeed = mPline.getLineSpeed();
			double newspeed = mPline.setLineSpeed(setpoint, 1);
			propChangeSupport.firePropertyChange(LINE_SPEED_CHANGE_EVENT, oldspeed, newspeed);
		}
	}
	
	public double getCurrentLineSpeed(){
		return mPline.getLineSpeed();
	}
	
	  /*// Simple example of how to fire an event when the value of 'foo' is changed.
	  protected void setFoo(int foo) {
	    if (this.foo != foo) {
	      // Remember previous value, assign new value and then fire event.
	      int oldFoo = this.foo;
	      this.foo = foo;
	      propChangeSupport.firePropertyChange("foo", oldFoo, this.foo);
	    }
	  }*/

}

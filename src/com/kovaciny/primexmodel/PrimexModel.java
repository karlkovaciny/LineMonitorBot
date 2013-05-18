package com.kovaciny.primexmodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class PrimexModel {
	// Create PropertyChangeSupport to manage listeners and fire events.
	  private final PropertyChangeSupport propChangeSupport = new PropertyChangeSupport(this);
	  private int foo;
	  
	  // Provide delegating methods to add / remove listeners to / from the support class.  
	  public void addPropertyChangeListener(PropertyChangeListener l) {
	    propChangeSupport.addPropertyChangeListener(l);
	  }

	  public void removePropertyChangeListener(PropertyChangeListener l) {
	    propChangeSupport.removePropertyChangeListener(l);
	  }

	  // Simple example of how to fire an event when the value of 'foo' is changed.
	  protected void setFoo(int foo) {
	    if (this.foo != foo) {
	      // Remember previous value, assign new value and then fire event.
	      int oldFoo = this.foo;
	      this.foo = foo;
	      propChangeSupport.firePropertyChange("foo", oldFoo, this.foo);
	    }
	  }
}

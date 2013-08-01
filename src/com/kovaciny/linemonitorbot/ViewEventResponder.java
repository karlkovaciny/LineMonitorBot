package com.kovaciny.linemonitorbot;

import java.beans.PropertyChangeEvent;

public interface ViewEventResponder {
	public abstract void modelPropertyChange(PropertyChangeEvent event); 

}

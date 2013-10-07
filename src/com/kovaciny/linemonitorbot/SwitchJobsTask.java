package com.kovaciny.linemonitorbot;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewSwitcher;

import com.kovaciny.primexmodel.PrimexModel;

public class SwitchJobsTask extends AsyncTask {

	Activity mActivity;
	PrimexModel mModel;
	PropertyChangeAccumulator mPropertyChangeAccumulator = new PropertyChangeAccumulator();
	int mLineNumberOrWoNumber; //didn't want to have another class to separate the two cases
	boolean mSwitchingLines;
	
	/*
	 * Takes an Activity for the context so we can check if it is valid with Activity.isFinishing.
	 */
	public SwitchJobsTask(Activity activity, PrimexModel model, int lineNumberOrWoNumber, boolean switchingLines) {
		mActivity = activity;
		mModel = model;
		mLineNumberOrWoNumber = lineNumberOrWoNumber;
		mSwitchingLines = switchingLines;
	}

	private class PropertyChangeAccumulator implements PropertyChangeListener {
		
		List<PropertyChangeEvent> mEventQueue;
		
		public PropertyChangeAccumulator() {
			mEventQueue = new ArrayList<PropertyChangeEvent>();
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.
		 * PropertyChangeEvent)
		 */
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			mEventQueue.add(event);
		}
		
		public void forwardPropertyChanges(PropertyChangeListener listener) {
			for (Iterator<PropertyChangeEvent> itr = mEventQueue.iterator(); itr.hasNext(); ) {
            	listener.propertyChange(itr.next());
            }
		}
	}
		
	@Override
	protected void onPreExecute() {
		ViewSwitcher viewSwitcherTimes = (ViewSwitcher) mActivity.findViewById(R.id.view_switcher_skid_times_fragment);
	    ViewSwitcher viewSwitcherRates = (ViewSwitcher) mActivity.findViewById(R.id.view_switcher_rates_fragment);

	    Animation fadeOut = AnimationUtils.loadAnimation(mActivity, R.anim.fade_out);
	    fadeOut.setFillAfter(true);
	    
	    viewSwitcherTimes.showNext(); //I want to animate the first view fading out, so I have to switch to it
	    viewSwitcherTimes.setAnimation(fadeOut);
	    viewSwitcherTimes.showNext(); 
	    
	    viewSwitcherRates.showNext();
	    viewSwitcherRates.setAnimation(fadeOut);
	    viewSwitcherRates.showNext();
	    
	    mModel.removePropertyChangeListener((PropertyChangeListener) mActivity);
	    mModel.addPropertyChangeListener(mPropertyChangeAccumulator);
	    super.onPreExecute();
	}
	
    @Override
    protected Object doInBackground(Object... arg0) {
    	if (mSwitchingLines) {
    		mModel.setSelectedLine(mLineNumberOrWoNumber);
		} else {
			mModel.setSelectedWorkOrder(mLineNumberOrWoNumber);			
		}
        return null;
    }

    @Override
    protected void onPostExecute(Object result) {
    	super.onPostExecute(result);
        if (!mActivity.isFinishing()) {
        	mModel.removePropertyChangeListener(mPropertyChangeAccumulator);
        	mModel.addPropertyChangeListener((PropertyChangeListener) mActivity);
        	mPropertyChangeAccumulator.forwardPropertyChanges((PropertyChangeListener) mActivity);

        	ViewSwitcher viewSwitcherTimes = (ViewSwitcher) mActivity.findViewById(R.id.view_switcher_skid_times_fragment);
    	    ViewSwitcher viewSwitcherRates = (ViewSwitcher) mActivity.findViewById(R.id.view_switcher_rates_fragment);

        	Animation fadeIn = AnimationUtils.loadAnimation(mActivity, R.anim.fade_in);
    	    viewSwitcherTimes.showPrevious();
    	    viewSwitcherTimes.setAnimation(fadeIn);
            viewSwitcherTimes.showPrevious();
            
            viewSwitcherRates.showPrevious();
            viewSwitcherRates.setAnimation(fadeIn);
            viewSwitcherRates.showPrevious();
        }
    }


}

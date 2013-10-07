package com.kovaciny.linemonitorbot;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.kovaciny.primexmodel.PrimexModel;

public class SwitchJobsTask extends AsyncTask {

	Activity mActivity;
	PrimexModel mModel;
	int mWoNumber;
	
	/*
	 * Takes an Activity for the context so we can check if it is valid with Activity.isFinishing.
	 */
	public SwitchJobsTask(Activity activity, PrimexModel model, int WoNumber) {
		mActivity = activity;
		mModel = model;
		mWoNumber = WoNumber;
	}

	@Override
	protected void onPreExecute() {
		Animation fadeOut = AnimationUtils.loadAnimation(mActivity, R.anim.fade_out);
		fadeOut.setFillAfter(true);
	    ViewSwitcher viewSwitcher = (ViewSwitcher) mActivity.findViewById(R.id.view_switcher_skid_times_fragment);
	    viewSwitcher.showNext(); //so it starts 
	    viewSwitcher.setAnimation(fadeOut);
	    viewSwitcher.showNext(); 
	    
	    
//        Toast.makeText(mActivity, "fading out", Toast.LENGTH_SHORT).show();
		super.onPreExecute();
	}
	
    @Override
    protected Object doInBackground(Object... arg0) {
    	SystemClock.sleep(3000); 
        return null;
    }

    @Override
    protected void onPostExecute(Object result) {
    	super.onPostExecute(result);
        if (!mActivity.isFinishing()) {
        	Animation fadeIn = AnimationUtils.loadAnimation(mActivity, R.anim.fade_in);
    	    ViewSwitcher viewSwitcher = (ViewSwitcher) mActivity.findViewById(R.id.view_switcher_skid_times_fragment);
    	    viewSwitcher.setAnimation(fadeIn);
            viewSwitcher.showNext();
//            Toast.makeText(mActivity, "fading in", Toast.LENGTH_SHORT).show();
        }
    }


}

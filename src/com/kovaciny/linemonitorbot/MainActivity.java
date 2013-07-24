package com.kovaciny.linemonitorbot;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.kovaciny.primexmodel.PrimexModel;
import com.kovaciny.primexmodel.Product;
import com.kovaciny.primexmodel.Products;
import com.kovaciny.primexmodel.Skid;
import com.kovaciny.primexmodel.SpeedValues;
import com.kovaciny.primexmodel.WorkOrder;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener, PropertyChangeListener, 
		SheetsPerMinuteDialogFragment.SheetsPerMinuteDialogListener {

	private static final int LINE_LIST_MENU_GROUP = 1111;
	private static final int LINE_LIST_ID_RANDOMIZER = 1234;
	private static final int JOB_LIST_MENU_GROUP = 2222;
	private static final int JOB_OPTIONS_MENU_GROUP = 2223;
	
	public static final int SKID_TIMES_FRAGMENT_POSITION = 0;
	public static final int RATES_FRAGMENT_POSITION = 1;
	public static final int DRAINING_FRAGMENT_POSITION = 2;
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	private MenuItem mJobPicker;
	private MenuItem mLinePicker;
	private PrimexModel mModel;
	   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Initialize settings on first run
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		//Create the model here so the fragments will have access to it when created.
		this.mModel = new PrimexModel(MainActivity.this);
		mModel.addPropertyChangeListener(this);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		doFirstRun();
		
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		mJobPicker = (MenuItem) menu.findItem(R.id.action_pick_job);
		mLinePicker = (MenuItem) menu.findItem(R.id.action_pick_line);
		
		//populate the line picker with line numbers from the database
		List<Integer> lineNumberList = new ArrayList<Integer>();
		lineNumberList = mModel.getLineNumbers();
				
		Menu pickLineSubMenu = mLinePicker.getSubMenu();
		pickLineSubMenu.clear();
				
		for (int i=0; i<lineNumberList.size(); i++) {
			//don't have access to View.generateViewId(), so fake a random ID
			pickLineSubMenu.add(LINE_LIST_MENU_GROUP, (lineNumberList.get(i) + LINE_LIST_ID_RANDOMIZER), Menu.FLAG_APPEND_TO_GROUP, String.valueOf(lineNumberList.get(i)));
		}
		
		if (!mModel.hasSelectedLine()) {
			mModel.loadState();
		}
		
		//populate the job picker with jobs
		Menu pickJobSubMenu = mJobPicker.getSubMenu();
		pickJobSubMenu.clear();
		List<Integer> jobList = new ArrayList<Integer>(); 
		jobList = mModel.getAllWoNumbersForLine(mModel.getSelectedLine().getLineNumber());
		
		for (int i=0; i<jobList.size(); i++) {
			CharSequence title = generateJobTitle(jobList.get(i));
			pickJobSubMenu.add(JOB_LIST_MENU_GROUP, jobList.get(i), Menu.FLAG_APPEND_TO_GROUP, title);
		}
		pickJobSubMenu.add(JOB_OPTIONS_MENU_GROUP , R.id.new_wo, Menu.FLAG_APPEND_TO_GROUP, "+ New");
		pickJobSubMenu.add(JOB_OPTIONS_MENU_GROUP , R.id.clear_wos, Menu.FLAG_APPEND_TO_GROUP, "Clear");
		
		//refresh the menu picker text from their default reinflated value
		CharSequence lineTitle = "Line " + String.valueOf(mModel.getSelectedLine().getLineNumber());
		mLinePicker.setTitle(lineTitle);
		if (mModel.hasSelectedWorkOrder()) {
			CharSequence jobTitle = generateJobTitle(mModel.getSelectedWorkOrder().getWoNumber());
			mJobPicker.setTitle(jobTitle);
		}
		return true;
	}
	
	private CharSequence generateJobTitle(int woNumber) {
		List<Integer> woNumbers = mModel.getAllWoNumbersForLine(mModel.getSelectedLine().getLineNumber());
		int position = woNumbers.indexOf(woNumber);
		return "WO " + String.valueOf(mModel.getSelectedLine().getLineNumber()) + "-" + String.valueOf(position + 1);
	}
	
	private void doFirstRun() {
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        if (settings.getLong("firstRunDate", -1) == -1) {
//            showDummyDialog("Welcome to LineMonitorBot!");
        	SharedPreferences.Editor editor = settings.edit();
            editor.putLong("firstRunDate", new Date().getTime());
            editor.commit();
        }
}
	public void showSheetsPerMinuteDialog() {
		// Create the fragment and show it as a dialog.
		SheetsPerMinuteDialogFragment newFragment = new SheetsPerMinuteDialogFragment();
		Product currentProd = mModel.getSelectedWorkOrder().getProduct();
		SpeedValues curSpeed = mModel.getSelectedLine().getSpeedValues();
		Bundle args = new Bundle();
		args.putDouble("SpeedFactor", curSpeed.speedFactor);
		args.putDouble("LineSpeed", curSpeed.lineSpeedSetpoint);
		args.putDouble("DifferentialSpeed", curSpeed.differentialSpeed);
		if (currentProd != null) {
			args.putDouble("Gauge", currentProd.getGauge());
			args.putDouble("SheetWidth", currentProd.getWidth());
			args.putDouble("SheetLength", currentProd.getLength());			
			args.putString("ProductType", currentProd.getType());
		}
		newFragment.setArguments(args);
		newFragment.show(this.getFragmentManager(),
				"SheetsPerMinuteDialog");
	}
	// Implementing interface for SheetsPerMinuteDialogFragment
    public void onClickPositiveButton(DialogFragment d) {
    	if (d.getTag() == "SheetsPerMinuteDialog") {
    		SheetsPerMinuteDialogFragment spmd = (SheetsPerMinuteDialogFragment)d;
    		
    		double lineSpeed = spmd.getLineSpeedValue();
    		double diffSpeed = spmd.getDifferentialSpeedValue();
    		double speedFactor = spmd.getSpeedFactorValue();
    		if ( !(lineSpeed > 0) ) lineSpeed = 0;
    		if ( !(diffSpeed > 0) ) diffSpeed = 0;
    		if ( !(speedFactor > 0) ) speedFactor = 0;
    		updateSpeedData(lineSpeed, diffSpeed, speedFactor);
    		
    		String productType;
    		if (spmd.getSheetsOrRollsState().equals(SheetsPerMinuteDialogFragment.ROLLS_MODE)) {
    			productType = Product.ROLLS_TYPE;
    		} else {
    			productType = Product.SHEETS_TYPE;
    		}
    		double gauge = spmd.getGauge();
    		double width = spmd.getSheetWidthValue();
    		double length = spmd.getSheetLengthValue();
    		if ( !(gauge > 0)) gauge = 0;
    		if ( !(width > 0)) width = 0;
    		if ( !(length > 0)) length = 0;
    		updateProductData(productType, gauge, width, length);
    	}
    }	
    
	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event == null) {
			throw new RuntimeException("something threw a null event");
		}
		String eventName = event.getPropertyName();
		Log.v("VERBOSE", eventName);
		Object newProperty = event.getNewValue();
		String objectDescription = (newProperty == null) ? "null" : newProperty.toString();
		Log.v("VERBOSE", objectDescription);
		
		SkidTimesFragment skidTimesFrag = (SkidTimesFragment) this.findFragmentByPosition(MainActivity.SKID_TIMES_FRAGMENT_POSITION);
		RatesFragment ratesFrag = (RatesFragment) this.findFragmentByPosition(MainActivity.RATES_FRAGMENT_POSITION);
		if (ratesFrag == null) throw new RuntimeException("Rates fragment not found");
		
		if (eventName == PrimexModel.SELECTED_LINE_CHANGE_EVENT) {
			CharSequence lineTitle = "Line " + String.valueOf(mModel.getSelectedLine().getLineNumber());
			mLinePicker.setTitle(lineTitle);
			ratesFrag.modelPropertyChange(event);

		} else if (eventName == PrimexModel.SELECTED_WO_CHANGE_EVENT) {
			WorkOrder newWo = (WorkOrder)newProperty; 
			CharSequence woTitle = generateJobTitle(newWo.getWoNumber());
			mJobPicker.setTitle(woTitle);
			invalidateOptionsMenu();

			skidTimesFrag.modelPropertyChange(event);
			ratesFrag.modelPropertyChange(event);
			
		} else if (eventName == PrimexModel.NEW_WORK_ORDER_EVENT) { //not safe to fire without a selected WO
			int newWonum = ((WorkOrder)newProperty).getWoNumber();
			CharSequence newTitle = generateJobTitle(newWonum);
			mJobPicker.getSubMenu().add(JOB_LIST_MENU_GROUP, newWonum, Menu.FLAG_APPEND_TO_GROUP, newTitle);
			invalidateOptionsMenu();
			skidTimesFrag.modelPropertyChange(event);
		} else if (eventName == PrimexModel.PRODUCT_CHANGE_EVENT) {
			skidTimesFrag.modelPropertyChange(event);
			ratesFrag.modelPropertyChange(event);
		} else if (eventName == PrimexModel.PRODUCTS_PER_MINUTE_CHANGE_EVENT) {
			skidTimesFrag.modelPropertyChange(event);
			
		} else if ((eventName == PrimexModel.CURRENT_SKID_FINISH_TIME_CHANGE_EVENT) ||
				(eventName == PrimexModel.JOB_FINISH_TIME_CHANGE_EVENT) ||
				(eventName == PrimexModel.LINE_SPEED_CHANGE_EVENT) ||
				(eventName == PrimexModel.SECONDS_TO_MAXSON_CHANGE_EVENT) ||
				(eventName == PrimexModel.SKID_CHANGE_EVENT)) {
			skidTimesFrag.modelPropertyChange(event);
		} else if (eventName == PrimexModel.CURRENT_SKID_START_TIME_CHANGE_EVENT) {
			skidTimesFrag.modelPropertyChange(event);
		} else if (eventName == PrimexModel.MINUTES_PER_SKID_CHANGE_EVENT) {
			skidTimesFrag.modelPropertyChange(event);
		} else if (eventName == PrimexModel.NUMBER_OF_SKIDS_CHANGE_EVENT){
			skidTimesFrag.modelPropertyChange(event);
		} else if ((eventName == PrimexModel.EDGE_TRIM_RATIO_CHANGE_EVENT) ||
				(eventName == PrimexModel.NET_PPH_CHANGE_EVENT) || 
				(eventName == PrimexModel.GROSS_PPH_CHANGE_EVENT) || 
				(eventName == PrimexModel.GROSS_WIDTH_CHANGE_EVENT) || 
				(eventName == PrimexModel.NOVATEC_CHANGE_EVENT) || 
				(eventName == PrimexModel.COLOR_PERCENT_CHANGE_EVENT)) {
			ratesFrag.modelPropertyChange(event);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if (item.getGroupId() == LINE_LIST_MENU_GROUP) {
			mModel.setSelectedLine(item.getItemId() - LINE_LIST_ID_RANDOMIZER);
		}
		if (item.getGroupId() == JOB_LIST_MENU_GROUP) {
			mModel.setSelectedWorkOrder(item.getItemId());
		}
		
		switch (item.getItemId()) {
		case R.id.new_wo:
			mModel.setSelectedWorkOrder(mModel.addWorkOrder().getWoNumber());
	        break;
		case R.id.clear_wos:
			//clear the menu
			mJobPicker.getSubMenu().removeGroup(JOB_LIST_MENU_GROUP);
			
			//clear the menu title
			mJobPicker.setTitle(R.string.action_pick_job_title);
			
			//clear the database
			mModel.clearWoNumbers();
			
			//make sure a new WO always exists
			mModel.setSelectedWorkOrder(mModel.addWorkOrder().getWoNumber());
			break;
		case R.id.action_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			this.startActivity(intent);
			break;
	    default:
	    }
		return super.onOptionsItemSelected(item);
	}
	
	public void updateSkidData(Integer skidNumber, Integer currentCount, Integer totalCount, Integer numberOfSkids) {
		mModel.changeNumberOfSkids(numberOfSkids);
		
		Skid<Product> skid = new Skid<Product>(
				currentCount, 
				totalCount,
				1);
		skid.setSkidNumber(skidNumber);
		mModel.getSelectedWorkOrder().addOrUpdateSkid(skid);
		mModel.changeSelectedSkid(skidNumber);
		mModel.saveSkid(skid);	

		mModel.calculateRates();
		mModel.calculateTimes();
	}
	
	protected void updateProductData(String productType, double gauge, double width, double length) {
		Product p;
		try {
			p = Products.makeProduct(productType, gauge, width, length);
		} catch (IllegalArgumentException e) {
			if (e.getCause().equals(PrimexModel.ERROR_NO_PRODUCT_SELECTED)) {
				throw new IllegalStateException(new Throwable(PrimexModel.ERROR_NO_PRODUCT_SELECTED));	
			} else throw e;	
		}
		mModel.changeProduct(p);
	}
	
	protected void updateSpeedData(double lineSpeed, double diffSpeed, double speedFactor) {
		SpeedValues sv = new SpeedValues(lineSpeed, diffSpeed, speedFactor);
		mModel.setCurrentSpeed(sv);	
	}
	
	public void updateRatesData(Double grossWidth, Double unitWeight, Double novaSetpoint) {
		mModel.getSelectedLine().setWebWidth(grossWidth);
		mModel.getSelectedLine().getNovatec().setControllerSetpoint(novaSetpoint); //TODO ug...ly.
		if (mModel.hasSelectedProduct()) {
			Product p = mModel.getSelectedWorkOrder().getProduct();
			p.setUnitWeight(unitWeight);
			mModel.changeProduct(p);
		}
		mModel.saveSelectedLine();
		mModel.calculateRates();
	}
	
/*
 * ---------------------------------------------------------
 * start of functions I never change
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */
	@Override
	public void onTabSelected(ActionBar.Tab tab,
		android.app.FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		int pos = tab.getPosition();
		mViewPager.setCurrentItem(pos);
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			android.app.FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			android.app.FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
						
			// Return a SectionFragment with the page number as its lone argument.
			Fragment fragment;
			Bundle args = new Bundle();
			
			switch(position){
			case SKID_TIMES_FRAGMENT_POSITION:
				fragment = new SkidTimesFragment();
				break;
			case RATES_FRAGMENT_POSITION: 
				fragment = new RatesFragment();
				break;
			case DRAINING_FRAGMENT_POSITION:
				fragment = new DrainingFragment();
				break;
			default:
				fragment = new SectionFragment();
			}
			
			args.putInt(SectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	} //end SectionsPagerAdapter class
	
	
	
	
	
	
	
	public Fragment findFragmentByPosition(int pos) {
		String tag = "android:switcher:" + mViewPager.getId() + ":" + pos;
		return getSupportFragmentManager().findFragmentByTag(tag);
	}
	
/*
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * end of functions I never change
 * ---------------------------------------
 */

	@Override
	protected void onPause() {
		super.onPause();
		//store persistent data
		SharedPreferences settings = getPreferences(MODE_PRIVATE);
	    SharedPreferences.Editor editor = settings.edit();
	    
	    editor.putInt("databaseVersion", mModel.getDatabaseVersion());

	    // Commit the edits!
	    editor.commit();

	    //Release the database?
	    //mModel.closeDb(); TODO, cause lag
	}
	
	@Override
	protected void onStop() {
		mModel.saveState();
		super.onStop();		
	}

	void showDummyDialog(String text) {
	    // Create the fragment and show it as a dialog.
	    DialogFragment newFragment = MyDialogFragment.newInstance(text);
	    newFragment.show(getFragmentManager(), "dialog");
	}

	public void hideKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager)this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        View focus = this.getCurrentFocus();
        if(focus != null) {inputMethodManager.hideSoftInputFromWindow(focus.getWindowToken(), 0);
        }
    }
}

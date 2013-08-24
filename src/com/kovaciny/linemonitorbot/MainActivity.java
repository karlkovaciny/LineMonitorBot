package com.kovaciny.linemonitorbot;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.kovaciny.database.PrimexSQLiteOpenHelper;
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
	
	public static final int POSITION_NONE = -1;
	public static final int SKID_TIMES_FRAGMENT_POSITION = 0;
	public static final int RATES_FRAGMENT_POSITION = 1;
	public static final int DRAINING_FRAGMENT_POSITION = 2;
	public static final int CALCULATOR_FRAGMENT_POSITION = POSITION_NONE;
	
	public static final String ERROR_FRAGMENT_NOT_FOUND = "MainActivity.Fragment not found";
	
	public static final boolean DEBUG = true;
	
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
	public PrimexModel mModel; //only public so I can do testing!
	
	private ActionBar mActionBar;
	   
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
		
	     // Float a calculator over all tabs by replacing the main content.
	     getSupportFragmentManager().beginTransaction()
	             .replace(android.R.id.content, new CalculatorFragment())
	             .commit();
	     
		mActionBar = actionBar;
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
			SharedPreferences settings = getPreferences(MODE_PRIVATE);
			int lineNum = settings.getInt("lastSelectedLine", 18);
			mModel.setSelectedLine(lineNum);
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
		
		if (DEBUG) {
			MenuItem viewDatabase = (MenuItem)menu.findItem(R.id.action_view_database);
			viewDatabase.setVisible(true);
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
        	AlertDialog.Builder builder = new AlertDialog.Builder(this).setMessage(
					"Welcome to LineMonitorBot!\n\n" +
					"* Pick your line from the top menu \n" +
					"* Then, enter your product details.\n\n" +
					"LineMonitorBot will\n\n" +
					"* Know when your skids are done\n" +
					"* Remind you by vibrating with 90 seconds left (optional)\n" +
					"* Calculate your run rates.");
			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// Do nothing					
				}
			});
			builder.show();
        	
        	SharedPreferences.Editor editor = settings.edit();
            editor.putLong("firstRunDate", new Date().getTime());
            editor.commit();
        }
}
	public Bundle showSheetsPerMinuteDialog() {
		// Create the fragment and show it as a dialog.
		SheetsPerMinuteDialogFragment newFragment = new SheetsPerMinuteDialogFragment();
		Product currentProd = mModel.getSelectedWorkOrder().getProduct();
		Bundle args = new Bundle();
		args.putDouble("SpeedFactor", mModel.getSelectedLine().getSpeedValues().speedFactor); //TODO it will bite me that these aren't all in WO
		
		//Load the line setpoints for this work order or if none, the last ones used on this line.
		Double lineSpeedSetpoint = mModel.getLineSpeedSetpoint();
		if (lineSpeedSetpoint == 0d) {
			lineSpeedSetpoint = mModel.getSelectedLine().getSpeedValues().lineSpeedSetpoint;
		}
		args.putDouble("LineSpeed", lineSpeedSetpoint);
		Log.v("MainActivity.showSheetsPerMinuteDialog()", "Just sent line speed " + mModel.getLineSpeedSetpoint() + "from line " + 
			mModel.getSelectedLine().getLineNumber() +	"to dialog");
		
		Double differentialSpeed = mModel.getDifferentialSetpoint();
		if (differentialSpeed == 0d) {
			differentialSpeed = mModel.getSelectedLine().getSpeedValues().differentialSpeed; //TODO seems ugly
		}
		
		args.putInt("NumberOfSkids", mModel.getNumberOfTableSkids());
		
		args.putDouble("DifferentialSpeed", differentialSpeed);
		args.putDouble("DifferentialLowValue", mModel.getSelectedLine().getDifferentialRangeLow());
		args.putDouble("DifferentialHighValue", mModel.getSelectedLine().getDifferentialRangeHigh());
		args.putString("SpeedControllerType", mModel.getSelectedLine().getSpeedControllerType());
		
		if (currentProd != null) {
			args.putDouble("Gauge", currentProd.getGauge());
			args.putDouble("SheetWidth", currentProd.getWidth()/currentProd.getNumberOfWebs());
			args.putDouble("SheetLength", currentProd.getLength());			
			args.putString("ProductType", currentProd.getType());
			args.putInt("NumberOfWebs", currentProd.getNumberOfWebs());
		}
		newFragment.setArguments(args);
		newFragment.show(this.getFragmentManager(),
				"SheetsPerMinuteDialog");
		return args; //for unit testing
	}
	
	// Implementing interface for SheetsPerMinuteDialogFragment
    public void onClickPositiveButton(DialogFragment d) {
    	if (d.getTag() == "SheetsPerMinuteDialog") {
    		SheetsPerMinuteDialogFragment spmd = (SheetsPerMinuteDialogFragment)d;
    		
    		double gauge = spmd.getGauge();
    		double width = spmd.getSheetWidthValue();
    		double length = spmd.getSheetLengthValue();
    		double lineSpeed = spmd.getLineSpeedValue();
    		double diffSpeed = spmd.getDifferentialSpeedValue();
    		double speedFactor = spmd.getSpeedFactorValue();
    		
    		mModel.setNumberOfTableSkids(spmd.getNumberOfSkids());
    		updateSpeedData(lineSpeed, diffSpeed, speedFactor);
    		String productType;
    		if (spmd.getSheetsOrRollsState().equals(SheetsPerMinuteDialogFragment.ROLLS_MODE)) {
    			if (spmd.getNumberOfWebs() == 1) {
    				productType = Product.ROLLS_TYPE;
    			} else {
    				productType = Product.ROLLSET_TYPE;
    			}
    		} else {
    			productType = Product.SHEETS_TYPE;
    		}
    		updateProductData(productType, gauge, width, length, spmd.getNumberOfWebs());
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
		
		SkidTimesFragment skidTimesFrag;
		RatesFragment ratesFrag;
		try {
			skidTimesFrag = (SkidTimesFragment) this.findFragmentByPosition(MainActivity.SKID_TIMES_FRAGMENT_POSITION);
			ratesFrag = (RatesFragment) this.findFragmentByPosition(MainActivity.RATES_FRAGMENT_POSITION);
		} catch (IllegalStateException e) {
			if (e.getCause().getMessage() == ERROR_FRAGMENT_NOT_FOUND) {
				return; //too early to handle events
			} else throw e;
		}
		
		String eventName = event.getPropertyName();
		Log.v("Event", eventName);
		Object newProperty = event.getNewValue();
		String objectDescription = (newProperty == null) ? "null" : newProperty.toString();
		Log.v("Event value", objectDescription);
		
		
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
				(eventName == PrimexModel.NUMBER_OF_WEBS_CHANGE_EVENT) ||
				(eventName == PrimexModel.NUMBER_OF_TABLE_SKIDS_CHANGE_EVENT) ||
				(eventName == PrimexModel.SKID_CHANGE_EVENT )) {
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
			ClearWorkOrdersDialogFragment clearDialog = new ClearWorkOrdersDialogFragment();
			clearDialog.show(getFragmentManager(), "clearDialog");
			break;
		case R.id.action_view_database:
			Intent dbintent = new Intent(this, DatabaseViewerActivity.class);
			this.startActivity(dbintent);
			break;
		case R.id.action_settings:
			Intent settingsIntent = new Intent(this, SettingsActivity.class);
			this.startActivity(settingsIntent);
			break;
	    default:
	    }
		return super.onOptionsItemSelected(item);
	}
	
	public void clearWos() {
		//clear the menu
		mJobPicker.getSubMenu().removeGroup(JOB_LIST_MENU_GROUP);

		//clear the menu title
		mJobPicker.setTitle(R.string.action_pick_job_title);			
				
		//clear the database
		mModel.deleteWorkOrders();
		
		//make sure a new WO always exists
		mModel.setSelectedWorkOrder(mModel.addWorkOrder().getWoNumber());
	}
		
	public void updateSkidData(Integer skidNumber, Integer currentCount, Integer totalCount, Double numberOfSkids) {
		mModel.changeNumberOfSkids(numberOfSkids);
		
		Skid<Product> skid = new Skid<Product>(
				currentCount, 
				totalCount,
				1);
		skid.setSkidNumber(skidNumber);
		mModel.getSelectedWorkOrder().addOrUpdateSkid(skid);
		mModel.changeSelectedSkid(skidNumber);
		mModel.saveSkid(skid);	

		mModel.calculateTimes();
	}
	
	protected void updateProductData(String productType, double gauge, double width, double length, int numberOfWebs) {
		Product p;
		try {
			p = Products.makeProduct(productType, gauge, width, length, numberOfWebs);
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
			if (p.getType().equals(Product.ROLLSET_TYPE)) {
				unitWeight *= p.getNumberOfWebs();
			}
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
			case CALCULATOR_FRAGMENT_POSITION:
				fragment = new CalculatorFragment();
				break;
			default:
				fragment = new DrainingFragment(); //better never get here!
			}
			
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
			case SKID_TIMES_FRAGMENT_POSITION:
				return getString(R.string.title_fragment_skid_times).toUpperCase(l);
			case RATES_FRAGMENT_POSITION:
				return getString(R.string.title_fragment_rates).toUpperCase(l);
			case DRAINING_FRAGMENT_POSITION:
				return getString(R.string.title_fragment_draining).toUpperCase(l);
			case CALCULATOR_FRAGMENT_POSITION:
				return getString(R.string.title_fragment_calculator_button).toUpperCase(l);
			}
			return null;
		}
	} //end SectionsPagerAdapter class
	
	
	
	
	
	
	
	public Fragment findFragmentByPosition(int pos) {
		String tag = "android:switcher:" + mViewPager.getId() + ":" + pos;
		Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
		if (fragment == null) {
			throw new IllegalStateException(new Throwable(ERROR_FRAGMENT_NOT_FOUND));
		} 
		return fragment;
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
	    if (mModel.hasSelectedLine()) {
	    	editor.putInt("lastSelectedLine", mModel.getSelectedLine().getLineNumber());
	    }

	    // Commit the edits!
	    editor.commit();

	    //Release the database?
	    //mModel.closeDb(); TODO, cause lag
	}
	
	@Override
	protected void onStop() {
		try { 
			mModel.saveState();
		} catch (IllegalStateException e) {
			if (DEBUG) Toast.makeText(this, "Saving state error: " + e.getCause().getMessage(), Toast.LENGTH_LONG).show();
			else throw e;
		}
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

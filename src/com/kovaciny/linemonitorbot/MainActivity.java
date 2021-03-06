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
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.kovaciny.helperfunctions.HelperFunction;
import com.kovaciny.linemonitorbot.GoByHeightDialogFragment.GoByHeightDialogListener;
import com.kovaciny.primexmodel.PrimexModel;
import com.kovaciny.primexmodel.Roll;
import com.kovaciny.primexmodel.WorkOrder;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener, PropertyChangeListener,
		EnterProductDialogFragment.EnterProductDialogListener, 
		GoByHeightDialogListener, View.OnClickListener {
    
    public static final boolean DEBUG = false;

	private static final int LINE_LIST_MENU_GROUP = 1111;
	private static final int LINE_LIST_ID_RANDOMIZER = 1234; //TODO replace this by adding line number to Menu.FIRST
	private static final int JOB_LIST_MENU_GROUP = 2222;
	private static final int JOB_OPTIONS_MENU_GROUP = 2223;
	private static final int SELECT_LINE_INTENT_CODE = 1;
	private static final int DEFAULT_LINE_NUMBER = 11;
	
	public static final int POSITION_NONE = -1;
	public static final int SKID_TIMES_FRAGMENT_POSITION = 0;
	public static final int RATES_FRAGMENT_POSITION = 1;
	public static final int DRAINING_FRAGMENT_POSITION = 2;
	public static final int CALCULATOR_FRAGMENT_POSITION = POSITION_NONE;

	public static final String ERROR_FRAGMENT_NOT_FOUND = "MainActivity.Fragment not found";
	
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
	 * Only public for testing.
	 */
	public ViewPager mViewPager;

	private MenuItem mJobPicker;
	private MenuItem mLinePicker;
	public PrimexModel mModel; // only public so I can do testing!
	private boolean mForceUserToSelectLine;
	private boolean mShowTutorial;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*removed to eliminate Internet permission
		 * if (!DEBUG) {
		    BugSenseHandler.initAndStartSession(this, "2100e887");
		}*/
		setContentView(R.layout.activity_main);
		
		// Initialize settings on first run
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the model here so the fragments will have access to it when
		// created.
		this.mModel = new PrimexModel(MainActivity.this);
		mModel.addPropertyChangeListener(this);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOffscreenPageLimit(2); //so that all fragments will always be available

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

		//Load the tab we were last on
		SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
		int lastTabPos = settings.getInt("LastSelectedTabPosition", -1);
		if ((lastTabPos == SKID_TIMES_FRAGMENT_POSITION) || 
				(lastTabPos == RATES_FRAGMENT_POSITION) || 
				(lastTabPos == DRAINING_FRAGMENT_POSITION)) {
			mViewPager.setCurrentItem(lastTabPos);			
		}
	}

    @Override
    protected void onStart() {
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        long lastRun = settings.getLong("mostRecentStartDate", 0);
        long now = new Date().getTime();
        if ( (lastRun > 0) && ((now - lastRun) > (12 * HelperFunction.ONE_HOUR_IN_MILLIS) )) {
            mModel.deleteWorkOrders();
            mForceUserToSelectLine = true;
        }
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("mostRecentStartDate", now);
        editor.commit();
        super.onStart();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		doFirstRun();

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		mJobPicker = (MenuItem) menu.findItem(R.id.action_pick_job);
		mLinePicker = (MenuItem) menu.findItem(R.id.action_pick_line);
		
		// populate the line picker with line numbers from the database
		List<Integer> lineNumberList = new ArrayList<Integer>();
		lineNumberList = mModel.getLineNumbers();

		Menu pickLineSubMenu = mLinePicker.getSubMenu();
		pickLineSubMenu.clear();

		for (int i = 0; i < lineNumberList.size(); i++) {
			// don't have access to View.generateViewId(), so fake a random ID
			pickLineSubMenu.add(LINE_LIST_MENU_GROUP,
					(lineNumberList.get(i) + LINE_LIST_ID_RANDOMIZER),
					Menu.FLAG_APPEND_TO_GROUP,
					"Line  " + String.valueOf(lineNumberList.get(i)));
		}

		if (!mModel.hasSelectedLine()) {
		    //that is, on launch, but not on invalidateOptionsMenu.
		    if (mForceUserToSelectLine) {
		        mForceUserToSelectLine = false;
		        Intent selectLineIntent = new Intent(this, FirstLaunchActivity.class);
		        selectLineIntent.putExtra("ShowTutorial", mShowTutorial);
	            this.startActivityForResult(selectLineIntent, SELECT_LINE_INTENT_CODE);
	            mModel.setSelectedLine(11);
		    } else {
		        SharedPreferences settings = getPreferences(MODE_PRIVATE);
		        int lineNum = settings.getInt("lastSelectedLine", DEFAULT_LINE_NUMBER);
		        mModel.setSelectedLine(lineNum);
		    }
		}

		// populate the job picker with jobs
		Menu pickJobSubMenu = mJobPicker.getSubMenu();
		pickJobSubMenu.clear();
		List<Integer> jobList = new ArrayList<Integer>();
		jobList = mModel.getAllWoNumbersForLine(mModel.getSelectedLine()
				.getLineNumber());

		for (int i = 0; i < jobList.size(); i++) {
			CharSequence title = generateJobTitle(jobList.get(i));
			pickJobSubMenu.add(JOB_LIST_MENU_GROUP, jobList.get(i),
					Menu.FLAG_APPEND_TO_GROUP, title);
		}
		pickJobSubMenu.add(JOB_OPTIONS_MENU_GROUP, R.id.new_wo,
				Menu.FLAG_APPEND_TO_GROUP, "+ New");
		pickJobSubMenu.add(JOB_OPTIONS_MENU_GROUP, R.id.clear_wos,
				Menu.FLAG_APPEND_TO_GROUP, "Clear");

		// refresh the menu picker text from their default reinflated value
		CharSequence lineTitle = "Line "
				+ String.valueOf(mModel.getSelectedLine().getLineNumber());
		mLinePicker.setTitle(lineTitle);
		if (mModel.hasSelectedWorkOrder()) {
			CharSequence jobTitle = generateJobTitle(mModel
					.getSelectedWorkOrder().getWoNumber());
			mJobPicker.setTitle(jobTitle);
		}

		if (DEBUG) {
			MenuItem action_viewDatabase = (MenuItem) menu
					.findItem(R.id.action_view_database);
			action_viewDatabase.setVisible(true);
		}

		return true;
	}

	private CharSequence generateJobTitle(int woNumber) {
	    List<Integer> woNumbers = mModel.getAllWoNumbersForLine(mModel
	            .getSelectedLine().getLineNumber());
	    int position = woNumbers.indexOf(woNumber);
	    return "WO " + String.valueOf(mModel.getSelectedLine().getLineNumber())
	            + "-" + String.valueOf(position + 1);
	}

	private void doFirstRun() {
	    SharedPreferences settings = getPreferences(MODE_PRIVATE);
	    if (settings.getLong("firstRunDate", -1) == -1) {
	        mForceUserToSelectLine = true;
	        mShowTutorial = true;
	        
	        SharedPreferences.Editor editor = settings.edit();
	        editor.putLong("firstRunDate", new Date().getTime());
	        editor.commit();
	    }
	}

    // Implementing interface for DialogFragments
    public void onClickPositiveButton(DialogFragment d) {
        DialogController dc = new DialogController(this);
        dc.onClickPositiveButton(d);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        	case (R.id.imgbtn_launch_skids_list):
        		Intent skidsListIntent = new Intent(this, SkidsListActivity.class);
        		skidsListIntent.putExtra("woNumber", mModel.getSelectedWorkOrder().getWoNumber());
        		mModel.saveState(); //TODO this is needed because onStop() gets called too late
        		this.startActivity(skidsListIntent);
        		break;
	    	case (R.id.btn_roll_math):
                Intent rollMathIntent = new Intent(this, RollMathActivity.class);

                Roll roll = (Roll) mModel.getSelectedWorkOrder().getProduct();
                rollMathIntent.putExtra("coreType", roll.getCoreType());
                rollMathIntent.putExtra("width", (float) (roll.getWidth() / Double.valueOf(roll.getNumberOfWebs())));
                rollMathIntent.putExtra("footWeight", (float) (roll.getUnitWeight() / Double.valueOf(roll.getNumberOfWebs())));
                
                EditText editFeet = (EditText) this.findViewById(R.id.edit_total_sheets_per_skid);
                if (editFeet.getText().length() > 0) {
                    rollMathIntent.putExtra("linearFeet", Integer.valueOf(editFeet.getText().toString()));
                }
                
                this.startActivity(rollMathIntent);
                break;
        }
    }


    /*
	 * (non-Javadoc)
	 * 
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.
	 * PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		Fragment stf = this.findFragmentByPosition(MainActivity.SKID_TIMES_FRAGMENT_POSITION);
		Fragment rf = this.findFragmentByPosition(MainActivity.RATES_FRAGMENT_POSITION);
		SkidTimesFragment skidTimesFrag = (SkidTimesFragment) stf;
		RatesFragment ratesFrag = (RatesFragment) rf;
		
		String eventName = event.getPropertyName();
		Object newProperty = event.getNewValue();
		
		if (eventName == PrimexModel.SELECTED_LINE_CHANGE_EVENT) {
			CharSequence lineTitle = "Line "
					+ String.valueOf(mModel.getSelectedLine().getLineNumber());
			mLinePicker.setTitle(lineTitle);
			ratesFrag.modelPropertyChange(event);

		} else if (eventName == PrimexModel.SELECTED_WO_CHANGE_EVENT) {
            HelperFunction.hideKeyboard(this);

            WorkOrder newWo = (WorkOrder) newProperty;
			CharSequence woTitle = generateJobTitle(newWo.getWoNumber());
			mJobPicker.setTitle(woTitle);
			invalidateOptionsMenu();

			skidTimesFrag.modelPropertyChange(event);
			ratesFrag.modelPropertyChange(event);
			
		} else if (eventName == PrimexModel.NEW_WORK_ORDER_EVENT) { // not safe
																	// to fire
																	// without a
																	// selected
																	// WO
			int newWonum = ((WorkOrder) newProperty).getWoNumber();
			CharSequence newTitle = generateJobTitle(newWonum);
			mJobPicker.getSubMenu().add(JOB_LIST_MENU_GROUP, newWonum,
					Menu.FLAG_APPEND_TO_GROUP, newTitle);
			invalidateOptionsMenu();

			skidTimesFrag.modelPropertyChange(event);
		} else if (eventName == PrimexModel.PRODUCT_CHANGE_EVENT) {
			skidTimesFrag.modelPropertyChange(event);
			ratesFrag.modelPropertyChange(event);
		} else if (eventName == PrimexModel.PRODUCTS_PER_MINUTE_CHANGE_EVENT) {
			skidTimesFrag.modelPropertyChange(event);

		} else if ((eventName == PrimexModel.CURRENT_SKID_FINISH_TIME_CHANGE_EVENT)
				|| (eventName == PrimexModel.JOB_FINISH_TIME_CHANGE_EVENT)
				|| (eventName == PrimexModel.LINE_SPEED_CHANGE_EVENT)
				|| (eventName == PrimexModel.SECONDS_TO_MAXSON_CHANGE_EVENT)
				|| (eventName == PrimexModel.NUMBER_OF_WEBS_CHANGE_EVENT)
				|| (eventName == PrimexModel.NUMBER_OF_TABLE_SKIDS_CHANGE_EVENT)
				|| (eventName == PrimexModel.SKID_CHANGE_EVENT)) {
			skidTimesFrag.modelPropertyChange(event);
		} else if (eventName == PrimexModel.CURRENT_SKID_START_TIME_CHANGE_EVENT) {
			skidTimesFrag.modelPropertyChange(event);
		} else if (eventName == PrimexModel.MINUTES_PER_SKID_CHANGE_EVENT) {
			skidTimesFrag.modelPropertyChange(event);
		} else if (eventName == PrimexModel.NUMBER_OF_SKIDS_CHANGE_EVENT) {
			skidTimesFrag.modelPropertyChange(event);
		} else if ((eventName == PrimexModel.EDGE_TRIM_RATIO_CHANGE_EVENT)
				|| (eventName == PrimexModel.NET_PPH_CHANGE_EVENT)
				|| (eventName == PrimexModel.GROSS_PPH_CHANGE_EVENT)
				|| (eventName == PrimexModel.GROSS_WIDTH_CHANGE_EVENT)
				|| (eventName == PrimexModel.NOVATEC_CHANGE_EVENT)
				|| (eventName == PrimexModel.TEN_SECOND_LETDOWN_CHANGE_EVENT)
		        || (eventName == PrimexModel.COLOR_PERCENT_CHANGE_EVENT)) {
			ratesFrag.modelPropertyChange(event);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getGroupId() == LINE_LIST_MENU_GROUP) {
			int lineNumber = item.getItemId() - LINE_LIST_ID_RANDOMIZER;
			SwitchJobsTask task = new SwitchJobsTask(this, this.mModel, lineNumber, true);
			task.execute();
		}
		if (item.getGroupId() == JOB_LIST_MENU_GROUP) {
			SwitchJobsTask task = new SwitchJobsTask(this, this.mModel, item.getItemId(), false);
			task.execute();
		}

		switch (item.getItemId()) {
		case R.id.new_wo:
			SwitchJobsTask task = new SwitchJobsTask(this, this.mModel, mModel.addWorkOrder().getWoNumber(), false);
			task.execute();
			break;
		case R.id.clear_wos:
			ClearWorkOrdersDialogFragment clearDialog = new ClearWorkOrdersDialogFragment();
			clearDialog.show(getFragmentManager(), "clearDialog");
			break;
		case R.id.action_view_database:
			Intent dbintent = new Intent(this, DatabaseViewerActivity.class);
			this.startActivity(dbintent);
			break;
		case R.id.action_tips_and_apps:
		    Intent tipsIntent = new Intent(this, TipsActivity.class);
		    this.startActivity(tipsIntent);
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
		// clear the menu
		mJobPicker.getSubMenu().removeGroup(JOB_LIST_MENU_GROUP);

		// clear the menu title
		mJobPicker.setTitle(R.string.action_pick_job_title);

		// clear the database, in case management worries we're collecting their data
		mModel.deleteWorkOrders();

		// make sure a new WO always exists
		mModel.setSelectedWorkOrder(mModel.addWorkOrder().getWoNumber());
	}

	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_LINE_INTENT_CODE) {
            if (resultCode == RESULT_FIRST_USER) {
                int lineNumber = data.getIntExtra("LineNumber", DEFAULT_LINE_NUMBER);
                mModel.setSelectedLine(lineNumber);
            } else if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*
	 * --------------------------------------------------------- start of
	 * functions I never change
	 */
	@Override
	public void onTabSelected(ActionBar.Tab tab,
			android.app.FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		int pos = tab.getPosition();
		mViewPager.setCurrentItem(pos);
		HelperFunction.hideKeyboard(this);
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

			// Return a SectionFragment with the page number as its lone
			// argument.
			Fragment fragment;
			
			switch (position) {
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
				fragment = new FloatingToolbarFragment();
				break;
			default:
				fragment = new DrainingFragment(); // better never get here!
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
				return getString(R.string.title_fragment_skid_times)
						.toUpperCase(l);
			case RATES_FRAGMENT_POSITION:
				return getString(R.string.title_fragment_rates).toUpperCase(l);
			case DRAINING_FRAGMENT_POSITION:
				return getString(R.string.title_fragment_draining).toUpperCase(
						l);
			case CALCULATOR_FRAGMENT_POSITION:
				return getString(R.string.title_fragment_calculator_button)
						.toUpperCase(l);
			}
			return null;
		}
	} // end SectionsPagerAdapter class

	public Fragment findFragmentByPosition(int pos) {
		String tag = "android:switcher:" + mViewPager.getId() + ":" + pos;
		Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
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
	 * end of functions I never change ---------------------------------------
	 */

	@Override
	protected void onPause() {
		super.onPause();
		// store persistent data
		SharedPreferences settings = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();

		editor.putInt("databaseVersion", mModel.getDatabaseVersion());
		if (mModel.hasSelectedLine()) {
			editor.putInt("lastSelectedLine", mModel.getSelectedLine()
					.getLineNumber());
		}
		editor.putInt("LastSelectedTabPosition", mViewPager.getCurrentItem());
		// Commit the edits!
		editor.commit();

		// Release the database?
		// mModel.closeDb(); TODO, cause lag
	}

	@Override
	protected void onStop() {
		if (mModel.hasSelectedLine()) {
			try {
				mModel.saveState();
			} catch (IllegalStateException e) {
				Toast.makeText(this,
						"Saving state error: " + e.getCause().getMessage(),
						Toast.LENGTH_LONG).show();
			}
		}
		super.onStop();
	}
}

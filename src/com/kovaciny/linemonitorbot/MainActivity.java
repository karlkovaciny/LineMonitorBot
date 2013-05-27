package com.kovaciny.linemonitorbot;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.kovaciny.database.PrimexDatabaseSchema;
import com.kovaciny.database.PrimexSQLiteOpenHelper;
import com.kovaciny.primexmodel.PrimexModel;
import com.kovaciny.primexmodel.WorkOrder;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener, SkidTimesFragment.OnSheetsPerMinuteChangeListener, PropertyChangeListener,
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
	private MenuItem mDebugDisplay;
	private Integer mSelectedLine = -1;
	private Integer mSelectedWorkOrder = 0;
	private PrimexSQLiteOpenHelper mDbHelper;
	private PrimexModel mModel;
		
   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

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
		
		mDbHelper = new PrimexSQLiteOpenHelper(getApplicationContext());
		mDbHelper.getWritableDatabase();
		
		this.mModel = new PrimexModel(MainActivity.this);
		mModel.addPropertyChangeListener(this);
	}

	//implementing interface
	public void onSheetsPerMinuteChanged(double sheetsPerMin){
		//TODO this function is NOT compatible with rotating the screen
		//tell model of change
		mModel.changeCurrentLineSpeed(sheetsPerMin);
		//ask model for updates
		//tell views to change
	}
	
	// Implementing interface for SheetsPerMinuteDialogFragment
    public void onClickPositiveButton(DialogFragment d) {
    	if (d.getTag() == "SheetsPerMinuteDialog") {
    		SheetsPerMinuteDialogFragment spmd = (SheetsPerMinuteDialogFragment)d; 
    		String s = "The numbers you entered were " + spmd.getSheetLengthValue() + ", " +
    				spmd.getLineSpeedValue() + ", and " + spmd.getSpeedFactorValue();
    		Toast t = Toast.makeText(this, s, Toast.LENGTH_SHORT);
        	t.show();	
    	}
    	
    }	
	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName() == PrimexModel.LINE_SPEED_CHANGE_EVENT) {
			Toast t = Toast.makeText(this, "Changed line speed to " + String.valueOf(mModel.getCurrentLineSpeed()), Toast.LENGTH_SHORT);				
			t.show();
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		mJobPicker = (MenuItem) menu.findItem(R.id.action_pick_job);
		mLinePicker = (MenuItem) menu.findItem(R.id.action_pick_line);
		
		//populate the line picker with line numbers from the database
		List<Integer> lineNumberList = new ArrayList<Integer>();
		lineNumberList = mDbHelper.getLineNumbers();
				
		Menu pickLineSubMenu = mLinePicker.getSubMenu();
		pickLineSubMenu.clear();
		
		for (int i=0; i<lineNumberList.size(); i++) {
			//don't have access to View.generateViewId(), so fake a random ID
			pickLineSubMenu.add(LINE_LIST_MENU_GROUP, (i + LINE_LIST_ID_RANDOMIZER), Menu.FLAG_APPEND_TO_GROUP, String.valueOf(lineNumberList.get(i)));
		}
		
		//populate the job picker with jobs
		Menu pickJobSubMenu = mJobPicker.getSubMenu();
		pickJobSubMenu.clear();
		List<Integer> jobList = new ArrayList<Integer>();
		Cursor c = mDbHelper.getWoNumbers();
		try {
			while (c.moveToNext()) {
				jobList.add( c.getInt( c.getColumnIndexOrThrow(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_WO_NUMBER) ) );
			}
		} finally {
	    	if (c != null) c.close();
		}
		
		for (int i=0; i<jobList.size(); i++) {
			String title = "WO #" + String.valueOf(jobList.get(i));
			pickJobSubMenu.add(JOB_LIST_MENU_GROUP, jobList.get(i), Menu.FLAG_APPEND_TO_GROUP, title);
		}
		pickJobSubMenu.add(JOB_OPTIONS_MENU_GROUP , R.id.new_wo, Menu.FLAG_APPEND_TO_GROUP, "+ New");
		pickJobSubMenu.add(JOB_OPTIONS_MENU_GROUP , R.id.clear_wos, Menu.FLAG_APPEND_TO_GROUP, "Clear");
		
		//Select the line and job that we used last time
		SharedPreferences settings = getPreferences(MODE_PRIVATE);
		setSelectedLine( settings.getInt("selectedLine", -1) );
		setSelectedWorkOrder( settings.getInt("selectedWorkOrder", 0));
		
		return true;
	}

	public Integer getSelectedLine() {
		return this.mSelectedLine;
	}

	public void setSelectedLine(Integer selectedLine) {
		this.mSelectedLine = selectedLine;
		if (mSelectedLine != -1) {
			CharSequence lineTitle = "Line " + mLinePicker.getSubMenu().findItem((mSelectedLine + LINE_LIST_ID_RANDOMIZER)).getTitle();
			mLinePicker.setTitle(lineTitle);
		} 		
	}

	public Integer getSelectedWorkOrder() {
		return mSelectedWorkOrder;
	}

	public void setSelectedWorkOrder(Integer selectedWorkOrder) {
		this.mSelectedWorkOrder = selectedWorkOrder;
		if (mSelectedWorkOrder != 0) {
			CharSequence woTitle = "WO #" + mJobPicker.getSubMenu().findItem(mSelectedWorkOrder).getItemId();
			mJobPicker.setTitle(woTitle);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if (item.getGroupId() == LINE_LIST_MENU_GROUP) {
			setSelectedLine(item.getItemId() - LINE_LIST_ID_RANDOMIZER);
		}
		if (item.getGroupId() == JOB_LIST_MENU_GROUP) {
			setSelectedWorkOrder(item.getItemId());
		}
		
		switch (item.getItemId()) {
		case R.id.new_wo:
			//increment highest WO
			int newWoNumber = mDbHelper.getHighestWoNumber() + 1;
			String newTitle = "WO #" + String.valueOf(newWoNumber);
			mJobPicker.getSubMenu().add(JOB_LIST_MENU_GROUP, newWoNumber, Menu.FLAG_APPEND_TO_GROUP, newTitle);
			mDbHelper.addWorkOrder(new WorkOrder(newWoNumber, 0));
			invalidateOptionsMenu(); //so it refreshes
			setSelectedWorkOrder(newWoNumber);
	        break;
		case R.id.clear_wos:
			//clear the menu
			mJobPicker.getSubMenu().removeGroup(JOB_LIST_MENU_GROUP);
			
			//clear the menu title
			mJobPicker.setTitle(R.string.action_pick_job_title);
			
			//clear the database
			mDbHelper.clearWoNumbers();
			
			//clear the selected WO
			mSelectedWorkOrder = 0;
			break;
	    default:
	    }
		return super.onOptionsItemSelected(item);
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
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
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
			default:
				fragment = new SectionFragment();
			}
			
			Bundle args = new Bundle();
			args.putInt(SectionFragment.ARG_SECTION_NUMBER, position + 1);
			args.putInt("passingavalue", 722);
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
	}
	
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
	protected void onStart() {
		super.onStart();		
		
		//look up database asynchronously.
		//new PopulateMenusTask().execute();		
	}

	
	public class PopulateMenusTask extends AsyncTask<Void,Void,Void> {
				
		public PopulateMenusTask() {
			super();
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			//Now it's safe to update UI elements.
			
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		//store persistent data
		mDbHelper.close();
		SharedPreferences settings = getPreferences(MODE_PRIVATE);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putInt("selectedLine", mSelectedLine);
	    editor.putInt("selectedWorkOrder", mSelectedWorkOrder);
	    
	    // Commit the edits!
	    editor.commit();

	}

	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	void showDummyDialog(String text) {
	    // Create the fragment and show it as a dialog.
	    DialogFragment newFragment = MyDialogFragment.newInstance(text);
	    newFragment.show(getFragmentManager(), "dialog");
	}

}

package com.kovaciny.linemonitorbot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {

	private static final int LINE_LIST_MENU_GROUP = 1111;
	private static final int LINE_LIST_ID_RANDOMIZER = 1234;
	private static final int JOB_LIST_MENU_GROUP = 2222;
	private static final int JOB_OPTIONS_MENU_GROUP = 2223;
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
	private Integer mSelectedLine;
	
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
		
		//make database entries -- comment out after doing once
		/*PrimexSQLiteOpenHelper dbHelper = new PrimexSQLiteOpenHelper(this);
		
		ProductionLine line10 = new ProductionLine(10, 50, 64, "direct", "Maxson");
		ProductionLine line18 = new ProductionLine(18, 50, 53, "direct", "Maxson");
		dbHelper.addLine(line10);
		dbHelper.addLine(line18);	
		
		WorkOrder wo1 = new WorkOrder(123456,1);
		dbHelper.addWorkOrder(wo1);
		WorkOrder wo2 = new WorkOrder(234567,69);
		dbHelper.addWorkOrder(wo2);*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		mJobPicker = (MenuItem) menu.findItem(R.id.action_pick_job);
		mLinePicker = (MenuItem) menu.findItem(R.id.action_pick_line);
		mDebugDisplay = (MenuItem) menu.findItem(R.id.debug_display);
		mDebugDisplay.setVisible(false);
		
		//populate the line picker with line numbers from the database
		PrimexSQLiteOpenHelper dbHelper = new PrimexSQLiteOpenHelper(this);
		List<Integer> lineNumberList = new ArrayList<Integer>();
		lineNumberList = dbHelper.getLineNumbers();
				
		Menu pickLineSubMenu = mLinePicker.getSubMenu();
		pickLineSubMenu.clear();
		
		for (int i=0; i<lineNumberList.size(); i++) {
			//don't have access to View.generateViewId(), so fake a random ID
			pickLineSubMenu.add(LINE_LIST_MENU_GROUP, i*LINE_LIST_ID_RANDOMIZER, Menu.FLAG_APPEND_TO_GROUP, String.valueOf(lineNumberList.get(i)));
		}
		
		//Select the line that we used last time
		/*setSelectedLine((Integer) mDataHelper.getCode("mSelectedLine", 0));*/
				
		//populate the job picker with jobs
		Menu pickJobSubMenu = mJobPicker.getSubMenu();
		pickJobSubMenu.clear();
		Integer[] jobList= dbHelper.getWoNumbers();
		for (int i=0; i<jobList.length; i++) {
			int menuId = i;
			pickJobSubMenu.add(JOB_LIST_MENU_GROUP, menuId, Menu.FLAG_APPEND_TO_GROUP, String.valueOf(jobList[i]));
		}
		pickJobSubMenu.add(JOB_OPTIONS_MENU_GROUP , R.id.new_wo, Menu.FLAG_APPEND_TO_GROUP, "+ New");
		pickJobSubMenu.add(JOB_OPTIONS_MENU_GROUP , R.id.clear_wos, Menu.FLAG_APPEND_TO_GROUP, "Clear");
		return true;
	}

	public Integer getSelectedLine() {
		return this.mSelectedLine;
	}

	public void setSelectedLine(Integer selectedLine) {
		this.mSelectedLine = selectedLine;
		if (mSelectedLine != null) {
			CharSequence lineTitle = mLinePicker.getSubMenu().findItem(mSelectedLine * LINE_LIST_ID_RANDOMIZER).getTitle();
			mLinePicker.setTitle(lineTitle);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if (item.getGroupId() == LINE_LIST_MENU_GROUP) {
			setSelectedLine(item.getItemId() / LINE_LIST_ID_RANDOMIZER);
		}
		if (item.getGroupId() == JOB_LIST_MENU_GROUP) {
			mJobPicker.setTitle(item.getTitle());
		}
		
		switch (item.getItemId()) {
		case R.id.new_wo:
	        item.setTitle("new wo");
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
			case 0:
				fragment = new SkidTimesFragment();
				break;
			case 1: 
				fragment = new RatesFragment();
				break;
			case 2:
				fragment = new DrainingFragment();
				break;
			default:
				fragment = new SectionFragment();
			}
			
			Bundle args = new Bundle();
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
		//store persistent data
		super.onPause();
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

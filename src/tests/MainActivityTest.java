package tests;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Instrumentation.ActivityMonitor;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.mock.MockDialogInterface;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kovaciny.linemonitorbot.EnterProductDialogFragment;
import com.kovaciny.linemonitorbot.MainActivity;
import com.kovaciny.linemonitorbot.MainActivity.SectionsPagerAdapter;
import com.kovaciny.linemonitorbot.R;
import com.kovaciny.linemonitorbot.RatesFragment;
import com.kovaciny.linemonitorbot.SettingsActivity;
import com.kovaciny.linemonitorbot.SkidTimesFragment;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

	MainActivity mActivity;
	SkidTimesFragment mSkidTimesFragment;
	RatesFragment mRatesFragment;
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	TextView mTxt_sheetsPerMinute;
	TextView mTxt_timePerSkid;
	EditText mEdit_currentCount;
	EditText mEdit_totalCount;
	EditText mEdit_numSkidsInJob;
	Button mBtn_calculateTimes;
	Button mBtn_calculateRates;
	Button mBtn_enterProduct;
	LinearLayout mContainerSkidTimesFragment;

	public static final String TEST_STATE_DESTROY_TEXT = "666";
	public static final int TEST_SWITCH_LINES_LINE_NUMBER = 10;
	
	public MainActivityTest() {
		super(MainActivity.class);
	}
	@Before
	public void setUp() throws Exception {
		setActivityInitialTouchMode(false);

		mActivity = (MainActivity)getActivity();
		mSkidTimesFragment = (SkidTimesFragment)mActivity.findFragmentByPosition(MainActivity.SKID_TIMES_FRAGMENT_POSITION);
	    mRatesFragment = (RatesFragment)mActivity.findFragmentByPosition(MainActivity.RATES_FRAGMENT_POSITION);
	    mTxt_timePerSkid = (TextView) mActivity.findViewById(R.id.txt_time_per_skid);
	    mTxt_sheetsPerMinute = (TextView)mActivity.findViewById(R.id.txt_products_per_minute);
	    mEdit_currentCount = (EditText)mActivity.findViewById(R.id.edit_current_count);
	    mEdit_totalCount = (EditText)mActivity.findViewById(R.id.edit_total_sheets_per_skid);
	    mEdit_numSkidsInJob = (EditText)mActivity.findViewById(R.id.edit_num_skids_in_job);
	    mBtn_calculateTimes = (Button)mActivity.findViewById((R.id.btn_calculate_times));
	    mBtn_enterProduct = (Button)mActivity.findViewById(R.id.btn_enter_product);
	    mBtn_calculateRates = (Button)mActivity.findViewById(R.id.btn_calculate_rates);
	    mContainerSkidTimesFragment = (LinearLayout)mActivity.findViewById(R.id.container_skid_times_fragment);
	    assertTrue(mSkidTimesFragment != null);
		assertTrue(mRatesFragment != null);
	}

	@Test
	public void testPreConditions() {
		assertTrue(mSkidTimesFragment != null);
		assertTrue(mRatesFragment != null);
	}
	
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	/*
	 * Utility functions
	 */
	
	public void destroyActivity() {
	    this.sendKeys(KeyEvent.KEYCODE_BACK);
	    
	    mActivity.finish();
	    setActivity(null);
	    mActivity = (MainActivity)getActivity();    
	}
	
	public void changeNumberOfSkidsAndCalcTimes(double number) {
        final double newNumber = number;
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                String newNumSkids = String.valueOf(newNumber);
                mEdit_numSkidsInJob.setText(newNumSkids);
                mBtn_calculateTimes.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().waitForIdleSync();
        getInstrumentation().waitForIdleSync();
        //asserts and this.sendKeys() OK here
        this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);        
    }
    
	
	public void testStateDestroyRestoreNumSkids() {
		//setup: add 2.5 skids to the current total, then subtract one, then change the sheet count, 
	    //then destroy the activity to see if the database has only your new number of skids.
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				Double currentNumSkids = Double.valueOf(mEdit_numSkidsInJob.getText().toString());
				String newNumSkids = String.valueOf(currentNumSkids + 2.5);
				mEdit_numSkidsInJob.setText(newNumSkids);
				mEdit_totalCount.setText("999");
				mBtn_calculateTimes.requestFocus();
			}
		});
		getInstrumentation().waitForIdleSync();
		//asserts and this.sendKeys() OK here
		this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
		
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				Double currentNumSkids = Double.valueOf(mEdit_numSkidsInJob.getText().toString());
				String newLowerNumSkids = String.valueOf(currentNumSkids - 1);
				mEdit_numSkidsInJob.setText(newLowerNumSkids);
				mEdit_currentCount.setText("69");
				mEdit_totalCount.setText("400");
				mBtn_calculateTimes.requestFocus();
			}
		});
		getInstrumentation().waitForIdleSync();
		//asserts and this.sendKeys() OK here
		this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
		
		String skidCountBeforeDestroy = mEdit_numSkidsInJob.getText().toString();

		destroyActivity();
		
		mEdit_currentCount = (EditText) mActivity.findViewById(R.id.edit_current_count);
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
//				getInstrumentation().callActivityOnResume(mActivity);				
				mEdit_currentCount.setText("71");
				mBtn_calculateTimes.requestFocus();
			}
		});
		getInstrumentation().waitForIdleSync();
		this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
		
	    mSkidTimesFragment = (SkidTimesFragment)mActivity.findFragmentByPosition(MainActivity.SKID_TIMES_FRAGMENT_POSITION);
	    mEdit_numSkidsInJob = (EditText)mActivity.findViewById(R.id.edit_num_skids_in_job);

		assertEquals(skidCountBeforeDestroy, mEdit_numSkidsInJob.getText().toString());		
	}
	
	
	
	public void testOpenSettingsActivity() {	
		String beforeClick = mTxt_sheetsPerMinute.getText().toString();
		ActivityMonitor am = getInstrumentation().addMonitor(SettingsActivity.class.getName(), null, false);
		
		getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		getInstrumentation().invokeMenuActionSync(mActivity, R.id.action_settings, 0);		

		Activity a = getInstrumentation().waitForMonitorWithTimeout(am, 1000);
		assertEquals(true, getInstrumentation().checkMonitorHit(am, 1));
		a.finish();
	}
	
	public void testSwitchLines() {
		switchLines(TEST_SWITCH_LINES_LINE_NUMBER);
		assertEquals(TEST_SWITCH_LINES_LINE_NUMBER, mActivity.mModel.getSelectedLine().getLineNumber());
	}
	
	public void switchLines(int lineNumber) {
		getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		getInstrumentation().invokeMenuActionSync(mActivity, R.id.action_pick_line, 0);
		final List<Integer> lineNumbers = Arrays.asList(1,6,7,9,10,  11,12,13,14,15,  16,17,18); //13 lines
		int lineNumberPosition = lineNumbers.indexOf(lineNumber);
		for (int i = 0; i < lineNumberPosition; i++) {
//			sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
		    KeyEvent ke = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN);
		    getInstrumentation().sendKeySync(ke);
		    KeyEvent keUp = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_DOWN);
		    getInstrumentation().sendKeySync(keUp);
			getInstrumentation().waitForIdleSync();
			getInstrumentation().waitForIdleSync();
			getInstrumentation().waitForIdleSync();
		}
		getInstrumentation().waitForIdleSync();
		getInstrumentation().waitForIdleSync();
		getInstrumentation().waitForIdleSync();
		this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
		getInstrumentation().waitForIdleSync();
		getInstrumentation().waitForIdleSync();
		getInstrumentation().waitForIdleSync();
		getInstrumentation().waitForIdleSync();
		if (mActivity.mModel.getSelectedLine().getLineNumber() != lineNumber) {
		    switchLines(lineNumber); //dammit, we'll try this till we get it right!
		}
	}
	
//		String afterClick = mTxt_sheetsPerMinute.getText().toString();
//		assertEquals("beforeClick = " + beforeClick + ", afterClick = " + afterClick, beforeClick, afterClick);

	/*
	 * this test can't be made to work now because of protected methods
	 */
	/*@Test
	public void testUpdateProductDataWithNullProductType() {
		try {
		
		
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					DialogFragment d = new SheetsPerMinuteDialogFragment();
					mActivity.onClickPositiveButton(d);
					mActivity.updateRatesData(70d,70d,70d);
				}
			});
			getInstrumentation().waitForIdleSync();
			//asserts and this.sendKeys() OK here
			  
			  
			 
		}
		catch (IllegalStateException e) {
			assertTrue("yey", true);
		}
		fail ("failed to generate an exception");
	}*/

	@Test
	public void testGetTimesWithoutProductDialog() {
		getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		getInstrumentation().invokeMenuActionSync(mActivity, R.id.action_pick_job, 0);
		this.sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
		this.sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
		this.sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
		this.sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
		this.sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
		this.sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
		this.sendKeys(KeyEvent.KEYCODE_DPAD_UP);
		this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER); //click + New
				
		mActivity.runOnUiThread(new Runnable() {
		     public void run() {
		    	 mBtn_calculateTimes.requestFocus();
		     }
		});
		getInstrumentation().waitForIdleSync();
		//asserts OK here
		try {
			this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
			//I think this should have thrown an exception even though the comment that was here said it shouldn't.
			//needs to run when certain lines don't have products?
			assertEquals(getActivity().getString(R.string.prompt_need_product), mBtn_enterProduct.getError()); 
			
		} catch (IllegalStateException e) {
			fail ("Threw exception don't know why");
		}   
	}
	
	public void testClickCalcRatesResetsSheetsPerMinute() {
		mActivity.runOnUiThread(new Runnable() {
		     public void run() {
		    	 mBtn_calculateRates.requestFocus();
		    	 mTxt_sheetsPerMinute.setVisibility(TextView.VISIBLE);
		     }
		});
		getInstrumentation().waitForIdleSync();
		this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
		assertEquals(mTxt_sheetsPerMinute.getVisibility(), TextView.VISIBLE);
	
	}
	
	@Test
	public void testShowEnterProductDialog() {
		clickButton(R.id.btn_enter_product);
		EnterProductDialogFragment spmd = (EnterProductDialogFragment) mActivity.getFragmentManager().findFragmentByTag("EnterProductDialog");
		assertTrue(spmd != null);
	}
	
	@Test
	public void testLoadSkidsAfterDestroy() {
	    
	}
	@Test
	public void testIncrementNumberOfSkids() {
		Double currentNumSkids = Double.valueOf(mEdit_numSkidsInJob.getText().toString());
		changeNumberOfSkidsAndCalcTimes(currentNumSkids + 1d);
		assertEquals("failed to increment", (Double)(currentNumSkids + 1), Double.valueOf(mEdit_numSkidsInJob.getText().toString()));
	}
	
	@Test
	public void testAddQuarterSkid() {
		Double currentNumSkids = Double.valueOf(mEdit_numSkidsInJob.getText().toString());
		changeNumberOfSkidsAndCalcTimes(currentNumSkids + .25d);
		assertEquals("failed to increment", (Double)(currentNumSkids + .25), Double.valueOf(mEdit_numSkidsInJob.getText().toString()));
	}
	
	@Test
	public void testEnterProductDialog() {
		final double NEW_WIDTH_SETPOINT = 40d;
		final double NEW_LENGTH_SETPOINT = 12d;
		final double NEW_LINE_SPEED_SETPOINT = 16.84d; //for line 9 -- equals 16.67 / factors, 2 decimal place.
		final double NEW_DIFFERENTIAL = 1d;
		switchLines(9);
		clickButton(R.id.btn_enter_product);
		EnterProductDialogFragment spmdf = (EnterProductDialogFragment)getActivity().getFragmentManager().findFragmentByTag("EnterProductDialog");
		final EditText spmEdit_sheetWidth = (EditText) spmdf.getDialog().findViewById(R.id.edit_sheet_width);
		final EditText spmEdit_sheetLength = (EditText) spmdf.getDialog().findViewById(R.id.edit_sheet_length);
		final EditText spmEdit_lineSpeed = (EditText) spmdf.getDialog().findViewById(R.id.edit_line_speed);
		final EditText spmEdit_diffSpeed = (EditText) spmdf.getDialog().findViewById(R.id.edit_differential_speed);
		
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				spmEdit_sheetWidth.setText(String.valueOf(NEW_WIDTH_SETPOINT));
				spmEdit_sheetLength.setText(String.valueOf(NEW_LENGTH_SETPOINT));
				spmEdit_lineSpeed.setText(String.valueOf(NEW_LINE_SPEED_SETPOINT));
				spmEdit_diffSpeed.setText(String.valueOf(NEW_DIFFERENTIAL));
				mEdit_totalCount.setText("1000");
			}
		});
		Button bpos = ((AlertDialog)spmdf.getDialog()).getButton(Dialog.BUTTON_POSITIVE);
		TouchUtils.clickView(this, bpos);
		assertEquals("did we correctly set the sheet width in the dialog", NEW_WIDTH_SETPOINT, spmdf.getSheetWidthValue());
		assertEquals("did we correctly set the line speed in the dialog", NEW_LINE_SPEED_SETPOINT, spmdf.getLineSpeedValue());
		Rect windowBounds = new Rect();
		mContainerSkidTimesFragment.getHitRect(windowBounds);
		assertTrue("is calculate times button visible", mBtn_calculateTimes.getLocalVisibleRect(windowBounds));
		TouchUtils.clickView(this, mBtn_calculateTimes);
		assertEquals("did the time display correctly", "0:59", mTxt_timePerSkid.getText().toString());
		switchLines(6);
		switchLines(10);
		clickButton(R.id.btn_enter_product);
		spmdf = (EnterProductDialogFragment)getActivity().getFragmentManager().findFragmentByTag("EnterProductDialog");
		final EditText edit_diffSpeed = (EditText) spmdf.getDialog().findViewById(R.id.edit_differential_speed);
		if (edit_diffSpeed.getText().length() > 0) {
		    assertTrue("Differential should be different from " + String.valueOf(NEW_DIFFERENTIAL) + ", but it's not", 
		            Double.valueOf(edit_diffSpeed.getText().toString()) != NEW_DIFFERENTIAL);
		}
	}
	
	@Test
	public void testCalcFinishTime() {
		MockSheetsPerMinuteDialogInterface mock = new MockSheetsPerMinuteDialogInterface();
		//TODO
	}
	
	public class MockSheetsPerMinuteDialogInterface extends MockDialogInterface {
		MockSheetsPerMinuteDialogInterface() {
		}
		
		public double getSheetWidthValue() {
			return 40d;
		}
	}
	/*
	@Test
	public void testOnTabSelected() {
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				MainActivity activity = getActivity();
				final ActionBar actionBar = getActivity().getActionBar();
				actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
				// Create the adapter that will return a fragment for each of the three
				// primary sections of the app.
				mSectionsPagerAdapter = new SectionsPagerAdapter(
						activity.getSupportFragmentManager());

				// Set up the ViewPager with the sections adapter.
				mViewPager = (ViewPager) getActivity().findViewById(R.id.pager);
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
							.setTabListener(getActivity()));
				}

			}			
		});
		getInstrumentation().waitForIdleSync();
		//asserts OK here
		fail("Not yet implemented"); // TODO
	}


	@Test
	public void testOnOptionsItemSelectedMenuItem() {
		getInstrumentation().invokeContextMenuAction(mActivity, R.id.action_settings, 0);
		
	}
	
	@Test
	public void testSendKeycodes() {
		mActivity.runOnUiThread(new Runnable() {
		     public void run() {
		    	 mTxt_sheetsPerMinute.setText("12345");
		    	 mTxt_sheetsPerMinute.setFocusable(true);
		    	 mTxt_sheetsPerMinute.requestFocus();
		    	 if (mTxt_sheetsPerMinute.hasFocus()) {
		    		 mTxt_sheetsPerMinute.setText("");
		    	 }
		    	 mEdit_currentCount.requestFocus();
		     }
		});
		getInstrumentation().waitForIdleSync();
		//asserts OK here
		this.sendKeys(KeyEvent.KEYCODE_7);
		assertTrue("value is wrong: " + mEdit_currentCount.getText().toString(), mEdit_currentCount.getText().toString().equals("7"));

	}
	*/	
/*	@Test
	public void testOnPause() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testOnStop() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testOnCreateBundle() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testOnCreateOptionsMenuMenu() {
		fail("Not yet implemented"); // TODO
	}

		@Test
	public void testOnClickPositiveButton() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testPropertyChange() {
		fail("Not yet implemented"); // TODO
	}


	@Test
	public void testUpdateSkidData() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testUpdateProductData() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testUpdateRatesData() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testOnTabUnselected() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testOnTabReselected() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testFindFragmentByPosition() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testShowDummyDialog() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testHideKeyboard() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testOnCreateBundle1() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testOnStart() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testOnResume() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testOnPostResume() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testOnConfigurationChanged() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testOnBackPressed() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testInvalidateOptionsMenu() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testOnCreateOptionsMenuMenu1() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testOnOptionsItemSelectedMenuItem1() {
		fail("Not yet implemented"); // TODO
	}*/
	public void clickButton(int buttonId) {
		final Button button = (Button) mActivity.findViewById(buttonId);
		mActivity.runOnUiThread(new Runnable() {
		     public void run() {
		    	 button.requestFocus();		    	 
		     }
		});
		getInstrumentation().waitForIdleSync();
		this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
	}

}

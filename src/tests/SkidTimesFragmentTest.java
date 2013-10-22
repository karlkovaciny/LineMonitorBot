package tests;

import java.beans.PropertyChangeEvent;
import java.util.GregorianCalendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kovaciny.linemonitorbot.MainActivity;
import com.kovaciny.linemonitorbot.R;
import com.kovaciny.linemonitorbot.SkidTimesFragment;
import com.kovaciny.primexmodel.PrimexModel;
import com.kovaciny.primexmodel.Product;
import com.kovaciny.primexmodel.Products;
import com.kovaciny.primexmodel.SpeedValues;

public class SkidTimesFragmentTest extends ActivityInstrumentationTestCase2<MainActivity>{

	MainActivity mActivity;
	SkidTimesFragment mSkidTimesFragment;
	private EditText mEdit_currentSkidNumber;
	private EditText mEdit_currentCount;
	private EditText mEdit_totalCountPerSkid;
	private EditText mEdit_numSkidsInJob;

	private Button mBtn_calculateTimes;

	TextView mTxt_jobFinishTime;
	TextView mTxt_sheetsPerMinute;
	
	public SkidTimesFragmentTest() {
		super(MainActivity.class);
	}
	
	@Before
	public void setUp() throws Exception {
	    super.setUp();

	    setActivityInitialTouchMode(false);

	    mActivity = (MainActivity)getActivity();
	    mSkidTimesFragment = (SkidTimesFragment)mActivity.findFragmentByPosition(MainActivity.SKID_TIMES_FRAGMENT_POSITION);
	    mTxt_jobFinishTime = (TextView)mActivity.findViewById(R.id.txt_job_finish_time);
	    mTxt_sheetsPerMinute = (TextView)mActivity.findViewById(R.id.txt_products_per_minute);
	    //set up editTexts		
	    mEdit_numSkidsInJob = (EditText) mActivity.findViewById(R.id.edit_num_skids_in_job);
	    mEdit_currentSkidNumber = (EditText) mActivity.findViewById(R.id.edit_skid_number);
	    mEdit_currentCount = (EditText) mActivity.findViewById(R.id.edit_current_count);
	    mEdit_totalCountPerSkid = (EditText) mActivity.findViewById(R.id.edit_total_sheets_per_skid);

	    mBtn_calculateTimes = (Button) mActivity.findViewById(R.id.btn_get_times);
		
	}

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
	
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testPreConditions() {
		assertTrue(mTxt_sheetsPerMinute != null);
	}
	
	public void loadTestCase(int caseNumber) {
        final PrimexTestCase testCase = new PrimexTestCase(caseNumber);
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                mActivity.mModel.setSelectedLine(testCase.mLineNumber);
                mActivity.mModel.getSelectedLine().setWebWidth(testCase.mWebWidth);
                Product p = Products.makeProduct(testCase.mProductType, testCase.mGauge, testCase.mWidth, testCase.mLength);
                p.setUnitWeight(testCase.mUnitWeight);
                mActivity.mModel.setCurrentSpeed(new SpeedValues(testCase.mLineSpeedSetpoint,testCase.mDifferentialSetpoint,testCase.mSpeedFactor));
                mActivity.mModel.changeProduct(p);
                mActivity.mModel.getSelectedWorkOrder().getSelectedSkid().setTotalItems(testCase.mTotalItems);
                mActivity.mModel.calculateTimes();
                mActivity.mModel.calculateRates(0d);
            }
        });
        getInstrumentation().waitForIdleSync();
    }
	
	@Test
	public void testErrorMessages() {
	    loadTestCase(PrimexTestCase.JOMA_ROLLS);
	    mActivity.runOnUiThread(new Runnable() {
		     public void run() {
		         mEdit_totalCountPerSkid.setText("0");
		    	 mEdit_currentCount.setText("1234");
		    	 mEdit_numSkidsInJob.setText("");
		    	 mEdit_currentSkidNumber.setText("220");
		     }
		});
		clickButton(R.id.btn_get_times);
		assertEquals(mActivity.getString(R.string.error_over_maximum_skids).replace("%1", String.valueOf(SkidTimesFragment.MAXIMUM_NUMBER_OF_SKIDS)), mEdit_numSkidsInJob.getError());
		assertEquals(mActivity.getString(R.string.error_current_greater_than_total), mEdit_currentCount.getError());
		assertEquals(mActivity.getString(R.string.error_need_nonzero), mEdit_totalCountPerSkid.getError());
		assertEquals(null, mEdit_currentSkidNumber.getError());
		
		mActivity.runOnUiThread(new Runnable() {
		     public void run() {
		    	 mEdit_totalCountPerSkid.setText("");
		    	 mEdit_currentCount.setText("");
		    	 mEdit_numSkidsInJob.setText("");
		    	 mEdit_currentSkidNumber.setText("");
		     }
		});
		clickButton(R.id.btn_get_times);
		assertEquals(mActivity.getString(R.string.error_over_maximum_skids).replace("%1", String.valueOf(SkidTimesFragment.MAXIMUM_NUMBER_OF_SKIDS)), mEdit_numSkidsInJob.getError());
		assertEquals(mActivity.getString(R.string.error_current_greater_than_total), mEdit_currentCount.getError());
		assertEquals(mActivity.getString(R.string.error_need_nonzero), mEdit_totalCountPerSkid.getError());
		assertEquals(null, mEdit_currentSkidNumber.getError());
	}
	/*@Test
	public void testOnPause() {
		fail("Not yet implemented"); // TODO
	}
	
	@Test
	public void testOnCreateBundle() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testOnCreateViewLayoutInflaterViewGroupBundle() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testOnEditorAction() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testOnFocusChange() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetSkidList() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testSetSkidList() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testOnItemSelected() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testOnNothingSelected() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testOnClick() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testOnetimeTimer() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testRepeatingTimer() {
		fail("Not yet implemented"); // TODO
	}
*/
	@Test
	public void testModelPropertyChange() {
		// As is you have to test this at the time it needs testing.
		GregorianCalendar nowDate = new GregorianCalendar(2013, 6, 20, 5, 04);
//		final GregorianCalendar oneHourFromNow = new GregorianCalendar(2013, 6, 19, 10, 45);
//		final GregorianCalendar sevenAmTomorrow = new GregorianCalendar(2013, 6, 20, 7, 0);
		final GregorianCalendar fiveAmTomorrow = new GregorianCalendar(2013, 6, 20, 5, 0);
		
		getActivity().runOnUiThread(new Runnable() {
		     public void run() {
//		    	 PropertyChangeEvent event = new PropertyChangeEvent(PrimexModel.class, PrimexModel.JOB_FINISH_TIME_CHANGE_EVENT, null, oneHourFromNow.getTime());
//		    	 PropertyChangeEvent event = new PropertyChangeEvent(PrimexModel.class, PrimexModel.JOB_FINISH_TIME_CHANGE_EVENT, null, sevenAmTomorrow.getTime());
		    	 PropertyChangeEvent event = new PropertyChangeEvent(PrimexModel.class, PrimexModel.JOB_FINISH_TIME_CHANGE_EVENT, null, fiveAmTomorrow.getTime());
		    	 mSkidTimesFragment.modelPropertyChange(event);
		     }
		});
		getInstrumentation().waitForIdleSync();
//		assertEquals("should not show date when it's today, unless it's before 6 am now.", "10:45 AM", mTxt_jobFinishTime.getText().toString());
//		assertEquals("should always show date when it's after 6 tomorrow.", "7:00 AM Sat", mTxt_jobFinishTime.getText().toString());
		assertEquals("should never show date when it's before 6 tomorrow.", "5:00 AM", mTxt_jobFinishTime.getText().toString());
		//can only test before 6 am
//		assertEquals("should always show date when it's before 6 now and finish time is after 6 today.", "7:00 AM Sat", mTxt_jobFinishTime.getText().toString());
//		assertEquals("should not show day when it's after midnight, but less than six hours after midnight", "1:00 AM", mTxt_jobFinishTime.getText().toString());
	}

}

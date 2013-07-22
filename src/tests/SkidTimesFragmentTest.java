package tests;

import java.beans.PropertyChangeEvent;
import java.util.GregorianCalendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import com.kovaciny.linemonitorbot.MainActivity;
import com.kovaciny.linemonitorbot.R;
import com.kovaciny.linemonitorbot.SkidTimesFragment;
import com.kovaciny.primexmodel.PrimexModel;

public class SkidTimesFragmentTest extends ActivityInstrumentationTestCase2<MainActivity>{

	MainActivity mActivity;
	SkidTimesFragment mSkidTimesFragment;
	TextView mTxt_jobFinishTime;
	
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
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testPreConditions() {
		
	}
	

	@Test @Ignore ("not ready yet") 
	public void testOnPause() {
		fail("Not yet implemented"); // TODO
	}
	/*
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

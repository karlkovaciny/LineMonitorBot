package tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import com.kovaciny.linemonitorbot.CoreTypeFragment;
import com.kovaciny.linemonitorbot.R;
import com.kovaciny.linemonitorbot.RollMathActivity;

public class CoreTypeFragmentTest extends
        ActivityInstrumentationTestCase2<RollMathActivity> {

    RollMathActivity mActivity;
    CoreTypeFragment mCoreTypeFragment;
    TextView mTxt_coreWeight;
    
    public CoreTypeFragmentTest() {
        super(RollMathActivity.class);
    }
    
    @Before
    public void setUp() throws Exception {
        super.setUp();

        setActivityInitialTouchMode(false);
        int j = 72;
        j++;
        Activity a = getActivity();
        mActivity = (RollMathActivity) a;
        
        mCoreTypeFragment = (CoreTypeFragment) mActivity.getSupportFragmentManager().findFragmentById(R.id.core_type_fragment_1);
        
        mTxt_coreWeight = (TextView) mActivity.findViewById(R.id.txt_core_weight);
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testOnCoreWeightChanged() {
        final double TEST_WEIGHT = 4.5;
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                mCoreTypeFragment.onCoreWeightChanged(TEST_WEIGHT);
            }
       });
       getInstrumentation().waitForIdleSync();

        String displayed = mTxt_coreWeight.getText().toString();
        assertEquals("4.5", displayed);
    }
}

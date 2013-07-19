package tests;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import com.kovaciny.linemonitorbot.MainActivity;
import com.kovaciny.linemonitorbot.R;
import com.kovaciny.linemonitorbot.RatesFragment;
import com.kovaciny.primexmodel.PrimexModel;
import com.kovaciny.primexmodel.Product;
import com.kovaciny.primexmodel.Roll;
import com.kovaciny.primexmodel.Sheet;

public class RatesFragmentTest extends
		ActivityInstrumentationTestCase2<MainActivity> {

	MainActivity mActivity;
	EditText mEdit_sheetWeight;
	List<PropertyChangeEvent> mPropertyChangeEventList;
	List<Object> mPropertyChangeResultList;
	RatesFragment mRatesFragment;
	Instrumentation mInstrumentation;
	
	public RatesFragmentTest() {
		super(MainActivity.class);
	}
	
	@Before
	public void setUp() throws Exception {
		super.setUp();

	    setActivityInitialTouchMode(false);

	    mActivity = getActivity();
	    mInstrumentation = getInstrumentation();
	    
	    mEdit_sheetWeight = (EditText) mActivity.findViewById(R.id.edit_sheet_weight);
	    mEdit_sheetWeight.setText("sissy");

		Product oldProduct = new Roll(.020, 55d, 1000);
		Product newProduct = new Sheet(.010, 40d, 28d);
		newProduct.setUnitWeight(1.015); //testing round up
		
		mPropertyChangeEventList = Arrays.asList(				
				new PropertyChangeEvent(PrimexModel.class, PrimexModel.PRODUCT_CHANGE_EVENT, oldProduct, newProduct),
				new PropertyChangeEvent(PrimexModel.class, PrimexModel.PRODUCT_CHANGE_EVENT, oldProduct, newProduct),
				new PropertyChangeEvent(PrimexModel.class, PrimexModel.PRODUCT_CHANGE_EVENT, oldProduct, newProduct),
				new PropertyChangeEvent(PrimexModel.class, PrimexModel.PRODUCT_CHANGE_EVENT, oldProduct, newProduct),
				new PropertyChangeEvent(PrimexModel.class, PrimexModel.NET_PPH_CHANGE_EVENT, 0.0d, 1000.0d)
		);
		mPropertyChangeResultList = Arrays.asList(
				null,
				null,
				null,
				null,
				null
			);
		mRatesFragment = new RatesFragment();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPreConditions() {
		assertTrue(mEdit_sheetWeight != null);
		assertTrue(mEdit_sheetWeight.getText().toString().equals("sissy"));
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
	public void testOnClick() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testModelPropertyChange() {
		getActivity().runOnUiThread(new Runnable() {
		     public void run() {
mEdit_sheetWeight.setText("I hate unit tests");
		    	 //		    	 mRatesFragment.modelPropertyChange(mPropertyChangeEventList.get(0));
//		    	 assertTrue("product needs to set right sheet weight", mEdit_sheetWeight.getText().toString().equals("1.01"));
//		    	 assertTrue("product needs to set right sheet weight", mEdit_sheetWeight.getText().toString().equals("1.02"));
		     }
		});
		mRatesFragment.modelPropertyChange(mPropertyChangeEventList.get(4));
		assertEquals((Double)mPropertyChangeEventList.get(4).getNewValue(), 1000.0d, .01);
		String s = mEdit_sheetWeight.getText().toString();
		assertEquals("dfd", s, "bab");
//		mInstrumentation.waitForIdleSync();
	}

}

package tests;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.support.v4.app.Fragment;
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
	
	public RatesFragmentTest() {
		super(MainActivity.class);
	}
	
	@Before
	public void setUp() throws Exception {
		super.setUp();

	    setActivityInitialTouchMode(false);

	    mActivity = (MainActivity)getActivity();
	    mRatesFragment = (RatesFragment)mActivity.findFragmentByPosition(MainActivity.RATES_FRAGMENT_POSITION);
	    
	    mEdit_sheetWeight = (EditText) mActivity.findViewById(R.id.edit_sheet_weight);

		Product oldProduct = new Roll(.020, 55d, 1000);
		Product newProduct = new Sheet(.010, 40d, 28d);
		newProduct.setUnitWeight(1.015); //testing round up
		oldProduct.setUnitWeight(0.990);
		
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
		
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testPreConditions() {
		assertTrue(mEdit_sheetWeight != null);
	}
/*	
	@Test
	public void testOnClick() {
		fail("Not yet implemented"); // TODO
	}




*/
	@Test
	public void testModelPropertyChange() {
		getActivity().runOnUiThread(new Runnable() {
		     public void run() {
		    	 mRatesFragment.modelPropertyChange(mPropertyChangeEventList.get(0));
		     }
		});
	    getInstrumentation().waitForIdleSync();
//	    assertEquals("product needs equal sheet weight but rounded down", "1.01", mRatesFragment.mEdit_sheetWeight.getText().toString());

	    assertEquals("product needs to set right sheet weight and round up", "1.02", mEdit_sheetWeight.getText().toString());
		
	    getActivity().runOnUiThread(new Runnable() {
		     public void run() {
		    	 mRatesFragment.modelPropertyChange(mPropertyChangeEventList.get(4));
		     }
		});
	    getInstrumentation().waitForIdleSync();
   	 	
		assertEquals((Double)mPropertyChangeEventList.get(4).getNewValue(), 1000.0d, .01);
	}

}

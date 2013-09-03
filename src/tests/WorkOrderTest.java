package tests;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.kovaciny.primexmodel.WorkOrder;

public class WorkOrderTest extends TestCase {

    WorkOrder mWorkOrder;
    
    @Before
    public void setUp() throws Exception {
        mWorkOrder = new WorkOrder(1);
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }
    
    @Test
    public void testUpdateSkidsList() {
        PrimexTestCase testCase = new PrimexTestCase(PrimexTestCase.JOMA_ROLLS);
        mWorkOrder = testCase.mWorkOrder;
        mWorkOrder.updateSkidsList(1, 1000, 5);
        assertEquals(5d, mWorkOrder.getNumberOfSkids());
        assertEquals(1000, mWorkOrder.getLastSkidSheetCount());
        
        mWorkOrder.selectSkid(5);
        mWorkOrder.updateSkidsList(3, 2000, 4);
        double numSkids = mWorkOrder.getNumberOfSkids();
        assertEquals(4d, numSkids);
        assertEquals(4, mWorkOrder.getSelectedSkid().getSkidNumber());
        try {
            mWorkOrder.getSkidsList().get(4);
            fail("expected out of bounds exception");
        } catch (IndexOutOfBoundsException e) {
            //passed test
        }
        
        mWorkOrder.updateSkidsList(2, 444, 6.25);
        assertEquals(6.25, mWorkOrder.getNumberOfSkids());
        assertEquals(1000, (int)mWorkOrder.getSkidsList().get(0).getTotalItems());
        assertEquals(444, (int)mWorkOrder.getSkidsList().get(1).getTotalItems());
        assertEquals(111, (int)mWorkOrder.getLastSkidSheetCount());
        assertEquals(444, (int)mWorkOrder.getSkidsList().get(4).getTotalItems());
        
        mWorkOrder.updateSkidsList(3, 666, 4.5);
        assertEquals(4.5, mWorkOrder.getNumberOfSkids());
        assertEquals(1000, (int)mWorkOrder.getSkidsList().get(0).getTotalItems());
        assertEquals(444, (int)mWorkOrder.getSkidsList().get(1).getTotalItems());
        assertEquals(666, (int)mWorkOrder.getSkidsList().get(2).getTotalItems());
        assertEquals(333, (int)mWorkOrder.getLastSkidSheetCount());
    }
    
    @Test
    public void testRemoveLastSkid() {
        PrimexTestCase testCase = new PrimexTestCase(PrimexTestCase.JOMA_ROLLS);
        mWorkOrder = testCase.mWorkOrder;
        assertEquals(3d, mWorkOrder.getNumberOfSkids());
        mWorkOrder.selectSkid(3);
        mWorkOrder.removeLastSkid();
        assertEquals(2d, mWorkOrder.getNumberOfSkids());
        assertEquals("Selected skid should be the last one in the list", 2, mWorkOrder.getSelectedSkid().getSkidNumber());
    }
    
}

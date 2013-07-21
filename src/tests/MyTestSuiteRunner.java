package tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class MyTestSuiteRunner extends TestCase {
	  public static Test suite() {
		    TestSuite suite = new TestSuite();
		    suite.addTestSuite(RatesFragmentTest.class);
		    suite.addTestSuite(SkidTimesFragmentTest.class);
		    suite.addTestSuite(PrimexModelTest.class);
		    suite.addTestSuite(SpeedValuesTest.class);
		    return suite;
		  }
}

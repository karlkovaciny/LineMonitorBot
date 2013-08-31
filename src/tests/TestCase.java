package tests;

import com.kovaciny.primexmodel.Product;

public class TestCase {
    public int mLineNumber;
    public double mSheetsPerMinute;
    public double mRollDiameter;
    public double mCoreDiameter;
    public int mLinearFeet;
    public double mLineSpeedSetpoint;
    public double mDifferentialSetpoint;
    public double mSpeedFactor;
    public double mWebWidth;
    public String mProductType;
    public double mGauge;
    public double mWidth;
    public double mLength;
    public double mUnitWeight;
    public int mTotalItems;
    
    public static final int JOMA_ROLLS = 1;
    
    TestCase(int caseNumber) {
        switch(caseNumber) {
            case JOMA_ROLLS:
                mLineNumber = 9;
                mSheetsPerMinute = 58.46;
                mRollDiameter = 35.1875;
                mCoreDiameter = 6d;
                mLinearFeet = 2950;
                mLineSpeedSetpoint = 56.7;
                mDifferentialSetpoint = 1.02;
                mSpeedFactor = 1.01;
                mWebWidth = 30d; //made up
                mProductType = Product.ROLLS_TYPE;
                mGauge = .025;
                mWidth = 24.25;
                mLength = 12d;
                mUnitWeight = .288; //semi made up
                mTotalItems = 2950;
                
                break;
            default:
                throw new IllegalArgumentException("invalid case number");
        }
    }
}

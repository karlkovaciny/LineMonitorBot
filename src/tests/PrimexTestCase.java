package tests;

import java.util.ArrayList;
import java.util.List;

import com.kovaciny.primexmodel.Product;
import com.kovaciny.primexmodel.Products;
import com.kovaciny.primexmodel.Skid;
import com.kovaciny.primexmodel.WorkOrder;

public class PrimexTestCase {
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
    public double mGaugeFactor;
    public double mWidth;
    public double mLength;
    public double mUnitWeight;
    public double mLetdownGrams = 0;
    public int mTotalItems;
    public Product mProduct;
    public WorkOrder mWorkOrder;
    public List<Skid<Product>> mSkidsList;
    
    public static final int JOMA_ROLLS = 1;
    public static final int LINE_14 = 14;
    public static final int LINE_6_ROLLS = 141;
    public static final int LINE_11_HORTICULTURAL = 151;
    
    PrimexTestCase(int caseNumber) {
        switch(caseNumber) {
            case JOMA_ROLLS:
                mLineNumber = 9;
                mSheetsPerMinute = 58.46; //note this gives inconsistent gross than the line speed, differential, and unit weight
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
                mLetdownGrams = 45; //made up
                mProduct = Products.makeProduct(mProductType, mGauge, mWidth, mLength);
                mWorkOrder = new WorkOrder(1);
                for (int i = 0; i < 3; i++) {
                    mWorkOrder.addOrUpdateSkid(new Skid<Product>(mTotalItems, mProduct));
                    mWorkOrder.selectSkid(1);
                }
                
                break;
            case LINE_14:
                mLineNumber = 14;
                mWebWidth = 65;
                mLineSpeedSetpoint = 29.6;
                mDifferentialSetpoint = 103;
                //top/bottom roll setpoint: 99.5/101.5
                mUnitWeight = 5.885;
            case LINE_6_ROLLS:
                mGaugeFactor = .036;
                mRollDiameter = 24d;
                mLinearFeet = 1980;
                //252/roll
                mUnitWeight = .12727;
                mGauge = 18.2;
            case LINE_11_HORTICULTURAL:
                mGaugeFactor = .0386;
                mGauge = .0146;
                mUnitWeight = .2298;
                mWidth = 35;
                mRollDiameter = 36.18;
            default:
                throw new IllegalArgumentException("invalid case number");
        }
    }
}

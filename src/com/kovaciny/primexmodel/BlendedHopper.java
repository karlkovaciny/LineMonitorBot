package com.kovaciny.primexmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import android.annotation.SuppressLint;
import com.kovaciny.helperfunctions.HelperFunction;

@SuppressLint("UseSparseArrays")
public class BlendedHopper extends Hopper {

    ArrayList<Material> mContents;
    ArrayList<Double> percents;
    HashMap<Integer, Double> mContentTypeToPercentsMap;
    
    public BlendedHopper() {
        mContentTypeToPercentsMap = new HashMap<Integer, Double>();
    }
    @Override
    /*
     * (non-Javadoc)
     * @see com.kovaciny.primexmodel.Hopper#setSetpoint(double)
     * For an ExtruderHopper, the setpoint represents what percent of the gross run rate comes from this extruder.
     */
    public void setSetpoint(double setpoint) {
        super.setSetpoint(setpoint);
    }

    @Override
    public int getContentsType() {
        if (mContentTypeToPercentsMap.isEmpty()) return Material.EMPTY;
        if (mContentTypeToPercentsMap.get(Material.RESIN_TYPE) > 70) return Material.RESIN_TYPE;
        if (mContentTypeToPercentsMap.get(Material.RESIN_TYPE) < 30) return Material.REGRIND_TYPE;
        return Material.RESIN_REGRIND_BLEND_30_TO_70_PERCENT;
    }

    @Override
    public void setContents(int materialType) {
        if (mContents == null) {
            mContentTypeToPercentsMap = new HashMap<Integer, Double>();
        } else {
            mContentTypeToPercentsMap.put(materialType, 100d);
        }
    }
    
    public void setContents(List<Integer> materialTypes, List<Double> percents) {
        double sumPercents = 0d;
        for (Iterator<Double> it = percents.iterator(); it.hasNext(); ) {
            sumPercents += it.next();
        }
        if ((sumPercents - 100d) > HelperFunction.EPSILON) {
            throw new IllegalArgumentException("Hopper percents must add to 100");
        }
        
        Iterator<Double> percentsIter = percents.iterator();
        for (Iterator<Integer> iter = materialTypes.iterator(); iter.hasNext(); ) {
            mContentTypeToPercentsMap.put(iter.next(), percentsIter.next());
        }
    }

    @Override
    public double getSafeDrainTime(double safetyMargin) {
        // TODO Auto-generated method stub
        return super.getSafeDrainTime(safetyMargin);
    }
    
}

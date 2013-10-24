package com.kovaciny.linemonitorbot;

import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kovaciny.helperfunctions.HelperFunction;
import com.kovaciny.primexmodel.PrimexModel;
import com.kovaciny.primexmodel.Product;
import com.kovaciny.primexmodel.Products;
import com.kovaciny.primexmodel.Roll;
import com.kovaciny.primexmodel.Rollset;
import com.kovaciny.primexmodel.Sheetset;
import com.kovaciny.primexmodel.Skid;
import com.kovaciny.primexmodel.SpeedValues;

public class DialogController {

    public PrimexModel mModel;
    private MainActivity mActivity;
    
    public DialogController(MainActivity activity) {
        mModel = activity.mModel;
        mActivity = activity;
    }
    
    public Bundle showEnterProductDialog() {
        // Create the fragment and show it as a dialog.
        EnterProductDialogFragment newFragment = new EnterProductDialogFragment();
        Product currentProd = mModel.getSelectedWorkOrder().getProduct();
        Bundle args = new Bundle();
        args.putDouble("SpeedFactor",
                mModel.getSelectedLine().getSpeedValues().speedFactor); // TODO
                                                                        // it
                                                                        // will
                                                                        // bite
                                                                        // me
                                                                        // that
                                                                        // these
                                                                        // aren't
                                                                        // all
                                                                        // in WO

        // Load the line setpoints for this work order or if none, the last ones
        // used on this line.
        Double lineSpeedSetpoint = mModel.getLineSpeedSetpoint();
        if (lineSpeedSetpoint == 0d) {
            lineSpeedSetpoint = mModel.getSelectedLine().getSpeedValues().lineSpeedSetpoint;
        }
        args.putDouble("LineSpeed", lineSpeedSetpoint);
        
        Double differentialSpeed = mModel.getDifferentialSetpoint();
        if (differentialSpeed == 0d) {
            differentialSpeed = mModel.getSelectedLine().getSpeedValues().differentialSpeed; // TODO
                                                                                                // seems
                                                                                                // ugly
        }

        args.putInt("NumberOfSkids", mModel.getNumberOfTableSkids());

        args.putDouble("DifferentialSpeed", differentialSpeed);
        args.putDouble("DifferentialLowValue", mModel.getSelectedLine()
                .getDifferentialRangeLow());
        args.putDouble("DifferentialHighValue", mModel.getSelectedLine()
                .getDifferentialRangeHigh());
        args.putString("SpeedControllerType", mModel.getSelectedLine()
                .getSpeedControllerType());

        if (currentProd != null) {
            args.putDouble("Gauge", currentProd.getGauge());
            args.putDouble("SheetWidth",
                    currentProd.getWidth() / Double.valueOf(currentProd.getNumberOfWebs()));
            args.putDouble("SheetLength", currentProd.getLength());
            args.putString("ProductType", currentProd.getType());
            args.putInt("NumberOfWebs", currentProd.getNumberOfWebs());
        }
        
        if (currentProd instanceof Roll) {
            args.putInt("CoreType", ((Roll)currentProd).getCoreType());
        }
        newFragment.setArguments(args);
        newFragment.show(mActivity.getFragmentManager(), "EnterProductDialog");
        return args; // for unit testing
    }

    public void showGoByHeightDialog(int totalSheets) {
        // Create the fragment and show it as a dialog.
        GoByHeightDialogFragment newFragment = new GoByHeightDialogFragment();
        Bundle args = new Bundle();
        Skid<Product> currentSkid = mModel.getSelectedWorkOrder()
                .getSelectedSkid();
        args.putInt("SheetsPerSkid", totalSheets);
        args.putInt("StacksPerSkid", currentSkid.getNumberOfStacks());
        args.putDouble("FinishedHeight", currentSkid.getFinishedStackHeight());

        Product currentProduct = mModel.getSelectedWorkOrder().getProduct();
        if (currentProduct != null) {
            args.putDouble("AverageGauge", currentProduct.getAverageGauge());
        }
        
        newFragment.setArguments(args);
        newFragment.show(mActivity.getFragmentManager(), "GoByHeightDialog");
    }

    public void onClickPositiveButton(DialogFragment d) {
        if (d.getTag() == "GoByHeightDialog") {
            GoByHeightDialogFragment gbhd = (GoByHeightDialogFragment) d;
            Skid<Product> currentSkid = mModel.getSelectedWorkOrder().getSelectedSkid();
            currentSkid.setTotalItems(gbhd.getTotalSheets());
            currentSkid.setNumberOfStacks(gbhd.getNumberOfStacks());
            currentSkid.setFinishedStackHeight(gbhd.getFinishedHeight()); //will be 0 if they entered average gauge
            
            if (mModel.hasSelectedProduct()) {
                Product currentProduct = mModel.getSelectedWorkOrder().getProduct();
                if (gbhd.getAverageGauge() > 0) currentProduct.setAverageGauge(gbhd.getAverageGauge());
                mModel.changeProduct(currentProduct);
            } 
            
            int items;
            double totalHeight;
            if (gbhd.getAverageGauge() > 0) {
                items = mModel.calculateSheetsFromGauge(gbhd.getCurrentHeight(), gbhd.getAverageGauge());
                totalHeight = gbhd.getTotalSheets() * gbhd.getAverageGauge();
            } else {
                items = mModel.calculateSheetsFromHeight(gbhd.getCurrentHeight(), gbhd.getFinishedHeight());
                totalHeight = gbhd.getFinishedHeight();
            }
            if (items > 0) {
                currentSkid.setCurrentItems(items);
                View currentItems = mActivity.findViewById(R.id.edit_current_count);
                if (currentItems != null) currentItems.requestFocus(); //to show what changed
            }

            mModel.changeSelectedSkid(currentSkid.getSkidNumber()); //to trigger events
            Spannable heightAsFraction = new SpannableStringBuilder(
                    HelperFunction.formatDecimalAsProperFraction(totalHeight, 16d)).append("\"");
            View heightButton = mActivity.findViewById(R.id.btn_go_by_height);
            if (heightButton != null) ((Button) heightButton).setText(heightAsFraction);            
        }
        
        if (d.getTag() == "EnterProductDialog") {
            EnterProductDialogFragment epd = (EnterProductDialogFragment)d;
            
            double gauge = epd.getGauge();
            double width = epd.getSheetWidthValue();
            double length = epd.getSheetLengthValue();
            double lineSpeed = epd.getLineSpeedValue();
            double diffSpeed = epd.getDifferentialSpeedValue();
            double speedFactor = epd.getSpeedFactorValue();
            
            mModel.setNumberOfTableSkids(epd.getNumberOfSkids());
            updateSpeedData(lineSpeed, diffSpeed, speedFactor);
            String productType;
            if (epd.getSheetsOrRollsState().equals(EnterProductDialogFragment.ROLLS_MODE)) {
                
                if (epd.getNumberOfWebs() == 1) {
                    productType = Product.ROLLS_TYPE;
                } else {
                    productType = Product.ROLLSET_TYPE;
                    Toast.makeText(mActivity, "Divide total rolls by " + String.valueOf(epd.getNumberOfWebs()) + 
                            " to get total rollsets", Toast.LENGTH_LONG).show();
                }
            } else {
                if (epd.getNumberOfWebs() == 1) productType = Product.SHEETS_TYPE;
                else {
                    productType = Product.SHEETSET_TYPE;
                    if (epd.getNumberOfSkids() == 2) {
                        Toast.makeText(mActivity, "Divide total skids by 2 to get total skidsets", Toast.LENGTH_LONG).show();
                    }
                }
            }
            updateProductData(productType, gauge, width, length, epd.getNumberOfWebs(), epd.getCoreTypeSelection());
        }
    }

    public void updateSkidData(Integer skidNumber, Integer currentCount,
            Integer totalCount, Double numberOfSkids) {
        mModel.getSelectedWorkOrder().updateSkidsList(skidNumber, totalCount, numberOfSkids);
        
        Skid<Product> skid = new Skid<Product>(currentCount, totalCount, 1);
        skid.setSkidNumber(skidNumber);
        mModel.getSelectedWorkOrder().addOrUpdateSkid(skid);
        mModel.changeSelectedSkid(skidNumber);
        mModel.saveSkid(skid);
        mModel.changeNumberOfSkids(numberOfSkids);
        
        mModel.calculateTimes();
    }

    protected void updateProductData(String productType, double gauge,
            double width, double length, int numberOfWebs, int coreTypeSelection) {
        Product p;
        try {
            p = Products.makeProduct(productType, gauge, width, length,
                    numberOfWebs);
            if (p instanceof Roll) {
                Roll pRoll = (Roll) p;
                switch (coreTypeSelection) {
                    case R.id.radio_r3:
                        pRoll.setCoreType(Roll.CORE_TYPE_R3);
                        break;
                    case R.id.radio_r6:
                        pRoll.setCoreType(Roll.CORE_TYPE_R6);
                        break;
                    case R.id.radio_r8:
                        pRoll.setCoreType(Roll.CORE_TYPE_R8);
                        break;
                }
            }
            
            Product oldProduct = mModel.getSelectedWorkOrder().getProduct();
            if (oldProduct != null) {
                p.setUnitWeight(oldProduct.getUnitWeight()
                        * Double.valueOf(p.getNumberOfWebs()) / Double.valueOf(oldProduct.getNumberOfWebs()));
            }
        } catch (IllegalArgumentException e) {
            if (e.getCause().equals(PrimexModel.ERROR_NO_PRODUCT_SELECTED)) {
                throw new IllegalStateException(new Throwable(
                        PrimexModel.ERROR_NO_PRODUCT_SELECTED));
            } else throw e;
        }
        mModel.changeProduct(p);
    }

    protected void updateSpeedData(double lineSpeed, double diffSpeed,
            double speedFactor) {
        SpeedValues sv = new SpeedValues(lineSpeed, diffSpeed, speedFactor);
        mModel.setCurrentSpeed(sv);
    }

    public void updateRatesData(Double grossWidth, Double unitWeight,
            Double novaSetpoint, Double letdownGrams) {
        mModel.getSelectedLine().setWebWidth(grossWidth);
        mModel.getSelectedLine().getPrimaryNovatec()
                .setSetpoint(novaSetpoint); // TODO ug...ly.
        if (mModel.hasSelectedProduct()) {
            Product p = mModel.getSelectedWorkOrder().getProduct();
            if (p instanceof Rollset || p instanceof Sheetset) {
                unitWeight *= Double.valueOf(p.getNumberOfWebs());
            }
            p.setUnitWeight(unitWeight);
            mModel.changeProduct(p);
        }
        mModel.saveSelectedLine();
        mModel.calculateRates(letdownGrams);
    }


}

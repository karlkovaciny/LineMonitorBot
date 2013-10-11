package com.kovaciny.database;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.kovaciny.primexmodel.Product;
import com.kovaciny.primexmodel.ProductionLine;

public class PrimexDatabaseCreator {

    public static final int DEFAULT_INITIAL_LINE_ID = 7;
    public static final int DEFAULT_INITIAL_WO_NUM = 123;

    private HashMap<Integer, Long> mLineNumberToLineIdMap;
    
    private static final String TEXT_TYPE = " TEXT";
    private static final String DOUBLE_TYPE = " DOUBLE";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String REAL_TYPE = " REAL";

    public static final String SQL_CREATE_MODEL_STATE = 
            "CREATE TABLE " + PrimexDatabaseSchema.ModelState.TABLE_NAME + " (" +
                    PrimexDatabaseSchema.ModelState._ID + " INTEGER PRIMARY KEY," +
                    PrimexDatabaseSchema.ModelState.COLUMN_NAME_SELECTED_WORK_ORDER + INTEGER_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.ModelState.COLUMN_NAME_CREATE_DATE + INTEGER_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.ModelState.COLUMN_NAME_EDGE_TRIM_PERCENT + DOUBLE_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.ModelState.COLUMN_NAME_NET_PPH + DOUBLE_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.ModelState.COLUMN_NAME_GROSS_PPH + DOUBLE_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.ModelState.COLUMN_NAME_COLOR_PERCENT + DOUBLE_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.ModelState.COLUMN_NAME_TEN_SECOND_LETDOWN_GRAMS + DOUBLE_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.ModelState.COLUMN_NAME_LINE_SPEED_SETPOINT + DOUBLE_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.ModelState.COLUMN_NAME_DIFFERENTIAL_SETPOINT + DOUBLE_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.ModelState.COLUMN_NAME_PRODUCTS_PER_MINUTE + DOUBLE_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.ModelState.COLUMN_NAME_NUMBER_OF_TABLE_SKIDS + INTEGER_TYPE + COMMA_SEP +
                    "UNIQUE (" + PrimexDatabaseSchema.ModelState.COLUMN_NAME_SELECTED_WORK_ORDER + ")" +
                    ")";

    public static final String SQL_CREATE_PRODUCTION_LINES =
            "CREATE TABLE " + PrimexDatabaseSchema.ProductionLines.TABLE_NAME + " (" +
                    PrimexDatabaseSchema.ProductionLines._ID + " INTEGER PRIMARY KEY," +
                    PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LINE_NUMBER + INTEGER_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LENGTH + INTEGER_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_DIE_WIDTH + INTEGER_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_WEB_WIDTH + REAL_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_SPEED_CONTROLLER_TYPE + TEXT_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_SPEED_SETPOINT + DOUBLE_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_DIFFERENTIAL_SPEED_SETPOINT + DOUBLE_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_DIFFERENTIAL_RANGE_LOW + DOUBLE_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_DIFFERENTIAL_RANGE_HIGH + DOUBLE_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_SPEED_FACTOR + DOUBLE_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_TAKEOFF_EQUIPMENT_TYPE + TEXT_TYPE + COMMA_SEP +
                    " UNIQUE (" + PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LINE_NUMBER + ")" +
                    " )";

    public static final String SQL_CREATE_WORK_ORDERS =
            "CREATE TABLE " + PrimexDatabaseSchema.WorkOrders.TABLE_NAME + " (" +
                    PrimexDatabaseSchema.WorkOrders._ID + " INTEGER PRIMARY KEY," +
                    PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_WO_NUMBER + INTEGER_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_SELECTED_PRODUCT_ID + INTEGER_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_TOTAL_PRODUCTS_ORDERED + DOUBLE_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_SELECTED_SKID_NUMBER + INTEGER_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_MAXIMUM_STACK_HEIGHT + DOUBLE_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_FINISH_TIME + INTEGER_TYPE + COMMA_SEP +
                    " UNIQUE (" + PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_WO_NUMBER + ")" +
                    " )";

    public static final String SQL_CREATE_LINE_WORK_ORDER_LINK = 
            "CREATE TABLE " + PrimexDatabaseSchema.LineWorkOrderLink.TABLE_NAME + " (" +
                    PrimexDatabaseSchema.LineWorkOrderLink._ID + " INTEGER PRIMARY KEY, " + 
                    PrimexDatabaseSchema.LineWorkOrderLink.COLUMN_NAME_LINE_ID + INTEGER_TYPE + COMMA_SEP + 
                    PrimexDatabaseSchema.LineWorkOrderLink.COLUMN_NAME_WO_ID + INTEGER_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.LineWorkOrderLink.COLUMN_NAME_WO_IS_SELECTED + INTEGER_TYPE + COMMA_SEP +
                    " UNIQUE (" + PrimexDatabaseSchema.LineWorkOrderLink.COLUMN_NAME_WO_ID + ")" +
                    ")";

    public static final String SQL_CREATE_PRODUCTS = 
            "CREATE TABLE " + PrimexDatabaseSchema.Products.TABLE_NAME + " (" +
                    PrimexDatabaseSchema.Products._ID + " INTEGER PRIMARY KEY," +
                    PrimexDatabaseSchema.Products.COLUMN_NAME_GAUGE + REAL_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.Products.COLUMN_NAME_WIDTH + REAL_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.Products.COLUMN_NAME_LENGTH + REAL_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.Products.COLUMN_NAME_TYPE_ID + INTEGER_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.Products.COLUMN_NAME_WO_NUMBER + INTEGER_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.Products.COLUMN_NAME_UNIT_WEIGHT + REAL_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.Products.COLUMN_NAME_NUMBER_OF_WEBS + INTEGER_TYPE + COMMA_SEP +                   
                    PrimexDatabaseSchema.Products.COLUMN_NAME_CORE_TYPE + INTEGER_TYPE + COMMA_SEP +                    
                    " UNIQUE ("  + PrimexDatabaseSchema.Products.COLUMN_NAME_WO_NUMBER + ")" +
                    " )";

    public static final String SQL_CREATE_HOPPERS = 
            "CREATE TABLE " + PrimexDatabaseSchema.Hoppers.TABLE_NAME + " (" +
                    PrimexDatabaseSchema.Hoppers._ID + " INTEGER PRIMARY KEY," +
                    PrimexDatabaseSchema.Hoppers.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP + 
                    PrimexDatabaseSchema.Hoppers.COLUMN_NAME_DISPLAY_NAME + TEXT_TYPE + COMMA_SEP + 
                    PrimexDatabaseSchema.Hoppers.COLUMN_NAME_SAFE_DRAIN_TIME_RESIN + REAL_TYPE + COMMA_SEP + 
                    PrimexDatabaseSchema.Hoppers.COLUMN_NAME_SAFE_DRAIN_TIME_PREMIX + REAL_TYPE + COMMA_SEP + 
                    PrimexDatabaseSchema.Hoppers.COLUMN_NAME_SAFE_DRAIN_TIME_BLEND + REAL_TYPE + COMMA_SEP + 
                    PrimexDatabaseSchema.Hoppers.COLUMN_NAME_SAFE_DRAIN_TIME_HICAL + REAL_TYPE + COMMA_SEP + 
                    PrimexDatabaseSchema.Hoppers.COLUMN_NAME_SAFE_DRAIN_TIME_HM10MAX + REAL_TYPE + COMMA_SEP + 
                    PrimexDatabaseSchema.Hoppers.COLUMN_NAME_SAFE_DRAIN_TIME_3414 + REAL_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.Hoppers.COLUMN_NAME_ESTIMATED_DRAIN_TIME_RESIN + REAL_TYPE + COMMA_SEP + 
                    PrimexDatabaseSchema.Hoppers.COLUMN_NAME_ESTIMATED_DRAIN_TIME_PREMIX + REAL_TYPE + COMMA_SEP + 
                    PrimexDatabaseSchema.Hoppers.COLUMN_NAME_ESTIMATED_DRAIN_TIME_BLEND + REAL_TYPE + COMMA_SEP + 
                    PrimexDatabaseSchema.Hoppers.COLUMN_NAME_ESTIMATED_DRAIN_TIME_HICAL + REAL_TYPE + COMMA_SEP + 
                    PrimexDatabaseSchema.Hoppers.COLUMN_NAME_ESTIMATED_DRAIN_TIME_HM10MAX + REAL_TYPE + COMMA_SEP + 
                    PrimexDatabaseSchema.Hoppers.COLUMN_NAME_ESTIMATED_DRAIN_TIME_3414 + REAL_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.Hoppers.COLUMN_NAME_CONTENTS + TEXT_TYPE + COMMA_SEP + 
                    PrimexDatabaseSchema.Hoppers.COLUMN_NAME_PERCENT_SETPOINT + REAL_TYPE + COMMA_SEP + 
                    PrimexDatabaseSchema.Hoppers.COLUMN_NAME_EXTRUDER_NUMBER + INTEGER_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.Hoppers.COLUMN_NAME_LINE_NUMBER_ID + INTEGER_TYPE + COMMA_SEP +
                    " UNIQUE (" + PrimexDatabaseSchema.Hoppers.COLUMN_NAME_NAME + ")" +

            " )";
    
    public static final String SQL_CREATE_BLENDED_HOPPERS = 
            "CREATE TABLE " + PrimexDatabaseSchema.BlendedHoppers.TABLE_NAME + " (" +
                    PrimexDatabaseSchema.BlendedHoppers._ID + " INTEGER PRIMARY KEY," +
                    PrimexDatabaseSchema.BlendedHoppers.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP + 
                    PrimexDatabaseSchema.BlendedHoppers.COLUMN_NAME_DISPLAY_NAME + TEXT_TYPE + COMMA_SEP + 
                    PrimexDatabaseSchema.BlendedHoppers.COLUMN_NAME_SAFE_DRAIN_TIME_RESIN + REAL_TYPE + COMMA_SEP + 
                    PrimexDatabaseSchema.BlendedHoppers.COLUMN_NAME_SAFE_DRAIN_TIME_REGRIND + REAL_TYPE + COMMA_SEP + 
                    PrimexDatabaseSchema.BlendedHoppers.COLUMN_NAME_SAFE_DRAIN_TIME_30_70_BLEND + REAL_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.BlendedHoppers.COLUMN_NAME_ESTIMATED_DRAIN_TIME_RESIN + REAL_TYPE + COMMA_SEP + 
                    PrimexDatabaseSchema.BlendedHoppers.COLUMN_NAME_ESTIMATED_DRAIN_TIME_REGRIND + REAL_TYPE + COMMA_SEP + 
                    PrimexDatabaseSchema.BlendedHoppers.COLUMN_NAME_ESTIMATED_DRAIN_TIME_30_70_BLEND + REAL_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.BlendedHoppers.COLUMN_NAME_CONTENTS + TEXT_TYPE + COMMA_SEP + 
                    PrimexDatabaseSchema.BlendedHoppers.COLUMN_NAME_PERCENT_SETPOINT + REAL_TYPE + COMMA_SEP + 
                    PrimexDatabaseSchema.BlendedHoppers.COLUMN_NAME_EXTRUDER_POSITION + INTEGER_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.BlendedHoppers.COLUMN_NAME_LINE_NUMBER_ID + INTEGER_TYPE + COMMA_SEP +
                    " UNIQUE (" + PrimexDatabaseSchema.BlendedHoppers.COLUMN_NAME_NAME + ")" +

            " )";

    public static final String SQL_CREATE_NOVATECS = 
            "CREATE TABLE " + PrimexDatabaseSchema.Novatecs.TABLE_NAME + " (" +
                    PrimexDatabaseSchema.Novatecs._ID + " INTEGER PRIMARY KEY," +
                    PrimexDatabaseSchema.Novatecs.COLUMN_NAME_SCREW_SIZE_FACTOR + REAL_TYPE + COMMA_SEP + 
                    PrimexDatabaseSchema.Novatecs.COLUMN_NAME_CURRENT_SETPOINT + REAL_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.Novatecs.COLUMN_NAME_LINE_NUMBER_ID + INTEGER_TYPE + COMMA_SEP +
                    " UNIQUE (" + PrimexDatabaseSchema.Novatecs.COLUMN_NAME_LINE_NUMBER_ID + ")" +

            " )";

    public static final String SQL_CREATE_SKIDS = 
            "CREATE TABLE " + PrimexDatabaseSchema.Skids.TABLE_NAME + " (" +
                    PrimexDatabaseSchema.Skids._ID + " INTEGER PRIMARY KEY," +
                    PrimexDatabaseSchema.Skids.COLUMN_NAME_SKID_NUMBER + INTEGER_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.Skids.COLUMN_NAME_CURRENT_ITEMS + INTEGER_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.Skids.COLUMN_NAME_TOTAL_ITEMS + INTEGER_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.Skids.COLUMN_NAME_STACKS + INTEGER_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.Skids.COLUMN_NAME_START_DATE + INTEGER_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.Skids.COLUMN_NAME_FINISH_DATE + INTEGER_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.Skids.COLUMN_NAME_TIME_PER_SKID + REAL_TYPE + COMMA_SEP +
                    PrimexDatabaseSchema.Skids.COLUMN_NAME_WO_ID + INTEGER_TYPE + COMMA_SEP +
                    " UNIQUE (" + PrimexDatabaseSchema.Skids.COLUMN_NAME_SKID_NUMBER + ", " + PrimexDatabaseSchema.Skids.COLUMN_NAME_WO_ID + ")" +
                    " )";

    public static final String SQL_CREATE_PRODUCT_TYPES = 
            "CREATE TABLE " + PrimexDatabaseSchema.ProductTypes.TABLE_NAME + " (" +
                    PrimexDatabaseSchema.ProductTypes._ID + " INTEGER PRIMARY KEY," +
                    PrimexDatabaseSchema.ProductTypes.COLUMN_NAME_TYPES + TEXT_TYPE +
                    //I'm just gonna be careful cause I don't want to turn foreign keys on.
                    //"FOREIGN KEY(" + PrimexDatabaseSchema.ProductTypes.COLUMN_NAME_TYPES + ") REFERENCES +" +
                    //      PrimexDatabaseSchema.ProductTypes.COLUMN_NAME_TYPES +
                    " )";

    public PrimexDatabaseCreator() {
        mLineNumberToLineIdMap = new HashMap<Integer, Long>();
    }

    public void create(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_MODEL_STATE);
        //Batch insert to SQLite database on Android
        try {
            db.beginTransaction();
            Integer woNum = DEFAULT_INITIAL_WO_NUM;
            ContentValues modvalues = new ContentValues();
            modvalues.put(PrimexDatabaseSchema.ModelState.COLUMN_NAME_SELECTED_WORK_ORDER, woNum);
            modvalues.put(PrimexDatabaseSchema.ModelState.COLUMN_NAME_CREATE_DATE,0);

            db.insertOrThrow(PrimexDatabaseSchema.ModelState.TABLE_NAME, null, modvalues);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        db.execSQL(SQL_CREATE_PRODUCTION_LINES);
        final List<Integer> lineNumbers = Arrays.asList(1,6,7,9,10,  11,12,13,14,15,  16,17,18); //13 lines
        try {
            List<Double> lengthsList = 
                    Arrays.asList(new Double[] {99.0d, 51.5d, 34.2d, 46.0d, 44.7d,  45.7d,56.7d,56.3d,64.3d,46.5d, 45.0d,61.9d, 71d});
            Iterator<Double> lengthsIterator = lengthsList.iterator();
            
            List<Double> dieWidthsList = Arrays.asList(new Double[]{130d,58d,53d,58d,64d, 64d,78d,75d,75d,64d, 64d, 58.5d, 53d});
            Iterator<Double> dieWidthsIterator = dieWidthsList.iterator();
            
            List<Double> speedFactorsList = Arrays.asList(new Double[]{.995d,.07746d,.01d,1.01d,.0102d,  1d,1d,.0098d,.0098d,1.01d, 1.01d,.0347d,.01003d});         
            Iterator<Double> speedFactorsIterator = speedFactorsList.iterator();
            
            List<String> speedControllerTypesList = Arrays.asList(
                    ProductionLine.SPEED_CONTROLLER_TYPE_NONE, 
                    ProductionLine.SPEED_CONTROLLER_TYPE_GEARED, 
                    ProductionLine.SPEED_CONTROLLER_TYPE_PERCENT, 
                    ProductionLine.SPEED_CONTROLLER_TYPE_GEARED, 
                    ProductionLine.SPEED_CONTROLLER_TYPE_PERCENT,
                    
                    ProductionLine.SPEED_CONTROLLER_TYPE_RATIO,
                    ProductionLine.SPEED_CONTROLLER_TYPE_NONE,
                    ProductionLine.SPEED_CONTROLLER_TYPE_PERCENT,
                    ProductionLine.SPEED_CONTROLLER_TYPE_PERCENT,
                    ProductionLine.SPEED_CONTROLLER_TYPE_RATIO,
                    
                    ProductionLine.SPEED_CONTROLLER_TYPE_RATIO,
                    ProductionLine.SPEED_CONTROLLER_TYPE_GEARED,
                    ProductionLine.SPEED_CONTROLLER_TYPE_PERCENT
                    );
            Iterator<String> speedControllerTypesIterator = speedControllerTypesList.iterator();

            //This number represents how long it takes to drain the extruder hopper to a dangerous level.
            List<Integer> drainFactorsList = Arrays.asList(new Integer[]{0,0,0,0,0, 22000,0,0,0,0, 0,0,0});
            Iterator<Integer> drainFactorsIterator = drainFactorsList.iterator();
            
            db.beginTransaction();
            for (Integer lineNum : lineNumbers) {
                ContentValues values = new ContentValues();
                values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LINE_NUMBER, lineNum);
                values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LENGTH, lengthsIterator.next());
                values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_DIE_WIDTH, dieWidthsIterator.next());
                values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_SPEED_SETPOINT, 0);
                values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_DIFFERENTIAL_SPEED_SETPOINT, 0);
                values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_SPEED_FACTOR, speedFactorsIterator.next());
                values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_SPEED_CONTROLLER_TYPE, speedControllerTypesIterator.next());
                values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_TAKEOFF_EQUIPMENT_TYPE, "Maxson");
                
                long rowId = db.insertOrThrow(
                        PrimexDatabaseSchema.ProductionLines.TABLE_NAME, 
                        null, 
                        values);
                
                mLineNumberToLineIdMap.put(lineNum, rowId);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        db.execSQL(SQL_CREATE_WORK_ORDERS);
        try {
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_WO_NUMBER, DEFAULT_INITIAL_WO_NUM);
            values.put(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_TOTAL_PRODUCTS_ORDERED,1000);
            values.put(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_MAXIMUM_STACK_HEIGHT,0);
            values.put(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_SELECTED_SKID_NUMBER,1);

            db.insertOrThrow(PrimexDatabaseSchema.WorkOrders.TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        db.execSQL(SQL_CREATE_LINE_WORK_ORDER_LINK);
        try {
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(PrimexDatabaseSchema.LineWorkOrderLink.COLUMN_NAME_LINE_ID, DEFAULT_INITIAL_LINE_ID);
            values.put(PrimexDatabaseSchema.LineWorkOrderLink.COLUMN_NAME_WO_ID, DEFAULT_INITIAL_WO_NUM);
            values.put(PrimexDatabaseSchema.LineWorkOrderLink.COLUMN_NAME_WO_IS_SELECTED, 1);
            db.insertOrThrow(PrimexDatabaseSchema.LineWorkOrderLink.TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.execSQL(SQL_CREATE_PRODUCT_TYPES);
        try {
            db.beginTransaction();
            String[] types = {Product.SHEETS_TYPE, Product.ROLLS_TYPE, Product.ROLLSET_TYPE, Product.SHEETSET_TYPE};
            for (int j = 0; j < types.length; j++) {
                ContentValues ptvalues = new ContentValues();
                ptvalues.put(PrimexDatabaseSchema.ProductTypes.COLUMN_NAME_TYPES, types[j]);

                db.insertOrThrow(PrimexDatabaseSchema.ProductTypes.TABLE_NAME, null, ptvalues);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        db.execSQL(SQL_CREATE_HOPPERS);
        try {
            db.beginTransaction();
            
            List<String> hopperNames = 
                    Arrays.asList(new String[] {"Line 6 extruder hopper", "Line 6 blender", "Line 6 blender hopper 1", "Line 6 blender hopper 2", "Line 6 blender hopper 3", "Line 6 blender hopper 4"});
            Iterator<String> hopperIterator = hopperNames.iterator();
            
            for (Integer lineNum : lineNumbers) {
                ContentValues hopperValues = new ContentValues();
                long lineId = mLineNumberToLineIdMap.get(lineNum);
                hopperValues.put(PrimexDatabaseSchema.Hoppers.COLUMN_NAME_LINE_NUMBER_ID, lineId);
                
                if (lineId == 5){ //line 11
                    hopperValues.put(PrimexDatabaseSchema.Hoppers.COLUMN_NAME_NAME, "Line_11_hopper_1");
                    hopperValues.put(PrimexDatabaseSchema.Hoppers.COLUMN_NAME_DISPLAY_NAME, "1");
                    hopperValues.put(PrimexDatabaseSchema.Hoppers.COLUMN_NAME_SAFE_DRAIN_TIME_RESIN, 9.5);
                    hopperValues.put(PrimexDatabaseSchema.Hoppers.COLUMN_NAME_EXTRUDER_NUMBER, 0);
                    
                    hopperValues.put(PrimexDatabaseSchema.Hoppers.COLUMN_NAME_NAME, "Line_11_hopper_2");
                    hopperValues.put(PrimexDatabaseSchema.Hoppers.COLUMN_NAME_DISPLAY_NAME, "2");
                    hopperValues.put(PrimexDatabaseSchema.Hoppers.COLUMN_NAME_SAFE_DRAIN_TIME_RESIN, 9.5);
                    hopperValues.put(PrimexDatabaseSchema.Hoppers.COLUMN_NAME_EXTRUDER_NUMBER, 0);
                }
                
                db.insertOrThrow(PrimexDatabaseSchema.Hoppers.TABLE_NAME, null, hopperValues);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        db.execSQL(SQL_CREATE_BLENDED_HOPPERS);
        try {
            db.beginTransaction();
            
            //"Blender + hopper" is treated as a separate hopper because just subtracting the hopper
                //didn't give consistent blender times.
            List<String> hopperNames = 
                    Arrays.asList(new String[] {
                            "Line 6 extruder hopper", 
                            "Line 6 blender + extruder hopper"
                            });
            
            List<Double> resinSafeDrainTimes = 
                    Arrays.asList(new Double[] {
                            12d, 
                            22.3                            
                    });
            
            List<Double> regrindSafeDrainTimes = 
                    Arrays.asList(new Double[] {
                            9d,
                            12d                            
                    });
            
            List<Double> blendSafeDrainTimes = 
                    Arrays.asList(new Double[] {
                            12d,
                            16d                            
                    });
            
            List<Integer> extruderPositions = 
                    Arrays.asList(new Integer[] {
                            0, 0                            
                    });
            
            //the line this hopper is on
            List<Integer> associatedLines = 
                    Arrays.asList(new Integer[] {
                            6, 6                            
                    });
            
            Iterator<Double> resinSafeTimesItr = resinSafeDrainTimes.iterator();
            Iterator<Double> regrindSafeTimesItr = regrindSafeDrainTimes.iterator();
            Iterator<Double> blendSafeTimesItr = blendSafeDrainTimes.iterator();
            Iterator<Integer> extruderPositionsItr = extruderPositions.iterator();
            Iterator<Integer> associatedLinesItr = associatedLines.iterator();
            
            for (String hopperName : hopperNames) {
                ContentValues blendedHopperValues = new ContentValues();
                long lineId = mLineNumberToLineIdMap.get(associatedLinesItr.next());
                blendedHopperValues.put(PrimexDatabaseSchema.BlendedHoppers.COLUMN_NAME_LINE_NUMBER_ID, lineId);
                blendedHopperValues.put(PrimexDatabaseSchema.BlendedHoppers.COLUMN_NAME_NAME, hopperName);
                blendedHopperValues.put(PrimexDatabaseSchema.BlendedHoppers.COLUMN_NAME_SAFE_DRAIN_TIME_RESIN, resinSafeTimesItr.next());
                blendedHopperValues.put(PrimexDatabaseSchema.BlendedHoppers.COLUMN_NAME_SAFE_DRAIN_TIME_REGRIND, regrindSafeTimesItr.next());
                blendedHopperValues.put(PrimexDatabaseSchema.BlendedHoppers.COLUMN_NAME_SAFE_DRAIN_TIME_30_70_BLEND, blendSafeTimesItr.next());
                blendedHopperValues.put(PrimexDatabaseSchema.BlendedHoppers.COLUMN_NAME_EXTRUDER_POSITION, extruderPositionsItr.next());
                
                /*if (lineId == 5){ //line 11
                    blendedHopperValues.put(PrimexDatabaseSchema.BlendedHoppers.COLUMN_NAME_NAME, "Line_11_hopper_1");
                    blendedHopperValues.put(PrimexDatabaseSchema.BlendedHoppers.COLUMN_NAME_DISPLAY_NAME, "1");
                    blendedHopperValues.put(PrimexDatabaseSchema.BlendedHoppers.COLUMN_NAME_SAFE_DRAIN_TIME_RESIN, 9.5);
                    blendedHopperValues.put(PrimexDatabaseSchema.BlendedHoppers.COLUMN_NAME_EXTRUDER_NUMBER, 0);
                    
                    blendedHopperValues.put(PrimexDatabaseSchema.BlendedHoppers.COLUMN_NAME_NAME, "Line_11_hopper_2");
                    blendedHopperValues.put(PrimexDatabaseSchema.BlendedHoppers.COLUMN_NAME_DISPLAY_NAME, "2");
                    blendedHopperValues.put(PrimexDatabaseSchema.BlendedHoppers.COLUMN_NAME_SAFE_DRAIN_TIME_RESIN, 9.5);
                    blendedHopperValues.put(PrimexDatabaseSchema.BlendedHoppers.COLUMN_NAME_EXTRUDER_NUMBER, 0);
                }*/
                
                db.insertOrThrow(PrimexDatabaseSchema.BlendedHoppers.TABLE_NAME, null, blendedHopperValues);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        db.execSQL(SQL_CREATE_NOVATECS);
        try {
            db.beginTransaction();
            Integer defaultSetpoint = 0;
            List<Integer> linesWithBigNovatecs = Arrays.asList(new Integer[] {1,11,12,13,14});
            double screwRatio;

            for (Integer lineNum : lineNumbers) {
                ContentValues novatecValues = new ContentValues();
                long lineId = mLineNumberToLineIdMap.get(lineNum);novatecValues.put(PrimexDatabaseSchema.Novatecs.COLUMN_NAME_LINE_NUMBER_ID, lineId);
                novatecValues.put(PrimexDatabaseSchema.Novatecs.COLUMN_NAME_CURRENT_SETPOINT, defaultSetpoint);
                if (linesWithBigNovatecs.contains(lineNum)) {
                    screwRatio = 1.5; 
                } else screwRatio = 1d;
                novatecValues.put(PrimexDatabaseSchema.Novatecs.COLUMN_NAME_SCREW_SIZE_FACTOR, screwRatio);
                db.insertOrThrow(PrimexDatabaseSchema.Novatecs.TABLE_NAME, null, novatecValues);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        db.execSQL(SQL_CREATE_PRODUCTS);
        db.execSQL(SQL_CREATE_SKIDS);
        try {
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(PrimexDatabaseSchema.Skids.COLUMN_NAME_SKID_NUMBER, 1);
            values.put(PrimexDatabaseSchema.Skids.COLUMN_NAME_WO_ID, 1);
            values.put(PrimexDatabaseSchema.Skids.COLUMN_NAME_TOTAL_ITEMS, 1000);
            db.insertOrThrow(PrimexDatabaseSchema.Skids.TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}

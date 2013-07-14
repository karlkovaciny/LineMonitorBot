package com.kovaciny.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.kovaciny.primexmodel.Novatec;
import com.kovaciny.primexmodel.Pallet;
import com.kovaciny.primexmodel.Product;
import com.kovaciny.primexmodel.ProductionLine;
import com.kovaciny.primexmodel.Roll;
import com.kovaciny.primexmodel.Sheet;
import com.kovaciny.primexmodel.Skid;
import com.kovaciny.primexmodel.SpeedValues;
import com.kovaciny.primexmodel.WorkOrder;

public class PrimexSQLiteOpenHelper extends SQLiteOpenHelper {
    
	public PrimexSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
	
	// If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 94;
    public static final String DATABASE_NAME = "Primex.db";
    
	private static final String TEXT_TYPE = " TEXT";
	private static final String DOUBLE_TYPE = " DOUBLE";
	private static final String INTEGER_TYPE = " INTEGER";
	private static final String COMMA_SEP = ",";
	private static final String REAL_TYPE = " REAL";
		
	private static final String SQL_CREATE_PRODUCTION_LINES =
    	    "CREATE TABLE " + PrimexDatabaseSchema.ProductionLines.TABLE_NAME + " (" +
    	    PrimexDatabaseSchema.ProductionLines._ID + " INTEGER PRIMARY KEY," +
    	    PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LINE_NUMBER + INTEGER_TYPE + COMMA_SEP +
    	    PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LENGTH + INTEGER_TYPE + COMMA_SEP +
    	    PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_DIE_WIDTH + INTEGER_TYPE + COMMA_SEP +
    	    PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_WEB_WIDTH + REAL_TYPE + COMMA_SEP +
    	    PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_SPEED_CONTROLLER_TYPE + TEXT_TYPE + COMMA_SEP +
    	    PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_SPEED_SETPOINT + DOUBLE_TYPE + COMMA_SEP +
    	    PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_DIFFERENTIAL_SPEED_SETPOINT + DOUBLE_TYPE + COMMA_SEP +
    	    PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_SPEED_FACTOR + DOUBLE_TYPE + COMMA_SEP +
    	    PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_TAKEOFF_EQUIPMENT_TYPE + TEXT_TYPE + COMMA_SEP +
    	    " UNIQUE (" + PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LINE_NUMBER + ")" +
    	    " )";
    
    private static final String SQL_CREATE_WORK_ORDERS =
    	    "CREATE TABLE " + PrimexDatabaseSchema.WorkOrders.TABLE_NAME + " (" +
    	    PrimexDatabaseSchema.WorkOrders._ID + " INTEGER PRIMARY KEY," +
	    	PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_WO_NUMBER + INTEGER_TYPE + COMMA_SEP +
	    	PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_SELECTED_PRODUCT_ID + INTEGER_TYPE + COMMA_SEP +
	    	PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_TOTAL_PRODUCTS_ORDERED + DOUBLE_TYPE + COMMA_SEP +
	    	PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_SELECTED_SKID_NUMBER + INTEGER_TYPE + COMMA_SEP +
	    	PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_MAXIMUM_STACK_HEIGHT + DOUBLE_TYPE + COMMA_SEP +
	    	PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_NOVATEC_SETPOINT + DOUBLE_TYPE + COMMA_SEP +
	    	PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_FINISH_TIME + INTEGER_TYPE + COMMA_SEP +
	    	" UNIQUE (" + PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_WO_NUMBER + ")" +
	    	" )";
    
    private static final String SQL_CREATE_LINE_WORK_ORDER_LINK = 
    		"CREATE TABLE " + PrimexDatabaseSchema.LineWorkOrderLink.TABLE_NAME + " (" +
    		PrimexDatabaseSchema.LineWorkOrderLink._ID + " INTEGER PRIMARY KEY, " + 
    		PrimexDatabaseSchema.LineWorkOrderLink.COLUMN_NAME_LINE_ID + INTEGER_TYPE + COMMA_SEP + 
    		PrimexDatabaseSchema.LineWorkOrderLink.COLUMN_NAME_WO_ID + INTEGER_TYPE + COMMA_SEP +
    		PrimexDatabaseSchema.LineWorkOrderLink.COLUMN_NAME_WO_IS_SELECTED + INTEGER_TYPE + COMMA_SEP +
    		" UNIQUE (" + PrimexDatabaseSchema.LineWorkOrderLink.COLUMN_NAME_WO_ID + ")" +
    		")";
    		

    private static final String SQL_CREATE_PRODUCTS = 
    		"CREATE TABLE " + PrimexDatabaseSchema.Products.TABLE_NAME + " (" +
    		PrimexDatabaseSchema.Products._ID + " INTEGER PRIMARY KEY," +
    		PrimexDatabaseSchema.Products.COLUMN_NAME_GAUGE + REAL_TYPE + COMMA_SEP +
    		PrimexDatabaseSchema.Products.COLUMN_NAME_WIDTH + REAL_TYPE + COMMA_SEP +
    		PrimexDatabaseSchema.Products.COLUMN_NAME_LENGTH + REAL_TYPE + COMMA_SEP +
    		PrimexDatabaseSchema.Products.COLUMN_NAME_TYPE + INTEGER_TYPE + COMMA_SEP +
    		PrimexDatabaseSchema.Products.COLUMN_NAME_WO_NUMBER + INTEGER_TYPE + COMMA_SEP +
    		PrimexDatabaseSchema.Products.COLUMN_NAME_UNIT_WEIGHT + REAL_TYPE + COMMA_SEP +
    		" UNIQUE ("  + PrimexDatabaseSchema.Products.COLUMN_NAME_WO_NUMBER + ")" +
    		" )";
    
    private static final String SQL_CREATE_NOVATECS = 
    		"CREATE TABLE " + PrimexDatabaseSchema.Novatecs.TABLE_NAME + " (" +
    		PrimexDatabaseSchema.Novatecs._ID + " INTEGER PRIMARY KEY," +
    		PrimexDatabaseSchema.Novatecs.COLUMN_NAME_LETDOWN_RATIO + REAL_TYPE + COMMA_SEP + 
    		PrimexDatabaseSchema.Novatecs.COLUMN_NAME_CURRENT_SETPOINT + REAL_TYPE + COMMA_SEP +
    		PrimexDatabaseSchema.Novatecs.COLUMN_NAME_LINE_NUMBER_ID + INTEGER_TYPE + COMMA_SEP +
    		" UNIQUE (" + PrimexDatabaseSchema.Novatecs.COLUMN_NAME_LINE_NUMBER_ID + ")" +

    		" )";
    
    private static final String SQL_CREATE_SKIDS = 
    		"CREATE TABLE " + PrimexDatabaseSchema.Skids.TABLE_NAME + " (" +
    		PrimexDatabaseSchema.Skids._ID + " INTEGER PRIMARY KEY," +
    		PrimexDatabaseSchema.Skids.COLUMN_NAME_SKID_NUMBER + INTEGER_TYPE + COMMA_SEP +
    		PrimexDatabaseSchema.Skids.COLUMN_NAME_CURRENT_ITEMS + INTEGER_TYPE + COMMA_SEP +
    		PrimexDatabaseSchema.Skids.COLUMN_NAME_TOTAL_ITEMS + INTEGER_TYPE + COMMA_SEP +
    		PrimexDatabaseSchema.Skids.COLUMN_NAME_STACKS + INTEGER_TYPE + COMMA_SEP +
    		PrimexDatabaseSchema.Skids.COLUMN_NAME_START_DATE + INTEGER_TYPE + COMMA_SEP +
    		PrimexDatabaseSchema.Skids.COLUMN_NAME_FINISH_DATE + INTEGER_TYPE + COMMA_SEP +
    		PrimexDatabaseSchema.Skids.COLUMN_NAME_WO_ID + INTEGER_TYPE + COMMA_SEP +
    		" UNIQUE (" + PrimexDatabaseSchema.Skids.COLUMN_NAME_SKID_NUMBER + ", " + PrimexDatabaseSchema.Skids.COLUMN_NAME_WO_ID + ")" +
    		" )";
    
    private static final String SQL_CREATE_PRODUCT_TYPES = 
    		"CREATE TABLE " + PrimexDatabaseSchema.ProductTypes.TABLE_NAME + " (" +
    		PrimexDatabaseSchema.ProductTypes._ID + " INTEGER PRIMARY KEY," +
    		PrimexDatabaseSchema.ProductTypes.COLUMN_NAME_TYPES + TEXT_TYPE +
    		//I'm just gonna be careful cause I don't want to turn foreign keys on.
    		//"FOREIGN KEY(" + PrimexDatabaseSchema.ProductTypes.COLUMN_NAME_TYPES + ") REFERENCES +" +
    		//		PrimexDatabaseSchema.ProductTypes.COLUMN_NAME_TYPES +
    		" )";
    
    private static final String SQL_CREATE_MODEL_STATE = 
    		"CREATE TABLE " + PrimexDatabaseSchema.ModelState.TABLE_NAME + " (" +
    		PrimexDatabaseSchema.ModelState._ID + " INTEGER PRIMARY KEY," +
    		PrimexDatabaseSchema.ModelState.COLUMN_NAME_SELECTED_LINE + INTEGER_TYPE + COMMA_SEP +
    		PrimexDatabaseSchema.ModelState.COLUMN_NAME_SELECTED_WORK_ORDER + INTEGER_TYPE +
    		" )";
    
	//list "child" tables, which have a foreign key, before their parent, so drop table works
    private static final String TABLE_NAME_NOVATECS = PrimexDatabaseSchema.Novatecs.TABLE_NAME;
    private static final String TABLE_NAME_SKIDS = PrimexDatabaseSchema.Skids.TABLE_NAME;
    private static final String TABLE_NAME_LINE_WORK_ORDER_LINK = PrimexDatabaseSchema.LineWorkOrderLink.TABLE_NAME;
    private static final String TABLE_NAME_PRODUCT_TYPES = PrimexDatabaseSchema.ProductTypes.TABLE_NAME;
    private static final String TABLE_NAME_PRODUCTS = PrimexDatabaseSchema.Products.TABLE_NAME;
    private static final String TABLE_NAME_WORK_ORDERS = PrimexDatabaseSchema.WorkOrders.TABLE_NAME;
    private static final String TABLE_NAME_PRODUCTION_LINES = PrimexDatabaseSchema.ProductionLines.TABLE_NAME;
    private static final String TABLE_NAME_MODEL_STATE = PrimexDatabaseSchema.ModelState.TABLE_NAME;

    private static final String SQL_DELETE_NOVATECS =
    		"DROP TABLE IF EXISTS " + TABLE_NAME_NOVATECS;
    private static final String SQL_DELETE_SKIDS = 
    		"DROP TABLE IF EXISTS " + TABLE_NAME_SKIDS;
    private static final String SQL_DELETE_LINE_WORK_ORDER_LINK =    		
    		"DROP TABLE IF EXISTS " + TABLE_NAME_LINE_WORK_ORDER_LINK;
    private static final String SQL_DELETE_PRODUCT_TYPES =
			"DROP TABLE IF EXISTS " + TABLE_NAME_PRODUCT_TYPES;
	private static final String SQL_DELETE_PRODUCTS =
			"DROP TABLE IF EXISTS " + TABLE_NAME_PRODUCTS;
	private static final String SQL_DELETE_PRODUCTION_LINES =
			"DROP TABLE IF EXISTS " + TABLE_NAME_PRODUCTION_LINES;
	private static final String SQL_DELETE_WORK_ORDERS =
			"DROP TABLE IF EXISTS " + TABLE_NAME_WORK_ORDERS;
	private static final String SQL_DELETE_MODEL_STATE =
			"DROP TABLE IF EXISTS " + TABLE_NAME_MODEL_STATE;
	
    public void onCreate(SQLiteDatabase db) {
    	final List<Integer> lineNumbers = Arrays.asList(1,6,7,9,10,  11,12,13,14,15,  16,17,18); //13 lines
    	db.execSQL(SQL_CREATE_PRODUCTION_LINES);
        //Batch insert to SQLite database on Android
        try {
        	List<Integer> linesWithGearedSpeedControl = Arrays.asList(6,9,12,16,17); //TODO remove this and speed controller type
        	List<Double> lengthsList = 
        			Arrays.asList(new Double[] {99.0d, 51.5d, 34.2d, 46.0d, 44.7d,  45.7d,56.7d,56.3d,64.3d,46.5d, 45.0d,61.9d, 71d});
        	Iterator<Double> lengthsIterator = lengthsList.iterator();
        	
        	List<Double> dieWidthsList = Arrays.asList(new Double[]{1000d,58d,53d,58d,64d, 64d,78d,75d,75d,64d, 64d, 58.5d, 53d});
        	Iterator<Double> dieWidthsIterator = dieWidthsList.iterator();
        	
        	List<Double> speedFactorsList = Arrays.asList(new Double[]{1d,.0769d,1d,.99d,1.015d,  1d,1d,.98d,1d,1.01d, 1d,.0347d,.987d});        	
        	Iterator<Double> speedFactorsIterator = speedFactorsList.iterator();
        	
	        db.beginTransaction();
	        for (Integer lineNum : lineNumbers) {
	        	ContentValues values = new ContentValues();
	        	values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LINE_NUMBER, lineNum);
	        	values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LENGTH, lengthsIterator.next());
	        	values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_DIE_WIDTH, dieWidthsIterator.next());
	        	values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_SPEED_SETPOINT, 0);
	        	values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_DIFFERENTIAL_SPEED_SETPOINT, 0);
	        	values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_SPEED_FACTOR, speedFactorsIterator.next());
	        	if (linesWithGearedSpeedControl.contains(lineNum)) {
	        		values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_SPEED_CONTROLLER_TYPE, ProductionLine.SPEED_CONTROLLER_TYPE_GEARED);
	        	} else {
	        		values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_SPEED_CONTROLLER_TYPE, ProductionLine.SPEED_CONTROLLER_TYPE_DIRECT);
	        	}
	        	values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_TAKEOFF_EQUIPMENT_TYPE, "Maxson");
	        	
	        	long rowId = db.insertOrThrow(
	        			PrimexDatabaseSchema.ProductionLines.TABLE_NAME, 
	        			null, 
	        			values);
	        	
	        }
	        db.setTransactionSuccessful();
        } finally {
          db.endTransaction();
        }
        
        db.execSQL(SQL_CREATE_WORK_ORDERS);
        try {
        	db.beginTransaction();
        	ContentValues values = new ContentValues();
        	values.put(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_WO_NUMBER,1);
        	values.put(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_TOTAL_PRODUCTS_ORDERED,69);
        	values.put(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_MAXIMUM_STACK_HEIGHT,0);
        	values.put(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_SELECTED_SKID_NUMBER,1);
        	values.put(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_NOVATEC_SETPOINT,0);
        	
        	db.insertOrThrow(PrimexDatabaseSchema.WorkOrders.TABLE_NAME, null, values);
        	db.setTransactionSuccessful();
        } finally {
        	db.endTransaction();
        }

        db.execSQL(SQL_CREATE_LINE_WORK_ORDER_LINK);
        try {
        	db.beginTransaction();
        	ContentValues values = new ContentValues();
        	values.put(PrimexDatabaseSchema.LineWorkOrderLink.COLUMN_NAME_LINE_ID, 7);
        	values.put(PrimexDatabaseSchema.LineWorkOrderLink.COLUMN_NAME_WO_ID, 1);
        	db.insertOrThrow(PrimexDatabaseSchema.LineWorkOrderLink.TABLE_NAME, null, values);
        	db.setTransactionSuccessful();
        } finally {
        	db.endTransaction();
        }
        db.execSQL(SQL_CREATE_PRODUCT_TYPES);
        try {
        	db.beginTransaction();
        	String[] types = {Product.SHEETS_TYPE, Product.ROLLS_TYPE};
        	for (int j = 0; j < 2; j++) {
	        	ContentValues ptvalues = new ContentValues();
	        	ptvalues.put(PrimexDatabaseSchema.ProductTypes.COLUMN_NAME_TYPES, types[j]);
	        	
	        	long rowId = db.insertOrThrow(PrimexDatabaseSchema.ProductTypes.TABLE_NAME, null, ptvalues);
        	}
	        db.setTransactionSuccessful();
        } finally {
        	db.endTransaction();
        }
        
        db.execSQL(SQL_CREATE_NOVATECS);
        try {
        	db.beginTransaction();
        	Integer defaultSetpoint = 0;
        	List<Integer> linesWithBigNovatecs = Arrays.asList(new Integer[] {1,12,13,14});
        	double screwRatio;
        	
        	ContentValues novatecValues = new ContentValues();
        	for (Integer lineNum : lineNumbers) {
        		int lineId = getIdOfValue(PrimexDatabaseSchema.ProductionLines.TABLE_NAME, 
        				PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LINE_NUMBER, 
        				lineNum, 
        				db);
        		novatecValues.put(PrimexDatabaseSchema.Novatecs.COLUMN_NAME_LINE_NUMBER_ID, lineId);
        		novatecValues.put(PrimexDatabaseSchema.Novatecs.COLUMN_NAME_CURRENT_SETPOINT, defaultSetpoint);
        		if (linesWithBigNovatecs.contains(lineNum)) {
        			screwRatio = 1.5; 
        		} else screwRatio = 1d;
        		novatecValues.put(PrimexDatabaseSchema.Novatecs.COLUMN_NAME_LETDOWN_RATIO, screwRatio);
        		long rowId = db.insertOrThrow(PrimexDatabaseSchema.Novatecs.TABLE_NAME, null, novatecValues);
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
        	db.insertOrThrow(PrimexDatabaseSchema.Skids.TABLE_NAME, null, values);
        	db.setTransactionSuccessful();
        } finally {
        	db.endTransaction();
        }
        db.execSQL(SQL_CREATE_MODEL_STATE);
        try {
        	db.beginTransaction();
        	Integer lineNum = 12;
        	Integer woNum = 123;
        	ContentValues modvalues = new ContentValues();
        	modvalues.put(PrimexDatabaseSchema.ModelState.COLUMN_NAME_SELECTED_LINE, lineNum);
        	modvalues.put(PrimexDatabaseSchema.ModelState.COLUMN_NAME_SELECTED_WORK_ORDER, woNum);
        	
        	long rowId = db.insertOrThrow(PrimexDatabaseSchema.ModelState.TABLE_NAME, null, modvalues);
        	
	        db.setTransactionSuccessful();
        } finally {
        	db.endTransaction();
        }
    }
    
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_NOVATECS);
    	db.execSQL(SQL_DELETE_SKIDS);
        db.execSQL(SQL_DELETE_LINE_WORK_ORDER_LINK);
    	db.execSQL(SQL_DELETE_PRODUCTION_LINES);
        db.execSQL(SQL_DELETE_WORK_ORDERS);
        db.execSQL(SQL_DELETE_PRODUCT_TYPES);
        db.execSQL(SQL_DELETE_PRODUCTS);
        db.execSQL(SQL_DELETE_MODEL_STATE);
        onCreate(db);
    }
    
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /*
     * CRUD methods go here, see http://pheide.com/page/11/tab/24#post13 if it gets to be too many
     * Just Ctrl+F for the name of the class
     */
    
    public void saveState() {
    	
    }
    
    public long insertOrUpdateLine(ProductionLine newLine) {
    	insertOrUpdateNovatec(newLine.getNovatec(), newLine.getLineNumber());
    	SQLiteDatabase db = getWritableDatabase();
    	
    	ContentValues values = new ContentValues();
    	values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LINE_NUMBER, newLine.getLineNumber());
    	values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LENGTH, newLine.getLineLength());
    	values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_DIE_WIDTH, newLine.getDieWidth());
    	values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_WEB_WIDTH, newLine.getWebWidth());
    	values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_SPEED_CONTROLLER_TYPE, newLine.getSpeedControllerType());
    	values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_TAKEOFF_EQUIPMENT_TYPE, newLine.getTakeoffEquipmentType());
    	
    	long rowId;
    	try {
			rowId = db.insertWithOnConflict(
		
				PrimexDatabaseSchema.ProductionLines.TABLE_NAME, 
				null, 
				values,
				SQLiteDatabase.CONFLICT_ABORT);
		} catch (SQLiteConstraintException sqle) {
			rowId = db.updateWithOnConflict(
				PrimexDatabaseSchema.ProductionLines.TABLE_NAME, 
				values, 
				PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LINE_NUMBER + "=?", 
				new String[]{String.valueOf(newLine.getLineNumber())}, 
				SQLiteDatabase.CONFLICT_REPLACE);
		}
    	return rowId;
    }
    
    public long insertOrUpdateNovatec(Novatec novatec, int lineNumber) {
    	SQLiteDatabase db = getWritableDatabase();
    	
    	ContentValues values = new ContentValues();
    	values.put(PrimexDatabaseSchema.Novatecs.COLUMN_NAME_CURRENT_SETPOINT, novatec.getControllerSetpoint());
    	values.put(PrimexDatabaseSchema.Novatecs.COLUMN_NAME_LETDOWN_RATIO, novatec.getLetdownRatio());
    	int lineId = getIdOfValue(PrimexDatabaseSchema.ProductionLines.TABLE_NAME, 
    			PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LINE_NUMBER, lineNumber);
    	values.put(PrimexDatabaseSchema.Novatecs.COLUMN_NAME_LINE_NUMBER_ID, lineId);
    	
    	long rowId;
    	try {
			rowId = db.insertWithOnConflict(
				PrimexDatabaseSchema.Novatecs.TABLE_NAME, 
				null, 
				values,
				SQLiteDatabase.CONFLICT_ABORT);
		} catch (SQLiteConstraintException sqle) {
			rowId = db.updateWithOnConflict(
				PrimexDatabaseSchema.Novatecs.TABLE_NAME, 
				values, 
				PrimexDatabaseSchema.Novatecs.COLUMN_NAME_LINE_NUMBER_ID + "=?", 
				new String[]{String.valueOf(lineId)}, 
				SQLiteDatabase.CONFLICT_REPLACE);
		}
    	return rowId;
    }
    public ProductionLine getLine(int lineNumber){
    	SQLiteDatabase db = getReadableDatabase();
    	
    	Cursor resultCursor = db.query(
    			PrimexDatabaseSchema.ProductionLines.TABLE_NAME,
    			null, 
    			PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LINE_NUMBER + "=?",
    			new String[] {String.valueOf(lineNumber)},
    			null,
    			null,
    			null
    			);
    			
    	try {
    		resultCursor.moveToFirst();
	    	int ln = resultCursor.getInt(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LINE_NUMBER));
	    	int ll = resultCursor.getInt(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LENGTH));
	    	int dw = resultCursor.getInt(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_DIE_WIDTH));
	    	double ww = resultCursor.getDouble(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_WEB_WIDTH));
	    	String sct = resultCursor.getString(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_SPEED_CONTROLLER_TYPE));
	    	String tet = resultCursor.getString(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_TAKEOFF_EQUIPMENT_TYPE));
	    	double sp = resultCursor.getDouble(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_SPEED_SETPOINT));
	    	double diff = resultCursor.getDouble(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_DIFFERENTIAL_SPEED_SETPOINT));
	    	double sf = resultCursor.getDouble(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_SPEED_FACTOR));
	    	
	    	ProductionLine newLine = new ProductionLine(ln,ll,dw,sct,tet);
	    	SpeedValues sv = new SpeedValues(sp,diff,sf);
	    	newLine.setSpeedValues(sv);
	    	newLine.setWebWidth(ww);
	    	return newLine;
	    } finally {
	    	if (resultCursor != null) resultCursor.close();
    	}    	
    }
    
    public Novatec getNovatec(int lineNumber) {
    	SQLiteDatabase db = getReadableDatabase();
		
		String sql = "SELECT * FROM " +
				PrimexDatabaseSchema.Novatecs.TABLE_NAME +
				" WHERE " + PrimexDatabaseSchema.Novatecs.COLUMN_NAME_LINE_NUMBER_ID + "=?";
		long lineId = getIdOfValue(PrimexDatabaseSchema.ProductionLines.TABLE_NAME, 
				PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LINE_NUMBER, 
				lineNumber);
		String[] whereargs = new String[]{String.valueOf(lineId)};
		Cursor resultCursor = db.rawQuery(sql, whereargs);

		Novatec n = null;
		
		try {
			if (resultCursor.getCount() > 1) {
				throw new RuntimeException("ERROR, You are not looking up a unique record for line " + String.valueOf(lineNumber) +
						" and are going to get errors until you implement multiple Novatecs");
			}
			
			if (resultCursor.moveToFirst()) {
				
				double setpoint = resultCursor.getDouble(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.Novatecs.COLUMN_NAME_CURRENT_SETPOINT));
				double letdownRatio = resultCursor.getDouble(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.Novatecs.COLUMN_NAME_LETDOWN_RATIO));
				n = new Novatec(50, setpoint, letdownRatio);
			} else {
				Log.e("error", "SQLiteOpenHelper::getNovatec returned no results");
			}
		} finally {
			if (resultCursor != null) {resultCursor.close();}
		}	
		return n;
	
    }
    
    public List<Integer> getLineNumbers() {
    	SQLiteDatabase db = getReadableDatabase();
    	
    	Cursor c = db.query(
    		    PrimexDatabaseSchema.ProductionLines.TABLE_NAME,  // The table to query
    		    null,                               // The columns to return
    		    null,                                // The columns for the WHERE clause
    		    null,                            // The values for the WHERE clause
    		    null,                                     // don't group the rows
    		    null,                                     // don't filter by row groups
    		    null	                                 // The sort order
    		    );
    	
    	try {
    		List<Integer> lineNumbers = new ArrayList<Integer>();
    		while (c.moveToNext()) {
				int column = c.getColumnIndexOrThrow(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LINE_NUMBER);
				lineNumbers.add(c.getInt(column));
    		}
	    	return lineNumbers;    		    		
    	} finally {
	    	if (c != null) c.close();
    	}
    }  








	
	
	
	
	
	
	
	
	
	public long insertOrUpdateWorkOrder(WorkOrder newWo) {
		SQLiteDatabase db = getWritableDatabase();
		
		if (newWo.hasProduct()) {
			insertOrReplaceProduct(newWo.getProduct(), newWo.getWoNumber());
			String query = "SELECT last_insert_rowid() FROM " + PrimexDatabaseSchema.Products.TABLE_NAME;
			Cursor c = db.rawQuery(query, null);
			int productId = 0;
			if (c != null && c.moveToFirst()) {
			    productId = c.getInt(0); //The 0 is the column index, we only have 1 column, so the index is 0
			}
			//debug TODO use getIdOfValue
			String sqll = "SELECT * FROM " + PrimexDatabaseSchema.Products.TABLE_NAME + " WHERE " + 
			PrimexDatabaseSchema.Products._ID + "=?";
			Cursor cc = db.rawQuery(sqll, new String[]{String.valueOf(productId)});
			if (cc != null && cc.moveToFirst()) {
			    String pproductId = cc.getString(1);
			}
			//values.put(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_SELECTED_SKID_ID, lastRowId);
		}
		
		ContentValues values = new ContentValues();
		values.put(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_WO_NUMBER, newWo.getWoNumber());
		double tpo = newWo.getTotalProductsOrdered();
		tpo = 69; //debug
		double nova = newWo.getNovatecSetpoint();
		values.put(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_TOTAL_PRODUCTS_ORDERED, tpo);
		values.put(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_MAXIMUM_STACK_HEIGHT, newWo.getMaximumStackHeight());
		if ( newWo.hasSelectedSkid()) {
			values.put(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_SELECTED_SKID_NUMBER, newWo.getSelectedSkid().getSkidNumber());	
		} else Log.e("ERROR", String.valueOf(newWo.getWoNumber()) + " doesn't have a selected skid (maybe it's new)");
		values.put(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_NOVATEC_SETPOINT, nova);
		if (newWo.getFinishDate() != null) {
			values.put(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_FINISH_TIME, newWo.getFinishDate().getTime());	
		}
		long rowId;
		try {
			rowId = db.insertWithOnConflict(
		
				PrimexDatabaseSchema.WorkOrders.TABLE_NAME, 
				null, 
				values,
				SQLiteDatabase.CONFLICT_ABORT);
		} catch (SQLiteConstraintException sqle) {
			rowId = db.updateWithOnConflict(
				PrimexDatabaseSchema.WorkOrders.TABLE_NAME, 
				values, 
				PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_WO_NUMBER + "=?", 
				new String[]{String.valueOf(newWo.getWoNumber())}, 
				SQLiteDatabase.CONFLICT_REPLACE);
		}
		return rowId;
	}
	
	public WorkOrder getWorkOrder(int woNumber){
		SQLiteDatabase db = getReadableDatabase();

		Cursor resultCursor = db.rawQuery("SELECT * FROM " + PrimexDatabaseSchema.WorkOrders.TABLE_NAME + " WHERE " + 
				PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_WO_NUMBER + "=?", new String[]{String.valueOf(woNumber)});
		int wonum = -1;
		int prod_id = -1;
		double ordered = -1d;
		int selected = -1;
		double height = -1d;
		double nova = -1d;
		int finish;
		try {
			if (resultCursor.moveToFirst()) {
		    	wonum = resultCursor.getInt(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_WO_NUMBER));
		    	prod_id = resultCursor.getInt(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_SELECTED_PRODUCT_ID));
		    	ordered = resultCursor.getDouble(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_TOTAL_PRODUCTS_ORDERED));
		    	selected = resultCursor.getInt(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_SELECTED_SKID_NUMBER));
		    	height = resultCursor.getDouble(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_MAXIMUM_STACK_HEIGHT));
		    	nova = resultCursor.getDouble(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_NOVATEC_SETPOINT));
		    	finish = resultCursor.getInt(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_FINISH_TIME));
		    	WorkOrder wo = new WorkOrder(wonum);
				if (prod_id != -1) {
					wo.setProduct(getProduct(wonum));
				}
				if (ordered != -1) { //TODO this all seems like really bad error checking.
					wo.setTotalProductsOrdered(ordered);	
				}
				
				List<Skid<Product>> skidList = getSkidList(wo.getWoNumber());
				if (skidList.isEmpty()) throw new RuntimeException ("No skid list found for workorder " + String.valueOf(wo.getWoNumber()));
				wo.setSkidsList(skidList);				
				
				if (selected != -1) {
					wo.selectSkid(selected);
				}
				wo.setMaximumStackHeight(height);
				wo.setNovatecSetpoint(nova);
				if (finish > 0) {
					wo.setFinishDate(new Date(finish));	
				}
				return wo;
			} else return null;
	    } finally {
	    	if (resultCursor != null) resultCursor.close();
		}    	
	}
	
	public int getHighestWoNumber() {
		SQLiteDatabase db = getReadableDatabase();
		
		Cursor c = db.query(
			    PrimexDatabaseSchema.WorkOrders.TABLE_NAME,  // The table to query
			    new String[] {"MAX(WO_number)"}, // The columns to return
			    null,                                // The columns for the WHERE clause
			    null,                            // The values for the WHERE clause
			    null,                                     // don't group the rows
			    null,                                     // don't filter by row groups
			    null	                                 // The sort order
			    );
		
		try { 
			if (c.moveToFirst()) {
				return c.getInt(0);
			}
			else return 0; 
		} finally { c.close(); } 
	}
	
	public List<Integer> getWoNumbers() {
		SQLiteDatabase db = getReadableDatabase();
		
		List<Integer> workOrders = new ArrayList<Integer>();
		    	
		Cursor c = db.query(
			    PrimexDatabaseSchema.WorkOrders.TABLE_NAME,  // The table to query
			    null,                               // The columns to return
			    null,                                // The columns for the WHERE clause
			    null,                            // The values for the WHERE clause
			    null,                                     // don't group the rows
			    null,                                     // don't filter by row groups
			    null	                                 // The sort order
			    );
		
		try {
			while (c.moveToNext()) {
				workOrders.add( c.getInt( c.getColumnIndexOrThrow(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_WO_NUMBER) ) );
			}
		} finally {
	    	if (c != null) c.close();
		}
		
		return workOrders;
	}
	
	public void clearWorkOrders() {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL(SQL_DELETE_LINE_WORK_ORDER_LINK);
		db.execSQL(SQL_CREATE_LINE_WORK_ORDER_LINK);
		db.execSQL(SQL_DELETE_WORK_ORDERS);
		db.execSQL(SQL_CREATE_WORK_ORDERS);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public int getProductType(int type){
		SQLiteDatabase db = getReadableDatabase();
		Cursor resultCursor = db.query(
				PrimexDatabaseSchema.ProductTypes.TABLE_NAME,
				null, 
				PrimexDatabaseSchema.ProductTypes.COLUMN_NAME_TYPES + "=?",
				new String[] {String.valueOf(type)},
				null,
				null,
				null
				);
		
		int thetype = 0;
		try {
			if (resultCursor.moveToFirst()) {
		    	thetype= resultCursor.getInt(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.ProductTypes.COLUMN_NAME_TYPES));
			}
	    	return thetype;
	    } finally {
	    	if (resultCursor != null) resultCursor.close();
		}
	}
	
	public int getProductTypeId(String type){ //TODO replace with getIdOfValue
		SQLiteDatabase db = getReadableDatabase();
		
		String sql = "SELECT * FROM " + PrimexDatabaseSchema.ProductTypes.TABLE_NAME + " WHERE " +
				PrimexDatabaseSchema.ProductTypes.COLUMN_NAME_TYPES + "=?";
		Cursor resultCursor = db.rawQuery(sql, new String[]{type});
		
		int thetype = 0;
		try {
			if (resultCursor.moveToFirst()) {
		    	thetype= resultCursor.getInt(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.ProductTypes._ID));
			} else Log.e("error", "you didn't match any darn rows");
	    	return thetype;
	    } finally {
	    	if (resultCursor != null) resultCursor.close();
		}
	}
	
	public String getFieldAsString(String tableName, String columnName, String whereColumn, String[] whereArgs){
		SQLiteDatabase db = getReadableDatabase();
		
		String sql = "SELECT " + columnName + " FROM " + tableName;
		if (whereArgs != null) {
			sql += " WHERE " + whereColumn + "=?";
		}
		Cursor resultCursor = db.rawQuery(sql, whereArgs);
		
		String value = null;
		try {
			if (resultCursor.moveToFirst()) {
		    	int index = resultCursor.getColumnIndexOrThrow(columnName);
				value = resultCursor.getString(index);
			} else Log.e("error", "you didn't match any darn rows");
	    	return value;
	    } finally {
	    	if (resultCursor != null) resultCursor.close();
		}
	}
	
	//never call this version in onCreate
	public int getIdOfValue (String tableName, String columnName, Object value) {
		 return getIdOfValue(tableName, columnName, value, this.getReadableDatabase());
	}
	
	/*
	 * Returns -1 if the id was not found.
	 */
	private int getIdOfValue (String tableName, String columnName, Object value, SQLiteDatabase db) {
		String sql = "SELECT _id" +
				" FROM " + tableName + 
				" WHERE " + columnName + 
				"=?";
		Cursor resultsCursor = db.rawQuery(sql, new String[]{String.valueOf(value)});
		int columnId = -1;
		try {
			if (resultsCursor.getCount() > 1) {
				Log.e("ERROR", "shouldn't get more than one result");
			}
			if (resultsCursor.moveToFirst()){
				columnId = resultsCursor.getInt(0);
			} else {
				Log.e("ERROR", "could not get an id for the value " + value.toString() + " in the string " + columnName);
				return -1;
			}
			return columnId;
		} finally {
			if (resultsCursor != null) {
				resultsCursor.close();
			}
		}		
	}
	
	
	
	public long insertOrReplaceProduct(Product newProduct, int woNumber) {
		SQLiteDatabase db = getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(PrimexDatabaseSchema.Products.COLUMN_NAME_GAUGE, newProduct.getGauge());
		values.put(PrimexDatabaseSchema.Products.COLUMN_NAME_WIDTH, newProduct.getWidth());
		values.put(PrimexDatabaseSchema.Products.COLUMN_NAME_LENGTH, newProduct.getLength());
		String type = newProduct.getType();
		int foreignKey = getProductTypeId(type);
		values.put(PrimexDatabaseSchema.Products.COLUMN_NAME_TYPE, foreignKey);
		int otherForeign = woNumber;
		values.put(PrimexDatabaseSchema.Products.COLUMN_NAME_WO_NUMBER, otherForeign);
		values.put(PrimexDatabaseSchema.Products.COLUMN_NAME_UNIT_WEIGHT, newProduct.getUnitWeight());
		long rowId = db.insertWithOnConflict(
				PrimexDatabaseSchema.Products.TABLE_NAME, 
				null, 
				values,
				SQLiteDatabase.CONFLICT_REPLACE);
		
		if (rowId == -1) {
			Log.v("verbose", "insert error code -1");
		} else {
			Log.v("verbose", "insert row ID " + String.valueOf(rowId));
		}
		return rowId;
	}
	
	public Product getProduct(int woNumber) {
		SQLiteDatabase db = getReadableDatabase();
		
		String sql = "SELECT * FROM " +
				PrimexDatabaseSchema.Products.TABLE_NAME + 
				" JOIN " + PrimexDatabaseSchema.ProductTypes.TABLE_NAME + 
				" ON " + PrimexDatabaseSchema.ProductTypes.TABLE_NAME + "." + PrimexDatabaseSchema.ProductTypes._ID +  
				"=" + PrimexDatabaseSchema.Products.TABLE_NAME + "." + PrimexDatabaseSchema.Products.COLUMN_NAME_TYPE + 
				" JOIN " + PrimexDatabaseSchema.WorkOrders.TABLE_NAME + 
				" ON " + PrimexDatabaseSchema.WorkOrders.TABLE_NAME + "." + PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_WO_NUMBER +
				"=" + woNumber +
				" WHERE " + PrimexDatabaseSchema.Products.TABLE_NAME + "." + PrimexDatabaseSchema.Products.COLUMN_NAME_WO_NUMBER + "=?";
		String[] whereargs = new String[]{String.valueOf(woNumber)};
		Cursor resultCursor = db.rawQuery(sql, whereargs);

		Product p = null;
		
		try {
			if (resultCursor.getCount() > 1) {
				Log.e("ERROR","You are not looking up a unique record for wo number " + String.valueOf(woNumber) +
						"and are going to get errors");
			}
			if (resultCursor.moveToFirst()) {
				int indexOfProductType = resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.ProductTypes.COLUMN_NAME_TYPES);
				String type = resultCursor.getString(indexOfProductType);
				if (type.equals(Product.SHEETS_TYPE)) {
					p = new Sheet(
						resultCursor.getDouble(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.Products.COLUMN_NAME_GAUGE)),
						resultCursor.getDouble(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.Products.COLUMN_NAME_WIDTH)),
						resultCursor.getDouble(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.Products.COLUMN_NAME_LENGTH))
						);
				} else if (type.equals(Product.ROLLS_TYPE)) {
					p = new Roll(
 							resultCursor.getDouble(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.Products.COLUMN_NAME_GAUGE)),
							resultCursor.getDouble(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.Products.COLUMN_NAME_WIDTH)),
							0
						);					
				} else throw new IllegalArgumentException("not a sheet or roll!");
				double unitWeight = resultCursor.getDouble(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.Products.COLUMN_NAME_UNIT_WEIGHT));
				p.setUnitWeight(unitWeight);
			} else {
				Log.e("error", "SQLiteOpenHelper::getProduct returned no results");
			}
		} finally {
			if (resultCursor != null) {resultCursor.close();}
		}	
		return p;
	}

	public List<Skid<Product>> getSkidList(int woNumber) {
		SQLiteDatabase db = getReadableDatabase();
		
		String sql = "SELECT * FROM " + PrimexDatabaseSchema.Skids.TABLE_NAME +
				" WHERE " + PrimexDatabaseSchema.Skids.COLUMN_NAME_WO_ID + 
				"=?";
		int woId = getIdOfValue(PrimexDatabaseSchema.WorkOrders.TABLE_NAME, PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_WO_NUMBER, woNumber);
		if (woId == -1) Log.e("ERROR", "Could not find a work order ID for the wo number " + String.valueOf(woNumber));
		Cursor resultCursor = db.rawQuery(sql, new String[]{String.valueOf(woId)});
		List<Skid<Product>> skidList = new ArrayList<Skid<Product>>();
		try {
			while (resultCursor.moveToNext()){
				Skid<Product> skid = new Skid<Product>(
						new Pallet(), 
						resultCursor.getInt(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.Skids.COLUMN_NAME_TOTAL_ITEMS)),
						0.0d, 
						resultCursor.getInt(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.Skids.COLUMN_NAME_STACKS)),
						new Date(resultCursor.getLong(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.Skids.COLUMN_NAME_START_DATE))),
						getProduct(woNumber)
						);
				int skidNumber = resultCursor.getInt(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.Skids.COLUMN_NAME_SKID_NUMBER));
				skid.setSkidNumber(skidNumber);
				int currentItems = resultCursor.getInt(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.Skids.COLUMN_NAME_CURRENT_ITEMS));
				skid.setCurrentItems(currentItems);
				Date finishTime = new Date(resultCursor.getLong(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.Skids.COLUMN_NAME_FINISH_DATE)));
				skid.setFinishTime(finishTime);
				skidList.add(skid);
			}
			Collections.sort(skidList);
			return skidList;
		} finally {
			if (resultCursor != null) {
				resultCursor.close();
			}
		}	
	}
	
	public long insertOrReplaceSkid(Skid<Product> skid, int woNumber) {
		SQLiteDatabase db = getWritableDatabase();
		
		ContentValues values = new ContentValues();
		if (!(skid.getSkidNumber() > 0) ) throw new RuntimeException("Tried to store invalid skid, number " + String.valueOf(skid.getSkidNumber()));
		values.put(PrimexDatabaseSchema.Skids.COLUMN_NAME_SKID_NUMBER, skid.getSkidNumber());
		values.put(PrimexDatabaseSchema.Skids.COLUMN_NAME_CURRENT_ITEMS, skid.getCurrentItems());
		values.put(PrimexDatabaseSchema.Skids.COLUMN_NAME_TOTAL_ITEMS, skid.getTotalItems());
		values.put(PrimexDatabaseSchema.Skids.COLUMN_NAME_STACKS, skid.getmNumberOfStacks());
		if (skid.getStartTime() != null) {
			values.put(PrimexDatabaseSchema.Skids.COLUMN_NAME_START_DATE, skid.getStartTime().getTime());	
		}
		if (skid.getFinishTime() != null) {
			values.put(PrimexDatabaseSchema.Skids.COLUMN_NAME_FINISH_DATE, skid.getFinishTime().getTime());
		}
		int woId = getIdOfValue(PrimexDatabaseSchema.WorkOrders.TABLE_NAME, PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_WO_NUMBER, woNumber);
		if (woId == -1) Log.e("ERROR", "could not find a WO id for WO#" + String.valueOf(woNumber) + " when inserting skid number " + String.valueOf(skid.getSkidNumber()));
		values.put(PrimexDatabaseSchema.Skids.COLUMN_NAME_WO_ID, woId);
		
		long rowId = db.insertWithOnConflict(
				PrimexDatabaseSchema.Skids.TABLE_NAME, 
				null, 
				values,
				SQLiteDatabase.CONFLICT_REPLACE);
		
		if (rowId == -1) {
			Log.v("verbose", "insert error code -1");
		} else {
			Log.v("verbose", "insert row ID " + String.valueOf(rowId));
		}
		return rowId;
	}
	
	/*
	 * 
	 */
	public int updateColumn(String tableName, String columnName, String where, String[] whereArgs, String newValue){
		SQLiteDatabase db = getWritableDatabase();
		
		ContentValues values = new ContentValues();

		values.put(columnName, newValue);
		
		int numAffectedRows = db.update(
				tableName,
				values, 
				where,
				whereArgs
				);
				
		return numAffectedRows;	    
	}
	
	public long updateLineWorkOrderLink(int lineNumber, int woNumber) {
		SQLiteDatabase db = getWritableDatabase();
		int lineId = getIdOfValue(
				PrimexDatabaseSchema.ProductionLines.TABLE_NAME, 
				PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LINE_NUMBER, 
				lineNumber);
		int woId = getIdOfValue(
				PrimexDatabaseSchema.WorkOrders.TABLE_NAME, 
				PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_WO_NUMBER,
				woNumber);
		
		//first, invalidate the previously selected work order for this line. //TODO maybe should be in model
		String where = PrimexDatabaseSchema.LineWorkOrderLink.COLUMN_NAME_LINE_ID + "=? " + 
				" AND " + PrimexDatabaseSchema.LineWorkOrderLink.COLUMN_NAME_WO_IS_SELECTED + "=?";
		String[] whereArgs = new String[]{String.valueOf(lineId), String.valueOf(1)};
		updateColumn(PrimexDatabaseSchema.LineWorkOrderLink.TABLE_NAME,
				PrimexDatabaseSchema.LineWorkOrderLink.COLUMN_NAME_WO_IS_SELECTED,
				where, 
				whereArgs, 
				"0");
		
		//Then, add the new work order and mark it selected.
		ContentValues values = new ContentValues();
		values.put (PrimexDatabaseSchema.LineWorkOrderLink.COLUMN_NAME_LINE_ID, lineId);
		values.put (PrimexDatabaseSchema.LineWorkOrderLink.COLUMN_NAME_WO_ID, woId);
		values.put (PrimexDatabaseSchema.LineWorkOrderLink.COLUMN_NAME_WO_IS_SELECTED, "1");
    	long rowId;
		try {
			rowId = db.insertWithOnConflict(
				PrimexDatabaseSchema.LineWorkOrderLink.TABLE_NAME, 
				null, 
				values,
				SQLiteDatabase.CONFLICT_ABORT);
		} catch (SQLiteConstraintException sqle) {
			rowId = db.updateWithOnConflict(
				PrimexDatabaseSchema.LineWorkOrderLink.TABLE_NAME, 
				values, 
				PrimexDatabaseSchema.LineWorkOrderLink.COLUMN_NAME_WO_ID + "=?", 
				new String[]{String.valueOf(woId)}, 
				SQLiteDatabase.CONFLICT_REPLACE);
		}
    	
		return rowId;	
	}
	
	/*
	 * Returns 0 if work order not found.
	 */
	public int getSelectedWoNumberByLine(int lineNumber) {
		SQLiteDatabase db = getReadableDatabase();
		String sql = "SELECT " + PrimexDatabaseSchema.WorkOrders.TABLE_NAME + "." + PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_WO_NUMBER + 
				" FROM " + PrimexDatabaseSchema.WorkOrders.TABLE_NAME +  
				" JOIN " + PrimexDatabaseSchema.LineWorkOrderLink.TABLE_NAME + 
				" ON " + PrimexDatabaseSchema.WorkOrders.TABLE_NAME + "." + PrimexDatabaseSchema.WorkOrders._ID + 
				"=" + PrimexDatabaseSchema.LineWorkOrderLink.TABLE_NAME + "." + PrimexDatabaseSchema.LineWorkOrderLink.COLUMN_NAME_WO_ID +
				" JOIN " + PrimexDatabaseSchema.ProductionLines.TABLE_NAME +
				" ON " + PrimexDatabaseSchema.ProductionLines.TABLE_NAME + "." + PrimexDatabaseSchema.ProductionLines._ID + 
				"=" + PrimexDatabaseSchema.LineWorkOrderLink.TABLE_NAME + "." + PrimexDatabaseSchema.LineWorkOrderLink.COLUMN_NAME_LINE_ID +
				" AND " + PrimexDatabaseSchema.ProductionLines.TABLE_NAME + "." + PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LINE_NUMBER + 
				"=" + lineNumber +
				" WHERE " + PrimexDatabaseSchema.LineWorkOrderLink.COLUMN_NAME_WO_IS_SELECTED + "=?";
		
		Cursor resultCursor = db.rawQuery(sql, new String[]{"1"});
		if (resultCursor.getCount() > 1) {
			Log.e("ERROR", "More than one work order for this query, you will have problems");
		}
		int woNumber = 0;
		try {
			if (resultCursor.moveToFirst()) {
				int columnIndex = resultCursor.getColumnIndex(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_WO_NUMBER);
				woNumber = resultCursor.getInt(columnIndex);
			}
			return woNumber;
		} finally {
			if (resultCursor != null) {
				resultCursor.close();
			}
		}
	}
	public List<Integer> getAllWoNumbersForLine(int lineNumber) {
		SQLiteDatabase db = getReadableDatabase();
		String sql = "SELECT " + PrimexDatabaseSchema.WorkOrders.TABLE_NAME + "." + PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_WO_NUMBER + 
				" FROM " + PrimexDatabaseSchema.WorkOrders.TABLE_NAME +  
				" JOIN " + PrimexDatabaseSchema.LineWorkOrderLink.TABLE_NAME + 
				" ON " + PrimexDatabaseSchema.WorkOrders.TABLE_NAME + "." + PrimexDatabaseSchema.WorkOrders._ID + 
				"=" + PrimexDatabaseSchema.LineWorkOrderLink.TABLE_NAME + "." + PrimexDatabaseSchema.LineWorkOrderLink.COLUMN_NAME_WO_ID +
				" JOIN " + PrimexDatabaseSchema.ProductionLines.TABLE_NAME +
				" ON " + PrimexDatabaseSchema.ProductionLines.TABLE_NAME + "." + PrimexDatabaseSchema.ProductionLines._ID + 
				"=" + PrimexDatabaseSchema.LineWorkOrderLink.TABLE_NAME + "." + PrimexDatabaseSchema.LineWorkOrderLink.COLUMN_NAME_LINE_ID +
				" WHERE " + PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LINE_NUMBER + "=?";
		
		Cursor resultCursor = db.rawQuery(sql, new String[]{String.valueOf(lineNumber)});
		List<Integer> woNumbers = new ArrayList<Integer>();
		try {
			int columnIndex = resultCursor.getColumnIndex(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_WO_NUMBER);
			int woNumber = 0;
			while (resultCursor.moveToNext()) {
				woNumber = resultCursor.getInt(columnIndex);
				woNumbers.add(woNumber);
			}
			return woNumbers;
		} finally {
			if (resultCursor != null) {
				resultCursor.close();
			}
		}
	}
}

package com.kovaciny.linemonitorbot;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PrimexSQLiteOpenHelper extends SQLiteOpenHelper {
    
	public PrimexSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
	
	private static final String TEXT_TYPE = " TEXT";
	private static final String DOUBLE_TYPE = " DOUBLE";
	private static final String INTEGER_TYPE = " INTEGER";
	private static final String COMMA_SEP = ",";
		
	// If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 8;
    public static final String DATABASE_NAME = "Primex.db";
    
    private static final String SQL_CREATE_PRODUCTION_LINES =
    	    "CREATE TABLE " + PrimexDatabaseSchema.ProductionLines.TABLE_NAME + " (" +
    	    PrimexDatabaseSchema.ProductionLines._ID + " INTEGER PRIMARY KEY," +
    	    PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LINE_NUMBER + INTEGER_TYPE + COMMA_SEP +
    	    PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LENGTH + INTEGER_TYPE + COMMA_SEP +
    	    PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_DIE_WIDTH + INTEGER_TYPE + COMMA_SEP +
    	    PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_SPEED_CONTROLLER_TYPE + TEXT_TYPE + COMMA_SEP +
    	    PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_TAKEOFF_EQUIPMENT_TYPE + TEXT_TYPE +
    	    " )";
    
    private static final String SQL_CREATE_WORK_ORDERS =
    	    "CREATE TABLE " + PrimexDatabaseSchema.WorkOrders.TABLE_NAME + " (" +
    	    PrimexDatabaseSchema.WorkOrders._ID + " INTEGER PRIMARY KEY," +
	    	PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_WO_NUMBER + INTEGER_TYPE + COMMA_SEP +
	    	PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_PRODUCTS_LIST_POINTER + INTEGER_TYPE + 
    	    " )";
    	
	//list "child" tables, which have a foreign key, before their parent, so drop table works
    private static final String TABLE_NAME_WORK_ORDERS = PrimexDatabaseSchema.WorkOrders.TABLE_NAME;
    private static final String TABLE_NAME_PRODUCTION_LINES = PrimexDatabaseSchema.ProductionLines.TABLE_NAME;

	private static final String SQL_DELETE_PRODUCTION_LINES =
			"DROP TABLE IF EXISTS " + TABLE_NAME_PRODUCTION_LINES;
	private static final String SQL_DELETE_WORK_ORDERS =
			"DROP TABLE IF EXISTS " + TABLE_NAME_WORK_ORDERS;
    	 
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PRODUCTION_LINES);
        //Batch insert to SQLite database on Android
        try
        {
        	Integer[] lineNumbers = {1,6,7,9,10,11,12,13,14,15,16,17,18};
          db.beginTransaction();
          for (int i = 0; i < lineNumbers.length; i++) {
        	ContentValues values = new ContentValues();
        	values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LINE_NUMBER, lineNumbers[i]);
        	values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LENGTH, 0);
        	values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_DIE_WIDTH, 0);
        	values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_SPEED_CONTROLLER_TYPE, "Direct");
        	values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_TAKEOFF_EQUIPMENT_TYPE, "Maxson");
        	
        	long rowId = db.insert(
        			PrimexDatabaseSchema.ProductionLines.TABLE_NAME, 
        			null, 
        			values);
        	
          }
          db.setTransactionSuccessful();
        } finally
        {
          db.endTransaction();
        }
        
        db.execSQL(SQL_CREATE_WORK_ORDERS);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_PRODUCTION_LINES);
        db.execSQL(SQL_DELETE_WORK_ORDERS);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /*
     * CRUD methods go here, see http://pheide.com/page/11/tab/24#post13 if it gets to be too many
     * Just Ctrl+F for the name of the class
     */
    
    
    
    public long addLine(ProductionLine newLine) {
    	SQLiteDatabase db = getWritableDatabase();
    	
    	ContentValues values = new ContentValues();
    	values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LINE_NUMBER, newLine.getLineNumber());
    	values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LENGTH, newLine.getLineLength());
    	values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_DIE_WIDTH, newLine.getDieWidth());
    	values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_SPEED_CONTROLLER_TYPE, newLine.getSpeedControllerType());
    	values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_TAKEOFF_EQUIPMENT_TYPE, newLine.getTakeoffEquipmentType());
    	
    	long rowId = db.insert(
    			PrimexDatabaseSchema.ProductionLines.TABLE_NAME, 
    			null, 
    			values);
    	
    	return rowId;
    }
    
    public ProductionLine getLine(int lineNumber){
    	SQLiteDatabase db = getReadableDatabase();
    	
    	String[] projection = {""}; //too lazy to pick columns now
    	
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
	    	int ln_index = resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LINE_NUMBER);
	    	int ln = resultCursor.getInt(ln_index);
	    	int ll = resultCursor.getInt(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LENGTH));
	    	int dw = resultCursor.getInt(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_DIE_WIDTH));
	    	String sct = resultCursor.getString(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_SPEED_CONTROLLER_TYPE));
	    	String tet = resultCursor.getString(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_TAKEOFF_EQUIPMENT_TYPE));
	    	
	    	return new ProductionLine(ln,ll,dw,sct,tet);
	    } finally {
	    	if (resultCursor != null) resultCursor.close();
    	}    	
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








	
	
	
	
	
	
	
	
	
	public long addWorkOrder(WorkOrder newWO) {
		SQLiteDatabase db = getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_WO_NUMBER, newWO.getWoNumber());
		values.put(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_PRODUCTS_LIST_POINTER, newWO.getProductsListPointer());
		
		long rowId = db.insertOrThrow(
				PrimexDatabaseSchema.WorkOrders.TABLE_NAME, 
				null, 
				values);
		
		return rowId;
	}
	
	public WorkOrder getWorkOrder(int woNumber){
		SQLiteDatabase db = getReadableDatabase();
		
		Cursor resultCursor = db.query(
				PrimexDatabaseSchema.WorkOrders.TABLE_NAME,
				null, 
				PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_WO_NUMBER + "=?",
				new String[] {String.valueOf(woNumber)},
				null,
				null,
				null
				);
		
		int wonum = 0;
		int prodlist = 0; 
		try {
			if (resultCursor.moveToFirst()) {
		    	wonum = resultCursor.getInt(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_WO_NUMBER));
		    	prodlist = resultCursor.getInt(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_PRODUCTS_LIST_POINTER));
			}
	    	return new WorkOrder(wonum, prodlist);
	    } finally {
	    	if (resultCursor != null) resultCursor.close();
		}    	
	}
	
	
	public int updateWorkOrder(WorkOrder changedWO){
		SQLiteDatabase db = getReadableDatabase();
		
		ContentValues values = new ContentValues();
		// don't change WO number, duh values.put(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_WO_NUMBER, changedWO.getWoNumber());
		values.put(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_PRODUCTS_LIST_POINTER, changedWO.getProductsListPointer());
		
		int numAffectedRows = db.update(
				PrimexDatabaseSchema.WorkOrders.TABLE_NAME,
				values, 
				PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_WO_NUMBER + "=?",
				new String[] {String.valueOf(changedWO.getWoNumber())}
				);
				
		return numAffectedRows;	    
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
			return workOrders;
		} finally {
	    	if (c != null) c.close();
		}
	}
}

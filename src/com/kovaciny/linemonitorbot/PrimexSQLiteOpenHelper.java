package com.kovaciny.linemonitorbot;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PrimexSQLiteOpenHelper extends SQLiteOpenHelper {
    
	public PrimexSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
	
	private static final String TEXT_TYPE = " TEXT";
	private static final String DOUBLE_TYPE = " DOUBLE";
	private static final String INTEGER_TYPE = " INTEGER";
	private static final String COMMA_SEP = ",";
		
	// If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Primex.db";
    
    private static final String SQL_CREATE_ENTRIES =
    	    "CREATE TABLE " + PrimexDatabaseSchema.ProductionLines.TABLE_NAME + " (" +
    	    PrimexDatabaseSchema.ProductionLines._ID + " INTEGER PRIMARY KEY," +
    	    PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LINE_NUMBER + INTEGER_TYPE + COMMA_SEP +
    	    PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LINE_LENGTH + INTEGER_TYPE + COMMA_SEP +
    	    PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_DIE_WIDTH + INTEGER_TYPE + COMMA_SEP +
    	    PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_SPEED_CONTROLLER_TYPE + TEXT_TYPE + COMMA_SEP +
    	    PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_TAKEOFF_EQUIPMENT_TYPE + TEXT_TYPE +
    	    " )";
    	
	//list "child" tables, which have a foreign key, before their parent, so drop table works
	private static final String TABLE_NAME_ENTRIES = PrimexDatabaseSchema.ProductionLines.TABLE_NAME;

	private static final String SQL_DELETE_ENTRIES =
			"DROP TABLE IF EXISTS " + TABLE_NAME_ENTRIES; 
    	 
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    
    public long addLine(ProductionLine newLine) {
    	SQLiteDatabase db = getWritableDatabase();
    	
    	ContentValues values = new ContentValues();
    	values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LINE_NUMBER, newLine.getLineNumber());
    	values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LINE_LENGTH, newLine.getLineLength());
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
	    	int ll = resultCursor.getInt(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LINE_LENGTH));
	    	int dw = resultCursor.getInt(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_DIE_WIDTH));
	    	String sct = resultCursor.getString(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_SPEED_CONTROLLER_TYPE));
	    	String tet = resultCursor.getString(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_TAKEOFF_EQUIPMENT_TYPE));
	    	
	    	return new ProductionLine(ln,ll,dw,sct,tet);
	    } finally {
	    	if (resultCursor != null) resultCursor.close();
    	}    	
    }
    
    public Integer[] getLineNumbers() {
    	SQLiteDatabase db = getReadableDatabase();
    	
    	String[] projection = {PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LINE_NUMBER};
    	    	
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
    		int cnt = c.getCount();
    		Integer[] returnArray;
    		returnArray = new Integer[cnt];
    		c.moveToFirst();
    		for(int i = 0; i < c.getCount(); i++) {
    			int index = c.getColumnIndexOrThrow(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LINE_NUMBER);
    			int ln = c.getInt(index);
    			returnArray[i] = ln;
    			c.moveToNext();
    		}
    		return returnArray;
    	} finally {
	    	if (c != null) c.close();
    	}
    }    
}


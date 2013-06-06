package com.kovaciny.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.kovaciny.primexmodel.Product;
import com.kovaciny.primexmodel.ProductionLine;
import com.kovaciny.primexmodel.Roll;
import com.kovaciny.primexmodel.Sheet;
import com.kovaciny.primexmodel.WorkOrder;

public class PrimexSQLiteOpenHelper extends SQLiteOpenHelper {
    
	public PrimexSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
	
	private static final String TEXT_TYPE = " TEXT";
	private static final String DOUBLE_TYPE = " DOUBLE";
	private static final String INTEGER_TYPE = " INTEGER";
	private static final String COMMA_SEP = ",";
	private static final String REAL_TYPE = " REAL";
		
	// If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 24;
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

    private static final String SQL_CREATE_PRODUCTS = 
    		"CREATE TABLE " + PrimexDatabaseSchema.Products.TABLE_NAME + " (" +
    		PrimexDatabaseSchema.Products._ID + " INTEGER PRIMARY KEY," +
    		PrimexDatabaseSchema.Products.COLUMN_NAME_GAUGE + REAL_TYPE + COMMA_SEP +
    		PrimexDatabaseSchema.Products.COLUMN_NAME_WIDTH + REAL_TYPE + COMMA_SEP +
    		PrimexDatabaseSchema.Products.COLUMN_NAME_LENGTH + REAL_TYPE + COMMA_SEP +
    		PrimexDatabaseSchema.Products.COLUMN_NAME_TYPE + INTEGER_TYPE + COMMA_SEP +
    		PrimexDatabaseSchema.Products.COLUMN_NAME_LINE + INTEGER_TYPE + COMMA_SEP +
    		" UNIQUE ("  + PrimexDatabaseSchema.Products.COLUMN_NAME_LINE + ")" +
    		" )";
    
    private static final String SQL_CREATE_PRODUCT_TYPES = 
    		"CREATE TABLE " + PrimexDatabaseSchema.ProductTypes.TABLE_NAME + " (" +
    		PrimexDatabaseSchema.ProductTypes._ID + " INTEGER PRIMARY KEY," +
    		PrimexDatabaseSchema.ProductTypes.COLUMN_NAME_TYPES + TEXT_TYPE +
    		//I'm just gonna be careful cause I don't want to turn foreign keys on.
    		//"FOREIGN KEY(" + PrimexDatabaseSchema.ProductTypes.COLUMN_NAME_TYPES + ") REFERENCES +" +
    		//		PrimexDatabaseSchema.ProductTypes.COLUMN_NAME_TYPES +
    		" )";
    	
	//list "child" tables, which have a foreign key, before their parent, so drop table works
    private static final String TABLE_NAME_PRODUCT_TYPES = PrimexDatabaseSchema.ProductTypes.TABLE_NAME;
    private static final String TABLE_NAME_PRODUCTS = PrimexDatabaseSchema.Products.TABLE_NAME;
    private static final String TABLE_NAME_WORK_ORDERS = PrimexDatabaseSchema.WorkOrders.TABLE_NAME;
    private static final String TABLE_NAME_PRODUCTION_LINES = PrimexDatabaseSchema.ProductionLines.TABLE_NAME;

    private static final String SQL_DELETE_PRODUCT_TYPES =
			"DROP TABLE IF EXISTS " + TABLE_NAME_PRODUCT_TYPES;
	private static final String SQL_DELETE_PRODUCTS =
			"DROP TABLE IF EXISTS " + TABLE_NAME_PRODUCTS;
	private static final String SQL_DELETE_PRODUCTION_LINES =
			"DROP TABLE IF EXISTS " + TABLE_NAME_PRODUCTION_LINES;
	private static final String SQL_DELETE_WORK_ORDERS =
			"DROP TABLE IF EXISTS " + TABLE_NAME_WORK_ORDERS;
    	 
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PRODUCTION_LINES);
        //Batch insert to SQLite database on Android
        try {
        	Integer[] lineNumbers = {1,6,7,9,10,11,12,13,14,15,16,17,18};
	        db.beginTransaction();
	        for (int i = 0; i < lineNumbers.length; i++) {
	        	ContentValues values = new ContentValues();
	        	values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LINE_NUMBER, lineNumbers[i]);
	        	values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LENGTH, 0);
	        	values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_DIE_WIDTH, 0);
	        	values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_SPEED_CONTROLLER_TYPE, "Direct");
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
        
        db.execSQL(SQL_CREATE_PRODUCTS);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_PRODUCTION_LINES);
        db.execSQL(SQL_DELETE_WORK_ORDERS);
        db.execSQL(SQL_DELETE_PRODUCT_TYPES);
        db.execSQL(SQL_DELETE_PRODUCTS);
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
    	
    	long rowId = db.insertOrThrow(
    			PrimexDatabaseSchema.ProductionLines.TABLE_NAME, 
    			null, 
    			values);
    	
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
		
		long rowId = db.insertWithOnConflict(
				PrimexDatabaseSchema.WorkOrders.TABLE_NAME, 
				null, 
				values,
				SQLiteDatabase.CONFLICT_IGNORE);
		
		return rowId;
	}
	
	public WorkOrder getWorkOrder(int woNumber){
		SQLiteDatabase db = getReadableDatabase();

		Cursor resultCursor = db.rawQuery("SELECT * FROM " + PrimexDatabaseSchema.WorkOrders.TABLE_NAME + " WHERE " + 
				PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_WO_NUMBER + "=?", new String[]{String.valueOf(woNumber)});
		/*Cursor resultCursor = db.query(
				PrimexDatabaseSchema.WorkOrders.TABLE_NAME,
				null, 
				PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_WO_NUMBER + "=?",
				new String[] {String.valueOf(woNumber)},
				null,
				null,
				null
				);*/
		
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
	
	public void clearWoNumbers() {
		SQLiteDatabase db = getWritableDatabase();
			
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
	
	public int getProductTypeId(String type){
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
	
	
	
	
	
	
	public long insertOrReplaceProduct(Product newProduct, int lineNumber) {
		SQLiteDatabase db = getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(PrimexDatabaseSchema.Products.COLUMN_NAME_GAUGE, newProduct.getGauge());
		values.put(PrimexDatabaseSchema.Products.COLUMN_NAME_WIDTH, newProduct.getWidth());
		values.put(PrimexDatabaseSchema.Products.COLUMN_NAME_LENGTH, newProduct.getLength());
		String type = newProduct.getType();
		int foreignKey = getProductTypeId(type);
		values.put(PrimexDatabaseSchema.Products.COLUMN_NAME_TYPE, foreignKey);
		int otherForeign = lineNumber;
		values.put(PrimexDatabaseSchema.Products.COLUMN_NAME_LINE, otherForeign);
		
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
	
	public Product getProduct(int lineNumber) {
		SQLiteDatabase db = getReadableDatabase();
		
		String sql = "SELECT * FROM " +
				PrimexDatabaseSchema.Products.TABLE_NAME + " JOIN " +
				PrimexDatabaseSchema.ProductTypes.TABLE_NAME + " ON " + 
				PrimexDatabaseSchema.ProductTypes.TABLE_NAME + "." +
				PrimexDatabaseSchema.ProductTypes._ID + "=" +
				PrimexDatabaseSchema.Products.TABLE_NAME + "." +
				PrimexDatabaseSchema.Products.COLUMN_NAME_TYPE + " WHERE " +
				PrimexDatabaseSchema.Products.COLUMN_NAME_LINE + "=?";
		String[] whereargs = new String[]{String.valueOf(lineNumber)};
		Cursor resultCursor = db.rawQuery(sql, whereargs);

		Product p = null;
		
		try {
			if (resultCursor.getCount() > 1) {
				Log.e("ERROR","You are not looking up a unique record for line number " + String.valueOf(lineNumber) +
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
				p.setLineNumber(lineNumber);
			} else {
				Log.e("error", "database query returned no results");
			}
		} finally {
			if (resultCursor != null) {resultCursor.close();}
		}	
		return p;
	}
	
}

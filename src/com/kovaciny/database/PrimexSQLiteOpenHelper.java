package com.kovaciny.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.kovaciny.primexmodel.Hopper;
import com.kovaciny.primexmodel.Novatec;
import com.kovaciny.primexmodel.Pallet;
import com.kovaciny.primexmodel.PrimexModel;
import com.kovaciny.primexmodel.Product;
import com.kovaciny.primexmodel.ProductionLine;
import com.kovaciny.primexmodel.Products;
import com.kovaciny.primexmodel.Roll;
import com.kovaciny.primexmodel.Skid;
import com.kovaciny.primexmodel.SpeedValues;
import com.kovaciny.primexmodel.WorkOrder;

public class PrimexSQLiteOpenHelper extends SQLiteOpenHelper {
    
	public PrimexSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
	
	// If you change the database schema, you must increment the database version. 
    public static final int DATABASE_VERSION = 159;
    public static final String DATABASE_NAME = "Primex.db";
    
	//list "child" tables, which have a foreign key, before their parent, so drop table works
    private static final String TABLE_NAME_BLENDED_HOPPERS = PrimexDatabaseSchema.BlendedHoppers.TABLE_NAME;
	private static final String TABLE_NAME_HOPPERS = PrimexDatabaseSchema.Hoppers.TABLE_NAME;
	private static final String TABLE_NAME_NOVATECS = PrimexDatabaseSchema.Novatecs.TABLE_NAME;
	private static final String TABLE_NAME_SKIDS = PrimexDatabaseSchema.Skids.TABLE_NAME;
	private static final String TABLE_NAME_LINE_WORK_ORDER_LINK = PrimexDatabaseSchema.LineWorkOrderLink.TABLE_NAME;
	private static final String TABLE_NAME_PRODUCT_TYPES = PrimexDatabaseSchema.ProductTypes.TABLE_NAME;
	private static final String TABLE_NAME_PRODUCTS = PrimexDatabaseSchema.Products.TABLE_NAME;
	private static final String TABLE_NAME_WORK_ORDERS = PrimexDatabaseSchema.WorkOrders.TABLE_NAME;
	private static final String TABLE_NAME_PRODUCTION_LINES = PrimexDatabaseSchema.ProductionLines.TABLE_NAME;
	private static final String TABLE_NAME_MODEL_STATE = PrimexDatabaseSchema.ModelState.TABLE_NAME;

	private static final String SQL_DELETE_BLENDED_HOPPERS =
	        "DROP TABLE IF EXISTS " + TABLE_NAME_BLENDED_HOPPERS;
    private static final String SQL_DELETE_HOPPERS =
            "DROP TABLE IF EXISTS " + TABLE_NAME_HOPPERS;
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
        PrimexDatabaseCreator creator = new PrimexDatabaseCreator();
        creator.create(db);
    }
    
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // upgrade policy is simply to discard the data and start over
        dropAllTables(db);
        onCreate(db);
    }
    
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    
    public void dropAllTables(SQLiteDatabase db) {
        db.execSQL(SQL_DELETE_BLENDED_HOPPERS);
        db.execSQL(SQL_DELETE_HOPPERS);
        db.execSQL(SQL_DELETE_NOVATECS);
        db.execSQL(SQL_DELETE_SKIDS);
        db.execSQL(SQL_DELETE_LINE_WORK_ORDER_LINK);
        db.execSQL(SQL_DELETE_PRODUCTION_LINES);
        db.execSQL(SQL_DELETE_WORK_ORDERS);
        db.execSQL(SQL_DELETE_PRODUCT_TYPES);
        db.execSQL(SQL_DELETE_PRODUCTS);
        db.execSQL(SQL_DELETE_MODEL_STATE);
    }
    
    public void clearWorkOrders() {
        SQLiteDatabase db = getWritableDatabase();
        dropAllTables(db);
        onCreate(db);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /*
     * CRUD methods go here, see http://pheide.com/page/11/tab/24#post13 if it gets to be too many
     * Just Ctrl+F for the name of the class
     */
    
    public long saveModelState(PrimexModel model) {
    	SQLiteDatabase db = getWritableDatabase();

    	ContentValues values = new ContentValues();
    	values.put(PrimexDatabaseSchema.ModelState.COLUMN_NAME_SELECTED_WORK_ORDER, model.getSelectedWorkOrder().getWoNumber());
    	values.put(PrimexDatabaseSchema.ModelState.COLUMN_NAME_COLOR_PERCENT, model.getColorPercent());
    	values.put(PrimexDatabaseSchema.ModelState.COLUMN_NAME_TEN_SECOND_LETDOWN_GRAMS, model.getTenSecondLetdownGrams());
    	if (model.getCreateDate() != null) {
    		values.put(PrimexDatabaseSchema.ModelState.COLUMN_NAME_CREATE_DATE, model.getCreateDate().getTime());
    	} 
    	values.put(PrimexDatabaseSchema.ModelState.COLUMN_NAME_DIFFERENTIAL_SETPOINT, model.getDifferentialSetpoint());
    	Log.v("SQLite", "just saved differential setpoint of " + String.valueOf(model.getDifferentialSetpoint()));
    	values.put(PrimexDatabaseSchema.ModelState.COLUMN_NAME_EDGE_TRIM_PERCENT, model.getEdgeTrimRatio());
    	values.put(PrimexDatabaseSchema.ModelState.COLUMN_NAME_GROSS_PPH, model.getGrossPph());
    	values.put(PrimexDatabaseSchema.ModelState.COLUMN_NAME_LINE_SPEED_SETPOINT, model.getLineSpeedSetpoint());
    	values.put(PrimexDatabaseSchema.ModelState.COLUMN_NAME_NET_PPH, model.getNetPph());
    	values.put(PrimexDatabaseSchema.ModelState.COLUMN_NAME_PRODUCTS_PER_MINUTE, model.getProductsPerMinute());
    	values.put(PrimexDatabaseSchema.ModelState.COLUMN_NAME_NUMBER_OF_TABLE_SKIDS, model.getNumberOfTableSkids());

    	long rowId;
    	try {
    		rowId = db.insertWithOnConflict(PrimexDatabaseSchema.ModelState.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_ABORT);
    	} catch (SQLiteConstraintException sqle) {
    		rowId = db.updateWithOnConflict(
    				PrimexDatabaseSchema.ModelState.TABLE_NAME, 
    				values,
    				PrimexDatabaseSchema.ModelState.COLUMN_NAME_SELECTED_WORK_ORDER + "=?",
    				new String[]{String.valueOf(model.getSelectedWorkOrder().getWoNumber())},
    				SQLiteDatabase.CONFLICT_REPLACE);
    	}
    	
    	return rowId;    
    }
  
    public Cursor loadModelState(int woNumber) {
    	SQLiteDatabase db = getReadableDatabase();

		Cursor resultCursor = db.rawQuery("SELECT * FROM " + PrimexDatabaseSchema.ModelState.TABLE_NAME + " WHERE " + 
				PrimexDatabaseSchema.ModelState.COLUMN_NAME_SELECTED_WORK_ORDER + "=?", new String[]{String.valueOf(woNumber)});
		
		return resultCursor;
    }
    /*
     * Also updates Novatec
     */
    public long insertOrUpdateLine(ProductionLine newLine) {
    	insertOrUpdateNovatec(newLine.getPrimaryNovatec(), newLine.getLineNumber());
    	SQLiteDatabase db = getWritableDatabase();
    	
    	ContentValues values = new ContentValues();
    	values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LINE_NUMBER, newLine.getLineNumber());
    	values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LENGTH, newLine.getLineLength());
    	values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_DIE_WIDTH, newLine.getDieWidth());
    	values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_WEB_WIDTH, newLine.getWebWidth());
    	values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_SPEED_CONTROLLER_TYPE, newLine.getSpeedControllerType());
    	values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_TAKEOFF_EQUIPMENT_TYPE, newLine.getTakeoffEquipmentType());
    	values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_DIFFERENTIAL_SPEED_SETPOINT , newLine.getSpeedValues().differentialSpeed);
    	values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_DIFFERENTIAL_RANGE_LOW , newLine.getDifferentialRangeLow());
    	values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_DIFFERENTIAL_RANGE_HIGH, newLine.getDifferentialRangeHigh());
		values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_SPEED_FACTOR, newLine.getSpeedValues().speedFactor);
		Log.v("insertLine", "just saved speed factor of " + String.valueOf(newLine.getSpeedValues().speedFactor));
        values.put(PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_SPEED_SETPOINT, newLine.getSpeedValues().lineSpeedSetpoint);
		
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
    	values.put(PrimexDatabaseSchema.Novatecs.COLUMN_NAME_CURRENT_SETPOINT, novatec.getSetpoint());
    	values.put(PrimexDatabaseSchema.Novatecs.COLUMN_NAME_SCREW_SIZE_FACTOR, novatec.getScrewSizeFactor());
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
    public ProductionLine loadLine(int lineNumber){
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
	    	
	    	ProductionLine newLine = new ProductionLine(ln,ll,dw,sct,tet, new Hopper());
	    	SpeedValues sv = new SpeedValues(sp,diff,sf);
	    	newLine.setSpeedValues(sv);
	    	Log.v("loadLine", "just loaded speed values of " + sv.toString());
	    	newLine.setWebWidth(ww);
	    	return newLine;
	    } finally {
	    	if (resultCursor != null) resultCursor.close();
    	}    	
    }
    
    public Novatec loadNovatec(int lineNumber) {
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
				double letdownRatio = resultCursor.getDouble(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.Novatecs.COLUMN_NAME_SCREW_SIZE_FACTOR));
				n = new Novatec(null, null, letdownRatio);
				n.setSetpoint(setpoint);
			} else {
				Log.e("error", "SQLiteOpenHelper::getNovatec returned no results");
			}
		} finally {
			if (resultCursor != null) {resultCursor.close();}
		}	
		return n;
    }
    
    public Cursor loadHoppers(int lineNumber) {
        SQLiteDatabase db = getReadableDatabase();
        
        String sql = "SELECT * FROM " +
                PrimexDatabaseSchema.Hoppers.TABLE_NAME +
                " WHERE " + PrimexDatabaseSchema.Hoppers.COLUMN_NAME_LINE_NUMBER_ID + "=?";
        long lineId = getIdOfValue(PrimexDatabaseSchema.ProductionLines.TABLE_NAME, 
                PrimexDatabaseSchema.ProductionLines.COLUMN_NAME_LINE_NUMBER, 
                lineNumber);
        String[] whereargs = new String[]{String.valueOf(lineId)};
        Cursor resultCursor = db.rawQuery(sql, whereargs);
        return resultCursor;
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
		}
		
		ContentValues values = new ContentValues();
		values.put(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_WO_NUMBER, newWo.getWoNumber());
		values.put(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_TOTAL_PRODUCTS_ORDERED, 
				newWo.getTotalProductsOrdered());
		values.put(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_MAXIMUM_STACK_HEIGHT, newWo.getMaximumStackHeight());
		if ( newWo.hasSelectedSkid()) {
			values.put(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_SELECTED_SKID_NUMBER, newWo.getSelectedSkid().getSkidNumber());	
		} else Log.v("dbHelper", String.valueOf(newWo.getWoNumber()) + " doesn't have a selected skid (maybe it's new)");
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
//		Log.v("Verbose", "just inserted this work order into row " + String.valueOf(rowId) + ": " + newWo.toString());
		return rowId;
	}
	
	public WorkOrder getWorkOrder(int woNumber){
		SQLiteDatabase db = getReadableDatabase();

		Cursor resultCursor = db.rawQuery("SELECT * FROM " + PrimexDatabaseSchema.WorkOrders.TABLE_NAME + " WHERE " + 
				PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_WO_NUMBER + "=?", new String[]{String.valueOf(woNumber)});
		
		try {
			if (resultCursor.moveToFirst()) {
		    	int wonum = resultCursor.getInt(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_WO_NUMBER));
		    	double ordered = resultCursor.getDouble(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_TOTAL_PRODUCTS_ORDERED));
		    	int selected = resultCursor.getInt(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_SELECTED_SKID_NUMBER));
		    	double height = resultCursor.getDouble(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_MAXIMUM_STACK_HEIGHT));
		    	long finish = resultCursor.getLong(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_FINISH_TIME));
		    	
		    	WorkOrder wo = new WorkOrder(wonum);
				wo.setProduct(getProduct(wonum));
				wo.setTotalProductsOrdered(ordered);	
				
				List<Skid<Product>> skidList = getSkidList(wo.getWoNumber());
				if (skidList.isEmpty()) throw new RuntimeException ("No skid list found for workorder " + String.valueOf(wo.getWoNumber()));
				wo.setSkidsList(skidList);				
				
				wo.selectSkid(selected);
				wo.setMaximumStackHeight(height);
				if (finish > 0) {
					wo.setFinishDate(new Date(finish));	
				}
				Log.v("Verbose", "just loaded this work order: " + wo.toString());
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
			    new String[] {"MAX(" + PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_WO_NUMBER + ")"}, // The columns to return
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
	
	public Date getLatestWoCreateDate() {
		SQLiteDatabase db = getReadableDatabase();

		Cursor c = db.query(
				PrimexDatabaseSchema.WorkOrders.TABLE_NAME,  // The table to query
				new String[] {"MAX(" + PrimexDatabaseSchema.ModelState.COLUMN_NAME_CREATE_DATE + ")"}, // The columns to return
				null,                                // The columns for the WHERE clause
				null,                            // The values for the WHERE clause
				null,                                     // don't group the rows
				null,                                     // don't filter by row groups
				null	                                 // The sort order
				);

		try { 
			if (c.moveToFirst()) {
				return new Date(c.getLong(0));
			}
			else return null; 
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
		int foreignKey = getIdOfValue(PrimexDatabaseSchema.ProductTypes.TABLE_NAME,
		        PrimexDatabaseSchema.ProductTypes.COLUMN_NAME_TYPES,
		        type);
		values.put(PrimexDatabaseSchema.Products.COLUMN_NAME_TYPE_ID, foreignKey);
		int otherForeign = woNumber;
		values.put(PrimexDatabaseSchema.Products.COLUMN_NAME_WO_NUMBER, otherForeign);
		values.put(PrimexDatabaseSchema.Products.COLUMN_NAME_UNIT_WEIGHT, newProduct.getUnitWeight());
		values.put(PrimexDatabaseSchema.Products.COLUMN_NAME_NUMBER_OF_WEBS, newProduct.getNumberOfWebs());
		if (newProduct instanceof Roll) {
		    values.put(PrimexDatabaseSchema.Products.COLUMN_NAME_CORE_TYPE, ((Roll) newProduct).getCoreType());
		}
    	
		long rowId = db.insertWithOnConflict(
				PrimexDatabaseSchema.Products.TABLE_NAME, 
				null, 
				values,
				SQLiteDatabase.CONFLICT_REPLACE);
		
		if (rowId == -1) {
			Log.d("insert product", "sqlite insert error code -1");
		} else {
//			Log.v("verbose", "inserted product " + newProduct.toString() + " into rowID " + String.valueOf(rowId)); //TODO firing on restart?
		}
		return rowId;
	}
	
	public Product getProduct(int woNumber) {
		SQLiteDatabase db = getReadableDatabase();
		
		String sql = "SELECT * FROM " +
				PrimexDatabaseSchema.Products.TABLE_NAME + 
				" JOIN " + PrimexDatabaseSchema.ProductTypes.TABLE_NAME + 
				" ON " + PrimexDatabaseSchema.ProductTypes.TABLE_NAME + "." + PrimexDatabaseSchema.ProductTypes._ID +  
				"=" + PrimexDatabaseSchema.Products.TABLE_NAME + "." + PrimexDatabaseSchema.Products.COLUMN_NAME_TYPE_ID + 
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
				double gauge = resultCursor.getDouble(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.Products.COLUMN_NAME_GAUGE));
				double width = resultCursor.getDouble(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.Products.COLUMN_NAME_WIDTH));
				double length = resultCursor.getDouble(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.Products.COLUMN_NAME_LENGTH));
				int numberOfWebs = resultCursor.getInt(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.Products.COLUMN_NAME_NUMBER_OF_WEBS));
				if (type.equals(Product.ROLLSET_TYPE) || type.equals(Product.SHEETSET_TYPE)) {
					width /= numberOfWebs;
				}
				int coreType = resultCursor.getInt(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.Products.COLUMN_NAME_CORE_TYPE));
				p = Products.makeProduct(type, gauge, width, length, numberOfWebs);
				if (p instanceof Roll) {
				    ((Roll) p).setCoreType(coreType);
				}
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
		int debug = resultCursor.getCount();
		List<Skid<Product>> skidList = new ArrayList<Skid<Product>>();
		try {
			while (resultCursor.moveToNext()){
				Skid<Product> skid = new Skid<Product>(
						new Pallet(), 
						resultCursor.getInt(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.Skids.COLUMN_NAME_TOTAL_ITEMS)),
						0.0d, 
						resultCursor.getInt(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.Skids.COLUMN_NAME_STACKS)),
						getProduct(woNumber)
						);
				int skidNumber = resultCursor.getInt(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.Skids.COLUMN_NAME_SKID_NUMBER));
				skid.setSkidNumber(skidNumber);
				int currentItems = resultCursor.getInt(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.Skids.COLUMN_NAME_CURRENT_ITEMS));
				skid.setCurrentItems(currentItems);
				long startTime = resultCursor.getLong(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.Skids.COLUMN_NAME_START_DATE));
				if (startTime > 0) {
					skid.setStartTime(new Date(startTime));
				}
				long finishTime = resultCursor.getLong(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.Skids.COLUMN_NAME_FINISH_DATE));
				if (finishTime > 0) {
					skid.setFinishTime(new Date(finishTime));
				}
				double minutesPerSkid = resultCursor.getDouble(resultCursor.getColumnIndexOrThrow(PrimexDatabaseSchema.Skids.COLUMN_NAME_TIME_PER_SKID));
				skid.setMinutesPerSkid(minutesPerSkid);
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
		values.put(PrimexDatabaseSchema.Skids.COLUMN_NAME_STACKS, skid.getNumberOfStacks());
		values.put(PrimexDatabaseSchema.Skids.COLUMN_NAME_TIME_PER_SKID, skid.getMinutesPerSkid());
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
//			Log.v("verbose", "inserted skid number " + skid.getSkidNumber() + " into row ID " + String.valueOf(rowId) + ": " + skid.toString());
		}
		return rowId;
	}
	
	/*
	 * This function will allow you to delete the currently selected skid, because it shouldn't know about that.
	 */
	public void deleteSkid(int woNumber, int skidNumber) {
		SQLiteDatabase db = getWritableDatabase();
		
		int woId = getIdOfValue(PrimexDatabaseSchema.WorkOrders.TABLE_NAME,
				PrimexDatabaseSchema.WorkOrders.COLUMN_NAME_WO_NUMBER,
				woNumber);
				
		String whereConditions = PrimexDatabaseSchema.Skids.COLUMN_NAME_SKID_NUMBER + "=?" + 
				" and " + PrimexDatabaseSchema.Skids.COLUMN_NAME_WO_ID + "=?";
				
		int rowsAffected = db.delete(
				PrimexDatabaseSchema.Skids.TABLE_NAME, 
				whereConditions, 
				new String[]{String.valueOf(skidNumber), String.valueOf(woId)});
				
		if (rowsAffected != 1) {
			throw new RuntimeException("deleted more than one skid");
		}
		
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

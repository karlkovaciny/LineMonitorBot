package com.kovaciny.database;

import java.util.ArrayList;
import java.util.List;

import com.kovaciny.primexmodel.WorkOrder;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseViewer extends SQLiteOpenHelper{

	public DatabaseViewer(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
	
    public static final int DATABASE_VERSION = PrimexSQLiteOpenHelper.DATABASE_VERSION;
    public static final String DATABASE_NAME = PrimexSQLiteOpenHelper.DATABASE_NAME;

/**
 * This function used to select the records from DB.
 * @param tableName
 * @param tableColumns
 * @param whereClase
 * @param whereArgs
 * @param groupBy
 * @param having
 * @param orderBy
 * @return A Cursor object, which is positioned before the first entry.
 */
public Cursor selectRecordsFromDB(String tableName, String[] tableColumns,String whereClase, String whereArgs[], String groupBy,String having, String orderBy) 
{
	SQLiteDatabase myDataBase = getReadableDatabase();
	return myDataBase.query(tableName, tableColumns, whereClase, whereArgs,groupBy, having, orderBy);
}

/**
 * select records from db and return in list
 * @param tableName
 * @param tableColumns
 * @param whereClase
 * @param whereArgs
 * @param groupBy
 * @param having
 * @param orderBy
 * @return ArrayList<ArrayList<String>>
 */
public ArrayList<ArrayList<String>> selectRecordsFromDBList(String tableName, String[] tableColumns,String whereClase, String whereArgs[], String groupBy,String having, String orderBy)
{       
	SQLiteDatabase myDataBase = getReadableDatabase();
    ArrayList<ArrayList<String>> retList = new ArrayList<ArrayList<String>>();
      ArrayList<String> list = new ArrayList<String>();
      Cursor cursor = myDataBase.query(tableName, tableColumns, whereClase, whereArgs,
                groupBy, having, orderBy);        
      if (cursor.moveToFirst()) 
      {
         do 
            {
             list = new ArrayList<String>();
             for(int i=0; i<cursor.getColumnCount(); i++)
             {                   
               list.add( cursor.getString(i) );
             }   
             retList.add(list);
         } while (cursor.moveToNext());
      }
      if (cursor != null && !cursor.isClosed()) {
         cursor.close();
      }
      return retList;

}

public List<String> getColumnNames(String tablename) {
	SQLiteDatabase db = getReadableDatabase();
	Cursor resultCursor = db.rawQuery("PRAGMA table_info(" + tablename + ")", null);
	List<String> colnames = new ArrayList<String>();
	try {
		while (resultCursor.moveToNext()) {
	    	colnames.add(resultCursor.getString(1));
	 	}
    	return colnames;
    } finally {
    	if (resultCursor != null) resultCursor.close();
	}
}

public List<String> getTableNames() {
	SQLiteDatabase db = getReadableDatabase();
	Cursor resultCursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type=?", new String[] {"table"});
	List<String> tableNames = new ArrayList<String>();
	try {
		while (resultCursor.moveToNext()) {
	    	tableNames.add(resultCursor.getString(0));
	 	}
    	return tableNames;
    } finally {
    	if (resultCursor != null) resultCursor.close();
	}	
}
/* (non-Javadoc)
 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
 */
@Override
public void onCreate(SQLiteDatabase db) {
	// TODO Auto-generated method stub
	
}

/* (non-Javadoc)
 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
 */
@Override
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	// TODO Auto-generated method stub
	
}   


}

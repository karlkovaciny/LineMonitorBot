package com.kovaciny.linemonitorbot;

import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataHelper {

    private static final String DATABASE_NAME = "autoMate.db";
    private static final int DATABASE_VERSION = 1;

    private Context context;
    private SQLiteDatabase db;
    private OpenHelper oh ;

    public DataHelper(Context context) {
        this.context = context;
        this.oh = new OpenHelper(this.context);
        this.db = oh.getWritableDatabase();

    }

    public void close()
    {
        db.close();
        oh.close();
        db= null;
        oh= null;
        SQLiteDatabase.releaseMemory();
    }

    public void upgradeDb(int oldVersion, int newVersion) {
    	oh.onUpgrade(db, oldVersion, newVersion);
    }

    public void setCode(String codeName, Object codeValue, String codeDataType)
    {
    	Cursor codeRow = null;
        try {
		        codeRow  = db.rawQuery("SELECT * FROM code WHERE codeName = '"+  codeName + "'", null);
		        
		        String cv = "" ;
		
		        if (codeDataType.toLowerCase().trim().equals("long") == true)
		        {   
		            cv = String.valueOf(codeValue);
		        }
		        else if (codeDataType.toLowerCase().trim().equals("integer") == true)
		        {
		            cv = String.valueOf(codeValue);
		        }
		        else if (codeDataType.toLowerCase().trim().equals("date") == true)
		        {
		            cv = String.valueOf(((Date)codeValue).getTime());
		        }
		        else if (codeDataType.toLowerCase().trim().equals("boolean") == true)
		        {
		            String.valueOf(codeValue);
		        }
		        else
		        {
		            cv = String.valueOf(codeValue);
		        }
		
		        if(codeRow.getCount() > 0) //exists-- update
		        { 
		            db.execSQL("update code set codeValue = '" + cv + 
		                "' where codeName = '" + codeName + "'");
		            //otherwise you can't correct bad data type
		            db.execSQL("update code set codeDataType = '" + codeDataType + 
			                "' where codeName = '" + codeName + "'");
		        }
		        else // does not exist, insert
		        {
		            db.execSQL("INSERT INTO code (codeName, codeValue, codeDataType) VALUES(" + 
		                    "'" + codeName + "'," + 
		                    "'" + cv + "'," + 
		                    "'" + codeDataType + "')" );
		        }
	        } finally {
	        	if (codeRow != null) codeRow.close();
	        }
    }

    public Object getCode(String codeName,Object defaultValue) //note, defaultValue can't be null because this PoS isn't parameterized and thinks 
    {
    	Cursor codeRow = null; 
    	try {
	        //check to see if it already exists
	        String codeValue = "";
	        String codeDataType = "";
	        boolean found = false;
	        codeRow  = db.rawQuery("SELECT * FROM code WHERE codeName = '"+  codeName + "'", null);
	        if(codeRow.moveToFirst()) 
	        {
	            codeValue = codeRow.getString(codeRow.getColumnIndex("codeValue"));
	            codeDataType = codeRow.getString(codeRow.getColumnIndex("codeDataType"));
	            found = true;
	        }
	
	        if (found == false)
	        {
	            return defaultValue;
	        }
	        else if (codeDataType.toLowerCase(Locale.getDefault()).trim().equals("long") == true)
	        {   
	            if (codeValue.equals("") == true)
	            {
	                return (long)0;
	            }
	            return Long.parseLong(codeValue);
	        }
	        else if (codeDataType.toLowerCase(Locale.getDefault()).trim().equals("integer") == true)
	        {
	            if ( (codeValue == null) || codeValue.equals("") || codeValue.equals("null") ) //because .equals("") didn't catch nulls.
	            {
	                return 0;  //can't return null because not an int
	            }
	            return Integer.parseInt(codeValue);
	        }
	        else if (codeDataType.toLowerCase(Locale.getDefault()).trim().equals("date") == true)
	        {
	            if (codeValue.equals("") == true)
	            {
	                return null;
	            }
	            return new Date(Long.parseLong(codeValue));
	        }
	        else if (codeDataType.toLowerCase(Locale.getDefault()).trim().equals("boolean") == true)
	        {
	            if (codeValue.equals("") == true)
	            {
	                return false;
	            }
	            return Boolean.parseBoolean(codeValue);
	        }
	        else
	        {
	        	return (String)codeValue;
	        }
    	} finally {
    		if (codeRow != null) codeRow.close();
    	}
    }


    private static class OpenHelper extends SQLiteOpenHelper {

        OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF  NOT EXISTS code" + 
            "(id INTEGER PRIMARY KEY, codeName TEXT, codeValue TEXT, codeDataType TEXT)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL("DROP TABLE IF EXISTS code");
            onCreate(db);
        }
    }
}

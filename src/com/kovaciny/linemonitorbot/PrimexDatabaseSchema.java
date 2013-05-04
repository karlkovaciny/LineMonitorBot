package com.kovaciny.linemonitorbot;

import android.provider.BaseColumns;

public class PrimexDatabaseSchema {
	private PrimexDatabaseSchema() {} //to keep from being instantiated
	
	public static abstract class PrimexLines implements BaseColumns {
	    public static final String TABLE_NAME = "lines";
	    public static final String COLUMN_NAME_LINE_NUMBER = "line number";
	    public static final String COLUMN_NAME_LINE_LENGTH = "line length";
	    public static final String COLUMN_NAME_SPEED_CONTROLLER_TYPE = "speed controller type";
	    public static final String COLUMN_NAME_DIE_WIDTH = "die width";
	    public static final String COLUMN_NAME_TAKEOFF_EQUIPMENT = "takeoff equipment";
	}
	
	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";
	private static final String SQL_CREATE_ENTRIES =
	    "CREATE TABLE " + PrimexDatabaseSchema
	    .PrimexLines.TABLE_NAME + " (" +
	    PrimexDatabaseSchema.PrimexLines._ID + " INTEGER PRIMARY KEY," +
	    PrimexDatabaseSchema.PrimexLines.COLUMN_NAME_LINE_NUMBER + TEXT_TYPE + COMMA_SEP +
	    PrimexDatabaseSchema.PrimexLines.COLUMN_NAME_LINE_LENGTH + TEXT_TYPE + COMMA_SEP +
	    PrimexDatabaseSchema.PrimexLines.COLUMN_NAME_SPEED_CONTROLLER_TYPE + TEXT_TYPE + COMMA_SEP +
	    PrimexDatabaseSchema.PrimexLines.COLUMN_NAME_DIE_WIDTH + TEXT_TYPE + COMMA_SEP +
	    PrimexDatabaseSchema.PrimexLines.COLUMN_NAME_TAKEOFF_EQUIPMENT + TEXT_TYPE + COMMA_SEP +
	    // ...Any other options for the CREATE command
	    " )";
	
	private static final String TABLE_NAME_ENTRIES = null;

	private static final String SQL_DELETE_ENTRIES =
	    "DROP TABLE IF EXISTS " + TABLE_NAME_ENTRIES;
	
}

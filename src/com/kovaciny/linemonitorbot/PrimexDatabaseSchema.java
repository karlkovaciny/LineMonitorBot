package com.kovaciny.linemonitorbot;

import android.provider.BaseColumns;

public class PrimexDatabaseSchema {
	private PrimexDatabaseSchema() {} //to keep from being instantiated
	
	//inner classes for each table
	public static abstract class ProductionLines implements BaseColumns {
	    public static final String TABLE_NAME = "production_lines";
	    public static final String COLUMN_NAME_LINE_NUMBER = "line_number";
	    public static final String COLUMN_NAME_LINE_LENGTH = "line_length";
	    public static final String COLUMN_NAME_SPEED_CONTROLLER_TYPE = "speed_controller_type";
	    public static final String COLUMN_NAME_DIE_WIDTH = "die_width";
	    public static final String COLUMN_NAME_TAKEOFF_EQUIPMENT_TYPE = "takeoff_equipment_type";
	}
}

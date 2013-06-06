package com.kovaciny.database;

import android.provider.BaseColumns;

public class PrimexDatabaseSchema {
	private PrimexDatabaseSchema() {} //to keep from being instantiated
	
	//inner classes for each table
	public static abstract class ProductionLines implements BaseColumns {
	    public static final String TABLE_NAME = "production_lines";
	    public static final String COLUMN_NAME_LINE_NUMBER = "line_number";
	    public static final String COLUMN_NAME_LENGTH = "length";
	    public static final String COLUMN_NAME_SPEED_CONTROLLER_TYPE = "speed_controller_type";
	    public static final String COLUMN_NAME_SPEED_SETPOINT = "speed_setpoint";
	    public static final String COLUMN_NAME_DIE_WIDTH = "die_width";
	    public static final String COLUMN_NAME_TAKEOFF_EQUIPMENT_TYPE = "takeoff_equipment_type";
	}
	
	public static abstract class WorkOrders implements BaseColumns {
		public static final String TABLE_NAME = "work_orders";
		public static final String COLUMN_NAME_WO_NUMBER = "WO_number";
		public static final String COLUMN_NAME_PRODUCTS_LIST_POINTER = "products_list";		
	}
	
	public static abstract class Products implements BaseColumns {
		public static final String TABLE_NAME = "products";
		public static final String COLUMN_NAME_GAUGE = "gauge";
		public static final String COLUMN_NAME_WIDTH = "width";
		public static final String COLUMN_NAME_LENGTH = "length";
		public static final String COLUMN_NAME_TYPE = "type";
		public static final String COLUMN_NAME_LINE = "line";
	}
	
	public static abstract class ProductTypes implements BaseColumns {
		public static final String TABLE_NAME = "product_types";
		public static final String COLUMN_NAME_TYPES = "types";		
	}
}

package com.kovaciny.database;

import java.util.Date;

import com.kovaciny.primexmodel.Pallet;
import com.kovaciny.primexmodel.Product;

import android.provider.BaseColumns;

public class PrimexDatabaseSchema {
	private PrimexDatabaseSchema() {} //to keep from being instantiated
	
	//inner classes for each table
	public static abstract class ModelState implements BaseColumns {
		public static final String TABLE_NAME = "model_state";
		public static final String COLUMN_NAME_SELECTED_LINE = "selected_line";
		public static final String COLUMN_NAME_SELECTED_WORK_ORDER = "selected_wo";
		public static final String COLUMN_NAME_CREATE_DATE = "create_date";
		public static final String COLUMN_NAME_EDGE_TRIM_PERCENT = "edge_trim_percent";
		public static final String COLUMN_NAME_NET_PPH = "net_pph";
		public static final String COLUMN_NAME_GROSS_PPH = "gross_pph";
		public static final String COLUMN_NAME_COLOR_PERCENT = "color_percent";
		public static final String COLUMN_NAME_LINE_SPEED_SETPOINT = "line_speed_setpoint";
		public static final String COLUMN_NAME_DIFFERENTIAL_SETPOINT = "differential_setpoint";
		public static final String COLUMN_NAME_PRODUCTS_PER_MINUTE = "products_per_minute";
	}
	
	public static abstract class ProductionLines implements BaseColumns {
	    public static final String TABLE_NAME = "production_lines";
	    public static final String COLUMN_NAME_LINE_NUMBER = "line_number";
	    public static final String COLUMN_NAME_LENGTH = "length"; //in feet
	    public static final String COLUMN_NAME_DIE_WIDTH = "die_width"; //in inches
	    public static final String COLUMN_NAME_WEB_WIDTH = "web_width"; //in inches
	    public static final String COLUMN_NAME_SPEED_CONTROLLER_TYPE = "speed_controller_type";
	    public static final String COLUMN_NAME_SPEED_SETPOINT = "speed_setpoint";
	    public static final String COLUMN_NAME_DIFFERENTIAL_SPEED_SETPOINT = "differential_speed";
	    public static final String COLUMN_NAME_DIFFERENTIAL_RANGE_LOW = "differential_range_low";
	    public static final String COLUMN_NAME_DIFFERENTIAL_RANGE_HIGH = "differential_range_high";
	    public static final String COLUMN_NAME_SPEED_FACTOR = "speed_factor";
	    public static final String COLUMN_NAME_TAKEOFF_EQUIPMENT_TYPE = "takeoff_equipment_type";
	}	
	
	public static abstract class WorkOrders implements BaseColumns {
		public static final String TABLE_NAME = "work_orders";
		public static final String COLUMN_NAME_WO_NUMBER = "WO_number";		
		public static final String COLUMN_NAME_SELECTED_PRODUCT_ID = "product_id";
		public static final String COLUMN_NAME_SELECTED_SKID_NUMBER = "skid_number";	
		public static final String COLUMN_NAME_TOTAL_PRODUCTS_ORDERED = "products_ordered";	
		public static final String COLUMN_NAME_MAXIMUM_STACK_HEIGHT = "max_stack_height";
		public static final String COLUMN_NAME_FINISH_TIME = "finish_time";
	}
	
	public static abstract class LineWorkOrderLink implements BaseColumns {
		public static final String TABLE_NAME = "line_work_order_link";
		public static final String COLUMN_NAME_LINE_ID = "line_id";
		public static final String COLUMN_NAME_WO_ID = "wo_id";
		public static final String COLUMN_NAME_WO_IS_SELECTED = "wo_selected";
	}
	
	public static abstract class Products implements BaseColumns {
		public static final String TABLE_NAME = "products";
		public static final String COLUMN_NAME_GAUGE = "gauge";
		public static final String COLUMN_NAME_WIDTH = "width";
		public static final String COLUMN_NAME_LENGTH = "length";
		public static final String COLUMN_NAME_TYPE = "type";
		public static final String COLUMN_NAME_WO_NUMBER = "wo_number";
		public static final String COLUMN_NAME_UNIT_WEIGHT = "unit_weight";
	}
	
	public static abstract class Novatecs implements BaseColumns {
		public static final String TABLE_NAME = "novatecs";
		public static final String COLUMN_NAME_LETDOWN_RATIO = "letdown_ratio";
		public static final String COLUMN_NAME_CURRENT_SETPOINT = "current_setpoint";
		public static final String COLUMN_NAME_LINE_NUMBER_ID = "line_number_id";
	}
	
	public static abstract class Skids implements BaseColumns {
		public static final String TABLE_NAME = "skids";
		public static final String COLUMN_NAME_SKID_NUMBER= "skid_number";
		public static final String COLUMN_NAME_CURRENT_ITEMS = "current_items";
		public static final String COLUMN_NAME_TOTAL_ITEMS = "total_items";
		public static final String COLUMN_NAME_STACKS = "stacks";
		public static final String COLUMN_NAME_START_DATE = "start_time";
		public static final String COLUMN_NAME_FINISH_DATE = "finish_time";
		public static final String COLUMN_NAME_TIME_PER_SKID = "time_per_skid";
		public static final String COLUMN_NAME_WO_ID = "wo_id";
	}
	
	public static abstract class ProductTypes implements BaseColumns {
		public static final String TABLE_NAME = "product_types";
		public static final String COLUMN_NAME_TYPES = "types";		
	}
}

package com.kovaciny.helperfunctions;

final public class HelperFunction {
	private HelperFunction() {
		//class can't be instantiated
	}
	public static final long ONE_MINUTE_IN_MILLIS=60000; //millisecs
	public static final long ONE_SECOND_IN_MILLIS=1000; 
	
	public static String formatMinutesAsHours(long minutes) {
        long hours = minutes/60;
        long remainingMinutes = minutes % 60;
        return String.format("%d:%02d", hours, remainingMinutes);
    }
	
}

package com.kovaciny.helperfunctions;

final public class HelperFunction {
	private HelperFunction() {
		//class can't be instantiated
	}
	
	public static String formatMinutesAsHours(long minutes) {
        long hours = minutes/60;
        long remainingMinutes = minutes % 60;
        return String.format("%d:%02d", hours, remainingMinutes);
    }
}

package com.kovaciny.linemonitorbot;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;

public class SkidFinishedBroadcastReceiver extends BroadcastReceiver {

	final public static String ONE_TIME = "onetime";
	final public static String REPEATING = "repeating";
	
	@Override
	 public void onReceive(Context context, Intent intent) {
	   PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
	         PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Skid finished");
	         
	         try {
		         //Acquire the lock 
		         wl.acquire();
		 
		         //You can do the processing here.
		      
		         // Create a nice static vibrator.
		 		 final Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		 		
		         
		         Bundle extras = intent.getExtras();
		         StringBuilder msgStr = new StringBuilder();
		          
		         if (extras != null && extras.getBoolean(ONE_TIME, Boolean.FALSE)) {
			          //See if this intent has been sent by the one-time timer button.
			          msgStr.append("One time Timer : ");
		         }
		         if (extras != null && extras.getBoolean(REPEATING, Boolean.FALSE)) {
			          //See if this intent has been sent by the repeating timer button.
			          msgStr.append("Repeating Timer : ");
		         }
		         SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss a");
		         msgStr.append(formatter.format(new Date()));
		 
//		         Toast.makeText(context, msgStr, Toast.LENGTH_LONG).show();
		         SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		         String durationPref = sharedPref.getString("vibrator_duration", "Off");
		         
		         if (durationPref.equals("Long")) {
		        	 long[] offOnPattern = {0, 300, 300, 300, 300, 2500, 300, 300, 300, 2500, 300, 300, 300, 2500, 300, 300, 300, 2500, 300, 300, 300, 2500,30000,2000};
		        	 vibe.vibrate(offOnPattern, -1);
		         } else if (durationPref.equals("Short")) {
		        	 long[] offOnPattern = {0, 1500, 300, 1500, 300, 1500};
		        	 vibe.vibrate(offOnPattern, -1);
		         }
		         
		     } finally {
		         //Release the lock
		         wl.release();
	         }
	 }
    public void setOnetimeTimer(Context context, long interval){
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
           Intent intent = new Intent(context, SkidFinishedBroadcastReceiver.class);
           intent.putExtra(ONE_TIME, Boolean.TRUE);
           PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
           am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+ interval, pi);
       }
    
    public void setRepeatingTimer(Context context, long trigger, long interval){
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
           Intent intent = new Intent(context, SkidFinishedBroadcastReceiver.class);
           intent.putExtra(REPEATING, Boolean.TRUE);
           PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
           am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+ trigger, interval, pi);
       }
}

package com.kovaciny.linemonitorbot;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.widget.Toast;

public class SkidFinishedBroadcastReceiver extends BroadcastReceiver {

	final public static String ONE_TIME = "onetime";
	
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
			          //Make sure this intent has been sent by the one-time timer button.
			          msgStr.append("One time Timer : ");
		         }
		         
		         SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss a");
		         msgStr.append(formatter.format(new Date()));
		 
		         Toast.makeText(context, msgStr, Toast.LENGTH_LONG).show();
//		         long[] offOnPattern = {0, 5000, 500, 3000, 300, 300, 300, 300, 300, 300, 300, 3000};
		         long[] offOnPattern = {0, 300, 300, 300, 300, 2500, 300, 300, 300, 2500, 300, 300, 300, 2500, 300, 300, 300, 2500};
		         vibe.vibrate(offOnPattern, -1);
		         
		     } finally {
		         //Release the lock
		         wl.release();
	         }
	 }
    public void setOnetimeTimer(Context context, Integer interval){
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
           Intent intent = new Intent(context, SkidFinishedBroadcastReceiver.class);
           intent.putExtra(ONE_TIME, Boolean.TRUE);
           PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
           am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+ interval, pi);
       }
}

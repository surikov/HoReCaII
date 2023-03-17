package sweetlife.android10;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;

import java.util.Calendar;

import android.content.Intent;
//import android.support.annotation.NonNull;

import sweetlife.android10.ui.Activity_Update;

public class SweetStart extends BroadcastReceiver {


    public SweetStart() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //System.out.println("SweetStart.onReceive");
        //Activity_UploadBids.logToFile("onReceive.txt","SweetStart.onReceive "+intent.getAction());
        //String act=intent.getAction();
        if (intent.getAction()!=null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // Set the alarm here.
            initSchedule(context);
        }else{
            //Activity_UploadBids.logToFile("startUpdate.txt","Activity_Update");
            try {
                Intent updateintent = new Intent();
                updateintent.setClass(context, Activity_Update.class);
                updateintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(updateintent);
            }catch(Throwable t){
                //Activity_UploadBids.logToFile("startUpdateCatch.txt",t.getMessage());
            }
        }
    }

    public void initSchedule(Context context) {
        //Activity_UploadBids.logToFile("initSchedule.txt","SweetStart.initSchedule");
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, SweetStart.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmMgr.cancel(alarmIntent);
        alarmIntent.cancel();
/*
        intent = new Intent(context, SweetStart.class);
        alarmIntent = PendingIntent.getBroadcast(context, 12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmMgr.cancel(alarmIntent);
        alarmIntent.cancel();
*/
        intent = new Intent(context, SweetStart.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 6);
        calendar.add(Calendar.DAY_OF_MONTH,1);
        System.out.println(calendar);

         alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);

    }
}

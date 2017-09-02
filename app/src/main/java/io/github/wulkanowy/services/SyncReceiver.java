package io.github.wulkanowy.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class SyncReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 12345;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, SyncService.class);
        context.startService(i);
        setAlarm(context);
    }

    public void setAlarm(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 30);

        Intent intent = new Intent(context, SyncReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, SyncReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        Log.d("TEST CRITICAL", "Set alarm manager");
    }
}

package io.github.wulkanowy.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class SyncService extends IntentService {

    public SyncService() {
        super(SyncService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i("Wulkanowy Services", "Services is running");
        WakefulBroadcastReceiver.completeWakefulIntent(intent);
        SyncData syncData = new SyncData(getApplicationContext());
        syncData.syncGradesAndSubjects();
    }
}

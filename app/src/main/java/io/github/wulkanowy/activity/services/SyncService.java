package io.github.wulkanowy.activity.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

public class SyncService extends IntentService {

    public SyncService() {
        super(SyncService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        SyncData syncData = new SyncData(getApplicationContext());
        syncData.syncGradesAndSubjects();
    }
}

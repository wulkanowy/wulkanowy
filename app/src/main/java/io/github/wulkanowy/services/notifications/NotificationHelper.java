package io.github.wulkanowy.services.notifications;


import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;

public class NotificationHelper extends ContextWrapper {

    public static final String CHANNEL_ONE_ID = "io.github.wulkanowy.newItem";

    public static final String CHANNEL_ONE_NAME = "New Item Channel";

    public static final int NOTIFIACTION_IMPORTANCE = NotificationManager.IMPORTANCE_HIGH;

    public NotificationHelper(Context context) {
        super(context);
    }

    public void createChannel() {
    }
}

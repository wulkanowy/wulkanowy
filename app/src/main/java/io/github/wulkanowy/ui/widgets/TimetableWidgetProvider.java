package io.github.wulkanowy.ui.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import io.github.wulkanowy.R;
import io.github.wulkanowy.services.widgets.TimetableWidgetServices;

public class TimetableWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.timetable_widget);
            Intent intent = new Intent(context, TimetableWidgetServices.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

            Intent refreshIntent = new Intent(context, TimetableWidgetProvider.class);
            refreshIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                    refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.button, pendingIntent);

            views.setRemoteAdapter(appWidgetId, R.id.timetable_widget_list, intent);
            views.setEmptyView(R.id.timetable_widget_list, R.id.empty);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.timetable_widget_list);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisWidget = new ComponentName(context.getPackageName(),
                    TimetableWidgetProvider.class.getName());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

            onUpdate(context, appWidgetManager, appWidgetIds);
        }
    }
}

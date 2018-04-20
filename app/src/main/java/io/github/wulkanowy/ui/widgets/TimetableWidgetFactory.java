package io.github.wulkanowy.ui.widgets;

import android.content.Context;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import javax.inject.Inject;

import io.github.wulkanowy.R;
import io.github.wulkanowy.WulkanowyApp;

public class TimetableWidgetFactory implements RemoteViewsService.RemoteViewsFactory,
        TimetableWidgetContract.Factory {

    private Context context;

    @Inject
    TimetableWidgetContract.Presenter presenter;

    public TimetableWidgetFactory(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        presenter = null;
        ((WulkanowyApp) context).getApplicationComponent().inject(this);
        presenter.onDataSetChanged(this);
    }

    @Override
    public void onDestroy() {
        presenter = null;
    }

    @Override
    public int getCount() {
        return presenter.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION) {
            return null;
        }

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.timetable_widget_item);
        views.setTextViewText(R.id.timetable_widget_item_subject, presenter.getSubjectName(position));
        views.setTextViewText(R.id.timetable_widget_item_room, presenter.getRoomText(position));
        views.setTextViewText(R.id.timetable_widget_item_time, presenter.getTimeText(position));

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return presenter.getViewTypeCount();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return presenter.hasStableIds();
    }

    @Override
    public String getRoomString() {
        return context.getString(R.string.timetable_dialog_room);
    }
}

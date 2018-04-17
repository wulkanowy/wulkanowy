package io.github.wulkanowy.ui.widgets;

public interface TimetableWidgetContract {

    interface Presenter {

        void onDataSetChanged(Factory widgetFactory);

        int getCount();

        int getViewTypeCount();

        String getSubjectName(int position);

        String getRoomText(int position);

        String getTimeText(int position);

        boolean hasStableIds();

    }

    interface Factory {

        String getRoomString();
    }
}

package io.github.wulkanowy.ui.widgets;

public interface TimetableWidgetContract {

    interface Presenter {

        void onStart();

        int getCount();

        int getViewTypeCount();

        String getSubjectName(int position);

        boolean hasStableIds();

    }
}

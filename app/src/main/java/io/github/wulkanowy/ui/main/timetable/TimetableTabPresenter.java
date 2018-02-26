package io.github.wulkanowy.ui.main.timetable;


import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.data.db.dao.entities.Day;
import io.github.wulkanowy.data.db.dao.entities.Lesson;
import io.github.wulkanowy.ui.base.BasePresenter;

public class TimetableTabPresenter extends BasePresenter<TimetableTabContract.View>
        implements TimetableTabContract.Presenter {

    private String date;

    private boolean isFirstSight = false;

    @Inject
    TimetableTabPresenter(RepositoryContract repository) {
        super(repository);
    }

    @Override
    public void onStart(TimetableTabContract.View view, boolean isPrimary) {
        super.onStart(view);
        onFragmentSelected(isPrimary);
    }

    @Override
    public void onFragmentSelected(boolean isSelected) {
        if (!isFirstSight && isSelected) {
            isFirstSight = true;

            List<Day> dayList = getRepository().getWeek(date).getDayList();

            List<TimetableHeaderItem> headerItems = new ArrayList<>();

            for (Day day : dayList) {
                TimetableHeaderItem headerItem = new TimetableHeaderItem(day);

                List<Lesson> lessonList = day.getLessons();

                List<TimetableSubItem> subItems = new ArrayList<>();

                for (Lesson lesson : lessonList) {
                    subItems.add(new TimetableSubItem(headerItem, lesson));
                }

                headerItem.setSubItems(subItems);
                headerItem.setExpanded(false);
                headerItems.add(headerItem);
            }

            getView().updateAdapterList(headerItems);
        }
    }

    @Override
    public void setArgumentDate(String date) {
        this.date = date;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isFirstSight = false;
    }
}

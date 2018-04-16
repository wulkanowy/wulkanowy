package io.github.wulkanowy.ui.widgets;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.data.db.dao.entities.TimetableLesson;
import io.github.wulkanowy.data.db.dao.entities.Week;
import io.github.wulkanowy.utils.TimeUtils;

public class TimetableWidgetPresenter implements TimetableWidgetContract.Presenter {

    private RepositoryContract repository;

    private List<TimetableLesson> lessonList = new ArrayList<>();

    @Inject
    TimetableWidgetPresenter(RepositoryContract repository) {
        this.repository = repository;
    }

    @Override
    public void onStart() {
        Week week = repository.getWeek(TimeUtils.getDateOfCurrentMonday(true));
        lessonList = week.getDayList().get(TimeUtils.getTodayDayValue() - 1)
                .getTimetableLessons();
    }

    @Override
    public int getCount() {
        return lessonList.isEmpty() ? 0 : lessonList.size();
    }

    @Override
    public String getSubjectName(int position) {
        return lessonList.get(position).getSubject();
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

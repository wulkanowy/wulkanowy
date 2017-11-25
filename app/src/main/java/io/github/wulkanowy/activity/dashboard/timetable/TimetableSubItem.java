package io.github.wulkanowy.activity.dashboard.timetable;

import android.view.View;
import android.widget.TextView;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractSectionableItem;
import eu.davidea.viewholders.FlexibleViewHolder;
import io.github.wulkanowy.R;
import io.github.wulkanowy.dao.entities.Lesson;

public class TimetableSubItem extends AbstractSectionableItem<TimetableSubItem.SubItemViewHolder, TimetableHeaderItem> {

    private Lesson lesson;

    public TimetableSubItem(TimetableHeaderItem header, Lesson lesson) {
        super(header);
        this.lesson = lesson;
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.timetable_subitem;
    }

    @Override
    public SubItemViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return new SubItemViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, SubItemViewHolder holder, int position, List payloads) {
        holder.getLessonName().setText(lesson.getSubject());
    }

    public static class SubItemViewHolder extends FlexibleViewHolder {

        private TextView lessonName;

        public SubItemViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);

            lessonName = view.findViewById(R.id.timetable_item_text);
        }

        public TextView getLessonName() {
            return lessonName;
        }
    }
}

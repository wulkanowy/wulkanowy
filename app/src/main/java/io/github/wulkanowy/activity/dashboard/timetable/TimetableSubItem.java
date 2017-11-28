package io.github.wulkanowy.activity.dashboard.timetable;

import android.app.Activity;
import android.app.DialogFragment;
import android.view.View;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractSectionableItem;
import eu.davidea.viewholders.FlexibleViewHolder;
import io.github.wulkanowy.R;
import io.github.wulkanowy.dao.entities.Lesson;

public class TimetableSubItem extends AbstractSectionableItem<TimetableSubItem.SubItemViewHolder, TimetableHeaderItem> {

    private Lesson lesson;

    private Activity activity;

    public TimetableSubItem(TimetableHeaderItem header, Lesson lesson, Activity activity) {
        super(header);
        this.lesson = lesson;
        this.activity = activity;
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
        holder.getLessonTime().setText(String.format("%1$s - %2$s", lesson.getStartTime(), lesson.getEndTime()));
        holder.getNumberOfLesson().setText(lesson.getNumber());
        holder.getRoom().setText(lesson.getRoom());
        holder.getDescription().setText(StringUtils.capitalize(lesson.getDescription()));

        holder.setDialog(lesson, activity);

        if (!lesson.getRoom().isEmpty()) {
            holder.getRoom().setText(holder.getContentView().getContext().getString(R.string.timetable_subitem_room, lesson.getRoom()));
        }
    }

    public static class SubItemViewHolder extends FlexibleViewHolder {

        private TextView lessonName;

        private TextView numberOfLesson;

        private TextView lessonTime;

        private TextView room;

        private TextView description;

        public SubItemViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);

            description = view.findViewById(R.id.timetable_subItem_description);
            lessonName = view.findViewById(R.id.timetable_subItem_lesson_text);
            numberOfLesson = view.findViewById(R.id.timetable_subItem_number_of_lesson);
            lessonTime = view.findViewById(R.id.timetable_subItem_time);
            room = view.findViewById(R.id.timetable_subItem_room);
        }

        public void setDialog(final Lesson lesson, final Activity activity) {
            getContentView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TimetableDialogFragment dialogFragment = TimetableDialogFragment.newInstance(lesson);
                    dialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                    dialogFragment.show(activity.getFragmentManager(), lesson.toString());
                }
            });

        }

        public TextView getLessonName() {
            return lessonName;
        }

        public TextView getNumberOfLesson() {
            return numberOfLesson;
        }

        public TextView getLessonTime() {
            return lessonTime;
        }

        public TextView getRoom() {
            return room;
        }

        public TextView getDescription() {
            return description;
        }
    }
}

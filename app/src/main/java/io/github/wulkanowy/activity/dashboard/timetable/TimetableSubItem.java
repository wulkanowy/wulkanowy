package io.github.wulkanowy.activity.dashboard.timetable;

import android.app.Activity;
import android.app.DialogFragment;
import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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

    public Lesson getLesson() {
        return lesson;
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
        holder.lessonName.setText(lesson.getSubject(), TextView.BufferType.SPANNABLE);
        holder.lessonTime.setText(String.format("%1$s - %2$s", lesson.getStartTime(), lesson.getEndTime()));
        holder.numberOfLesson.setText(lesson.getNumber());
        holder.room.setText(lesson.getRoom());

        holder.setDialog(lesson, activity);

        if (lesson.getIsMovedOrCanceled() || lesson.getIsNewMovedInOrChanged()) {
            holder.change.setVisibility(View.VISIBLE);
        } else {
            holder.change.setVisibility(View.GONE);
        }

        if (lesson.getIsMovedOrCanceled()) {
            holder.lessonName.setPaintFlags(holder.lessonName.getPaintFlags()
                    | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.lessonName.setPaintFlags(holder.lessonName.getPaintFlags()
                    & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        if (!lesson.getRoom().isEmpty()) {
            holder.room.setText(holder.getContentView().getContext().getString(R.string.timetable_subitem_room, lesson.getRoom()));
        }
    }

    public static class SubItemViewHolder extends FlexibleViewHolder {

        @BindView(R.id.timetable_subItem_lesson_text)
        public TextView lessonName;

        @BindView(R.id.timetable_subItem_number_of_lesson)
        public TextView numberOfLesson;

        @BindView(R.id.timetable_subItem_time)
        public TextView lessonTime;

        @BindView(R.id.timetable_subItem_room)
        public TextView room;

        @BindView(R.id.timetable_subItem_change_image)
        public ImageView change;

        public SubItemViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
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
    }
}

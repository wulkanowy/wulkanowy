package io.github.wulkanowy.ui.main.attendance;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
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
import io.github.wulkanowy.data.db.dao.entities.AttendanceLesson;

class AttendanceSubItem
        extends AbstractSectionableItem<AttendanceSubItem.SubItemViewHolder, AttendanceHeaderItem> {

    private AttendanceLesson lesson;

    AttendanceSubItem(AttendanceHeaderItem header, AttendanceLesson lesson) {
        super(header);
        this.lesson = lesson;
    }

    AttendanceLesson getLesson() {
        return lesson;
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.attendance_subitem;
    }

    @Override
    public AttendanceSubItem.SubItemViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return new AttendanceSubItem.SubItemViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, AttendanceSubItem.SubItemViewHolder holder, int position, List payloads) {
        holder.onBind(lesson);
    }

    static class SubItemViewHolder extends FlexibleViewHolder {

        @BindView(R.id.attendance_subItem_lesson)
        TextView lessonName;

        @BindView(R.id.attendance_subItem_number)
        TextView lessonNumber;

        @BindView(R.id.attendance_subItem_description)
        TextView lessonDescription;

        @BindView(R.id.attendance_subItem_alert_image)
        ImageView alert;

        private Context context;

        private AttendanceLesson item;

        SubItemViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
            context = view.getContext();
            view.setOnClickListener(this);
        }

        void onBind(AttendanceLesson lesson) {
            item = lesson;

            lessonName.setText(lesson.getSubject());
            lessonNumber.setText(lesson.getNumber());
            lessonDescription.setText(AttendanceTypeHelper.getLessonDescription(lesson));
            alert.setVisibility(lesson.getIsAbsenceUnexcused() ? View.VISIBLE : View.INVISIBLE);
        }

        @Override
        public void onClick(View view) {
            super.onClick(view);
            showDialog();
        }

        private void showDialog() {
            AttendanceDialogFragment dialogFragment = AttendanceDialogFragment.newInstance(item);
            dialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
            dialogFragment.show(((FragmentActivity) context).getSupportFragmentManager(), item.toString());
        }
    }
}

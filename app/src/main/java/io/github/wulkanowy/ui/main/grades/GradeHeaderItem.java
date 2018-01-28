package io.github.wulkanowy.ui.main.grades;


import android.view.View;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractExpandableHeaderItem;
import eu.davidea.viewholders.ExpandableViewHolder;
import io.github.wulkanowy.R;
import io.github.wulkanowy.db.dao.entities.Lesson;

public class GradeHeaderItem
        extends AbstractExpandableHeaderItem<GradeHeaderItem.HeaderViewHolder, GradesSubItem> {

    private Lesson lesson;

    public GradeHeaderItem(Lesson lesson) {
        this.lesson = lesson;
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.grade_header;
    }

    @Override
    public HeaderViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return new HeaderViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, HeaderViewHolder holder, int position, List payloads) {

    }

    public class HeaderViewHolder extends ExpandableViewHolder {

        public HeaderViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
        }
    }
}

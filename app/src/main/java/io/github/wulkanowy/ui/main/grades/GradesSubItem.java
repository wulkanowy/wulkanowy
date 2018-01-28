package io.github.wulkanowy.ui.main.grades;

import android.view.View;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractSectionableItem;
import eu.davidea.viewholders.FlexibleViewHolder;
import io.github.wulkanowy.R;
import io.github.wulkanowy.db.dao.entities.Grade;

public class GradesSubItem
        extends AbstractSectionableItem<GradesSubItem.SubItemViewHolder, GradeHeaderItem> {

    private Grade grade;

    public GradesSubItem(GradeHeaderItem header, Grade grade) {
        super(header);
        this.grade = grade;
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.grade_subitem;
    }

    @Override
    public SubItemViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return new SubItemViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, SubItemViewHolder holder, int position, List payloads) {

    }

    public class SubItemViewHolder extends FlexibleViewHolder {

        public SubItemViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
        }
    }
}

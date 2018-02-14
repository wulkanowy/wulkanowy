package io.github.wulkanowy.ui.main.grades;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractExpandableHeaderItem;
import eu.davidea.viewholders.ExpandableViewHolder;
import io.github.wulkanowy.R;
import io.github.wulkanowy.data.db.dao.entities.Subject;

public class GradeHeaderItem
        extends AbstractExpandableHeaderItem<GradeHeaderItem.HeaderViewHolder, GradesSubItem> {

    private Subject subject;

    public GradeHeaderItem(Subject subject) {
        this.subject = subject;
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
        holder.subjectName.setText(subject.getName());
    }

    class HeaderViewHolder extends ExpandableViewHolder {

        @BindView(R.id.grade_header_subject_text)
        TextView subjectName;

        @BindView(R.id.grade_header_average_text)
        TextView averageText;

        @BindView(R.id.grade_header_number_of_grade_text)
        TextView numberText;

        @BindView(R.id.grade_header_alert_image)
        ImageView alertImage;

        HeaderViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleExpansion();
                }
            });
        }
    }
}

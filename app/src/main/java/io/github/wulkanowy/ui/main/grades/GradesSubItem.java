package io.github.wulkanowy.ui.main.grades;

import android.content.Context;
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
import io.github.wulkanowy.data.db.dao.entities.Grade;

public class GradesSubItem
        extends AbstractSectionableItem<GradesSubItem.SubItemViewHolder, GradeHeaderItem> {

    private Grade grade;

    GradesSubItem(GradeHeaderItem header, Grade grade) {
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
        holder.onBind(grade);
    }

    class SubItemViewHolder extends FlexibleViewHolder {

        @BindView(R.id.grade_subitem_value)
        TextView value;

        @BindView(R.id.grade_subitem_description)
        TextView description;

        @BindView(R.id.grade_subitem_date)
        TextView date;

        @BindView(R.id.grade_subitem_alert_image)
        ImageView alert;

        private Context context;

        SubItemViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
            context = view.getContext();
        }

        void onBind(Grade item) {
            value.setText(item.getValue());
            value.setBackgroundResource(item.getValueColor());
            date.setText(item.getDate());
            description.setText(getDescriptionString(item));
            alert.setVisibility(View.INVISIBLE);
        }

        private String getDescriptionString(Grade item) {
            if (item.getDescription() == null || "".equals(item.getDescription())) {
                if (!"".equals(item.getSymbol())) {
                    return item.getSymbol();
                } else {
                    return context.getString(R.string.noDescription_text);
                }
            } else {
                return item.getDescription();
            }
        }
    }
}

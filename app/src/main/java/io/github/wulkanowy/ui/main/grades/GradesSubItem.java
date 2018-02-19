package io.github.wulkanowy.ui.main.grades;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        GradesSubItem that = (GradesSubItem) o;

        return new EqualsBuilder()
                .append(grade, that.grade)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(grade)
                .toHashCode();
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

        private Grade item;

        SubItemViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
            context = view.getContext();
        }

        void onBind(Grade item) {
            this.item = item;
            getContentView().setOnClickListener(this);

            value.setText(item.getValue());
            value.setBackgroundResource(item.getValueColor());
            date.setText(item.getDate());
            description.setText(getDescriptionString());
            alert.setVisibility(item.getRead() ? View.INVISIBLE : View.VISIBLE);
        }

        @Override
        public void onClick(View view) {
            super.onClick(view);
            showDialog();
        }

        private String getDescriptionString() {
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

        private void showDialog() {
            GradesDialogFragment dialogFragment = GradesDialogFragment.newInstance(item);
            dialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
            dialogFragment.show(((FragmentActivity) context).getSupportFragmentManager(), grade.toString());
        }
    }
}

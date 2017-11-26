package io.github.wulkanowy.activity.dashboard.timetable;

import android.view.View;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractExpandableHeaderItem;
import eu.davidea.viewholders.ExpandableViewHolder;
import io.github.wulkanowy.R;
import io.github.wulkanowy.dao.entities.Day;

public class TimetableHeaderItem
        extends AbstractExpandableHeaderItem<TimetableHeaderItem.HeaderViewHolder, TimetableSubItem> {

    private Day day;

    public TimetableHeaderItem(Day day) {
        this.day = day;
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.timetable_header;
    }

    @Override
    public HeaderViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return new HeaderViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, HeaderViewHolder holder, int position, List payloads) {
        holder.getDayName().setText(StringUtils.capitalize(day.getDayName()));
        holder.getDate().setText(day.getDate());
    }

    public static class HeaderViewHolder extends ExpandableViewHolder {

        private TextView dayName;

        private TextView date;

        public HeaderViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleExpansion();
                }
            });

            dayName = view.findViewById(R.id.timetable_header_dayName_text);
            date = view.findViewById(R.id.timetable_header_date_text);
        }

        public TextView getDayName() {
            return dayName;
        }

        public TextView getDate() {
            return date;
        }
    }
}

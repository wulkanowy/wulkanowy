package io.github.wulkanowy.activity.dashboard.grades;


import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.List;

import io.github.wulkanowy.R;
import io.github.wulkanowy.dao.entities.Grade;

public class GradesAdapter extends ExpandableRecyclerViewAdapter<GradesAdapter.SubjectViewHolder, GradesAdapter.GradeViewHolder> {

    private Activity activity;

    public GradesAdapter(List<? extends ExpandableGroup> groups, Context context) {
        super(groups);
        activity = (Activity) context;
    }

    @Override
    public SubjectViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_item, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public GradeViewHolder onCreateChildViewHolder(ViewGroup child, int viewType) {
        View view = LayoutInflater.from(child.getContext()).inflate(R.layout.grade_item, child, false);
        return new GradeViewHolder(view);
    }

    @Override
    public void onBindGroupViewHolder(SubjectViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.bind(group);
    }

    @Override
    public void onBindChildViewHolder(GradeViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        holder.bind((Grade) group.getItems().get(childIndex));
    }

    public class SubjectViewHolder extends GroupViewHolder {

        private TextView subjectName;

        public SubjectViewHolder(View itemView) {
            super(itemView);
            subjectName = itemView.findViewById(R.id.subject_text);

        }

        public void bind(ExpandableGroup group) {
            subjectName.setText(group.getTitle());
        }
    }

    public class GradeViewHolder extends ChildViewHolder {

        private TextView gradeValue;

        private TextView descriptionGrade;

        private TextView dateGrade;

        private Grade grade;

        public GradeViewHolder(final View itemView) {
            super(itemView);
            gradeValue = itemView.findViewById(R.id.grade_text);
            descriptionGrade = itemView.findViewById(R.id.description_grade_text);
            dateGrade = itemView.findViewById(R.id.grade_date_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GradesDialogFragment gradesDialogFragment = GradesDialogFragment.newInstance(grade);
                    gradesDialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                    gradesDialogFragment.show(activity.getFragmentManager(), grade.toString());
                }
            });
        }

        public void bind(Grade grade) {
            this.grade = grade;
            gradeValue.setText(grade.getValue());
            gradeValue.setBackgroundResource(grade.getValueColor());
            dateGrade.setText(grade.getDate());

            if (grade.getDescription().equals("") || grade.getDescription() == null) {
                if (!grade.getSymbol().equals("")) {
                    descriptionGrade.setText(grade.getSymbol());
                } else {
                    descriptionGrade.setText(R.string.noDescription_text);
                }
            } else {
                descriptionGrade.setText(grade.getDescription());
            }

        }
    }
}

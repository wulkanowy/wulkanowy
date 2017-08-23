package io.github.wulkanowy.activity.dashboard.grades;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.List;

import io.github.wulkanowy.R;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class GradesAdapter extends ExpandableRecyclerViewAdapter<GradesAdapter.SubjectViewHolder, GradesAdapter.GradeViewHolder> {

    public GradesAdapter(List<? extends ExpandableGroup> groups) {
        super(groups);
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
        holder.bind((GradeItem) group.getItems().get(childIndex));
    }

    public class SubjectViewHolder extends GroupViewHolder {

        private TextView textView;

        private ImageView imageView;

        public SubjectViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.subject_text);
            imageView = (ImageView) itemView.findViewById(R.id.group_indicator);
        }

        public void bind(ExpandableGroup group) {
            textView.setText(group.getTitle());
        }

        @Override
        public void expand() {
            RotateAnimation rotate =
                    new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(300);
            rotate.setFillAfter(true);
            imageView.setAnimation(rotate);
        }

        @Override
        public void collapse() {
            RotateAnimation rotate =
                    new RotateAnimation(180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(300);
            rotate.setFillAfter(true);
            imageView.setAnimation(rotate);
        }
    }

    public class GradeViewHolder extends ChildViewHolder {

        private TextView textView;

        public GradeViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.grade_text);
        }

        public void bind(GradeItem grade) {
            textView.setText(grade.getValue());
            textView.setBackgroundResource(grade.getValueColor());
        }
    }
}

package io.github.wulkanowy.activity.dashboard.marks;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.github.wulkanowy.R;
import io.github.wulkanowy.api.grades.Grade;

public class SubjectGradeAdapter extends ExpandableRecyclerAdapter<SubjectGradeAdapter.SubjectViewHolder, SubjectGradeAdapter.GradeViewHolder> {

    private List<? extends ParentListItem> parentItemList;
    private Context context;

    public SubjectGradeAdapter(List<? extends ParentListItem> parentItemList, Context context) {
        super(parentItemList);
        this.context = context;
    }

    @Override
    public SubjectViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
        View subjectGroup = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.subject_item, viewGroup, false);
        return new SubjectViewHolder(subjectGroup);
    }

    @Override
    public GradeViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
        View gradeGroup = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grade_item, viewGroup, false);
        return new GradeViewHolder(gradeGroup);
    }

    @Override
    public void onBindParentViewHolder(SubjectViewHolder parentViewHolder, int position, ParentListItem parentListItem) {
        SubjectParent subjectParent = (SubjectParent) parentListItem;
        parentViewHolder.textView.setText(subjectParent.getName());
    }

    @Override
    public void onBindChildViewHolder(GradeViewHolder gradeViewHolder, int position, Object childListItem) {
        Grade grade = (Grade) childListItem;
        gradeViewHolder.tv_android.setText(grade.getValue());
        Picasso.with(context)
                .load(R.drawable.sample_0)
                .resize(240, 120)
                .noFade()
                .into(gradeViewHolder.img_android);
    }

    public class SubjectViewHolder extends ParentViewHolder {

        private TextView textView;

        public SubjectViewHolder(View view) {
            super(view);

            textView = (TextView) itemView.findViewById(R.id.subject_text);
        }
    }

    public class GradeViewHolder extends ChildViewHolder {

        private TextView tv_android;
        private ImageView img_android;

        public GradeViewHolder(View view) {
            super(view);

            tv_android = (TextView) view.findViewById(R.id.tv_android);
            img_android = (ImageView) view.findViewById(R.id.img_android);
        }
    }

}

package io.github.wulkanowy.activity.dashboard.grades;


import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class SubjectWithGrades extends ExpandableGroup<GradeChild> {

    public SubjectWithGrades(String title, List<GradeChild> items) {
        super(title, items);
    }
}

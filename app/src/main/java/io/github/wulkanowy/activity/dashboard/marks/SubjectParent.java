package io.github.wulkanowy.activity.dashboard.marks;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.List;

public class SubjectParent implements ParentListItem {

    private List<?> gradesChildren;
    private String name;

    @Override
    public List<?> getChildItemList() {
        return gradesChildren;
    }

    public SubjectParent setChildItemList(List<?> gradesChildren) {
        this.gradesChildren = gradesChildren;
        return this;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }

    public String getName() {
        return name;
    }

    public SubjectParent setName(String name) {
        this.name = name;
        return this;
    }
}

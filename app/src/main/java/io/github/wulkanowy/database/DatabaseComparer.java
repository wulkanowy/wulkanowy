package io.github.wulkanowy.database;


import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.grades.Grade;

public class DatabaseComparer {

    public static List<Grade> compareGradesLists(List<Grade> newLists, List<Grade> oldLists) {

        List<Grade> addedOrUpdatedGradesList = new ArrayList<>(CollectionUtils.removeAll(newLists, oldLists));
        newLists = new ArrayList<>(CollectionUtils.removeAll(newLists, addedOrUpdatedGradesList));

        for (Grade grade : addedOrUpdatedGradesList) {
            grade.setIsNew(true);
            newLists.add(grade);
        }

        return newLists;
    }
}

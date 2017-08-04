package io.github.wulkanowy.database;


import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.grades.Grade;

public class DatabaseComparer {

    public static List<Grade> compareGradesLists(List<Grade> newLists, List<Grade> oldLists) {

        List<Grade> updatedLists = new ArrayList<>();

        for (Grade newGrade : newLists) {

            boolean isNewGrade = true;

            for (Grade oldGrade : oldLists) {
                if (newGrade.toString().equals(oldGrade.toString())) {

                    updatedLists.add(newGrade);
                    isNewGrade = false;
                }
            }
            if (isNewGrade) {
                newGrade.setIsNew(true);
                updatedLists.add(newGrade);
            }
        }
        return updatedLists;
    }
}

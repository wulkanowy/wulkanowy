package io.github.wulkanowy.utilities;


import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.dao.Subject;

public abstract class ConversionVulcanObject {

    public static List<Subject> subjectsToSubjectEntities(List<io.github.wulkanowy.api.grades.Subject> subjectList) {

        List<Subject> subjectEntityList = new ArrayList<>();

        for (io.github.wulkanowy.api.grades.Subject subject : subjectList) {
            Subject subjectEntity = new Subject()
                    .setName(subject.getName())
                    .setPredictedRating(subject.getPredictedRating())
                    .setFinalRating(subject.getFinalRating());
            subjectEntityList.add(subjectEntity);
        }

        return subjectEntityList;
    }
}

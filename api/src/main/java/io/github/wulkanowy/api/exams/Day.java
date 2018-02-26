package io.github.wulkanowy.api.exams;

import java.util.ArrayList;
import java.util.List;

public class Day extends io.github.wulkanowy.api.generic.Day {

    private List<Exam> examList = new ArrayList<>();

    public List<Exam> getExamList() {
        return examList;
    }

    public Day addExam(Exam exam) {
        this.examList.add(exam);
        return this;
    }
}

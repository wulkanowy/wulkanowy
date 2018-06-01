package io.github.wulkanowy.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.wulkanowy.R;
import io.github.wulkanowy.data.db.dao.entities.Grade;
import io.github.wulkanowy.data.db.dao.entities.Subject;

public final class GradeUtils {

    private final static Pattern validGradePattern = Pattern.compile("^(\\++|-|--|=)?[0-6](\\++|-|--|=)?$");
    private final static Pattern simpleGradeValuePattern = Pattern.compile("([0-6])");

    private GradeUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static float calculateGradesAverage(List<Grade> gradeList) {

        float counter = 0f;
        float denominator = 0f;

        for (Grade grade : gradeList) {
            int weight = getIntegerForWeightOfGrade(grade.getWeight());
            float value = getMathematicalValueOfString(grade.getValue());

            if (value != -1f) {
                counter += value * weight;
                denominator += weight;
            }
        }

        if (counter == 0f) {
            return -1f;
        }
        return counter / denominator;
    }

    public static float calculateSubjectsAverage(List<Subject> subjectList, boolean usePredicted) {
        return calculateSubjectsAverage(subjectList, usePredicted, false);
    }

    public static float calculateDetailedSubjectsAverage(List<Subject> subjectList) {
        return calculateSubjectsAverage(subjectList, false, true);
    }

    private static float calculateSubjectsAverage(List<Subject> subjectList, boolean usePredicted,
                                                  boolean useSubjectsAverages) {
        float counter = 0f;
        float denominator = 0f;

        for (Subject subject : subjectList) {
            float value;

            if (useSubjectsAverages) {
                value = calculateGradesAverage(subject.getGradeList());
            } else {
                value = getMathematicalValueOfSubjectGrade(usePredicted ? subject.getPredictedRating()
                        : subject.getFinalRating());
            }

            if (value != -1f) {
                counter += value;
                denominator++;
            }
        }

        if (counter == 0) {
            return -1f;
        }
        return counter / denominator;
    }

    private static float getMathematicalValueOfSubjectGrade(String subjectGrade) {
        float valueOfSubjectGrade;

        if (!validGradePattern.matcher(subjectGrade).matches()) {
            switch (subjectGrade) {
                case "celujący":
                    valueOfSubjectGrade = 6f;
                    break;
                case "bardzo dobry":
                    valueOfSubjectGrade = 5f;
                    break;
                case "dobry":
                    valueOfSubjectGrade = 4f;
                    break;
                case "dostateczny":
                    valueOfSubjectGrade = 3f;
                    break;
                case "dopuszczający":
                    valueOfSubjectGrade = 2f;
                    break;
                case "niedostateczny":
                    valueOfSubjectGrade = 1f;
                    break;
                default:
                    valueOfSubjectGrade = -1f;
            }
        } else {
            valueOfSubjectGrade = getMathematicalValueOfString(subjectGrade);
        }
        return valueOfSubjectGrade;
    }

    private static float getMathematicalValueOfString(String value) {
        if (value.matches("[-|+|=]{0,2}[0-6]")
                || value.matches("[0-6][-|+|=]{0,2}")) {
            if (value.matches("[-][0-6]")
                    || value.matches("[0-6][-]")) {
                String replacedValue = value.replaceAll("[-]", "");
                return Float.valueOf(replacedValue) - 0.33f;
            } else if (value.matches("[+][0-6]")
                    || value.matches("[0-6][+]")) {
                String replacedValue = value.replaceAll("[+]", "");
                return Float.valueOf((replacedValue)) + 0.33f;
            } else if (value.matches("[-|=]{1,2}[0-6]")
                    || value.matches("[0-6][-|=]{1,2}")) {
                String replacedValue = value.replaceAll("[-|=]{1,2}", "");
                return Float.valueOf((replacedValue)) - 0.5f;
            } else {
                return Float.valueOf(value);
            }
        } else {
            return -1;
        }
    }

    private static int getIntegerForWeightOfGrade(String weightOfGrade) {
        return Integer.valueOf(weightOfGrade.substring(0, weightOfGrade.length() - 3));
    }

    public static int getValueColor(String value) {
        Matcher m1 = validGradePattern.matcher(value);
        if (!m1.find()) {
            return R.color.default_grade;
        }

        Matcher m2 = simpleGradeValuePattern.matcher(m1.group());
        if (!m2.find()) {
            return R.color.default_grade;
        }

        switch (Integer.parseInt(m2.group())) {
            case 6:
                return R.color.six_grade;
            case 5:
                return R.color.five_grade;
            case 4:
                return R.color.four_grade;
            case 3:
                return R.color.three_grade;
            case 2:
                return R.color.two_grade;
            case 1:
                return R.color.one_grade;
            default:
                return R.color.default_grade;
        }
    }
}

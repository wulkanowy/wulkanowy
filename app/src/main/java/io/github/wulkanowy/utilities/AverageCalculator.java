package io.github.wulkanowy.utilities;

import android.util.Log;

import java.util.List;

import io.github.wulkanowy.dao.entities.Grade;

public abstract class AverageCalculator {

    public static float calculate(List<Grade> gradeList) {

        float counter = 0f;
        float denominator = 0f;

        for (Grade grade : gradeList) {
            int integerWeight = getIntegerForWeightOfGrade(grade.getWeight());
            float floatValue = getMathematicalValueOfGrade(grade.getValue());

            if (floatValue != -1.0f) {
                counter += floatValue * integerWeight;
                denominator += integerWeight;
            }
        }

        if (counter > 0 && denominator == 0) {
            Log.wtf("AverageCalculatorWulkanowy", "DIVISION BY ZERO!!!");
            return -10f;
        } else if (counter == 0) {
            return -1f;
        }

        return counter / denominator;
    }

    private static float getMathematicalValueOfGrade(String valueOfGrade) {
        if (valueOfGrade.matches("[-|+|=]{1,2}[0-6]")) {
            if (valueOfGrade.matches("[-][^-]*")) {
                String replacedValue = valueOfGrade.replaceAll("[-]", "");
                return Float.valueOf(replacedValue) - 0.25f;
            } else if (valueOfGrade.matches("[+].*")) {
                String replacedValue = valueOfGrade.replaceAll("[+]", "");
                return Float.valueOf((replacedValue)) + 0.25f;
            } else if (valueOfGrade.matches("[-|=]{1,2}.*")) {
                String replacedValue = valueOfGrade.replaceAll("[-|=]{1,2}", "");
                return Float.valueOf((replacedValue)) - 0.5f;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    private static int getIntegerForWeightOfGrade(String weightOfGrade) {
        return Integer.valueOf(weightOfGrade.substring(0, weightOfGrade.length() - 3));
    }
}

package io.github.wulkanowy.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.R;
import io.github.wulkanowy.data.db.dao.entities.Grade;

import static org.junit.Assert.assertEquals;

public class GradeUtilsTest {

    @Test
    public void averageTest() {
        List<Grade> gradeList = new ArrayList<>();
        gradeList.add(new Grade().setValue("np.").setWeight("1,00"));
        gradeList.add(new Grade().setValue("-5").setWeight("10,00"));
        gradeList.add(new Grade().setValue("--5").setWeight("10,00"));
        gradeList.add(new Grade().setValue("=5").setWeight("10,00"));
        gradeList.add(new Grade().setValue("+5").setWeight("10,00"));
        gradeList.add(new Grade().setValue("5").setWeight("10,00"));

        List<Grade> gradeList1 = new ArrayList<>();
        gradeList1.add(new Grade().setValue("np.").setWeight("1,00"));
        gradeList1.add(new Grade().setValue("5-").setWeight("10,00"));
        gradeList1.add(new Grade().setValue("5--").setWeight("10,00"));
        gradeList1.add(new Grade().setValue("5=").setWeight("10,00"));
        gradeList1.add(new Grade().setValue("5+").setWeight("10,00"));
        gradeList1.add(new Grade().setValue("5").setWeight("10,00"));

        assertEquals(4.8f, GradeUtils.calculateWeightedAverage(gradeList), 0.0f);
        assertEquals(4.8f, GradeUtils.calculateWeightedAverage(gradeList1), 0.0f);
    }

    @Test
    public void errorAverageTest() {
        List<Grade> gradeList = new ArrayList<>();
        gradeList.add(new Grade().setValue("np.").setWeight("1,00"));

        assertEquals(-1f, GradeUtils.calculateWeightedAverage(gradeList), 0.0f);
    }

    @Test
    public void getGradeValueTest() {
        assertEquals(1f, GradeUtils.getGradeValue("niedostateczny"), 0);
        assertEquals(1f, GradeUtils.getGradeValue("1"), 0);
        assertEquals(1f, GradeUtils.getGradeValue("1+"), 0);
        assertEquals(2f, GradeUtils.getGradeValue("2--"), 0);
    }

    @Test
    public void getVerbalGradeValueTest() {
        assertEquals(6, GradeUtils.getVerbalGradeValue("celujący"), 0);
        assertEquals(-1, GradeUtils.getVerbalGradeValue("wzorowe"), 0);
    }

    @Test
    public void getWeightedGradeValueTest() {
        assertEquals(1.67f, GradeUtils.getWeightedGradeValue("2-"), 0);
        assertEquals(1.5f, GradeUtils.getWeightedGradeValue("2--"), 0);
        assertEquals(2.33f, GradeUtils.getWeightedGradeValue("2+"), 0);
    }

    @Test
    public void getWeightValueTest() {
        assertEquals(1, GradeUtils.getWeightValue("1.00"));
        assertEquals(10, GradeUtils.getWeightValue("10.50"));
    }

    @Test
    public void getValueColorTest() {
        assertEquals(R.color.six_grade, GradeUtils.getValueColor("-6"));
        assertEquals(R.color.five_grade, GradeUtils.getValueColor("--5"));
        assertEquals(R.color.four_grade, GradeUtils.getValueColor("=4"));
        assertEquals(R.color.three_grade, GradeUtils.getValueColor("3-"));
        assertEquals(R.color.two_grade, GradeUtils.getValueColor("2--"));
        assertEquals(R.color.two_grade, GradeUtils.getValueColor("2="));
        assertEquals(R.color.one_grade, GradeUtils.getValueColor("1+"));
        assertEquals(R.color.one_grade, GradeUtils.getValueColor("+1"));
        assertEquals(R.color.default_grade, GradeUtils.getValueColor("6 (.XI)"));
        assertEquals(R.color.default_grade, GradeUtils.getValueColor("Np"));
        assertEquals(R.color.default_grade, GradeUtils.getValueColor("7"));
        assertEquals(R.color.default_grade, GradeUtils.getValueColor(""));
    }
}

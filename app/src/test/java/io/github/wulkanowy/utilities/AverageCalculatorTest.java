package io.github.wulkanowy.utilities;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.dao.entities.Grade;

public class AverageCalculatorTest extends AverageCalculator {

    private List<Grade> gradeList = new ArrayList<>();

    @Before
    public void setUp() {
        gradeList.clear();
        gradeList.add(new Grade().setValue("-5").setWeight("10,00"));
        gradeList.add(new Grade().setValue("--5").setWeight("10,00"));
        gradeList.add(new Grade().setValue("=5").setWeight("10,00"));
        gradeList.add(new Grade().setValue("+5").setWeight("10,00"));
    }

    @Test
    public void averageTest() {
        Assert.assertEquals(4.75f, AverageCalculator.calculate(gradeList), 0.0f);
    }
}

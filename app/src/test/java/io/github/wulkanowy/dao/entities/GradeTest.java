package io.github.wulkanowy.dao.entities;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.github.wulkanowy.R;

public class GradeTest {

    private Grade grade1;

    private Grade grade2;

    private Grade grade3;

    @Before
    public void prepareObjects() {
        grade1 = new Grade()
                .setSubject("Matematyka")
                .setValue("6")
                .setColor("FFFFFF")
                .setSymbol("S")
                .setDescription("Lorem ipsum")
                .setWeight("10")
                .setDate("01.01.2017")
                .setTeacher("Andrzej")
                .setSemester("777");

        grade2 = new Grade()
                .setSubject("Religia")
                .setValue("20")
                .setColor("FFFFFF")
                .setSymbol("S")
                .setDescription("Wolna wola")
                .setWeight("10")
                .setDate("01.01.2017")
                .setTeacher("Andrzej")
                .setSemester("777");

        grade3 = grade1;
    }

    @Test
    public void selectiveGetValueColorTest() {
        Assert.assertEquals(R.color.default_grade, grade2.getValueColor());
        Assert.assertEquals(R.color.six_grade, grade1.getValueColor());
    }

    @Test
    public void equalsTest() {
        Assert.assertEquals(true, grade1.equals(grade3));
        Assert.assertEquals(false, grade1.equals(grade2));

        Assert.assertEquals(grade1.hashCode(), grade3.hashCode());
    }
}

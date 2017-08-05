package io.github.wulkanowy.api.grades;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import java.util.List;

import io.github.wulkanowy.api.FixtureHelper;
import io.github.wulkanowy.api.Semester;
import io.github.wulkanowy.api.StudentAndParent;

public class GradesListTest {

    private String fixtureFileName = "OcenyWszystkie-filled.html";

    private GradesList gradesList;

    @Before
    public void setUp() throws Exception {
        String input = FixtureHelper.getAsString(getClass().getResourceAsStream(fixtureFileName));
        Document gradesPageDocument = Jsoup.parse(input);

        StudentAndParent snp = Mockito.mock(StudentAndParent.class);
        PowerMockito.whenNew(StudentAndParent.class).withAnyArguments().thenReturn(snp);
        Mockito.when(snp.getLocationID()).thenReturn("symbol");
        Mockito.when(snp.getID()).thenReturn("123456");
        Mockito.when(snp.getGradesPageUrl()).thenReturn("http://example.null");
        Mockito.when(snp.getSemesters()).thenCallRealMethod();
        Mockito.when(snp.getSemesters(Mockito.any(Document.class))).thenCallRealMethod();
        Mockito.when(snp.getCurrentSemester(Mockito.anyListOf(Semester.class)))
                .thenCallRealMethod();

        Grades grades = Mockito.mock(Grades.class);
        PowerMockito.whenNew(Grades.class).withAnyArguments().thenReturn(grades);
        Mockito.when(grades.getGradesPageDocument(Mockito.anyString()))
                .thenReturn(gradesPageDocument);

        gradesList = new GradesList(grades, snp);
    }

    @Test
    public void getAllTest() throws Exception {
        List<Grade> grades = gradesList.getAll();
        Assert.assertEquals(4, grades.size()); // 2 items are skipped

        Grade grade1 = grades.get(0);
        Assert.assertEquals("Zajęcia z wychowawcą", grade1.getSubject());
        Assert.assertEquals("5", grade1.getValue());
        Assert.assertEquals("000000", grade1.getColor());
        Assert.assertEquals("A1", grade1.getSymbol());
        Assert.assertEquals("Dzień Kobiet w naszej klasie", grade1.getDescription());
        Assert.assertEquals("1,00", grade1.getWeight());
        Assert.assertEquals("21.03.2017", grade1.getDate());
        Assert.assertEquals("Patryk Maciejewski", grade1.getTeacher());
        Assert.assertEquals("7654321", grade1.getSemester());

        Grade grade2 = grades.get(3);
        Assert.assertEquals("Język angielski", grade2.getSubject());
        Assert.assertEquals("5", grade2.getValue());
        Assert.assertEquals("1289F7", grade2.getColor());
        Assert.assertEquals("BW3", grade2.getSymbol());
        Assert.assertEquals("Writing", grade2.getDescription());
        Assert.assertEquals("3,00", grade2.getWeight());
        Assert.assertEquals("02.06.2017", grade2.getDate());
        Assert.assertEquals("Oliwia Woźniak", grade2.getTeacher());
        Assert.assertEquals("7654321", grade2.getSemester());
    }
}

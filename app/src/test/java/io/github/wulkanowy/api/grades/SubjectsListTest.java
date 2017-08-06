package io.github.wulkanowy.api.grades;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import io.github.wulkanowy.api.FixtureHelper;
import io.github.wulkanowy.api.Semester;
import io.github.wulkanowy.api.StudentAndParent;

public class SubjectsListTest {

    private String fixtureStdFileName = "OcenyWszystkie-subjects.html";

    private String fixtureAverageFileName = "OcenyWszystkie-subjects-average.html";

    public SubjectsList getSetUpSubjectsList(String fixtureFileName) throws Exception {
        String input = FixtureHelper.getAsString(getClass().getResourceAsStream(fixtureFileName));
        Document gradesPageDocument = Jsoup.parse(input);

        StudentAndParent snp = Mockito.mock(StudentAndParent.class);
        Mockito.when(snp.getSnPPageDocument(Mockito.anyString()))
                .thenReturn(gradesPageDocument);
        Mockito.when(snp.getSemesters(Mockito.any(Document.class))).thenCallRealMethod();
        Mockito.when(snp.getCurrentSemester(Mockito.anyListOf(Semester.class)))
                .thenCallRealMethod();

        return new SubjectsList(snp);
    }

    @Test
    public void getAllStdTest() throws Exception {
        List<Subject> list = getSetUpSubjectsList(fixtureStdFileName).getAll();

        Assert.assertEquals(5, list.size());

        Subject subject0 = list.get(0);
        Assert.assertEquals("Zachowanie", subject0.getName());
        Assert.assertEquals("bardzo dobre", subject0.getPredictedRating());
        Assert.assertEquals("bardzo dobre", subject0.getFinalRating());

        Subject subject1 = list.get(1);
        Assert.assertEquals("Praktyka zawodowa", subject1.getName());
        Assert.assertEquals("-", subject1.getPredictedRating());
        Assert.assertEquals("celujący", subject1.getFinalRating());

        Subject subject2 = list.get(2);
        Assert.assertEquals("Metodologia programowania", subject2.getName());
        Assert.assertEquals("bardzo dobry", subject2.getPredictedRating());
        Assert.assertEquals("celujący", subject2.getFinalRating());

        Subject subject3 = list.get(3);
        Assert.assertEquals("Podstawy przedsiębiorczości", subject3.getName());
        Assert.assertEquals("3/4", subject3.getPredictedRating());
        Assert.assertEquals("dostateczny", subject3.getFinalRating());

        Subject subject4 = list.get(4);
        Assert.assertEquals("Wychowanie do życia w rodzinie", subject4.getName());
        Assert.assertEquals("-", subject4.getPredictedRating());
        Assert.assertEquals("-", subject4.getFinalRating());
    }

    @Test
    public void getAllAverageTest() throws Exception {
        List<Subject> list = getSetUpSubjectsList(fixtureAverageFileName).getAll();

        Assert.assertEquals(5, list.size());

        Subject subject0 = list.get(0);
        Assert.assertEquals("Zachowanie", subject0.getName());
        Assert.assertEquals("bardzo dobre", subject0.getPredictedRating());
        Assert.assertEquals("bardzo dobre", subject0.getFinalRating());

        Subject subject1 = list.get(1);
        Assert.assertEquals("Język polski", subject1.getName());
        Assert.assertEquals("-", subject1.getPredictedRating());
        Assert.assertEquals("dobry", subject1.getFinalRating());

        Subject subject2 = list.get(2);
        Assert.assertEquals("Wychowanie fizyczne", subject2.getName());
        Assert.assertEquals("bardzo dobry", subject2.getPredictedRating());
        Assert.assertEquals("celujący", subject2.getFinalRating());

        Subject subject3 = list.get(3);
        Assert.assertEquals("Język angielski", subject3.getName());
        Assert.assertEquals("4/5", subject3.getPredictedRating());
        Assert.assertEquals("bardzo dobry", subject3.getFinalRating());

        Subject subject4 = list.get(4);
        Assert.assertEquals("Wiedza o społeczeństwie", subject4.getName());
        Assert.assertEquals("-", subject4.getPredictedRating());
        Assert.assertEquals("-", subject4.getFinalRating());
    }
}

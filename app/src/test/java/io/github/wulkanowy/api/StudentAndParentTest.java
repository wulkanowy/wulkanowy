package io.github.wulkanowy.api;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.login.LoginErrorException;

public class StudentAndParentTest {

    private String fixtureFileName = "OcenyWszystkie-semester.html";

    private StudentAndParent snp;

    @Before
    public void setUp() throws Exception {
        String input = FixtureHelper.getAsString(getClass().getResourceAsStream(fixtureFileName));
        Document gradesPageDocument = Jsoup.parse(input);

        snp = Mockito.mock(StudentAndParent.class);

        Mockito.when(snp.getSnPPageDocument(Mockito.anyString())).thenReturn(gradesPageDocument);
        Mockito.when(snp.getExtractedIdFromUrl(Mockito.anyString())).thenCallRealMethod();
        Mockito.when(snp.getBaseUrl()).thenReturn("https://uonetplus-opiekun.vulcan.net.pl/{symbol}/{ID}/");
        Mockito.when(snp.getSymbol()).thenReturn("symbol");
        Mockito.when(snp.getId()).thenReturn("123456");
        Mockito.when(snp.getSemesters()).thenCallRealMethod();
        Mockito.when(snp.getGradesPageUrl()).thenReturn("http://wulkanowy.null");
        Mockito.when(snp.getSemesters(Mockito.any(Document.class))).thenCallRealMethod();
        Mockito.when(snp.getCurrentSemester(Mockito.<Semester>anyList())).thenCallRealMethod();
    }

    @Test
    public void getUonetPlusOpiekunUrlWithIdTest() throws Exception {
        Mockito.when(snp.getUonetPlusOpiekunUrl()).thenCallRealMethod();
        Assert.assertEquals("https://uonetplus-opiekun.vulcan.net.pl/symbol/123456/",
                snp.getUonetPlusOpiekunUrl());
    }

    @Test
    public void getUonetPlusOpiekunUrlWithoutIdTest() throws Exception {
        String input = FixtureHelper.getAsString(getClass().getResourceAsStream("Start.html"));
        Document startPageDocument = Jsoup.parse(input);

        Mockito.when(snp.getPageByUrl(Mockito.anyString())).thenReturn(startPageDocument);
        Mockito.when(snp.getStartPageUrl()).thenReturn("http://wulkan.io");
        Mockito.when(snp.getId()).thenReturn(null);

        Mockito.when(snp.getUonetPlusOpiekunUrl()).thenCallRealMethod();
        Assert.assertEquals("https://uonetplus-opiekun.vulcan.net.pl/symbol/534213/Start/Index/",
                snp.getUonetPlusOpiekunUrl());
    }

    @Test
    public void getExtractedIDStandardTest() throws Exception {
        Assert.assertEquals("123456", snp.getExtractedIdFromUrl("https://uonetplus-opiekun"
                + ".vulcan.net.pl/powiat/123456/Start/Index/"));
    }

    @Test
    public void getExtractedIDDemoTest() throws Exception {
        Assert.assertEquals("demo12345", snp.getExtractedIdFromUrl("https://uonetplus-opiekundemo"
                + ".vulcan.net.pl/demoupowiat/demo12345/Start/Index/"));
    }

    @Test(expected = LoginErrorException.class)
    public void getExtractedIDNotLoggedTest() throws Exception {
        Assert.assertEquals("123", snp.getExtractedIdFromUrl("https://uonetplus"
                + ".vulcan.net.pl/powiat/"));
    }

    @Test
    public void getSemestersTest() throws Exception {
        List<Semester> semesters = snp.getSemesters();

        Assert.assertEquals(2, semesters.size());

        Assert.assertEquals("1", semesters.get(0).getId());
        Assert.assertEquals("1234", semesters.get(0).getNumber());
        Assert.assertFalse(semesters.get(0).isCurrent());

        Assert.assertEquals("2", semesters.get(1).getId());
        Assert.assertEquals("1235", semesters.get(1).getNumber());
        Assert.assertTrue(semesters.get(1).isCurrent());
    }

    @Test
    public void getCurrentSemesterTest() throws Exception {
        List<Semester> semesters = new ArrayList<>();
        semesters.add(new Semester().setNumber("1500100900").setId("1").setCurrent(false));
        semesters.add(new Semester().setNumber("1500100901").setId("2").setCurrent(true));

        Assert.assertTrue(snp.getCurrentSemester(semesters).isCurrent());
        Assert.assertEquals("2", snp.getCurrentSemester(semesters).getId());
        Assert.assertEquals("1500100901", snp.getCurrentSemester(semesters).getNumber());
    }

    @Test
    public void getCurrentSemesterFromEmptyTest() throws Exception {
        List<Semester> semesters = new ArrayList<>();

        Assert.assertNull(snp.getCurrentSemester(semesters));
    }
}

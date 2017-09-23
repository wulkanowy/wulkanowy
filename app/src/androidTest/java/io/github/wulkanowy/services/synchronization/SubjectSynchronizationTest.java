package io.github.wulkanowy.services.synchronization;


import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.greenrobot.greendao.database.Database;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.grades.SubjectsList;
import io.github.wulkanowy.dao.entities.DaoMaster;
import io.github.wulkanowy.dao.entities.DaoSession;
import io.github.wulkanowy.dao.entities.Subject;
import io.github.wulkanowy.services.LoginSession;
import io.github.wulkanowy.services.synchronisation.SubjectsSynchronisation;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(AndroidJUnit4.class)
public class SubjectSynchronizationTest {

    private static DaoSession daoSession;

    @BeforeClass
    public static void setUpClass() {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(InstrumentationRegistry.getTargetContext(), "wulkanowyTest-database");
        Database database = devOpenHelper.getWritableDb();

        daoSession = new DaoMaster(database).newSession();
    }

    @Before
    public void setUp() {
        daoSession.getSubjectDao().deleteAll();
        daoSession.clear();
    }

    @Test
    public void syncSubjectTest() throws Exception {
        List<io.github.wulkanowy.api.grades.Subject> subjectList = new ArrayList<>();
        subjectList.add(new io.github.wulkanowy.api.grades.Subject()
                .setName("Matematyka")
                .setFinalRating("5")
                .setPredictedRating("4"));

        SubjectsList subjectsListApi = mock(SubjectsList.class);
        doReturn(subjectList).when(subjectsListApi).getAll();

        Vulcan vulcan = mock(Vulcan.class);
        doReturn(subjectsListApi).when(vulcan).getSubjectsList();

        LoginSession loginSession = mock(LoginSession.class);
        doReturn(vulcan).when(loginSession).getVulcan();
        doReturn(2L).when(loginSession).getUserId();
        doReturn(daoSession).when(loginSession).getDaoSession();

        SubjectsSynchronisation subjectsSynchronisation = new SubjectsSynchronisation();
        subjectsSynchronisation.sync(loginSession);

        Subject subject = daoSession.getSubjectDao().load(1L);

        Assert.assertNotNull(subject);
        Assert.assertEquals(2, subject.getUserId().longValue());
        Assert.assertEquals("Matematyka", subject.getName());
        Assert.assertEquals("5", subject.getFinalRating());
        Assert.assertEquals("4", subject.getPredictedRating());

    }

    @AfterClass
    public static void cleanUp() {
        daoSession.getSubjectDao().deleteAll();
        daoSession.clear();
    }
}

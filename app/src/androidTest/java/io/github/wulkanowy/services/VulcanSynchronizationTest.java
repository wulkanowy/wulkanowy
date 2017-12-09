package io.github.wulkanowy.services;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import io.github.wulkanowy.dao.entities.DaoSession;

@RunWith(AndroidJUnit4.class)
public class VulcanSynchronizationTest {

    private Context context;

    private DaoSession daoSession;

    @Before
    public void setUp() {
        this.context = InstrumentationRegistry.getTargetContext();
        this.daoSession = Mockito.mock(DaoSession.class);
    }

    @Test
    public void syncNoLoginSessionSubjectTest() {
        VulcanSynchronization vulcanSynchronization = new VulcanSynchronization(context, daoSession, new LoginSession());
        Assert.assertFalse(vulcanSynchronization.syncSubjectsAndGrades());
    }

    @Test
    public void syncNoLoginSessionGradeTest() {
        VulcanSynchronization vulcanSynchronization = new VulcanSynchronization(context, daoSession, new LoginSession());
        Assert.assertFalse(vulcanSynchronization.syncGrades());
    }
}

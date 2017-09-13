package io.github.wulkanowy.services.synchronisation;

import android.util.Log;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import io.github.wulkanowy.api.grades.SubjectsList;
import io.github.wulkanowy.api.login.LoginErrorException;
import io.github.wulkanowy.dao.DaoSession;
import io.github.wulkanowy.dao.Subject;
import io.github.wulkanowy.dao.SubjectDao;
import io.github.wulkanowy.services.jobs.VulcanSync;
import io.github.wulkanowy.utilities.ConversionVulcanObject;

public class SubjectsSynchronisation {

    public void sync(VulcanSynchronisation vulcanSynchronisation, DaoSession daoSession) throws IOException,
            ParseException, LoginErrorException {

        SubjectsList subjectsList = new SubjectsList(vulcanSynchronisation.getStudentAndParent());

        List<Subject> subjectEntitiesList = ConversionVulcanObject.subjectsToSubjectEntities(subjectsList.getAll());

        SubjectDao subjectDao = daoSession.getSubjectDao();
        subjectDao.insertInTx(subjectEntitiesList);

        Log.d(VulcanSync.DEBUG_TAG, "Synchronization subjects (amount = " + String.valueOf(subjectEntitiesList.size() + ")"));
    }
}

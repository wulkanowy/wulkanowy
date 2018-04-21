package io.github.wulkanowy.data.sync.subjects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.VulcanException;
import io.github.wulkanowy.data.Repository;
import io.github.wulkanowy.data.db.dao.entities.DaoSession;
import io.github.wulkanowy.data.db.dao.entities.DiaryDao;
import io.github.wulkanowy.data.db.dao.entities.Semester;
import io.github.wulkanowy.data.db.dao.entities.SemesterDao;
import io.github.wulkanowy.data.db.dao.entities.StudentDao;
import io.github.wulkanowy.data.db.dao.entities.Subject;
import io.github.wulkanowy.data.db.shared.SharedPrefContract;
import io.github.wulkanowy.data.sync.SyncContract;
import io.github.wulkanowy.utils.DataObjectConverter;
import io.github.wulkanowy.utils.LogUtils;

@Singleton
public class SubjectSync implements SyncContract {

    private final DaoSession daoSession;

    private final Vulcan vulcan;

    private final SharedPrefContract sharedPref;

    private Long userId;

    private long semesterId;

    @Inject
    SubjectSync(DaoSession daoSession, SharedPrefContract sharedPref, Vulcan vulcan) {
        this.daoSession = daoSession;
        this.sharedPref = sharedPref;
        this.vulcan = vulcan;
    }

    @Override
    public void sync() throws VulcanException, IOException {
        userId = sharedPref.getCurrentUserId();
        semesterId = getCurrentSemesterId();

        List<Subject> lastList = getUpdatedList(getSubjectsFromNet());

        daoSession.getSubjectDao().deleteInTx(getSubjectsFromDb());
        daoSession.getSubjectDao().insertInTx(lastList);

        LogUtils.debug("Synchronization subjects (amount = " + lastList.size() + ")");
    }

    /**
     * FIXME: duplicated {@link Repository#getCurrentSemesterId()} ()}
     */
    private long getCurrentSemesterId() {
        long symbolId = daoSession.getDiaryDao().queryBuilder().where(
                DiaryDao.Properties.StudentId.eq(userId),
                DiaryDao.Properties.Current.eq(true)
        ).unique().getId();

        long studentId = daoSession.getStudentDao().queryBuilder().where(
                StudentDao.Properties.SymbolId.eq(symbolId),
                StudentDao.Properties.Current.eq(true)
        ).unique().getId();

        long diaryId = daoSession.getDiaryDao().queryBuilder().where(
                DiaryDao.Properties.StudentId.eq(studentId),
                DiaryDao.Properties.Current.eq(true)
        ).unique().getId();

        return daoSession.getSemesterDao().queryBuilder().where(
                SemesterDao.Properties.DiaryId.eq(diaryId),
                SemesterDao.Properties.Current.eq(true)
        ).unique().getId();
    }

    private List<Subject> getSubjectsFromNet() throws VulcanException, IOException {
        return DataObjectConverter.subjectsToSubjectEntities(
                vulcan.getSubjectsList().getAll(String.valueOf(semesterId)), semesterId);
    }

    private List<Subject> getSubjectsFromDb() {
        Semester semester = daoSession.getSemesterDao().load(semesterId);
        semester.resetSubjectList();
        return semester.getSubjectList();
    }

    private List<Subject> getUpdatedList(List<Subject> subjectsFromNet) {
        List<Subject> updatedList = new ArrayList<>();

        for (Subject subject : subjectsFromNet) {
            subject.setSemesterId(semesterId);
            updatedList.add(subject);
        }
        return updatedList;
    }
}

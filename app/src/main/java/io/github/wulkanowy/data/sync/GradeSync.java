package io.github.wulkanowy.data.sync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.VulcanException;
import io.github.wulkanowy.data.db.dao.entities.DaoSession;
import io.github.wulkanowy.data.db.dao.entities.Grade;
import io.github.wulkanowy.data.db.dao.entities.Semester;
import io.github.wulkanowy.data.db.dao.entities.SubjectDao;
import io.github.wulkanowy.utils.DataObjectConverter;
import io.github.wulkanowy.utils.EntitiesCompare;

@Singleton
public class GradeSync {

    private final DaoSession daoSession;

    private final Vulcan vulcan;

    private long semesterId;

    private static final Logger logger = LoggerFactory.getLogger(GradeSync.class);

    @Inject
    GradeSync(DaoSession daoSession, Vulcan vulcan) {
        this.daoSession = daoSession;
        this.vulcan = vulcan;
    }

    public void sync(long semesterId) throws IOException, VulcanException {
        this.semesterId = semesterId;

        Semester semester = daoSession.getSemesterDao().load(semesterId);
        resetSemesterRelations(semester);

        List<Grade> lastList = getUpdatedList(getComparedList(semester));

        daoSession.getGradeDao().deleteInTx(semester.getGradeList());
        daoSession.getGradeDao().insertInTx(lastList);

        logger.debug("Grades synchronization complete ({})", lastList.size());
    }

    private void resetSemesterRelations(Semester semester) {
        semester.resetSubjectList();
        semester.resetGradeList();
    }

    private List<Grade> getUpdatedList(List<Grade> comparedList) {
        List<Grade> updatedList = new ArrayList<>();

        for (Grade grade : comparedList) {
            grade.setSemesterId(semesterId);
            grade.setSubjectId(getSubjectId(grade.getSubject()));
            updatedList.add(grade);
        }

        return updatedList;
    }

    private List<Grade> getComparedList(Semester semester) throws IOException, VulcanException {
        List<Grade> gradesFromNet = DataObjectConverter.gradesToGradeEntities(
                vulcan.getGradesList().getAll(semester.getValue()), semesterId);

        List<Grade> gradesFromDb = semester.getGradeList();

        return EntitiesCompare.compareGradeList(gradesFromNet, gradesFromDb);
    }

    private Long getSubjectId(String subjectName) {
        return daoSession.getSubjectDao().queryBuilder().where(
                SubjectDao.Properties.Name.eq(subjectName),
                SubjectDao.Properties.SemesterId.eq(semesterId)
        ).build().uniqueOrThrow().getId();
    }
}

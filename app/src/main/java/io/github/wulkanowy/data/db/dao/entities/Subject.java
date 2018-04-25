package io.github.wulkanowy.data.db.dao.entities;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

@Entity(
        nameInDb = "Subjects",
        active = true
)
public class Subject {

    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "semester_id")
    private Long semesterId;

    @Property(nameInDb = "name")
    private String name;

    @Property(nameInDb = "predicted_rating")
    private String predictedRating;

    @Property(nameInDb = "final_rating")
    private String finalRating;

    @ToMany(referencedJoinProperty = "subjectId")
    private List<Grade> gradeList;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1644932788)
    private transient SubjectDao myDao;

    @Generated(hash = 1817932538)
    public Subject(Long id, Long semesterId, String name, String predictedRating,
                   String finalRating) {
        this.id = id;
        this.semesterId = semesterId;
        this.name = name;
        this.predictedRating = predictedRating;
        this.finalRating = finalRating;
    }

    @Generated(hash = 1617906264)
    public Subject() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSemesterId() {
        return this.semesterId;
    }

    public Subject setSemesterId(Long semesterId) {
        this.semesterId = semesterId;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Subject setName(String name) {
        this.name = name;
        return this;
    }

    public String getPredictedRating() {
        return this.predictedRating;
    }

    public Subject setPredictedRating(String predictedRating) {
        this.predictedRating = predictedRating;
        return this;
    }

    public String getFinalRating() {
        return this.finalRating;
    }

    public Subject setFinalRating(String finalRating) {
        this.finalRating = finalRating;
        return this;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1358847893)
    public List<Grade> getGradeList() {
        if (gradeList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            GradeDao targetDao = daoSession.getGradeDao();
            List<Grade> gradeListNew = targetDao._querySubject_GradeList(id);
            synchronized (this) {
                if (gradeList == null) {
                    gradeList = gradeListNew;
                }
            }
        }
        return gradeList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1939990047)
    public synchronized void resetGradeList() {
        gradeList = null;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 937984622)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getSubjectDao() : null;
    }
}

package io.github.wulkanowy.data.db.dao.entities;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

@Entity(
        nameInDb = "Days",
        active = true,
        indexes = {@Index(value = "weekId,date", unique = true)}
)
public class Day {

    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "week_id")
    private Long weekId;

    @Property(nameInDb = "date")
    private String date = "";

    @Property(nameInDb = "day_name")
    private String dayName = "";

    @ToMany(referencedJoinProperty = "dayId")
    private List<Exam> exams;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 312167767)
    private transient DayDao myDao;

    @Generated(hash = 322427092)
    public Day(Long id, Long weekId, String date, String dayName) {
        this.id = id;
        this.weekId = weekId;
        this.date = date;
        this.dayName = dayName;
    }

    @Generated(hash = 866989762)
    public Day() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWeekId() {
        return this.weekId;
    }

    public void setWeekId(Long weekId) {
        this.weekId = weekId;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDayName() {
        return this.dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1231531946)
    public List<Exam> getExams() {
        if (exams == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ExamDao targetDao = daoSession.getExamDao();
            List<Exam> examsNew = targetDao._queryDay_Exams(id);
            synchronized (this) {
                if (exams == null) {
                    exams = examsNew;
                }
            }
        }
        return exams;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 841969952)
    public synchronized void resetExams() {
        exams = null;
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

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1409317752)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDayDao() : null;
    }
}

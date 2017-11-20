package io.github.wulkanowy.dao.entities;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

@Entity(
        nameInDb = "Weeks",
        active = true
)
public class Week {

    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "START_DAY_DATE")
    private String startDayDate = "";

    @ToMany(referencedJoinProperty = "weekId")
    private List<Day> days;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1019310398)
    private transient WeekDao myDao;

    @Generated(hash = 62778756)
    public Week(Long id, String startDayDate) {
        this.id = id;
        this.startDayDate = startDayDate;
    }

    @Generated(hash = 2135529658)
    public Week() {
    }

    public String getStartDayDate() {
        return startDayDate;
    }

    public Week setStartDayDate(String startDayDate) {
        this.startDayDate = startDayDate;
        return this;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 422888579)
    public List<Day> getDays() {
        if (days == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DayDao targetDao = daoSession.getDayDao();
            List<Day> daysNew = targetDao._queryWeek_Days(id);
            synchronized (this) {
                if (days == null) {
                    days = daysNew;
                }
            }
        }
        return days;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 222078010)
    public synchronized void resetDays() {
        days = null;
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
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 665278367)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getWeekDao() : null;
    }
}

package io.github.wulkanowy.data.db.dao.entities;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

import java.io.Serializable;

@Entity(
        nameInDb = "TimetableLessons",
        active = true,
        indexes = {@Index(value = "diaryId,date,number,startTime,endTime", unique = true)}
)
public class TimetableLesson implements Serializable {

    private static final long serialVersionUID = 42L;

    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "diary_id")
    private Long diaryId;

    @Property(nameInDb = "number")
    private int number = 0;

    @Property(nameInDb = "subject")
    private String subject = "";

    @Property(nameInDb = "teacher")
    private String teacher = "";

    @Property(nameInDb = "room")
    private String room = "";

    @Property(nameInDb = "description")
    private String description = "";

    @Property(nameInDb = "group")
    private String group = "";

    @Property(nameInDb = "start_time")
    private String startTime = "";

    @Property(nameInDb = "end_time")
    private String endTime = "";

    @Property(nameInDb = "date")
    private String date = "";

    @Property(nameInDb = "free_day_name")
    private String freeDayName = "";

    @Property(nameInDb = "empty")
    private boolean empty = false;

    @Property(nameInDb = "division_into_groups")
    private boolean divisionIntoGroups = false;

    @Property(nameInDb = "planning")
    private boolean planning = false;

    @Property(nameInDb = "realized")
    private boolean realized = false;

    @Property(nameInDb = "moved_canceled")
    private boolean movedOrCanceled = false;

    @Property(nameInDb = "new_moved_in_canceled")
    private boolean newMovedInOrChanged = false;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1119360138)
    private transient TimetableLessonDao myDao;

    @Generated(hash = 1497054786)
    public TimetableLesson(Long id, Long diaryId, int number, String subject, String teacher, String room,
            String description, String group, String startTime, String endTime, String date, String freeDayName,
            boolean empty, boolean divisionIntoGroups, boolean planning, boolean realized,
            boolean movedOrCanceled, boolean newMovedInOrChanged) {
        this.id = id;
        this.diaryId = diaryId;
        this.number = number;
        this.subject = subject;
        this.teacher = teacher;
        this.room = room;
        this.description = description;
        this.group = group;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
        this.freeDayName = freeDayName;
        this.empty = empty;
        this.divisionIntoGroups = divisionIntoGroups;
        this.planning = planning;
        this.realized = realized;
        this.movedOrCanceled = movedOrCanceled;
        this.newMovedInOrChanged = newMovedInOrChanged;
    }

    @Generated(hash = 1878030142)
    public TimetableLesson() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDiaryId() {
        return this.diaryId;
    }

    public void setDiaryId(Long diaryId) {
        this.diaryId = diaryId;
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTeacher() {
        return this.teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getRoom() {
        return this.room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGroup() {
        return this.group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean getEmpty() {
        return this.empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public boolean getDivisionIntoGroups() {
        return this.divisionIntoGroups;
    }

    public void setDivisionIntoGroups(boolean divisionIntoGroups) {
        this.divisionIntoGroups = divisionIntoGroups;
    }

    public boolean getPlanning() {
        return this.planning;
    }

    public void setPlanning(boolean planning) {
        this.planning = planning;
    }

    public boolean getRealized() {
        return this.realized;
    }

    public void setRealized(boolean realized) {
        this.realized = realized;
    }

    public boolean getMovedOrCanceled() {
        return this.movedOrCanceled;
    }

    public void setMovedOrCanceled(boolean movedOrCanceled) {
        this.movedOrCanceled = movedOrCanceled;
    }

    public boolean getNewMovedInOrChanged() {
        return this.newMovedInOrChanged;
    }

    public void setNewMovedInOrChanged(boolean newMovedInOrChanged) {
        this.newMovedInOrChanged = newMovedInOrChanged;
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

    public String getFreeDayName() {
        return this.freeDayName;
    }

    public void setFreeDayName(String freeDayName) {
        this.freeDayName = freeDayName;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1885258429)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getTimetableLessonDao() : null;
    }
}

package io.github.wulkanowy.data.db.dao;

import org.threeten.bp.LocalDate;

import java.util.List;

import io.github.wulkanowy.data.db.dao.entities.AttendanceLesson;
import io.github.wulkanowy.data.db.dao.entities.Diary;
import io.github.wulkanowy.data.db.dao.entities.Grade;
import io.github.wulkanowy.data.db.dao.entities.Subject;
import io.github.wulkanowy.data.db.dao.entities.Symbol;
import io.github.wulkanowy.data.db.dao.entities.TimetableLesson;
import io.github.wulkanowy.data.db.dao.entities.Week;

public interface DbContract {

    Week getWeek(String date);

    Week getWeek(long diaryId, String date);

    Diary getDiary();

    List<Subject> getSubjectList(int semesterName);

    List<Grade> getNewGrades(int semesterName);

    long getCurrentSchoolId();

    long getCurrentStudentId();

    long getCurrentSymbolId();

    Symbol getCurrentSymbol();

    long getCurrentDiaryId();

    long getSemesterId(int name);

    long getCurrentSemesterId();

    int getCurrentSemesterName();

    void recreateDatabase();

    List<AttendanceLesson> getAttendance(LocalDate start);

    List<TimetableLesson> getTimetable(LocalDate start);
}

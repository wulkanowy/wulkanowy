package io.github.wulkanowy.api;

import java.io.IOException;

import io.github.wulkanowy.api.attendance.AttendanceStatistics;
import io.github.wulkanowy.api.attendance.AttendanceTable;
import io.github.wulkanowy.api.grades.GradesList;
import io.github.wulkanowy.api.grades.SubjectsList;
import io.github.wulkanowy.api.login.AccountPermissionException;
import io.github.wulkanowy.api.login.BadCredentialsException;
import io.github.wulkanowy.api.login.Login;
import io.github.wulkanowy.api.login.LoginErrorException;
import io.github.wulkanowy.api.notes.AchievementsList;
import io.github.wulkanowy.api.notes.NotesList;
import io.github.wulkanowy.api.school.SchoolInfo;
import io.github.wulkanowy.api.school.TeachersInfo;
import io.github.wulkanowy.api.timetable.Timetable;
import io.github.wulkanowy.api.user.BasicInformation;
import io.github.wulkanowy.api.user.FamilyInformation;

public class Vulcan {

    private String id;

    private String symbol;

    private Cookies cookies = new Cookies();

    private StudentAndParent snp;

    public Vulcan(String email, String password, String symbol)
            throws BadCredentialsException, AccountPermissionException, LoginErrorException {
        Login login = new Login(cookies);
        login.login(email, password, symbol);

        this.symbol = symbol;
        this.cookies = login.getCookiesObject();
    }

    public Vulcan(String email, String password, String symbol, String id)
            throws BadCredentialsException, AccountPermissionException, LoginErrorException {
        this(email, password, symbol);
        this.id = id;
    }

    public StudentAndParent getStudentAndParent() throws IOException, LoginErrorException {
        if (null != snp) {
            return snp;
        }

        if (null != id) {
            return new StudentAndParent(cookies, symbol, id);
        }

        return new StudentAndParent(cookies, symbol);
    }

    public AttendanceStatistics getAttendanceStatistics() throws IOException, LoginErrorException {
        return new AttendanceStatistics(getStudentAndParent());
    }

    public AttendanceTable getAttendanceTable() throws IOException, LoginErrorException {
        return new AttendanceTable(getStudentAndParent());
    }

    public GradesList getGradesList() throws IOException, LoginErrorException {
        return new GradesList(getStudentAndParent());
    }

    public SubjectsList getSubjectsList() throws IOException, LoginErrorException {
        return new SubjectsList(getStudentAndParent());
    }

    public AchievementsList getAchievementsList() throws IOException, LoginErrorException {
        return new AchievementsList(getStudentAndParent());
    }

    public NotesList getNotesList() throws IOException, LoginErrorException {
        return new NotesList(getStudentAndParent());
    }

    public SchoolInfo getSchoolInfo() throws IOException, LoginErrorException {
        return new SchoolInfo(getStudentAndParent());
    }

    public TeachersInfo getTeachersInfo() throws IOException, LoginErrorException {
        return new TeachersInfo(getStudentAndParent());
    }

    public Timetable getTimetable() throws IOException, LoginErrorException {
        return new Timetable(getStudentAndParent());
    }

    public BasicInformation getBasicInformation() throws IOException, LoginErrorException {
        return new BasicInformation(getStudentAndParent());
    }

    public FamilyInformation getFamilyInformation() throws IOException, LoginErrorException {
        return new FamilyInformation(getStudentAndParent());
    }
}

package io.github.wulkanowy.api;

import java.io.IOException;

import io.github.wulkanowy.api.attendance.Statistics;
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
import io.github.wulkanowy.api.timetable.Table;
import io.github.wulkanowy.api.user.BasicInformation;
import io.github.wulkanowy.api.user.FamilyInformation;

public class Vulcan {

    private String symbol;

    private Cookies cookies = new Cookies();

    public Vulcan(String email, String password, String symbol)
            throws BadCredentialsException, AccountPermissionException, LoginErrorException {
        Login login = new Login(cookies);
        login.login(email, password, symbol);

        this.symbol = symbol;
        this.cookies = login.getCookiesObject();
    }

    public StudentAndParent getStudentAndParent() throws IOException, LoginErrorException {
        return new StudentAndParent(cookies, symbol);
    }

    public Statistics getAttendanceStatistics() throws IOException, LoginErrorException {
        return new Statistics(getStudentAndParent());
    }

    public io.github.wulkanowy.api.attendance.Table getAttendance() throws IOException,
            LoginErrorException {
        return new io.github.wulkanowy.api.attendance.Table(getStudentAndParent());
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

    public Table getTimetable() throws IOException, LoginErrorException {
        return new Table(getStudentAndParent());
    }

    public BasicInformation getBasicInformation() throws IOException, LoginErrorException {
        return new BasicInformation(getStudentAndParent());
    }

    public FamilyInformation getFamilyInformation() throws IOException, LoginErrorException {
        return new FamilyInformation(getStudentAndParent());
    }
}

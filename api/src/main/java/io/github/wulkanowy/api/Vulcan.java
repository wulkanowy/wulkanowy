package io.github.wulkanowy.api;

import java.io.IOException;

import io.github.wulkanowy.api.attendance.AttendanceStatistics;
import io.github.wulkanowy.api.attendance.AttendanceTable;
import io.github.wulkanowy.api.exams.ExamsWeek;
import io.github.wulkanowy.api.grades.GradesList;
import io.github.wulkanowy.api.grades.SubjectsList;
import io.github.wulkanowy.api.login.Login;
import io.github.wulkanowy.api.messages.Messages;
import io.github.wulkanowy.api.notes.AchievementsList;
import io.github.wulkanowy.api.notes.NotesList;
import io.github.wulkanowy.api.school.SchoolInfo;
import io.github.wulkanowy.api.school.TeachersInfo;
import io.github.wulkanowy.api.timetable.Timetable;
import io.github.wulkanowy.api.user.BasicInformation;
import io.github.wulkanowy.api.user.FamilyInformation;

public class Vulcan {

    private String protocolSchema = "https";

    private String logHost = "vulcan.net.pl";

    private String email;

    private String password;

    private String symbol;

    private String id;

    private SnP snp;

    private Client client;

    private Login login;

    public void login(String email, String password, String symbol, String id) throws IOException, VulcanException {
        this.email = email;
        this.password = password;
        this.symbol = symbol;
        this.id = id;

        setFullEndpointInfo(email);
    }

    private void setFullEndpointInfo(String info) {
        String[] creds = info.split("\\\\");

        email = info;

        if (creds.length >= 2) {
            String[] url = creds[0].split("://");

            protocolSchema = url[0];
            logHost = url[1];
            email = creds[2];
        }
    }

    private void performLogin() throws IOException, VulcanException {
        this.symbol = getLogin().login(email, password, symbol);
    }

    String getProtocolSchema() {
        return protocolSchema;
    }

    String getLogHost() {
        return logHost;
    }

    String getEmail() {
        return email;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getId() throws IOException, VulcanException {
        return getStudentAndParent().getId();
    }

    Client getClient() throws IOException, VulcanException {
        return getClient(false);
    }

    private Client getClient(boolean notLoginBefore) throws IOException, VulcanException {
        if (null != client) {
            if (null != login && !client.isLoggedIn() && !notLoginBefore) {
                performLogin();
            }
            return client;
        }

        setClient(new Client(getProtocolSchema(), getLogHost(), symbol));

        if (!notLoginBefore) {
            performLogin();
        }

        return client;
    }

    void setClient(Client client) {
        this.client = client;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    Login getLogin() throws IOException, VulcanException{
        if (null != login) {
            return login;
        }

        setLogin(new Login(getClient(true)));
        return login;
    }

    public SnP getStudentAndParent() throws IOException, VulcanException {
        if (null != this.snp) {
            return this.snp;
        }

        SnP snp = new StudentAndParent(getClient(), id);
        snp.storeContextCookies();
        this.snp = snp;

        return this.snp;
    }

    public AttendanceStatistics getAttendanceStatistics() throws IOException, VulcanException {
        return new AttendanceStatistics(getStudentAndParent());
    }

    public AttendanceTable getAttendanceTable() throws IOException, VulcanException {
        return new AttendanceTable(getStudentAndParent());
    }

    public ExamsWeek getExamsList() throws IOException, VulcanException {
        return new ExamsWeek(getStudentAndParent());
    }

    public GradesList getGradesList() throws IOException, VulcanException {
        return new GradesList(getStudentAndParent());
    }

    public SubjectsList getSubjectsList() throws IOException, VulcanException {
        return new SubjectsList(getStudentAndParent());
    }

    public AchievementsList getAchievementsList() throws IOException, VulcanException {
        return new AchievementsList(getStudentAndParent());
    }

    public NotesList getNotesList() throws IOException, VulcanException {
        return new NotesList(getStudentAndParent());
    }

    public SchoolInfo getSchoolInfo() throws IOException, VulcanException {
        return new SchoolInfo(getStudentAndParent());
    }

    public TeachersInfo getTeachersInfo() throws IOException, VulcanException {
        return new TeachersInfo(getStudentAndParent());
    }

    public Timetable getTimetable() throws IOException, VulcanException {
        return new Timetable(getStudentAndParent());
    }

    public BasicInformation getBasicInformation() throws IOException, VulcanException {
        return new BasicInformation(getStudentAndParent());
    }

    public FamilyInformation getFamilyInformation() throws IOException, VulcanException {
        return new FamilyInformation(getStudentAndParent());
    }

    public Messages getMessages() throws VulcanException, IOException {
        return new Messages(getClient());
    }
}

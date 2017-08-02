package io.github.wulkanowy.api.grades;

public class Grade {
    private int id;

    private int userID;

    private int subjectID;

    private String subject;

    private String value;

    private String color;

    private String description;

    private String weight;

    private String date;

    private String teacher;

    public int getId() {
        return id;
    }

    public Grade setId(int id) {
        this.id = id;

        return this;
    }

    public int getUserID() {
        return userID;
    }

    public Grade setUserID(int userID) {
        this.userID = userID;

        return this;
    }

    public int getSubjectID() {
        return subjectID;
    }

    public Grade setSubjectID(int subjectID) {
        this.subjectID = subjectID;

        return this;
    }

    public String getSubject() {
        return subject;
    }

    public Grade setSubject(String subject) {
        this.subject = subject;

        return this;
    }

    public String getValue() {
        return value;
    }

    public Grade setValue(String value) {
        this.value = value;

        return this;
    }

    public String getColor() {
        return color;
    }

    public Grade setColor(String color) {
        this.color = color;

        return this;
    }

    public String getDescription() {
        return description;
    }

    public Grade setDescription(String description) {
        this.description = description;

        return this;
    }

    public String getWeight() {
        return weight;
    }

    public Grade setWeight(String weight) {
        this.weight = weight;

        return this;
    }

    public String getDate() {
        return date;
    }

    public Grade setDate(String date) {
        this.date = date;

        return this;
    }

    public String getTeacher() {
        return teacher;
    }

    public Grade setTeacher(String teacher) {
        this.teacher = teacher;

        return this;
    }
}

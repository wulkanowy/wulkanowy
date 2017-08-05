package io.github.wulkanowy.api.grades;

public class Grade {
    private int id;

    private int userID;

    private int subjectID;

    private String subject;

    private String value;

    private String color;

    private String symbol;

    private String description;

    private String weight;

    private String date;

    private String teacher;

    private String semester;

    private boolean isNew;

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

    public String getSymbol() {
        return symbol;
    }

    public Grade setSymbol(String symbol) {
        this.symbol = symbol;

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

    public String getSemester() {
        return semester;
    }

    public Grade setSemester(String semester) {
        this.semester = semester;

        return this;
    }

    public boolean isNew() {
        return isNew;
    }

    public Grade setIsNew(boolean isNew) {
        this.isNew = isNew;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Grade grade = (Grade) o;

        if (subject != null ? !subject.equals(grade.subject) : grade.subject != null) return false;
        if (value != null ? !value.equals(grade.value) : grade.value != null) return false;
        if (color != null ? !color.equals(grade.color) : grade.color != null) return false;
        if (symbol != null ? !symbol.equals(grade.symbol) : grade.symbol != null) return false;
        if (description != null ? !description.equals(grade.description) : grade.description != null)
            return false;
        if (weight != null ? !weight.equals(grade.weight) : grade.weight != null) return false;
        if (date != null ? !date.equals(grade.date) : grade.date != null) return false;
        if (teacher != null ? !teacher.equals(grade.teacher) : grade.teacher != null) return false;
        return semester != null ? semester.equals(grade.semester) : grade.semester == null;

    }

    @Override
    public int hashCode() {
        int result = subject != null ? subject.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (color != null ? color.hashCode() : 0);
        result = 31 * result + (symbol != null ? symbol.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (weight != null ? weight.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (teacher != null ? teacher.hashCode() : 0);
        result = 31 * result + (semester != null ? semester.hashCode() : 0);
        return result;
    }
}

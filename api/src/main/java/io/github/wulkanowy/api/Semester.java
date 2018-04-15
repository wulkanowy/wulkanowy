package io.github.wulkanowy.api;

public class Semester implements ParamItem {

    private String id = "";

    private String studentId = "";

    private String name = "";

    private boolean current = false;

    public String getId() {
        return id;
    }

    public Semester setId(String id) {
        this.id = id;
        return this;
    }

    public String getStudentId() {
        return studentId;
    }

    @Override
    public Semester setStudentId(String studentId) {
        this.studentId = studentId;
        return this;
    }

    public String getName() {
        return name;
    }

    public Semester setName(String number) {
        this.name = number;
        return this;
    }

    public boolean isCurrent() {
        return current;
    }

    public Semester setCurrent(boolean current) {
        this.current = current;
        return this;
    }
}

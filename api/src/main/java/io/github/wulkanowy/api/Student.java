package io.github.wulkanowy.api;

public class Student implements ParamItem {

    private String id = "";

    private String studentId = "";

    private String name = "";

    private boolean isCurrent = false;

    public String getId() {
        return id;
    }

    public Student setId(String id) {
        this.id = id;
        return this;
    }

    public String getStudentId() {
        return studentId;
    }

    @Override
    public Student setStudentId(String studentId) {
        this.studentId = studentId;
        return this;
    }

    public String getName() {
        return name;
    }

    public Student setName(String name) {
        this.name = name;
        return this;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public Student setCurrent(boolean current) {
        isCurrent = current;
        return this;
    }
}

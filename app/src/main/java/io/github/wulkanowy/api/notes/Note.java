package io.github.wulkanowy.api.notes;

public class Note {

    String date;

    String teacher;

    String category;

    String content;

    public String getDate() {
        return date;
    }

    public Note setDate(String date) {
        this.date = date;
        return this;
    }

    public String getTeacher() {
        return teacher;
    }

    public Note setTeacher(String teacher) {
        this.teacher = teacher;
        return this;
    }

    public String getCategory() {
        return category;
    }

    public Note setCategory(String category) {
        this.category = category;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Note setContent(String content) {
        this.content = content;
        return this;
    }
}

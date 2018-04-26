package io.github.wulkanowy.data.sync.exams;

public interface ExamsSyncContract {

    void sync(long diaryId);

    void sync(long diaryId, String date);
}

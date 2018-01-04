package io.github.wulkanowy.db.shared;

public interface SharedAccess {

    long getCurrentUserId();

    void setCurrentUserId(long userId);
}

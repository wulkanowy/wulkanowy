package io.github.wulkanowy.data.db.shared;

public interface SharedPrefContract {

    long getCurrentUserId();

    void setCurrentUserId(long userId);

    int getStartupTab();

    boolean isServicesEnable();

    boolean isNotifyEnable();
}

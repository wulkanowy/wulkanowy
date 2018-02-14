package io.github.wulkanowy.data;

import javax.inject.Singleton;

import io.github.wulkanowy.data.db.resources.ResourcesContract;
import io.github.wulkanowy.data.sync.LoginSyncContract;

@Singleton
public interface RepositoryContract extends ResourcesContract, LoginSyncContract {

    long getCurrentUserId();
}

package io.github.wulkanowy.data;

import java.io.IOException;
import java.text.ParseException;

import javax.inject.Singleton;

import io.github.wulkanowy.api.login.NotLoggedInErrorException;
import io.github.wulkanowy.data.db.dao.entities.Account;
import io.github.wulkanowy.data.db.resources.ResourcesContract;
import io.github.wulkanowy.data.sync.login.LoginSyncContract;

@Singleton
public interface RepositoryContract extends ResourcesContract, LoginSyncContract {

    long getCurrentUserId();

    void syncGrades() throws NotLoggedInErrorException, IOException, ParseException;

    void syncSubjects() throws NotLoggedInErrorException, IOException, ParseException;

    void syncAll() throws NotLoggedInErrorException, IOException, ParseException;

    Account getCurrentUser();

    int getNumberOfNewGrades();
}

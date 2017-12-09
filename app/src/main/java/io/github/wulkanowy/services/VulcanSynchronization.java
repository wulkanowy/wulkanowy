package io.github.wulkanowy.services;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.login.AccountPermissionException;
import io.github.wulkanowy.api.login.BadCredentialsException;
import io.github.wulkanowy.api.login.LoginErrorException;
import io.github.wulkanowy.api.login.NotLoggedInErrorException;
import io.github.wulkanowy.api.login.VulcanOfflineException;
import io.github.wulkanowy.dao.entities.DaoSession;
import io.github.wulkanowy.security.CryptoException;
import io.github.wulkanowy.services.jobs.VulcanJobHelper;
import io.github.wulkanowy.services.synchronisation.CurrentAccountLogin;
import io.github.wulkanowy.services.synchronisation.FirstAccountLogin;
import io.github.wulkanowy.services.synchronisation.GradesSynchronisation;
import io.github.wulkanowy.services.synchronisation.SubjectsSynchronisation;

public class VulcanSynchronization {

    private final Context context;
    private final DaoSession daoSession;
    private LoginSession loginSession;

    public VulcanSynchronization(Context context, DaoSession daoSession, LoginSession loginSession) {
        this.context = context;
        this.daoSession = daoSession;
        this.loginSession = loginSession;
    }

    public void firstLoginSignInStep(String email, String password, String symbol)
            throws NotLoggedInErrorException, AccountPermissionException, IOException, CryptoException, VulcanOfflineException, BadCredentialsException {
        FirstAccountLogin firstAccountLogin = new FirstAccountLogin(context, daoSession, new Vulcan());
        loginSession = firstAccountLogin.login(email, password, symbol);
    }

    public void loginCurrentUser(Vulcan vulcan)
            throws CryptoException, BadCredentialsException, AccountPermissionException, LoginErrorException, IOException, VulcanOfflineException {
        CurrentAccountLogin currentAccountLogin = new CurrentAccountLogin(context, daoSession, vulcan);
        loginSession = currentAccountLogin.loginCurrentUser();
    }

    public boolean syncGrades() {
        if (loginSession != null) {
            GradesSynchronisation gradesSynchronisation = new GradesSynchronisation();
            try {
                gradesSynchronisation.sync(loginSession);
                return true;
            } catch (Exception e) {
                Log.e(VulcanJobHelper.DEBUG_TAG, "Synchronisation of grades failed", e);
                return false;
            }
        } else {
            Log.e(VulcanJobHelper.DEBUG_TAG, "Before synchronization, should login user to log",
                    new UnsupportedOperationException());
            return false;
        }
    }

    public boolean syncSubjectsAndGrades() {
        if (loginSession != null) {
            SubjectsSynchronisation subjectsSynchronisation = new SubjectsSynchronisation();
            try {
                subjectsSynchronisation.sync(loginSession);
                syncGrades();
                return true;
            } catch (Exception e) {
                Log.e(VulcanJobHelper.DEBUG_TAG, "Synchronisation of subjects failed", e);
                return false;
            }
        } else {
            Log.e(VulcanJobHelper.DEBUG_TAG, "Before synchronization, should login user to log",
                    new UnsupportedOperationException());
            return false;
        }
    }
}

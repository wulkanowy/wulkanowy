package io.github.wulkanowy.activity;

import android.app.Application;

import org.greenrobot.greendao.database.Database;

import io.github.wulkanowy.dao.DaoMaster;
import io.github.wulkanowy.dao.DaoSession;


public class WulkanowyApp extends Application {

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(this, "wulkanowy-database");
        Database database = devOpenHelper.getWritableDb();

        daoSession = new DaoMaster(database).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}

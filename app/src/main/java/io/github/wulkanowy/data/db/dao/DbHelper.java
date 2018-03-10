package io.github.wulkanowy.data.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.github.yuweiguocn.library.greendao.MigrationHelper;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.StandardDatabase;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.data.db.dao.entities.AccountDao;
import io.github.wulkanowy.data.db.dao.entities.DaoMaster;
import io.github.wulkanowy.data.db.dao.entities.GradeDao;
import io.github.wulkanowy.data.db.dao.entities.SubjectDao;
import io.github.wulkanowy.data.db.shared.SharedPrefContract;
import io.github.wulkanowy.di.annotations.ApplicationContext;
import io.github.wulkanowy.di.annotations.DatabaseInfo;
import io.github.wulkanowy.utils.LogUtils;

@Singleton
public class DbHelper extends DaoMaster.OpenHelper {

    private SharedPrefContract sharedPref;

    @Inject
    DbHelper(@ApplicationContext Context context, @DatabaseInfo String dbName,
             SharedPrefContract sharedPref) {
        super(context, dbName, null);
        this.sharedPref = sharedPref;
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        MigrationHelper.DEBUG = true;
        MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {
            @Override
            public void onCreateAllTables(Database db, boolean ifNotExists) {
                DaoMaster.createAllTables(db, ifNotExists);
            }
            @Override
            public void onDropAllTables(Database db, boolean ifExists) {
                DaoMaster.dropAllTables(db, ifExists);
            }
        }, AccountDao.class, SubjectDao.class, GradeDao.class);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        cleanUserData(new StandardDatabase(db), oldVersion, newVersion);
    }

    private void cleanUserData(Database database, int oldVersion, int newVersion) {
        LogUtils.info("Cleaning user data oldVersion=" + oldVersion + " newVersion=" + newVersion);
        DaoMaster.dropAllTables(database, true);
        onCreate(database);
        sharedPref.setCurrentUserId(0);
    }
}

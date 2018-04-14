package io.github.wulkanowy.data.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.StandardDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.data.db.dao.entities.DaoMaster;
import io.github.wulkanowy.data.db.dao.entities.DaoSession;
import io.github.wulkanowy.data.db.dao.migrations.Migration23;
import io.github.wulkanowy.data.db.shared.SharedPrefContract;
import io.github.wulkanowy.di.annotations.ApplicationContext;
import io.github.wulkanowy.di.annotations.DatabaseInfo;
import io.github.wulkanowy.utils.LogUtils;

@Singleton
public class DbHelper extends DaoMaster.OpenHelper {

    private final SharedPrefContract sharedPref;

    private final Vulcan vulcan;

    @Inject
    DbHelper(@ApplicationContext Context context, @DatabaseInfo String dbName,
             SharedPrefContract sharedPref, Vulcan vulcan) {
        super(context, dbName);
        this.sharedPref = sharedPref;
        this.vulcan = vulcan;
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Database database = new StandardDatabase(db);
        DaoMaster.dropAllTables(database, true);
        onCreate(database);
        sharedPref.setCurrentUserId(0);
        LogUtils.info("Cleaning user data oldVersion=" + oldVersion + " newVersion=" + newVersion);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        List<Migration> migrations = getMigrations();
        DaoSession daoSession = new DaoMaster(db).newSession();

        // Only run migrations past the old version
        for (Migration migration : migrations) {
            if (oldVersion < migration.getVersion()) {
                try {
                    migration.runMigration(db, sharedPref, daoSession, vulcan);
                } catch (Exception e) {
                    e.printStackTrace();
                    DaoMaster.dropAllTables(db, true);
                    sharedPref.setCurrentUserId(0);
                    break;
                }
            }
        }
    }

    private List<Migration> getMigrations() {
        List<Migration> migrations = new ArrayList<>();
        migrations.add(new Migration23());

        // Sorting just to be safe, in case other people add migrations in the wrong order.
        Comparator<Migration> migrationComparator = new Comparator<Migration>() {
            @Override
            public int compare(Migration m1, Migration m2) {
                return m1.getVersion().compareTo(m2.getVersion());
            }
        };
        Collections.sort(migrations, migrationComparator);

        return migrations;
    }

    public interface Migration {
        Integer getVersion();

        void runMigration(Database db, SharedPrefContract sharedPref, DaoSession daoSession, Vulcan vulcan) throws Exception;
    }
}

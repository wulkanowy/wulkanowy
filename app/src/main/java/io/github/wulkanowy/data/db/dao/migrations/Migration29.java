package io.github.wulkanowy.data.db.dao.migrations;

import android.database.Cursor;

import org.greenrobot.greendao.database.Database;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.data.db.dao.DbHelper;
import io.github.wulkanowy.data.db.shared.SharedPrefContract;

public class Migration29 implements DbHelper.Migration {

    @Override
    public Integer getVersion() {
        return 29;
    }

    @Override
    public void runMigration(final Database db, final SharedPrefContract sharedPref, final Vulcan vulcan) {
        createSchoolsTable(db);
        modifyStudents(db);
        insertSchool(db, getRealSchoolId(db));
    }

    private void createSchoolsTable(Database db) {
        db.execSQL("DROP TABLE IF EXISTS \"Schools\";");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"Schools\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"symbol_id\" INTEGER," + // 1: symbolId
                "\"current\" INTEGER NOT NULL ," + // 2: current
                "\"real_id\" TEXT," + // 3: realId
                "\"name\" TEXT);"); // 4: name
    }

    private void modifyStudents(Database db) {
        db.execSQL("ALTER TABLE Students ADD COLUMN school_id INTEGER");
        db.execSQL("UPDATE Students SET (school_id) = ('1')");
    }

    private String getRealSchoolId(Database db) {
        Cursor cursor = null;
        try {
            String id = "";
            cursor = db.rawQuery("SELECT school_id FROM Symbols WHERE _id=?", new String[]{"1"});

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                id = cursor.getString(cursor.getColumnIndex("school_id"));
            }

            return id;
        } finally {
            cursor.close();
        }
    }

    private void insertSchool(Database db, String realId) {
        db.execSQL("INSERT INTO Schools(symbol_id, current, real_id, name) VALUES(" +
                "\"1\"," +
                "\"1\"," +
                "\"" + realId + "\"," +
                "\"Uczeń\"" +
                ")");
    }
}

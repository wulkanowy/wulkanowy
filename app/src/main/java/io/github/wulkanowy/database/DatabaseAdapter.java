package io.github.wulkanowy.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DatabaseAdapter {

    private final String DATABASE_NAME = "accountdatabase.db";
    private final int DATABASE_VERSION = 2;
    public SQLiteDatabase database;
    private DatabaseHelper databaseHelper;
    public Context context;

    public DatabaseAdapter(Context context) {
        this.context = context;
    }

    public DatabaseAdapter open() {

        databaseHelper = new DatabaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);

        try {
            database = databaseHelper.getWritableDatabase();
        } catch (SQLException e) {
            database = databaseHelper.getReadableDatabase();
            Log.w(DatabaseHelper.DEBUG_TAG, "Database in read-only");
        }

        Log.d(DatabaseHelper.DEBUG_TAG, "Open database");

        return this;
    }

    public void close() {
        databaseHelper.close();

        Log.d(DatabaseHelper.DEBUG_TAG, "Close database");
    }

    public boolean checkExist(String nameTable) {

        open();

        Log.d(DatabaseHelper.DEBUG_TAG, "Check exist table");

        Cursor cursor;

        if(nameTable == null) {
            cursor = database.rawQuery("SELECT COUNT(*) FROM accounts", null);
        }
        else{
            cursor = database.rawQuery("SELECT COUNT(*) FROM " + nameTable, null);
        }

        if (cursor != null) {
            cursor.moveToFirst();

            int count = cursor.getInt(0);

            if (count > 0) {
                return true;
            }

            cursor.close();
            close();
        }

        return false;
    }
}

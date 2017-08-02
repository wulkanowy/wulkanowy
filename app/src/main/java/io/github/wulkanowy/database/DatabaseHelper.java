package io.github.wulkanowy.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    public final static String DEBUG_TAG = "SQLiteAccountsDatabase";
    private final String ACCOUNT_TABLE = "CREATE TABLE accounts( " +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT, " +
            "email TEXT," +
            "password TEXT, " +
            "county TEXT );";
    private final String SUBJECT_TABLE = "CREATE TABLE subjects( " +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT, " +
            "predictedRating1 TEXT, " +
            "finalRating1 TEXT, " +
            "predictedRating2 TEXT, " +
            "finalRating2 TEXT );";
    private final String GRADE_TABLE = "CREATE TABLE grades( " +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "userID INTEGER, " +
            "subjectID INTEGER, " +
            "value TEXT, " +
            "color TEXT, " +
            "description TEXT, " +
            "weight TEXT, " +
            "date TEXT, " +
            "teacher TEXT );";

    private final String DROP_TABLE = "DROP TABLE IF EXISTS";

    public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ACCOUNT_TABLE);
        db.execSQL(SUBJECT_TABLE);
        db.execSQL(GRADE_TABLE);
        Log.d(DEBUG_TAG, "Create database");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE + "accounts");
        db.execSQL(DROP_TABLE + "subjects");
        db.execSQL(DROP_TABLE + "grades");
        onCreate(db);
        Log.d(DEBUG_TAG, "Upgrade database");
    }
}

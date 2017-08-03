package io.github.wulkanowy.database.grades;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.grades.Grade;
import io.github.wulkanowy.database.DatabaseAdapter;
import io.github.wulkanowy.database.DatabaseHelper;

public class GradesDatabase extends DatabaseAdapter {

    private String idText = "id";
    private String userID = "userID";
    private String subjectID = "subjectID";
    private String value = "value";
    private String color = "color";
    private String decription = "description";
    private String weight = "weight";
    private String date = "date";
    private String teacher = "teacher";
    private String grades = "grades";

    public GradesDatabase(Context context) {
        super(context);
    }

    public long put(Grade grade) throws SQLException {

        ContentValues newGrade = new ContentValues();
        newGrade.put(userID, grade.getUserID());
        newGrade.put(subjectID, grade.getSubjectID());
        newGrade.put(value, grade.getValue());
        newGrade.put(color, grade.getColor());
        newGrade.put(decription, grade.getDescription());
        newGrade.put(weight, grade.getWeight());
        newGrade.put(date, grade.getDate());
        newGrade.put(teacher, grade.getTeacher());

        if (!database.isReadOnly()) {
            long newId = database.insertOrThrow(grades, null, newGrade);
            Log.d(DatabaseHelper.DEBUG_TAG, "Put grade " + newId + " into database");
            return newId;
        }

        Log.e(DatabaseHelper.DEBUG_TAG, "Attempt to write on read-only database");
        throw new SQLException("Attempt to write on read-only database");
    }

    public List<Long> put(List<Grade> gradeList) throws SQLException {

        List<Long> newIdList = new ArrayList<>();

        if (!database.isReadOnly()) {
            for (Grade grade : gradeList) {
                ContentValues newGrade = new ContentValues();
                newGrade.put(userID, grade.getUserID());
                newGrade.put(subjectID, grade.getSubjectID());
                newGrade.put(value, grade.getValue());
                newGrade.put(color, grade.getColor());
                newGrade.put(decription, grade.getDescription());
                newGrade.put(weight, grade.getWeight());
                newGrade.put(date, grade.getDate());
                newGrade.put(teacher, grade.getTeacher());

                long newId = database.insertOrThrow(grades, null, newGrade);
                Log.d(DatabaseHelper.DEBUG_TAG, "Put subject " + newId + " into database");
                newIdList.add(newId);
            }

            return newIdList;
        }
        Log.e(DatabaseHelper.DEBUG_TAG, "Attempt to write on read-only database");
        throw new SQLException("Attempt to write on read-only database");
    }

    public long update(Grade grade) throws SQLException {

        ContentValues updateGrade = new ContentValues();
        updateGrade.put(userID, grade.getUserID());
        updateGrade.put(subjectID, grade.getSubjectID());
        updateGrade.put(value, grade.getValue());
        updateGrade.put(color, grade.getColor());
        updateGrade.put(decription, grade.getDescription());
        updateGrade.put(weight, grade.getWeight());
        updateGrade.put(date, grade.getDate());
        updateGrade.put(teacher, grade.getTeacher());
        String args[] = {grade.getId() + ""};

        if (!database.isReadOnly()) {
            long updateId = database.update(grades, updateGrade, "id=?", args);
            Log.d(DatabaseHelper.DEBUG_TAG, "Update grade " + updateId + " into database");
            return updateId;
        }

        Log.e(DatabaseHelper.DEBUG_TAG, "Attempt to write on read-only database");
        throw new SQLException("Attempt to write on read-only database");

    }

    public Grade getGrade(long id) throws SQLException {

        Grade grade = new Grade();

        String[] columns = {idText, userID, subjectID, value, color, decription, weight, date, teacher};
        String args[] = {id + ""};

        try {
            Cursor cursor = database.query(grades, columns, "id=?", args, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                grade.setId(cursor.getInt(0));
                grade.setUserID(cursor.getInt(1));
                grade.setSubjectID(cursor.getInt(2));
                grade.setValue(cursor.getString(3));
                grade.setColor(cursor.getString(4));
                grade.setDescription(cursor.getString(5));
                grade.setWeight(cursor.getString(6));
                grade.setDate(cursor.getString(7));
                grade.setTeacher(cursor.getString(8));
                cursor.close();
            }
        } catch (SQLException e) {

            Log.e(DatabaseHelper.DEBUG_TAG, e.getMessage());
            throw e;
        } catch (CursorIndexOutOfBoundsException e) {

            Log.e(DatabaseHelper.DEBUG_TAG, e.getMessage());
            throw new SQLException(e.getMessage());
        }

        Log.d(DatabaseHelper.DEBUG_TAG, "Extract grade " + id + " from database");

        return grade;
    }

    public List<Grade> getSubjectGrades(long userId, long subjectId) throws SQLException {

        String whereExec = "SELECT * FROM " + grades + " WHERE " + userId + "=? AND " + subjectId + "=?";

        List<Grade> gradesList = new ArrayList<>();

        Cursor cursor = database.rawQuery(whereExec, new String[]{String.valueOf(userId), String.valueOf(subjectId)});

        while (cursor.moveToNext()) {
            Grade grade = new Grade();
            grade.setId(cursor.getInt(0));
            grade.setUserID(cursor.getInt(1));
            grade.setSubjectID(cursor.getInt(2));
            grade.setValue(cursor.getString(3));
            grade.setColor(cursor.getString(4));
            grade.setDescription(cursor.getString(5));
            grade.setWeight(cursor.getString(6));
            grade.setDate(cursor.getString(7));
            grade.setTeacher(cursor.getString(8));
            gradesList.add(grade);
        }

        cursor.close();
        return gradesList;

    }
}


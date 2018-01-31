package com.tilatina.campi.Utilities;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Derechos reservados tilatina.
 */
public class DBManager {
    private SQLiteDatabase database;
    private DBHandler dbHandler;

    public DBManager(Context context) {
        dbHandler = new DBHandler(context);
        database = dbHandler.getWritableDatabase();
        dbHandler.onCreate(database);
        database.close();
    }

    public void insertNovelty(String userId,
                       String elementId,
                       String ticketId,
                       double lat,
                       double lng,
                       String date,
                       String description,
                       String fileTitle,
                       String filePath) {
        String insertQuery = String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s) " +
                "VALUES (%s, %s, %s, %s, %s, '%s', '%s', '%s', '%s')",
                DBHandler.NOVELTIES_TABLE, DBHandler.NOVELTY_USER_ID,  DBHandler.NOVELTY_ELEMENT_ID,
                DBHandler.NOVELTY_TICKET_ID, DBHandler.NOVELTY_LAT, DBHandler.NOVELTY_LNG,
                DBHandler.NOVELTY_DATE, DBHandler.NOVELTY_DESCRIPTION,
                DBHandler.NOVELTY_FILE_TITLE, DBHandler.NOVELTY_FILE_PATH,
                userId, elementId, ticketId, lat, lng, date, description, fileTitle, filePath);
        database = dbHandler.getWritableDatabase();
        try {
            database.execSQL(insertQuery);
        } catch (Exception e) {
            e.printStackTrace();
        }

        database.close();
    }

    public void insertComments(String userId,
                        String ticketId,
                        double lat,
                        double lng,
                        String date,
                        String comment) {
        String insertQuery = String.format(
                "INSERT INTO %s (%s, %s, %s, %s, %s, %s)" +
                        "VALUES (%s, %s, %s, %s, '%s', '%s')",
                DBHandler.COMMENTS_TABLE, DBHandler.COMMENT_USER_ID, DBHandler.COMMENT_TICKET_ID,
                DBHandler.COMMENT_LAT, DBHandler.COMMENT_LNG, DBHandler.COMMENT_DATE,
                DBHandler.COMMENT_COMMENT,
                userId, ticketId, lat, lng, date, comment
        );

        database = dbHandler.getWritableDatabase();
        try {
            database.execSQL(insertQuery);
        } catch (Exception e) {
            e.printStackTrace();
        }

        database.close();
    }

    public void insertSigns(String userId,
                     String elementId,
                     String ticketId,
                     double lat,
                     double lng,
                     String date,
                     String clientName,
                     String fileTitle,
                     String filePath,
                     String rate){
        String insertQuery = String.format(
                "INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s) " +
                        "VALUES (%s, %s, %s, %s, %s, '%s', '%s', '%s', '%s', '%s')",
                DBHandler.SIGNS_TABLE, DBHandler.SIGN_USER_ID, DBHandler.SIGN_ELEMENT_ID,
                DBHandler.SIGN_TICKET_ID, DBHandler.SIGN_LAT, DBHandler.SIGN_LNG,
                DBHandler.SIGN_DATE, DBHandler.SIGN_CLIENT_NAME, DBHandler.SIGN_RATE,
                DBHandler.SIGN_FILE_TITLE, DBHandler.SIGN_FILE_PATH,
                userId, elementId, ticketId, lat, lng, date, clientName, fileTitle, filePath, rate
        );

        database = dbHandler.getWritableDatabase();
        try {
            database.execSQL(insertQuery);
        } catch (Exception e) {
            e.printStackTrace();
        }

        database.close();
    }

    public void insertArrival(String userId,
                       String elementId,
                       double lat,
                       double lng,
                       String date) {
        String insertQuery = String.format(
                "INSERT INTO %s (%s, %s, %s, %s, %s)" +
                        "VALUES (%s, %s, %s, %s, '%s')",
                DBHandler.ARRIVALS_TABLE, DBHandler.ARRIVAL_USER_ID, DBHandler.ARRIVAL_ELEMENT_ID,
                DBHandler.ARRIVAL_LAT, DBHandler.ARRIVAL_LNG, DBHandler.ARRIVAL_DATE,
                userId, elementId, lat, lng, date
        );

        database = dbHandler.getWritableDatabase();
        try {
            database.execSQL(insertQuery);
        } catch (Exception e) {
            e.printStackTrace();
        }

        database.close();
    }

    public void insertExit(String userId,
                       String elementId,
                       double lat,
                       double lng,
                       String date) {
        String insertQuery = String.format(
                "INSERT INTO %s (%s, %s, %s, %s, %s)" +
                        "VALUES (%s, %s, %s, %s, '%s')",
                DBHandler.EXITS_TABLE, DBHandler.EXIT_USER_ID, DBHandler.EXIT_ELEMENT_ID,
                DBHandler.EXIT_LAT, DBHandler.EXIT_LNG, DBHandler.EXIT_DATE,
                userId, elementId, lat, lng, date
        );

        database = dbHandler.getWritableDatabase();
        try {
            database.execSQL(insertQuery);
        } catch (Exception e) {
            e.printStackTrace();
        }

        database.close();
    }

    public Cursor getAllNovelties() {
        String selectQuery = String.format(
                "SELECT * FROM %s",
                DBHandler.NOVELTIES_TABLE
        );

        SQLiteDatabase db = dbHandler.getReadableDatabase();
        return db.rawQuery(selectQuery, null);
    }

    public Cursor getAllComments() {
        String selectQuery = String.format(
                "SELECT * FROM %s",
                DBHandler.COMMENTS_TABLE
        );

        SQLiteDatabase db = dbHandler.getReadableDatabase();
        return db.rawQuery(selectQuery, null);
    }

    public Cursor getAllSigns() {
        String selectQuery = String.format(
                "SELECT * FROM %s",
                DBHandler.SIGNS_TABLE
        );

        SQLiteDatabase db = dbHandler.getReadableDatabase();
        return db.rawQuery(selectQuery, null);
    }

    public Cursor getAllArrivals() {
        String selectQuery = String.format(
                "SELECT * FROM %s",
                DBHandler.ARRIVALS_TABLE
        );

        SQLiteDatabase db = dbHandler.getReadableDatabase();
        return db.rawQuery(selectQuery, null);
    }

    public Cursor getAllExits() {
        String selectQuery = String.format(
                "SELECT * FROM %s",
                DBHandler.EXITS_TABLE
        );

        SQLiteDatabase db = dbHandler.getReadableDatabase();
        return db.rawQuery(selectQuery, null);
    }

    public void deleteNovelty(String noveltyId) {
        String deleteQuery = String.format(
                "DELETE FROM %s WHERE %s = %s",
                DBHandler.NOVELTIES_TABLE, DBHandler.NOVELTY_ID,
                noveltyId
        );

        database = dbHandler.getWritableDatabase();
        database.execSQL(deleteQuery);
        database.close();
    }

    public void deleteComment(String commentId) {
        String deleteQuery = String.format(
                "DELETE FROM %s WHERE %s = %s",
                DBHandler.COMMENTS_TABLE, DBHandler.COMMENT_ID,
                commentId
        );

        database = dbHandler.getWritableDatabase();
        database.execSQL(deleteQuery);
        database.close();
    }

    public void deleteSign(String signId) {
        String deleteQuery = String.format(
                "DELETE FROM %s WHERE %s = %s",
                DBHandler.SIGNS_TABLE, DBHandler.SIGN_ID,
                signId
        );

        database = dbHandler.getWritableDatabase();
        database.execSQL(deleteQuery);
        database.close();
    }

    public void deleteArrival(String arrivalId) {
        String deleteQuery = String.format(
                "DELETE FROM %s WHERE %s = %s",
                DBHandler.ARRIVALS_TABLE, DBHandler.ARRIVAL_ID,
                arrivalId
        );

        database = dbHandler.getWritableDatabase();
        database.execSQL(deleteQuery);
        database.close();
    }

    public void deleteExit(String exitId) {
        String deleteQuery = String.format(
                "DELETE FROM %s WHERE %s = %s",
                DBHandler.EXITS_TABLE, DBHandler.EXIT_ID,
                exitId
        );

        database = dbHandler.getWritableDatabase();
        database.execSQL(deleteQuery);
        database.close();
    }

    public void deleteNovelties() {
        String deleteQuery = String.format(
                "DELETE FROM %s", DBHandler.NOVELTIES_TABLE);
        String restartQuery = String.format(
                "delete from sqlite_sequence where name='%s'", DBHandler.NOVELTIES_TABLE);

        database = dbHandler.getWritableDatabase();
        database.execSQL(deleteQuery);
        database.execSQL(restartQuery);
        database.close();
    }

    public void deleteComments() {
        String deleteQuery = String.format(
                "DELETE FROM %s", DBHandler.COMMENTS_TABLE);
        String restartQuery = String.format(
                "delete from sqlite_sequence where name='%s'", DBHandler.COMMENTS_TABLE);

        database = dbHandler.getWritableDatabase();
        database.execSQL(deleteQuery);
        database.execSQL(restartQuery);
        database.close();
    }

    public void deleteSigns() {
        String deleteQuery = String.format(
                "DELETE FROM %s", DBHandler.SIGNS_TABLE);
        String restartQuery = String.format(
                "delete from sqlite_sequence where name='%s'", DBHandler.SIGNS_TABLE);

        database = dbHandler.getWritableDatabase();
        database.execSQL(deleteQuery);
        database.execSQL(restartQuery);
        database.close();
    }

    public void deleteArrivals() {
        String deleteQuery = String.format(
                "DELETE FROM %s", DBHandler.ARRIVALS_TABLE);
        String restartQuery = String.format(
                "delete from sqlite_sequence where name='%s'", DBHandler.ARRIVALS_TABLE);

        database = dbHandler.getWritableDatabase();
        database.execSQL(deleteQuery);
        database.execSQL(restartQuery);
        database.close();
    }

    public void deleteExits() {
        String deleteQuery = String.format(
                "DELETE FROM %s", DBHandler.EXITS_TABLE);
        String restartQuery = String.format(
                "delete from sqlite_sequence where name='%s'", DBHandler.EXITS_TABLE);

        database = dbHandler.getWritableDatabase();
        database.execSQL(deleteQuery);
        database.execSQL(restartQuery);
        database.close();
    }
}

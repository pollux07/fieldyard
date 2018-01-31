package com.tilatina.campi.Utilities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Derechos reservado tilatina.
 */
class DBHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "campi_event.db";
    private static final int DB_VERSION = 8;

    static final String NOVELTIES_TABLE = "novelties";
    static final String NOVELTY_ID = "_id";
    static final String NOVELTY_USER_ID = "user_id";
    static final String NOVELTY_ELEMENT_ID = "element_id";
    static final String NOVELTY_TICKET_ID = "ticket_id";
    static final String NOVELTY_LAT = "lat";
    static final String NOVELTY_LNG = "lng";
    static final String NOVELTY_DATE = "date";
    static final String NOVELTY_DESCRIPTION = "description";
    static final String NOVELTY_FILE_TITLE = "file_title";
    static final String NOVELTY_FILE_PATH = "file_path";

    static final String COMMENTS_TABLE = "comments";
    static final String COMMENT_ID = "_id";
    static final String COMMENT_USER_ID = "user_id";
    static final String COMMENT_TICKET_ID = "ticket_id";
    static final String COMMENT_LAT = "lat";
    static final String COMMENT_LNG = "lng";
    static final String COMMENT_DATE = "date";
    static final String COMMENT_COMMENT = "comment";

    static final String SIGNS_TABLE = "signs";
    static final String SIGN_ID = "_id";
    static final String SIGN_USER_ID = "user_id";
    static final String SIGN_ELEMENT_ID = "element_id";
    static final String SIGN_TICKET_ID = "ticket_id";
    static final String SIGN_LAT = "lat";
    static final String SIGN_LNG = "lng";
    static final String SIGN_DATE = "date";
    static final String SIGN_CLIENT_NAME = "client_name";
    static final String SIGN_FILE_TITLE = "file_title";
    static final String SIGN_FILE_PATH = "file_path";
    static final String SIGN_RATE = "rate";

    static final String ARRIVALS_TABLE = "arrivals";
    static final String ARRIVAL_ID = "_id";
    static final String ARRIVAL_USER_ID = "user_id";
    static final String ARRIVAL_ELEMENT_ID = "element_id";
    static final String ARRIVAL_LAT = "lat";
    static final String ARRIVAL_LNG = "lng";
    static final String ARRIVAL_DATE = "date";

    static final String EXITS_TABLE = "exits";
    static final String EXIT_ID = "_id";
    static final String EXIT_USER_ID = "user_id";
    static final String EXIT_ELEMENT_ID = "element_id";
    static final String EXIT_LAT = "lat";
    static final String EXIT_LNG = "lng";
    static final String EXIT_DATE = "date";

    private static final String CREATE_TABLE_NOVELTIES = String.format(
            "CREATE TABLE IF NOT EXISTS %s (" +
                    "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL)",
            NOVELTIES_TABLE,
            NOVELTY_ID,
            NOVELTY_USER_ID,
            NOVELTY_ELEMENT_ID,
            NOVELTY_TICKET_ID,
            NOVELTY_LAT,
            NOVELTY_LNG,
            NOVELTY_DATE,
            NOVELTY_DESCRIPTION,
            NOVELTY_FILE_TITLE,
            NOVELTY_FILE_PATH
    );

    private static final String CREATE_TABLE_COMMENTS = String.format(
            "CREATE TABLE IF NOT EXISTS %s (" +
                    "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL)",
            COMMENTS_TABLE,
            COMMENT_ID,
            COMMENT_USER_ID,
            COMMENT_TICKET_ID,
            COMMENT_LAT,
            COMMENT_LNG,
            COMMENT_DATE,
            COMMENT_COMMENT
    );

    private static final String CREATE_TABLE_SIGNS = String.format(
            "CREATE TABLE IF NOT EXISTS %s (" +
                    "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NULL, " +
                    "%s TEXT NULL, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL) ",
            SIGNS_TABLE,
            SIGN_ID,
            SIGN_USER_ID,
            SIGN_ELEMENT_ID,
            SIGN_TICKET_ID,
            SIGN_LAT,
            SIGN_LNG,
            SIGN_DATE,
            SIGN_CLIENT_NAME,
            SIGN_RATE,
            SIGN_FILE_TITLE,
            SIGN_FILE_PATH
    );

    private static final String CREATE_TABLE_ARRIVALS = String.format(
            "CREATE TABLE IF NOT EXISTS %s (" +
                    "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL)",
            ARRIVALS_TABLE,
            ARRIVAL_ID,
            ARRIVAL_USER_ID,
            ARRIVAL_ELEMENT_ID,
            ARRIVAL_LAT,
            ARRIVAL_LNG,
            ARRIVAL_DATE
    );

    private static final String CREATE_TABLE_EXITS = String.format(
            "CREATE TABLE IF NOT EXISTS %s (" +
                    "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL, " +
                    "%s TEXT NOT NULL)",
            EXITS_TABLE,
            EXIT_ID,
            EXIT_USER_ID,
            EXIT_ELEMENT_ID,
            EXIT_LAT,
            EXIT_LNG,
            EXIT_DATE
    );
    DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_NOVELTIES);
        db.execSQL(CREATE_TABLE_COMMENTS);
        db.execSQL(CREATE_TABLE_SIGNS);
        db.execSQL(CREATE_TABLE_ARRIVALS);
        db.execSQL(CREATE_TABLE_EXITS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < DB_VERSION) {
            String stmtDropNv = String.format("drop table if exists %s; %s",
                    NOVELTIES_TABLE,
                    CREATE_TABLE_NOVELTIES);
            String stmtDropCm = String.format("drop table if exists %s; %s",
                    COMMENTS_TABLE,
                    CREATE_TABLE_COMMENTS);
            String stmtDropSg = String.format("drop table if exists %s; %s",
                    SIGNS_TABLE,
                    CREATE_TABLE_SIGNS);
            String stmtDropAr = String.format("drop table if exists %s; %s",
                    ARRIVALS_TABLE,
                    CREATE_TABLE_ARRIVALS);
            String stmtDropEx = String.format("drop table if exists %s; %s",
                    EXITS_TABLE,
                    CREATE_TABLE_EXITS);

            db.execSQL(stmtDropNv);
            db.execSQL(stmtDropCm);
            db.execSQL(stmtDropSg);
            db.execSQL(stmtDropAr);
            db.execSQL(stmtDropEx);
            onCreate(db);
        }
    }
}

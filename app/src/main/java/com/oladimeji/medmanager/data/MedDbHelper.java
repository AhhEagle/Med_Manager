package com.oladimeji.medmanager.data;

/**
 * Created by Oladimeji on 4/5/2018.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.oladimeji.medmanager.data.MedContract.MedEntry;
import com.oladimeji.medmanager.data.MedContract.ProfileEntry;


/**
 * Database helper for MedReminder app. Manages database creation and version management.
 */
public class MedDbHelper extends SQLiteOpenHelper{

    public static final String LOG_TAG = MedDbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "medicine.db";


    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link MedDbHelper}.
     *
     * @param context of the app
     */

    public MedDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    /**
     * This is called when the database is created for the first time.
     */

    @Override
    public void onCreate(SQLiteDatabase db) {
        //SQL statement that creates the db
        String SQL_CREATE_MED_TABLE = "CREATE TABLE " + MedEntry.TABLE_NAME + " ("
                + MedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MedEntry.COLUMN_MED_NAME + " TEXT NOT NULL, "
                + MedEntry.COLUMN_MED_DESCRIPTION + " INTEGER NOT NULL, "
                + MedEntry.COLUMN_MED_FREQUENCY  + " INTEGER NOT NULL, "
                + MedEntry.COLUMN_MED_START_DATE  + " TEXT NOT NULL, "
                + MedEntry.COLUMN_MED_END_DATE  + " TEXT NOT NULL);";
        //Execute the SQL statement
        db.execSQL(SQL_CREATE_MED_TABLE);

        String SQL_CREATE_PROF_TABLE =  "CREATE TABLE " + ProfileEntry.TABLE_NAME_PROF + " ("
                + ProfileEntry._ID1 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProfileEntry.COLUMN_PROF_NAME + " TEXT NOT NULL, "
                + ProfileEntry.COLUMN_PROF_ADDRESS + " TEXT NOT NULL, "
                + ProfileEntry.COLUMN_PROF_SEX + " TEXT NOT NULL, "
                + ProfileEntry.COLUMN_PROF_AGE  + " INTEGER NOT NULL, "
                + ProfileEntry.COLUMN_PROF_PHONE  + " TEXT NOT NULL);";
        db.execSQL(SQL_CREATE_PROF_TABLE);


    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //called when the database is upgraded
        db.execSQL("DROP TABLE IF EXISTS " + MedEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ProfileEntry.TABLE_NAME_PROF);
        onCreate(db);
    }
}

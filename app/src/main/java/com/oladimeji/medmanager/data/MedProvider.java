package com.oladimeji.medmanager.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Oladimeji on 4/5/2018.
 */

public class MedProvider  extends ContentProvider{

    /** Tag for the log messages*/
    public static final String LOG_TAG = MedProvider.class.getSimpleName();

    /**
     * URI matcher code for the content URI for the med table
     */
    private static final int MED = 20;
    /**
     * URI matcher code for the content URI for a single med in the med table
     */
    private static final int MED_ID = 21;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passes into the constructor represents the code to return for the root URI
     * Its common to use NO_MATCH as the input for this case.
     */

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    //Static Initiator, This is run the first time anything is called from this class.
    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        //The calls to addURI() go here, for all of the content URI patterns that the provider
        //should recognize. All paths added to the UriMatcher have a corresponding code to return.
        //when a match is found

        uriMatcher.addURI(MedContract.CONTENT_AUTHORITY, MedContract.PATH_MED, MED);

        //In this case, the # wildcard is used where "#" can be substituted for an integer.
        //for example "contents://com.oladimeji.medManager.meds/med/8" matches
        uriMatcher.addURI(MedContract.CONTENT_AUTHORITY, MedContract.PATH_MED + "/#", MED_ID);
        return uriMatcher;
    }


    /*Database helper object*/
    private MedDbHelper mdbHelper;

    /**
     * Initialize the provider and the database helper object
     */


    @Override
    public boolean onCreate() {
        mdbHelper = new MedDbHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri,  String[] projection,  String selection,  String[] selectionArgs, String sortOrder) {

        //Get readable database
        SQLiteDatabase database = mdbHelper.getReadableDatabase();

        //This cursor will hold the result of the query
        Cursor cursor;

        //Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case MED:
                cursor = database.query(MedContract.MedEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case MED_ID:
                //for the MED_ID code, extract out the ID from the URI
                //the selection will be "_id" and the selection argument will be a

                selection = MedContract.MedEntry._ID + "=?";
                selectionArgs = new String[]{ String.valueOf(ContentUris.parseId(uri)) };

                //This will perform a query on the meds table where thr _id equals # to return a
                //Cursor containing that row of the table.
                cursor = database.query(MedContract.MedEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("cannot query unknown UI " + uri);
        }
        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MED:
                return MedContract.MedEntry.CONTENT_LIST_TYPE;
            case MED_ID:
                return MedContract.MedEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MED:
                return insertMed(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    //Insert a pill into the database with the given content values. Return the new content URI
    //for that specific row in the database.

    private Uri insertMed(Uri uri, ContentValues values){
        //Check that the name is not null
        String name = values.getAsString(MedContract.MedEntry.COLUMN_MED_NAME);
        if (name == null){
            throw new IllegalArgumentException("A valid Medicine name is required");
        }
        //Check that the description is provided and is not less than 0
        Integer description = values.getAsInteger(MedContract.MedEntry.COLUMN_MED_DESCRIPTION);
        if (description != null && description < 1 ){
            throw new IllegalArgumentException("Medicine description is required");
        }
        //Check that the frequency is provided and is not less than 1
        Integer frequency = values.getAsInteger(MedContract.MedEntry.COLUMN_MED_FREQUENCY);
        if (frequency != null && frequency < 1 ){
            throw new IllegalArgumentException("Medicine description is required");
        }
        // I do not need to check for start date and end date since its using a calender view

        //Get writable database
        SQLiteDatabase database = mdbHelper.getWritableDatabase();

        //Insert the new pill with the given values
        long id = database.insert(MedContract.MedEntry.TABLE_NAME, null, values);
        // If the ID = -1, that means the insertion failed. An error message should be displayed
        if (id == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        //Notify all listeners that the data has changed for the med content URI
        // uri: content://com.oladimeji.medmanager/meds
        getContext().getContentResolver().notifyChange(uri, null);

        // return the new URI with the ID  (of the newly inserted row) appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mdbHelper.getWritableDatabase();

        //Track the number of rows that were deleted.
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MED:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(MedContract.MedEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MED_ID:
                // Delete a single row given by the ID in the URI
                selection = MedContract.MedEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(MedContract.MedEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MED:
                return updateMed(uri, values, selection, selectionArgs);
            case MED_ID:
                // For the MED_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = MedContract.MedEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateMed(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }
    /**
     * Update med in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more meds).
     * Return the number of rows that were successfully updated.
     */
    private int updateMed(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        //check that the name values is not null if the key is present and also do that for all other fields
        if (values.containsKey(MedContract.MedEntry.COLUMN_MED_NAME)){
            String name = values.getAsString(MedContract.MedEntry.COLUMN_MED_NAME);
            if (name == null){
                throw new IllegalArgumentException("A valid Medicine name is required");
            }
        }
        if (values.containsKey(MedContract.MedEntry.COLUMN_MED_DESCRIPTION)){
            Integer description = values.getAsInteger(MedContract.MedEntry.COLUMN_MED_DESCRIPTION);
            if (description != null && description < 1 ){
                throw new IllegalArgumentException("Medicine description is required");
            }
        }

        if (values.containsKey(MedContract.MedEntry.COLUMN_MED_FREQUENCY)){
            Integer frequency = values.getAsInteger(MedContract.MedEntry.COLUMN_MED_FREQUENCY);
            if (frequency != null && frequency < 1 ){
                throw new IllegalArgumentException("Medicine description is required");
            }

        }
        //Start date and end date are calender values picked from datepicker

        //If there are no values to update, then don't try to update the database
        if (values.size() == 0){
            return 0;
        }

        //Otherwise, get a writeable database to update the data
        SQLiteDatabase database = mdbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(MedContract.MedEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }
}

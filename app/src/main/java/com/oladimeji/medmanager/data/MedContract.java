package com.oladimeji.medmanager.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Oladimeji on 4/5/2018.
 */

public final class MedContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private MedContract() {}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.oladimeji.medmanager";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //Possible path (appended to base content URI for possible URI's)


    public static final String PATH_MED = "med";


    //Inner class that defines constant values for the med database table.


    public static final class MedEntry implements BaseColumns{

        /**The content URI to access the pill data in the provider*/
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MED);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pills.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MED;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pill.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MED;


        /** Name of database table for pills */
        public final static String TABLE_NAME = "med";


        /**
         * Unique ID number for the pills (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the pill.
         *
         * Type: TEXT
         */
        public final static String COLUMN_MED_NAME ="name";

        /**
         * Description of the pill.
         *
         * Type: TEXT
         */
        public final static String COLUMN_MED_DESCRIPTION = "description";

        /**
         * Frequency of the pill.
         *
         * Type: TEXT
         */
        public final static String COLUMN_MED_FREQUENCY = "frequency";

        /**
         * Starting date  of the pill.
         *
         * Type: TEXT
         */
        public final static String COLUMN_MED_START_DATE = "startdate";

        /**
         * End date  of the pill.
         *
         * Type: TEXT
         */
        public final static String COLUMN_MED_END_DATE = "enddate";

    }
}

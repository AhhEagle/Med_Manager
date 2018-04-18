package com.oladimeji.medmanager;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.oladimeji.medmanager.data.MedContract.ProfileEntry;

/**
 * Created by Oladimeji on 4/10/2018.
 */

public class ProfileLabel extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Identifier for the Profile data loader
     */
    private static final int EXISTING_PROF_LOADER = 0;

    /**
     * Content URI for the existing profile
     */
    private Uri mCurrentProfileUri;

    /**
     * Boolean flag that keeps track of whether the pill has been edited (true) or not (false)
     */
    private boolean mProfHasChanged = false;


    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mPROFHas Changed boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProfHasChanged = true;
            return false;
        }
    };
    private TextView mName;
    private TextView mHomeaddress;
    private TextView mSex;
    private TextView mAge;
    private TextView mPhone;
    private Button mSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.label_profile);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new User or editing an existing one.
        Intent intent = getIntent();
        mCurrentProfileUri = intent.getData();

        // If the intent DOES NOT contain a Profile content URI, then we know that we are
        // creating a new Profile.
        if (mCurrentProfileUri == null) {
            // This is a new profile, so change the app bar to say "NEW PROFILE"
            setTitle(getString(R.string.label_profile_title_new_profile));

        } else {
            // Otherwise this is an existing profile, so change app bar to say "Edit PROFILE"
            setTitle(getString(R.string.label_profile_title_edit_profile));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            // Initialize a loader to read the user data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_PROF_LOADER, null, this);
        }

        mName = findViewById(R.id.label_profile_name);
        mHomeaddress = findViewById(R.id.label_profile_home);
        mSex = findViewById(R.id.label_profile_sex);
        mAge = findViewById(R.id.label_profile_age);
        mPhone = findViewById(R.id.label_profile_phone);
        mSave = findViewById(R.id.label_profile_save);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the menu without saving.
        mName.setOnTouchListener(mTouchListener);
        mHomeaddress.setOnTouchListener(mTouchListener);
        mSex.setOnTouchListener(mTouchListener);
        mAge.setOnTouchListener(mTouchListener);
        mPhone.setOnTouchListener(mTouchListener);
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProf();
                Intent intent1 = new Intent(ProfileLabel.this, CatalogActivity.class);
                startActivity(intent1);

            }
        });
    }

    private void saveProf() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mName.getText().toString().trim();
        String homeString = mHomeaddress.getText().toString().trim();
        String sexString = mSex.getText().toString().trim();
        String ageString = mAge.getText().toString().trim();
        String phoneString = mPhone.getText().toString().trim();

        // Check if this is supposed to be a new user
        // and check if all the fields in the profile are blank

        if (mCurrentProfileUri == null && TextUtils.isEmpty(nameString) && TextUtils.isEmpty(homeString)
                && TextUtils.isEmpty(sexString) && TextUtils.isEmpty(ageString) && TextUtils.isEmpty(phoneString)) {
            return;
        }
        // Create a ContentValues object where column names are the keys,
        // and prof attributes from the labelProfile are the values.

        ContentValues values = new ContentValues();
        values.put(ProfileEntry.COLUMN_PROF_NAME, nameString);
        values.put(ProfileEntry.COLUMN_PROF_ADDRESS, homeString);
        values.put(ProfileEntry.COLUMN_PROF_SEX, sexString);
        values.put(ProfileEntry.COLUMN_PROF_AGE, ageString);
        values.put(ProfileEntry.COLUMN_PROF_PHONE, phoneString);

        //Determine if this is a new or existing profile by checking if mCurrentProfileUri ia null or not

        if (mCurrentProfileUri == null) {
            //This is a New Profile, so insert a new profile into the provider,
            //return the content URI for the new pill.
            Uri newUri = getContentResolver().insert(ProfileEntry.CONTENT_URI1, values);

            if (newUri == null) {
                //show a toast message that an error occurred
                Toast.makeText(this, getString(R.string.label_insert_profile_failed), Toast.LENGTH_SHORT).show();
            } else {
                //show a toast message that it was saved successfully
                Log.e("database values", newUri.toString());
                Toast.makeText(this, getString(R.string.label_insert_profile_successful), Toast.LENGTH_SHORT).show();

            }
        } else {
            // Otherwise this is an EXISTING profile, so update the profile with content URI: mCurrentProfileUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentProfileUri as a single row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentProfileUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.label_update_profile_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.label_update_profile_successful),
                        Toast.LENGTH_SHORT).show();

            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!mProfHasChanged) {
            super.onBackPressed();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //This class shows user profile  attributes, a projection is defined  that contains
        //all columns from the prof table
        String[] projection = {
                ProfileEntry._ID1,
                ProfileEntry.COLUMN_PROF_NAME,
                ProfileEntry.COLUMN_PROF_ADDRESS,
                ProfileEntry.COLUMN_PROF_SEX,
                ProfileEntry.COLUMN_PROF_AGE,
                ProfileEntry.COLUMN_PROF_PHONE
        };
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentProfileUri,            // Query the content URI for the current user
                projection,                    // Columns to include in the resulting Cursor
                null,                 // No selection clause
                null,             // No selection arguments
                null);              // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1){
            return;
        }
        // proceed to moving to the first row of the cursor and reading data from it

        if (data.moveToFirst()){
            int nameColumnIndex = data.getColumnIndex(ProfileEntry.COLUMN_PROF_NAME);
            int addressColumnIndex = data.getColumnIndex(ProfileEntry.COLUMN_PROF_ADDRESS);
            int frequencyColumnIndex = data.getColumnIndex(ProfileEntry.COLUMN_PROF_SEX);
            int startdateColumnIndex = data.getColumnIndex(ProfileEntry.COLUMN_PROF_AGE);
            int enddateColumnIndex = data.getColumnIndex(ProfileEntry.COLUMN_PROF_PHONE);

            // Extract out the value from the Cursor for the given column index
            String name = data.getString(nameColumnIndex);
           String address = data.getString(addressColumnIndex);
            String sex = data.getString(frequencyColumnIndex);
            int age = data.getInt(startdateColumnIndex);
            String phoneno = data.getString(enddateColumnIndex);

            // Update the views on the screen with the values from the database
            mName.setText(name);
            mHomeaddress.setText(address);
            mSex.setText(sex);
            mAge.setText(Integer.toString(age));
            mPhone.setText(phoneno);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mName.setText("");
        mHomeaddress.setText("");
        mSex.setText("");
        mAge.setText("");
        mPhone.setText("");

    }
    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the labelProfile.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the Pill.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
        case android.R.id.home:
        //if nothing as changed go to parent activity CatalogActivity
        if(!mProfHasChanged){
            NavUtils.navigateUpFromSameTask(ProfileLabel.this);
            return true;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that
        // changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, navigate to parent activity.
                        NavUtils.navigateUpFromSameTask(ProfileLabel.this);
                    }
                };

        // Show a dialog that notifies the user they have unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
        break;
       // return true;
    }
        return super.onOptionsItemSelected(item);
    }

}

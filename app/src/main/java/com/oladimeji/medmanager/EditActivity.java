package com.oladimeji.medmanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.oladimeji.medmanager.data.MedContract;

import java.util.Calendar;

public class EditActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<Cursor>{

    /** Identifier for the pill data loader */
    private static final int EXISTING_PET_LOADER = 0;

    /** Content URI for the existing pill (null if it's a new pill) */
    private Uri mCurrentMedUri;

    /** Boolean flag that keeps track of whether the pill has been edited (true) or not (false) */
    private boolean mMedHasChanged = false;


    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mMedHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mMedHasChanged = true;
            return false;
        }
    };

    //class variable for the views
    private EditText mNameEditText;
    private EditText mDescriptionEditText;
    private EditText mFrequencyEditText;
    private EditText mStartdateEditText;
    private EditText mEnddateEditText;
    DatePickerDialog datePickerDialog;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new Med or editing an existing one.
        Intent intent = getIntent();
        mCurrentMedUri = intent.getData();

        // If the intent DOES NOT contain a Med content URI, then we know that we are
        // creating a new Med.
        if (mCurrentMedUri == null) {
            // This is a new med, so change the app bar to say "ADD NEW PILLS"
            setTitle(getString(R.string.edit_activity_title_new_med));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a med that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing med, so change app bar to say "Edit A Pill"
            setTitle(getString(R.string.edit_activity_title_edit_med));

            // Initialize a loader to read the pet data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_PET_LOADER, null, this);
        }


        mNameEditText = findViewById(R.id.edit_med_name);
        mDescriptionEditText = findViewById(R.id.edit_med_description);
        mFrequencyEditText = findViewById(R.id.edit_med_frequency);
        mStartdateEditText =  findViewById(R.id.edit_start_date);
        mEnddateEditText = findViewById(R.id.edit_end_date);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mDescriptionEditText.setOnTouchListener(mTouchListener);
        mFrequencyEditText.setOnTouchListener(mTouchListener);
        mStartdateEditText.setOnTouchListener(mTouchListener);
        mEnddateEditText.setOnTouchListener(mTouchListener);

        //perform click event on edit text
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    //calender class instance to get current date, month and year
                    final Calendar c = Calendar.getInstance();
                    int mYear = c.get(Calendar.YEAR);  // year selected
                    int mMonth = c.get(Calendar.MONTH); // month selected
                    int mDay = c.get(Calendar.DAY_OF_MONTH); //day selected
                    //date picker dialog
                    datePickerDialog = new DatePickerDialog(EditActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            //set day of month, month and year value in the edit text
                            mStartdateEditText.setText((month + 1) + "/" + dayOfMonth + "/" + year);
                            mStartdateEditText.setFocusable(false);
                        }
                    }, mYear, mMonth, mDay);
                    datePickerDialog.show();
            }
        };
        //on click listener for start and end date selection
        mStartdateEditText.setOnClickListener(onClickListener);

        mEnddateEditText.setOnClickListener(onClickListener);
    }

    private void saveMed(){
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String descriptionString = mDescriptionEditText.getText().toString().trim();
        String frequencyString = mFrequencyEditText.getText().toString().trim();
        String startdateString = mStartdateEditText.getText().toString().trim();
        String enddateString = mEnddateEditText.getText().toString().trim();

        // Check if this is supposed to be a new Pill
        // and check if all the fields in the editor are blank

        if (mCurrentMedUri == null && TextUtils.isEmpty(nameString) && TextUtils.isEmpty(descriptionString)
                && TextUtils.isEmpty(frequencyString) && TextUtils.isEmpty(startdateString) && TextUtils.isEmpty(enddateString)){
            return;
        }
        // Create a ContentValues object where column names are the keys,
        // and med attributes from the editor are the values.

        ContentValues values = new ContentValues();
        values.put(MedContract.MedEntry.COLUMN_MED_NAME, nameString);
        values.put(MedContract.MedEntry.COLUMN_MED_DESCRIPTION, descriptionString);
        values.put(MedContract.MedEntry.COLUMN_MED_FREQUENCY, frequencyString);
        values.put(MedContract.MedEntry.COLUMN_MED_START_DATE, startdateString);
        values.put(MedContract.MedEntry.COLUMN_MED_END_DATE, enddateString);

        //Determine if this is a new or existing pill by checking if mCurrentMedUri ia null or not

        if (mCurrentMedUri == null){
            //This is a New Pill, so insert a new pill into the provider,
            //return the content URI for the new pill.
            Uri newUri = getContentResolver().insert(MedContract.MedEntry.CONTENT_URI, values);

            if (newUri == null){
                //show a toast message that an error occured
                Toast.makeText(this, getString(R.string.edit_insert_pill_failed), Toast.LENGTH_SHORT).show();
            } else {
                //show a toast message that it was saved successfully
                Toast.makeText(this, getString(R.string.edit_insert_pill_successful), Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING pill, so update the pill with content URI: mCurrentMedUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentPillUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentMedUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.edit_update_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.edit_update_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        //if its a new pill, hide the Delete menu item.
        if (mCurrentMedUri == null){
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()){
           //If save menu is clicked
           case R.id.action_save:
               saveMed();
               finish();
               return true;
               //If delete menu is clicked
           case R.id.action_delete:
               //Pop up a confirmation dialog for deletion
               showDeleteConfirmationDialog();
               return true;
               //if back arrow button is clicked
           case android.R.id.home:
               //if nothing as changed go to parent activity CatalogActivity
               if(!mMedHasChanged){
                   NavUtils.navigateUpFromSameTask(EditActivity.this);
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
                               NavUtils.navigateUpFromSameTask(EditActivity.this);
                           }
                       };

               // Show a dialog that notifies the user they have unsaved changes
               showUnsavedChangesDialog(discardButtonClickListener);
               return true;
       }
          return super.onOptionsItemSelected(item);
       }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mMedHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        //This class shows all pill attributes, a projection is defined  that contains
        //all columns from the med table
        String[] projection = {
                MedContract.MedEntry._ID,
                MedContract.MedEntry.COLUMN_MED_NAME,
                MedContract.MedEntry.COLUMN_MED_DESCRIPTION,
                MedContract.MedEntry.COLUMN_MED_FREQUENCY,
                MedContract.MedEntry.COLUMN_MED_START_DATE,
                MedContract.MedEntry.COLUMN_MED_END_DATE
        };
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentMedUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1){
            return;
        }
        // proceed to moving to the first row of the cursor and reading data from it

        if (data.moveToFirst()){
            int nameColumnIndex = data.getColumnIndex(MedContract.MedEntry.COLUMN_MED_NAME);
            int descriptionColumnIndex = data.getColumnIndex(MedContract.MedEntry.COLUMN_MED_DESCRIPTION);
            int frequencyColumnIndex = data.getColumnIndex(MedContract.MedEntry.COLUMN_MED_FREQUENCY);
            int startdateColumnIndex = data.getColumnIndex(MedContract.MedEntry.COLUMN_MED_START_DATE);
            int enddateColumnIndex = data.getColumnIndex(MedContract.MedEntry.COLUMN_MED_END_DATE);

            // Extract out the value from the Cursor for the given column index
            String name = data.getString(nameColumnIndex);
            int description = data.getInt(descriptionColumnIndex);
            int frequency = data.getInt(frequencyColumnIndex);
            String startdate = data.getString(startdateColumnIndex);
            String enddate = data.getString(enddateColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mDescriptionEditText.setText(Integer.toString(description));
            mFrequencyEditText.setText(Integer.toString(frequency));
            mStartdateEditText.setText(startdate);
            mEnddateEditText.setText(enddate);
        }

    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mDescriptionEditText.setText("");
        mFrequencyEditText.setText("");
        mStartdateEditText.setText("");
        mEnddateEditText.setText("");

    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
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

    /**
     * Prompt the user to confirm that they want to delete this pet.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the Pill.
                deleteMed();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the Pill in the database.
     */
    private void deleteMed() {
        // Only perform the delete if this is an existing Pill.
        if (mCurrentMedUri != null) {
            // Call the ContentResolver to delete the Pill at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the Pill that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentMedUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.edit_delete_med_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.edit_delete_med_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

}

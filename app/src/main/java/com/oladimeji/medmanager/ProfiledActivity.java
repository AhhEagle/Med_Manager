package com.oladimeji.medmanager;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.oladimeji.medmanager.data.MedContract.ProfileEntry;

public class ProfiledActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int PROFILE_LOADER = 0;
    //variables for the views
    private TextView mNameProfiledText;
    private TextView mAddressProfiledText;
    private TextView mSexProfiledText;
    private TextView mAgeProfiledText;
    private TextView mPhoneProfiledText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiled);
        mNameProfiledText = findViewById(R.id.profiled_prof_name);
        mAddressProfiledText = findViewById(R.id.profiled_prof_address);
        mSexProfiledText = findViewById(R.id.profiled_prof_sex);
        mAgeProfiledText = findViewById(R.id.profiled_prof_age);
        mPhoneProfiledText = findViewById(R.id.profiled_prof_phone_no);
        Toast.makeText(ProfiledActivity.this, "Click on any of your data to edit them", Toast.LENGTH_LONG).show();
        View.OnClickListener onclick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create new intent to go to {@link ProfileLabel}
                Intent intent = new Intent(ProfiledActivity.this, ProfileLabel.class);

                // Form the content URI that represents the profile that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link ProfEntry#CONTENT_URI1}.
                Uri currentProfUri = ContentUris.withAppendedId(ProfileEntry.CONTENT_URI1,  1);

                // Set the URI on the data field of the intent
                intent.setData(currentProfUri);

                // Launch the {@link EditActivity} to display the data for the user.
                startActivity(intent);

            }
            };
        mNameProfiledText.setOnClickListener(onclick);
        mAddressProfiledText.setOnClickListener(onclick);
        mSexProfiledText.setOnClickListener(onclick);
        mAgeProfiledText.setOnClickListener(onclick);
        mPhoneProfiledText.setOnClickListener(onclick);
        getLoaderManager().initLoader(PROFILE_LOADER, null, this);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                ProfileEntry._ID1,
                ProfileEntry.COLUMN_PROF_NAME, ProfileEntry.COLUMN_PROF_ADDRESS, ProfileEntry.COLUMN_PROF_SEX, ProfileEntry.COLUMN_PROF_AGE, ProfileEntry.COLUMN_PROF_PHONE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                ProfileEntry.CONTENT_URI1,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
       // getLoaderManager().initLoader(PROFILE_LOADER, null, this);
      /*  if (data == null || data.getCount() < 1){
            return;
        } */
        if(data != null && data.moveToFirst() ) {
            int idIndex = data.getColumnIndex(ProfileEntry._ID1);
            int nameIndex = data.getColumnIndex(ProfileEntry.COLUMN_PROF_NAME);
            int homeIndex = data.getColumnIndex(ProfileEntry.COLUMN_PROF_ADDRESS);
            int sexIndex = data.getColumnIndex(ProfileEntry.COLUMN_PROF_SEX);
            int ageIndex = data.getColumnIndex(ProfileEntry.COLUMN_PROF_AGE);
            int phoneIndex = data.getColumnIndex(ProfileEntry.COLUMN_PROF_PHONE);

            //Determine the values of the wanted data

            final int id = data.getInt(idIndex);
            String name = data.getString(nameIndex);
            String home_address = data.getString(homeIndex);
            String sex = data.getString(sexIndex);
            int age = data.getInt(ageIndex);
            String phone = data.getString(phoneIndex);

            String ageString = "" + age; // convert int to String
            mNameProfiledText.setText(name);
            mAddressProfiledText.setText(home_address);
            mSexProfiledText.setText(sex);
            mAgeProfiledText.setText(ageString);
            mPhoneProfiledText.setText(phone);
        } else {
            Toast.makeText(this, "Data not saved", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

}

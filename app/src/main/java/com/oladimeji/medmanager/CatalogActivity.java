package com.oladimeji.medmanager;

import android.app.AlarmManager;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.oladimeji.medmanager.utilities.AlarmBroadcastReceiver;
import com.oladimeji.medmanager.data.MedContract;

import java.util.ArrayList;
import java.util.List;

import com.oladimeji.medmanager.MedCursorAdapter;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the pill data loader
     */
    private static final int PILL_LOADER = 0;

    private static final int RC_SIGN_IN = 123;
    private static final int PENDING_INTENT_REQUEST_CODE = 2;

    /**
     * Adapter for the recyclerView
     */
    private MedCursorAdapter mAdapter;

    //EmptyView member variable
    private View emptyView;

    private ListView listView;

    //Firebase Instance Variables

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    //private ChildEventListener mChildEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        if (isFirstRun) {
            //show start activity

            startActivity(new Intent(CatalogActivity.this, ProfileLabel.class));
        }
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).apply();

       /* long currentTime = System.currentTimeMillis();
        long alarmTime = ;

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //intent targeted at launching a broadcast receiver
        Intent alarmIntent = new Intent(this, AlarmBroadcastReceiver.class);
        //Pendingintent allows Alarm manager to work even when my app is not running
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, PENDING_INTENT_REQUEST_CODE, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //set a repeating alarm
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggertime, pendingIntent); */


        //Get firebase instance
        mFirebaseAuth = FirebaseAuth.getInstance();


        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });

        listView = findViewById(R.id.list);
        emptyView = findViewById(R.id.empty_view);

        listView.setEmptyView(emptyView);
        // Set the RecyclerView to its corresponding view
        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new MedCursorAdapter(this, null);
        listView.setAdapter(mAdapter);

        //setup item click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(CatalogActivity.this, EditActivity.class);

                // Form the content URI that represents the specific pill that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link MedEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.oladimeji.medmanager.med/med/1"
                // if the pill with ID 1 was clicked on.
                Uri currentMedUri = ContentUris.withAppendedId(MedContract.MedEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentMedUri);

                // Launch the {@link EditActivity} to display the data for the current pill.
                startActivity(intent);
            }
        });


        /*
         Ensure a loader is initialized and active. If the loader doesn't already exist, one is
         created, otherwise the last created loader is re-used.
         */
        getLoaderManager().initLoader(PILL_LOADER, null, this);

        //firebase Auth listening to weather you are signed in or not ..If you are signed in
        //you are allowed to access the app else you will be forced to sign in

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //User is signed in
                    //mAdapter = new MedCursorAdapter(CatalogActivity.this, null);
                    listView.setAdapter(mAdapter);
                } else {
                    List<AuthUI.IdpConfig> selectedProviders = new ArrayList<>();
                    selectedProviders.add(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build());
                    selectedProviders.add(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build());
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(selectedProviders)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // mAdapter = new MedCursorAdapter(CatalogActivity.this, null);
                listView.setAdapter(mAdapter);
                Toast.makeText(this, "Signed in", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Signed Cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        if (null != searchView) {
            searchView.setSearchableInfo(searchManager
                    .getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(true);
        }

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                // this is your adapter that will be filtered
                return false;
            }

            public boolean onQueryTextSubmit(String query) {
                //Here u can get the value "query" which is entered in the search box.
                return false;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_out:
                AuthUI.getInstance().signOut(this);
                return true;
            case R.id.action_profile:
                Intent intent = new Intent(this, ProfiledActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);


        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                MedContract.MedEntry._ID,
                MedContract.MedEntry.COLUMN_MED_NAME, MedContract.MedEntry.COLUMN_MED_DESCRIPTION, MedContract.MedEntry.COLUMN_MED_FREQUENCY, MedContract.MedEntry.COLUMN_MED_START_DATE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                MedContract.MedEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                MedContract.MedEntry.COLUMN_MED_START_DATE);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);

    }
}

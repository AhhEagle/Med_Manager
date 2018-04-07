package com.oladimeji.medmanager;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.oladimeji.medmanager.data.MedContract;

import java.util.Locale;

/**
 * Created by Oladimeji on 4/5/2018.
 */

public class MedCursorAdapter  extends CursorAdapter implements Filterable {


    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @return the newly created list item view.
     */

    public MedCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.item_med, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameView;
        TextView descriptionView;
        TextView frequencyView;
        TextView startdateView;

        nameView =  view.findViewById(R.id.catalog_med_name);
        descriptionView =  view.findViewById(R.id.catalog_med_description);
        frequencyView =  view.findViewById(R.id.catalog_med_frequency);
        startdateView =  view.findViewById(R.id.catalog_med_start_date);

        int idIndex = cursor.getColumnIndex(MedContract.MedEntry._ID);
        int nameIndex = cursor.getColumnIndex(MedContract.MedEntry.COLUMN_MED_NAME);
        int descriptionIndex = cursor.getColumnIndex(MedContract.MedEntry.COLUMN_MED_DESCRIPTION);
        int frequencyIndex = cursor.getColumnIndex(MedContract.MedEntry.COLUMN_MED_FREQUENCY);
        int startdateIndex = cursor.getColumnIndex(MedContract.MedEntry.COLUMN_MED_START_DATE);

        //Determine the values of the wanted data
        final int id = cursor.getInt(idIndex);
        String name = cursor.getString(nameIndex);
        int description = cursor.getInt(descriptionIndex);
        int frequency = cursor.getInt(frequencyIndex);
        String startdate = cursor.getString(startdateIndex);

        String descriptionString = "" + description + "pill(s)"; // convert int to String
        String frequencyString = "" + frequency + "hr(s)";
        view.setTag(id);
        nameView.setText(name);
        descriptionView.setText(descriptionString);
        frequencyView.setText(frequencyString);
        startdateView.setText(startdate);


    }

}


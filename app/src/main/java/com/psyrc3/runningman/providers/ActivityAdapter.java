package com.psyrc3.runningman.providers;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.psyrc3.runningman.ConversionHelper;

/*
    This adapter is used to bind the database results to the ListView on the main activity.
 */
public class ActivityAdapter extends CursorAdapter {

    public ActivityAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(android.R.layout.simple_expandable_list_item_2,
                viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvTitle = view.findViewById(android.R.id.text1);
        TextView tvSubtitle = view.findViewById(android.R.id.text2);

        String title = cursor.getString(cursor.getColumnIndexOrThrow(
                ActivityProviderContract.TITLE));
        double distance = cursor.getDouble(cursor.getColumnIndexOrThrow(
                ActivityProviderContract.DISTANCE));

        title = filterString(title);
        title = truncateString(title, 20);

        String distanceString = ConversionHelper.distanceToString(distance);

        tvTitle.setText(title);
        tvSubtitle.setText(distanceString);
    }

    private String filterString(String str) {
        // remove newline characters to keep the list tidy
        str = str.replaceAll("\\n", " ");
        return str.replaceAll("\\r", " ");
    }

    private String truncateString(String str, int len) {
        // Shorten the string to a summary
        return str.substring(0, Math.min(str.length(), len));
    }
}

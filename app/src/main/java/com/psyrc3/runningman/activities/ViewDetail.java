package com.psyrc3.runningman.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.psyrc3.runningman.R;
import com.psyrc3.runningman.activities.components.ActivityDetailTable;
import com.psyrc3.runningman.providers.ActivityEntry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ViewDetail extends AppCompatActivity {

    ActivityEntry activityEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_detail);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                int res_id = Integer.valueOf(extras.getString("res_id"));
                activityEntry = new ActivityEntry(res_id, getContentResolver());
                updateIUState();
            }
        }
    }

    private void updateIUState() {
        ActivityDetailTable detailTable = findViewById(R.id.detailsTable);
        detailTable.setActivity(activityEntry);

        ((TextView) findViewById(R.id.title_tv)).setText(activityEntry.title);
        ((TextView) findViewById(R.id.description_tv)).setText(getActivityDescription());
    }

    private String getActivityDescription() {
        Date date = new Date(activityEntry.date);
        DateFormat displayFormat = new SimpleDateFormat("E MMMM d, yyyy HH:mm", Locale.ENGLISH);

        return String.format(Locale.ENGLISH, "A %s on %s",
                activityEntry.type.toLowerCase(), displayFormat.format(date));
    }

}

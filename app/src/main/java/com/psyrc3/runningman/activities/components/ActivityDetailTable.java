package com.psyrc3.runningman.activities.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TextView;

import com.psyrc3.runningman.ConversionHelper;
import com.psyrc3.runningman.R;
import com.psyrc3.runningman.providers.ActivityEntry;
import com.psyrc3.runningman.services.PathKeeper;

public class ActivityDetailTable extends TableLayout {
    public ActivityDetailTable(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.activity_detail_table, this);

    }

    public ActivityDetailTable(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.activity_detail_table, this);
    }

    public void setActivity(PathKeeper path) {
        ((TextView) findViewById(R.id.speed_tv))
                .setText(ConversionHelper.paceToString(path.getAvgPace()));

        ((TextView) findViewById(R.id.distance_tv))
                .setText(ConversionHelper.distanceToString(path.getDistance()));

        ((TextView) findViewById(R.id.time_tv))
                .setText(ConversionHelper.millisElapsedToTimer(path.getTimeElapsed()));
    }

    public void setActivity(ActivityEntry entry) {
        ((TextView) findViewById(R.id.speed_tv))
                .setText(ConversionHelper.paceToString(entry.avgPace));

        ((TextView) findViewById(R.id.distance_tv))
                .setText(ConversionHelper.distanceToString(entry.distance));

        ((TextView) findViewById(R.id.time_tv))
                .setText(ConversionHelper.millisElapsedToTimer(entry.timeElapsed));
    }
}

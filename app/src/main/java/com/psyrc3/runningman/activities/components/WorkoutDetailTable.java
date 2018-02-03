package com.psyrc3.runningman.activities.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TextView;

import com.psyrc3.runningman.ConversionHelper;
import com.psyrc3.runningman.R;
import com.psyrc3.runningman.providers.WorkoutEntry;
import com.psyrc3.runningman.services.PathKeeper;


/*
    This UI component aggregates the speed, distance and time of a workout
    into a single UI component that jsut takes a PathKeeper object as input and handles
    setting up it's own UI state.

    It exists to move some ui state code away from the Activities and make a reusable
    component that we can use on the 'Save Workout' and 'View Detail' screens simply
    and without code duplication.

 */
public class WorkoutDetailTable extends TableLayout {
    public WorkoutDetailTable(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.activity_detail_table, this);

    }

    public WorkoutDetailTable(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.activity_detail_table, this);
    }

    public void setWorkout(PathKeeper path) {
        ((TextView) findViewById(R.id.speed_tv))
                .setText(ConversionHelper.paceToString(path.getAvgPace()));

        ((TextView) findViewById(R.id.distance_tv))
                .setText(ConversionHelper.distanceToString(path.getDistance()));

        ((TextView) findViewById(R.id.time_tv))
                .setText(ConversionHelper.millisElapsedToTimer(path.getTimeElapsed()));
    }

    public void setWorkout(WorkoutEntry entry) {
        ((TextView) findViewById(R.id.speed_tv))
                .setText(ConversionHelper.paceToString(entry.avgPace));

        ((TextView) findViewById(R.id.distance_tv))
                .setText(ConversionHelper.distanceToString(entry.distance));

        ((TextView) findViewById(R.id.time_tv))
                .setText(ConversionHelper.millisElapsedToTimer(entry.timeElapsed));
    }
}

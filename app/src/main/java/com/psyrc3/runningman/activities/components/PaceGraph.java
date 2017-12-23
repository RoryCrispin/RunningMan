package com.psyrc3.runningman.activities.components;

import android.content.Context;
import android.util.AttributeSet;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.psyrc3.runningman.services.PathKeeper;

import java.util.List;

/*
    This class is an extension on the GraphView UI component.
    The base class was extended to add the setPath method so we can
    deal with laying out the PathKeeper points in a format compatible
    with the library in a re-usable way, away from the activity code.
 */
public class PaceGraph extends GraphView {

    public PaceGraph(Context context) {
        super(context);
    }

    public PaceGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PaceGraph(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setPath(PathKeeper path) {
        List<Double> paceList = path.getIncrementalPace();
        DataPoint[] points = new DataPoint[paceList.size()];
        // Build a data structure of points that we can pass to the graphview
        int i = 0;
        for (Double d : paceList) {
            points[i] = new DataPoint(i, d);
            i++;
        }
        setTitle("Pace: minutes per kilometer");
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
        addSeries(series);
        getGridLabelRenderer().setHorizontalLabelsVisible(false);
    }
}

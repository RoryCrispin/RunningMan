package com.psyrc3.runningman.services;


import android.location.Location;
import android.util.Log;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class PathKeeper {

    double distance;
    double pace;
    private List<TimedPoint> path = new ArrayList<>();

    void addPoint(Location newPoint) {
        if (!path.isEmpty()) {
            TimedPoint lastPoint = path.get(path.size() - 1);
            distance += lastPoint.loc.distanceTo(newPoint);
        }
        path.add(new TimedPoint(newPoint));
    }

    double getDistance() {
        return distance;
    }

    public double getPace() {
        if (path.size() > 4) {
            TimedPoint lastPoint = path.get(path.size() - 1);
            TimedPoint oldPoint = path.get(path.size() - 3);
            double distanceFromPoint = oldPoint.loc.distanceTo(lastPoint.loc);
            double timeBetween = lastPoint.time - oldPoint.time;

            return 16.666667 / (distanceFromPoint / timeBetween) / 1000;
        }
        Log.d("G53MDP", "path size too small");
        return 0;
    }

    public long getTimeElapsed() {
        if (!path.isEmpty()) {
            return path.get(0).getTimeSince();
        } else return 0;
    }

    public List<GeoPoint> getGeoPointPath() {
        List<GeoPoint> list = new ArrayList<>();
        for (TimedPoint tp : path) {
            list.add(new GeoPoint(tp.loc));
        }
        return list;
    }
}

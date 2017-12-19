package com.psyrc3.runningman.services;


import android.location.Location;

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
        return pace;
    }
}

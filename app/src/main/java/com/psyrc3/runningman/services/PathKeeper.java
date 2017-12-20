package com.psyrc3.runningman.services;


import android.location.Location;

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

    public double getDistance() {
        return distance;
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

    public double getRecentAvgPace() {
        if (path.size() > 4) {
            TimedPoint lastPoint = path.get(path.size() - 1);
            TimedPoint oldPoint = path.get(path.size() - 3);
            return getPace(oldPoint, lastPoint);
        }
        return 0;
    }

    public double getAvgPace() {
        if (path.size() > 1) {
            TimedPoint lastPoint = path.get(path.size() - 1);
            TimedPoint oldPoint = path.get(0);
            return getPace(oldPoint, lastPoint);
        }
        return 0;
    }

    private double getPace(TimedPoint first, TimedPoint second) {
        double distanceFromPoint = first.loc.distanceTo(second.loc);
        double timeBetween = second.time - first.time;

        return 16.666667 / (distanceFromPoint / timeBetween) / 1000;
    }

    public List<Double> getIncrementalPace() {
        List<Double> list = new ArrayList<>();
        int i = 1;
        for (TimedPoint e1 : path) {
            if (i < path.size()) {
                list.add(getPace(e1, path.get(i)));
                i++;
            }
        }
        return list;
    }

    public long getStartTime() {
        if (path.size() > 0) {
            return path.get(0).time;
        }
        return 0;
    }

}

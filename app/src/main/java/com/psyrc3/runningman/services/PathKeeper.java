package com.psyrc3.runningman.services;


import android.location.Location;

import com.psyrc3.runningman.GPXHelper;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/*
    This path stores activity paths and provides utility functions to calculate pace,
    distance, time, ect.
    It keeps a running total of the distance which is incremented with every new point
    so we don't have to read every single point on every call to getDistance()
 */
public class PathKeeper {

    private double distance;
    private List<TimedPoint> path = new ArrayList<>();

    PathKeeper() {
    }

    public PathKeeper(List<TimedPoint> points) {
        path = points;
    }

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

    private double getPace(TimedPoint first, TimedPoint second) {
        double distanceFromPoint = first.loc.distanceTo(second.loc);
        double timeBetween = second.time - first.time;

        return 16.666667 / (distanceFromPoint / timeBetween) / 1000;
    }

    double getRecentAvgPace() {
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

    // Returns a GPX representation of the path.
    public String reprGPX() {
        StringBuilder body = new StringBuilder();
        for (TimedPoint p : path) {
            body.append(p.reprGPX());
        }
        return String.format(Locale.ENGLISH, "%s <trk> <name>RunningManActivity</name><time>%s</time>" +
                        "<trkseg>%s</trkseg></trk></gpx>", GPXHelper.header,
                GPXHelper.df.format(new Date(getStartTime())), body.toString());
    }
}

package com.psyrc3.runningman.services;

import android.location.Location;

import com.psyrc3.runningman.GPXHelper;

import java.util.Date;
import java.util.Locale;

/*
    This class stores GPS points and the time when they were captured.
 */
public class TimedPoint {
    public long time;
    Location loc;

    TimedPoint(Location loc) {
        this.time = System.currentTimeMillis();
        this.loc = loc;
    }

    public TimedPoint(Location loc, long time) {
        this.time = time;
        this.loc = loc;
    }

    long getTimeSince() {
        return System.currentTimeMillis() - this.time;
    }

    // Return a GPX XML representation of this object.
    String reprGPX() {
        return (String.format(Locale.ENGLISH, "<trkpt lat=\"%.6f\" lon=\"%.6f\"> <time>%s</time>" +
                        " </trkpt>\n",
                loc.getLatitude(), loc.getLongitude(),
                GPXHelper.df.format(new Date(time))));
    }
}

package com.psyrc3.runningman.services;

import android.location.Location;

import com.psyrc3.runningman.GPXHelper;

import java.util.Date;
import java.util.Locale;

public class TimedPoint {
    public long time;
    public Location loc;

    public TimedPoint(Location loc) {
        this.time = System.currentTimeMillis();
        this.loc = loc;
    }

    public TimedPoint(Location loc, long time) {
        this.time = time;
        this.loc = loc;
    }

    public long getTimeSince() {
        return System.currentTimeMillis() - this.time;
    }

    public String reprGPX() {
        return (String.format(Locale.ENGLISH, "<trkpt lat=\"%.6f\" lon=\"%.6f\"> <time>%s</time> </trkpt>\n",
                loc.getLatitude(), loc.getLongitude(),
                GPXHelper.df.format(new Date(time))));
    }
}

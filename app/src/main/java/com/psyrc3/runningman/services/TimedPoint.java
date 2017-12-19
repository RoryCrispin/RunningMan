package com.psyrc3.runningman.services;

import android.location.Location;

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
}

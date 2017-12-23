package com.psyrc3.runningman.providers;


import android.net.Uri;

public class ActivityProviderContract {
    public static final String AUTHORITY = "com.psyrc3.runningman.providers.ActivityProvider";

    public static final Uri ACTIVITY_URI = Uri.parse("content://" + AUTHORITY + "/activity");
    public static final Uri ALL_ACTIVITIES = Uri.parse("content://" + AUTHORITY + "/activities");

    public static final String ID = "_id";
    public static final String TITLE = "title";
    public static final String DATE = "date";
    public static final String DISTANCE = "distance";
    public static final String AVGPACE = "avgpace";
    public static final String TIMEELAPSED = "timeElapsed";
    public static final String TRACK = "track";
    public static final String TYPE = "type";


    public static final int _ID = 0;
    public static final int _TITLE = 1;
    public static final int _DATE = 2;
    public static final int _DISTANCE = 3;
    public static final int _AVGPACE = 4;
    public static final int _TIMEELAPSED = 5;
    public static final int _TRACK = 6;
    public static final int _TYPE = 7;

}

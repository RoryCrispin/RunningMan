package com.psyrc3.runningman.providers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.psyrc3.runningman.GPXHelper;
import com.psyrc3.runningman.services.PathKeeper;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/*
    This class is used to represent saved PathKeeper activities. We bake the distance,
    avg pace, time elapsed ect. details into the data structure so that they could be
    displayed/searched in listview ect without having to parse the raw GPX data of every activity
    in the listviews.
    //TODO: all time distance run ect. Maybe in a snackbar
 */

public class ActivityEntry {
    public int id;
    public String title;
    public long date;
    public double distance;
    public double avgPace;
    public long timeElapsed;
    public String track;
    public String type;
    public PathKeeper path;
    public ActivityEntry(String title, String type, PathKeeper path) {
        this.title = title;
        date = path.getStartTime();
        distance = path.getDistance();
        avgPace = path.getAvgPace();
        timeElapsed = path.getTimeElapsed();
        track = path.reprGPX();
        this.type = type;
    }
    public ActivityEntry(int res_id, ContentResolver contentResolver) {
        Cursor c = contentResolver.query(ActivityProviderContract.ACTIVITY_URI,
                null, String.valueOf(res_id), null, null);
        this.id = res_id;
        this.title = c.getString(ActivityProviderContract._TITLE);
        this.date = c.getLong(ActivityProviderContract._DATE);
        this.distance = c.getDouble(ActivityProviderContract._DISTANCE);
        this.avgPace = c.getDouble(ActivityProviderContract._AVGPACE);
        this.timeElapsed = c.getLong(ActivityProviderContract._TIMEELAPSED);
        this.track = c.getString(ActivityProviderContract._TRACK);
        this.type = c.getString(ActivityProviderContract._TYPE);
        c.close();
        try {
            path = GPXHelper.parseTrack(track);
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }
    public ActivityEntry(ContentValues vals) {
        id = vals.getAsInteger(ActivityProviderContract.ID);
        title = vals.getAsString(ActivityProviderContract.TITLE);
        date = vals.getAsLong(ActivityProviderContract.DATE);
        distance = vals.getAsLong(ActivityProviderContract.DISTANCE);
        avgPace = vals.getAsLong(ActivityProviderContract.AVGPACE);
        timeElapsed = vals.getAsLong(ActivityProviderContract.TIMEELAPSED);
        track = vals.getAsString(ActivityProviderContract.TRACK);
        type = vals.getAsString(ActivityProviderContract.TYPE);
        try {
            path = GPXHelper.parseTrack(track);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ContentValues toContentValues() {
        ContentValues newEntry = new ContentValues();
        newEntry.put(ActivityProviderContract.ID, id);
        newEntry.put(ActivityProviderContract.TITLE, title);
        newEntry.put(ActivityProviderContract.DATE, date);
        newEntry.put(ActivityProviderContract.DISTANCE, distance);
        newEntry.put(ActivityProviderContract.AVGPACE, avgPace);
        newEntry.put(ActivityProviderContract.TIMEELAPSED, timeElapsed);
        newEntry.put(ActivityProviderContract.TRACK, track);
        newEntry.put(ActivityProviderContract.TYPE, type);
        return newEntry;
    }

}

package com.psyrc3.runningman.providers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.location.Location;

import com.psyrc3.runningman.services.PathKeeper;
import com.psyrc3.runningman.services.TimedPoint;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ActivityEntry {
    final String TAG_GPX = "gpx";
    final String TAG_TRACK = "trk";
    final String TAG_SEGMENT = "trkseg";
    final String TAG_POINT = "trkpt";
    final String TAG_LAT = "lat";
    final String TAG_LON = "lon";
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
        this.id = c.getInt(ActivityProviderContract._ID);
        this.title = c.getString(ActivityProviderContract._TITLE);
        this.date = c.getLong(ActivityProviderContract._DATE);
        this.distance = c.getDouble(ActivityProviderContract._DISTANCE);
        this.avgPace = c.getDouble(ActivityProviderContract._AVGPACE);
        this.timeElapsed = c.getLong(ActivityProviderContract._TIMEELAPSED);
        this.track = c.getString(ActivityProviderContract._TRACK);
        this.type = c.getString(ActivityProviderContract._TYPE);
        try {
            path = parseTrack();
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }
    public ActivityEntry(ContentValues vals) {
        title = vals.getAsString(ActivityProviderContract.TITLE);
        date = vals.getAsLong(ActivityProviderContract.DATE);
        distance = vals.getAsLong(ActivityProviderContract.DISTANCE);
        avgPace = vals.getAsLong(ActivityProviderContract.AVGPACE);
        timeElapsed = vals.getAsLong(ActivityProviderContract.TIMEELAPSED);
        track = vals.getAsString(ActivityProviderContract.TRACK);
        type = vals.getAsString(ActivityProviderContract.TYPE);
        try {
            path = parseTrack();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ContentValues toContentValues() {
        ContentValues newEntry = new ContentValues();
        newEntry.put(ActivityProviderContract.TITLE, title);
        newEntry.put(ActivityProviderContract.DATE, date);
        newEntry.put(ActivityProviderContract.DISTANCE, distance);
        newEntry.put(ActivityProviderContract.AVGPACE, avgPace);
        newEntry.put(ActivityProviderContract.TIMEELAPSED, timeElapsed);
        newEntry.put(ActivityProviderContract.TRACK, track);
        newEntry.put(ActivityProviderContract.TYPE, type);
        return newEntry;
    }

    public PathKeeper parseTrack() throws XmlPullParserException, IOException {
        XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = xmlPullParserFactory.newPullParser();
        parser.setInput(new StringReader(track));

        List<TimedPoint> points = new ArrayList<>();
        parser.next();
        parser.require(XmlPullParser.START_TAG, null, TAG_GPX);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            if (tagName.equals(TAG_TRACK)) {
                continue;
            } else if (tagName.equals(TAG_SEGMENT)) {
                continue;
            } else if (tagName.equals(TAG_POINT)) {
                points.add(parsePoint(parser));
            } else {
                skip(parser);
            }
        }

        return new PathKeeper(points);
    }


    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }


    TimedPoint parsePoint(XmlPullParser parser) throws IOException, XmlPullParserException {
        double lat, lon;
        Date time = null;
        parser.require(XmlPullParser.START_TAG, null, TAG_POINT);
        lat = Double.valueOf(parser.getAttributeValue(null, TAG_LAT));
        lon = Double.valueOf(parser.getAttributeValue(null, TAG_LON));
        // Skip the date, we don't need it to display a map
        skip(parser);
        // Build the location object
        Location loc = new Location("");
        loc.setLatitude(lat);
        loc.setLongitude(lon);
        // Fill the time with a dummy value
        time = new Date(System.currentTimeMillis());
        return new TimedPoint(loc, time.getTime());
    }
}

package com.psyrc3.runningman;


import android.location.Location;

import com.psyrc3.runningman.services.PathKeeper;
import com.psyrc3.runningman.services.TimedPoint;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GPXHelper {
    public static final String header = "<gpx xmlns=\"http://www.topografix.com/GPX/1/1\" xmlns:gpxx=\"http://www.garmin.com/xmlschemas/GpxExtensions/v3\" xmlns:gpxtpx=\"http://www.garmin.com/xmlschemas/TrackPointExtension/v1\" creator=\"Oregon 400t\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd http://www.garmin.com/xmlschemas/GpxExtensions/v3 http://www.garmin.com/xmlschemas/GpxExtensionsv3.xsd http://www.garmin.com/xmlschemas/TrackPointExtension/v1 http://www.garmin.com/xmlschemas/TrackPointExtensionv1.xsd\">";
    public static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'", Locale.ENGLISH);

    private static final String TAG_GPX = "gpx";
    private static final String TAG_TRACK = "trk";
    private static final String TAG_SEGMENT = "trkseg";
    private static final String TAG_POINT = "trkpt";
    private static final String TAG_LAT = "lat";
    private static final String TAG_LON = "lon";

    public static PathKeeper parseTrack(String track) throws XmlPullParserException, IOException {
        // Parse the GPX representation of the track.
        // This is a naive implementation that just parses our simple subset of the GPX spec,
        // such that we can show a simple map with the user's run on it.
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

    /*
        Parse a GPX TrackPoint node of the following format:
          <trkpt lat="47.644548" lon="-122.326897">
            <time>2009-10-17T18:37:31Z</time>
          </trkpt>
          We just care about the lat and lon in this implementation.
     */
    private static TimedPoint parsePoint(XmlPullParser parser) throws IOException, XmlPullParserException {
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

    /*
     This function was provided by Android API docs
     https://developer.android.com/training/basics/network-ops/xml.html#skip
     It recursively skips tags (and their bodies) that we don't care about.
        Ie:
        we can skip the whole medatata tag with one call to skip

        <metadata>
            <link href="http://www.garmin.com">
                <text>Garmin International</text>
            </link>
            <time>2009-10-17T22:58:43Z</time>
        </metadata>
     */
    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
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
}

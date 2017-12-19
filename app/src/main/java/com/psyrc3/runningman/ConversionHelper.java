package com.psyrc3.runningman;

import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class ConversionHelper {

    public static String paceToString(double pace) {
        if (pace == 0) return "-:-- min/km";
        return String.format(Locale.ENGLISH, "%.2f min/km", pace);
    }

    // Takes milliseconds : long and returns a readable string MM:SS:mm for the UI
    public static String millisElapsedToTimer(long millis) {
        return String.format(Locale.ENGLISH, "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)
                        ),
                millis % 100
        );
    }

    // Takes distance : double and returns "2 s.f. km" for the UI
    public static String distanceToString(double distance) {
        return String.format(Locale.ENGLISH, "%.2f km", distance / 1000);
    }
}


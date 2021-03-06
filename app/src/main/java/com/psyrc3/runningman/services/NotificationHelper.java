package com.psyrc3.runningman.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.psyrc3.runningman.ConversionHelper;
import com.psyrc3.runningman.R;
import com.psyrc3.runningman.activities.NewRecording;

/*
    This class moves notification generation code away from the service file, keeping it tidy.
 */

public class NotificationHelper {
    public static Notification generateNotification(Context c, String sub) {
        return generateNotification(c, sub, new Intent(c, NewRecording.class));
    }

    public static Notification generateNotification(Context c, String sub, Intent intent) {
        return new NotificationCompat.Builder(c, "runningman")
                .setSmallIcon(R.drawable.ic_run_man_black_24dp)
                .setContentTitle("RunningMan")
                .setContentText(sub)
                .setContentIntent(PendingIntent.getActivity(c, 1,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT)).build();
    }


    static Notification recordingNotification(Context c, double pace, double distance) {
        Intent stopIntent = new Intent(c, NewRecording.class);
        stopIntent.putExtra("requestCode", 99);

        PendingIntent stopPI = PendingIntent.getActivity(c, 3,
                stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(c, "runningman")
                .setSmallIcon(R.drawable.ic_run_man_black_24dp)
                .setContentTitle("RunningMan")
                .addAction(R.drawable.ic_stop_black_24dp, "FINISH", stopPI)
                .setContentText(String.format("Recording: %s -- %s",
                        ConversionHelper.paceToString(pace),
                        ConversionHelper.distanceToString(distance)))
                .setContentIntent(PendingIntent.getActivity(c, 1,
                        new Intent(c, NewRecording.class),
                        PendingIntent.FLAG_UPDATE_CURRENT)).build();
    }
}

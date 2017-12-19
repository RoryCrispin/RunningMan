package com.psyrc3.runningman.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.psyrc3.runningman.R;
import com.psyrc3.runningman.activities.NewRecording;

public class NotificationHelper {
    Notification generateNotification(Context c, String head, String sub) {

        Intent stopIntent = new Intent(c, NewRecording.class);
        stopIntent.putExtra("requestCode", 99);

        PendingIntent stopPI = PendingIntent.getActivity(c, 3,
                stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(c, "runningman")
                .setSmallIcon(R.drawable.ic_run_man_black_24dp)
                .setContentTitle(head)
                .addAction(R.drawable.ic_pause_circle_outline_black_24dp, "Pause", stopPI)
                .setContentText(sub)
                .setContentIntent(PendingIntent.getActivity(c, 1,
                        new Intent(c, NewRecording.class),
                        PendingIntent.FLAG_UPDATE_CURRENT)).build();
    }
}

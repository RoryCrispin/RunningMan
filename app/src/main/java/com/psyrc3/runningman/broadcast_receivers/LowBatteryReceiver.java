package com.psyrc3.runningman.broadcast_receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.psyrc3.runningman.services.NotificationHelper;

public class LowBatteryReceiver {
    private BroadcastReceiver br;

    public void registerReceiver(Context c) {
         br = new LowBatteryBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_LOW);
        c.registerReceiver(br, filter);
    }

    public void unregisterReceiver(Context c) {
        c.unregisterReceiver(br);
    }

    public class LowBatteryBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Show a notification to the user that battery is low.
            Notification bb = NotificationHelper.generateNotification(context,
                    "Battery low, consider stopping workout!");
            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0x22, bb);


        }
    }

}

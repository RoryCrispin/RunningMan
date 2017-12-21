package com.psyrc3.runningman.broadcast_receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class PhotoReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        Toast.makeText(context, "Action: " + intent.getAction(), Toast.LENGTH_SHORT).show();
        Log.d("G53MDP", "PHOTO");
    }
}

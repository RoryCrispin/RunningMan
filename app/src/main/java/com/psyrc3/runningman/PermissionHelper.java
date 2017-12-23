package com.psyrc3.runningman;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionHelper {
    public static int G53MDP_REQUEST_PERMISSION_EXT_STORAGE = 0x101;
    public static int G53MDP_REQUEST_PERMISSION_LOCATION = 0x102;


    public static boolean checkRequestStoragePermission(Activity ctx) {
        if (isStoragePermissionGranted(ctx)) {
            return true;
        }
        requestStoragePermission(ctx);
        return false;
    }


    public static boolean checkRequestLocationPermission(Activity ctx) {
        if (isLocationPermissionGrated(ctx)) {
            return true;
        }
        requestLocationPermission(ctx);
        return false;
    }

    private static boolean isStoragePermissionGranted(Context ctx) {
        return (ContextCompat.checkSelfPermission(ctx,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED);
    }

    private static boolean isLocationPermissionGrated(Context ctx) {
        return (ContextCompat.checkSelfPermission(ctx,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    public static void requestStoragePermission(Activity ctx) {
        ActivityCompat.requestPermissions(ctx,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                G53MDP_REQUEST_PERMISSION_EXT_STORAGE);
    }

    public static void requestLocationPermission(Activity ctx) {
        ActivityCompat.requestPermissions(ctx,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                G53MDP_REQUEST_PERMISSION_LOCATION);
    }

    public void handle_permission_response() {
    }

}

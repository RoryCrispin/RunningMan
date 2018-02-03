package com.psyrc3.runningman.services;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import com.psyrc3.runningman.activities.SaveWorkout;
import com.psyrc3.runningman.broadcast_receivers.LowBatteryReceiver;

import org.osmdroid.util.GeoPoint;

import java.util.List;

/*
    This is the main service used to track user location.
    Activities wishing to receive location updates may implement the
    LocationServiceCallbacks interface.

    The service manages it's own notification and keeps it updated with user progress.
    It stores activities in the PathKeeper class - which stores the location and time
    of each gps location.
 */

public class LocationService extends Service implements LocationListener {
    final int noticationID = 0x118118;
    private final IBinder mBinder = new LocationBinder();
    boolean isRecording;
    LocationManager locationManager;
    NotificationManager notificationManager;
    private PathKeeper path;
    private LocationServiceCallbacks lsCallbacks;
    private LowBatteryReceiver lowBatteryReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // We've already checked for permissions so we can ignore the warning here.
    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();
        path = new PathKeeper();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        startForeground(noticationID,
                NotificationHelper.generateNotification(this,
                        "Not recording"));
        locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1, 1, this);

        lowBatteryReceiver = new LowBatteryReceiver();
        lowBatteryReceiver.registerReceiver(this);
    }

    public void setCallbacks(LocationServiceCallbacks lsCallbacks) {
        this.lsCallbacks = lsCallbacks;
    }

    @Override
    public void onDestroy() {
        notificationManager.cancel(noticationID);
        lowBatteryReceiver.unregisterReceiver(this);
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent i) {
        lsCallbacks = null;
        return super.onUnbind(i);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            if (isRecording) {
                path.addPoint(location);
                // Update the notification to reflect workout progress
                notificationManager.notify(noticationID, NotificationHelper.recordingNotification(this,
                        path.getRecentAvgPace(), path.getDistance()));
            }
            // Notify bound activities of new location
            if (lsCallbacks != null)
                lsCallbacks.notifyLocationUpdated(new GeoPoint(location), 0, path.getDistance());
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }
    @Override
    public void onProviderEnabled(String s) {
    }
    @Override
    public void onProviderDisabled(String s) {
    }

    public void startRecording() {
        isRecording = true;
    }

    public void stopRecording() {
        isRecording = false;
        locationManager.removeUpdates(this);
        // Update the notification to show the progress.
        notificationManager.notify(noticationID, NotificationHelper.generateNotification(this,
                "Save your workout!", new Intent(this, SaveWorkout.class)));

    }

    public long getTimeElapsed() {
        return path.getTimeElapsed();
    }

    public boolean isRecording() {
        return isRecording;
    }

    public List<GeoPoint> getPoints() {
        return path.getGeoPointPath();
    }

    public double getPace() {
        return path.getRecentAvgPace();
    }

    public double getDistance() {
        return path.getDistance();
    }

    public PathKeeper getPath() {
        return path;
    }

    public interface LocationServiceCallbacks {
        void notifyLocationUpdated(GeoPoint newLocation, double pace, double distance);
    }

    public class LocationBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }
}

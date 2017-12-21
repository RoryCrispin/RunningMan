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

import org.osmdroid.util.GeoPoint;

import java.util.List;


public class LocationService extends Service implements LocationListener {
    final int noticationID = 0x118118;
    private final IBinder mBinder = new LocationBinder();
    boolean isRecording;
    LocationManager locationManager;
    NotificationHelper notificationHelper;
    NotificationManager notificationManager;
    private boolean activityIsSubscribed = false;
    private PathKeeper path;
    private LocationServiceCallbacks lsCallbacks;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();
        path = new PathKeeper();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationHelper = new NotificationHelper();
        startForeground(noticationID,
                notificationHelper.generateNotification(this,
                        "Not recording"));
        locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //TODO: Check permissions!
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1, 1, this);


    }

    public void setCallbacks(LocationServiceCallbacks lsCallbacks) {
        this.lsCallbacks = lsCallbacks;
    }

    @Override
    public void onDestroy() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(noticationID);
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
                // Update the notification to reflect activity progress
                notificationManager.notify(noticationID, notificationHelper.recordingNotification(this,
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

    public long getTimeElapsed() {
        return path.getTimeElapsed();
    }

    public boolean isRecording() {
        return isRecording;
    }

    public List<GeoPoint> getPoints() {
        return path.getGeoPointPath();
    }

    public void startRecording() {
        isRecording = true;
    }

    public void stopRecording() {
        isRecording = false;
        locationManager.removeUpdates(this);
        notificationManager.cancel(noticationID);
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

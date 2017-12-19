package com.psyrc3.runningman.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.psyrc3.runningman.R;
import com.psyrc3.runningman.services.LocationService;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;


public class NewRecording extends AppCompatActivity implements LocationService.LocationServiceCallbacks {
    final List<GeoPoint> runningTrackPoints = new ArrayList<>();
    MyLocationNewOverlay mLocationOverlay;
    boolean mBound;
    LocationService locationService;
    MapView mapView;
    Polyline runningTackLine;
    boolean isRecording = false;
    TextView distance_tv, pace_tv;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LocationService.LocationBinder binder = (LocationService.LocationBinder) iBinder;
            locationService = binder.getService();
            mBound = true;
            locationService.setCallbacks(NewRecording.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recording);


        Intent serviceIntent = new Intent(this, LocationService.class);
        startService(serviceIntent);
        bindService(serviceIntent, mConnection, 0);

        mapView = findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.HIKEBIKEMAP);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(17);

        this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
        this.mLocationOverlay.enableMyLocation();
        mapView.getOverlays().add(this.mLocationOverlay);

        runningTackLine = new Polyline();
        runningTackLine.setPoints(runningTrackPoints);
        mapView.getOverlays().add(runningTackLine);

        distance_tv = findViewById(R.id.distance_tv);
        pace_tv = findViewById(R.id.pace_tv);
    }


    public void beginClicked(View v) {
        if (((ToggleButton) v).isChecked()) {
            Log.d("G53MDP", "Start Recording");
            beginRecording();
        } else {
            Log.d("G53MDP", "Pause Recording");
        }
    }

    @Override
    public void notifyLocationUpdated(GeoPoint newLocation, double pace, double distance) {
        mapView.getController().animateTo(newLocation);
        if (isRecording) {
            updateLine(newLocation);
            distance_tv.setText(distanceToString(distance));
        }
    }

    private String distanceToString(double distance) {
        return String.format("%.2f km", distance / 1000);
    }

    private void updateLine(GeoPoint newLocation) {
        runningTrackPoints.add(newLocation);
        runningTackLine.setPoints(runningTrackPoints);
    }

    private void beginRecording() {
        isRecording = true;
    }
}

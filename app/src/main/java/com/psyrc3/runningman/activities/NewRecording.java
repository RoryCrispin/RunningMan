package com.psyrc3.runningman.activities;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.psyrc3.runningman.ConversionHelper;
import com.psyrc3.runningman.PermissionHelper;
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
import java.util.Timer;
import java.util.TimerTask;

/*
    This activity is the main screen used when recording activitues.
 */

public class NewRecording extends AppCompatActivity implements LocationService.LocationServiceCallbacks {
    final List<GeoPoint> runningTrackPoints = new ArrayList<>();
    MyLocationNewOverlay mLocationOverlay;
    boolean mBound;
    LocationService locationService;
    MapView mapView;
    Polyline runningTackLine;
    TextView distance_tv, pace_tv, time_tv;
    Timer timer;
    long millis;
    Button startStopButton;

    // Update the UI timer every millisecond.
    // Instead of polling the service every ms, we fetch the current time elapsed whenever we connect
    // to the service and have the timer increment itself on the UI thread for efficiency.
    private Runnable stopwatchUpdater = new Runnable() {
        @Override
        public void run() {
            time_tv.setText(ConversionHelper.millisElapsedToTimer(millis));
            millis++;
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LocationService.LocationBinder binder = (LocationService.LocationBinder) iBinder;
            locationService = binder.getService();
            mBound = true;
            locationService.setCallbacks(NewRecording.this);
            resumeActivityState();
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recording);

        if (PermissionHelper.checkRequestStoragePermission(this)) {
            withPermiions();
        }


        // Start and bind the service
        Intent serviceIntent = new Intent(this, LocationService.class);
        startService(serviceIntent);
        bindService(serviceIntent, mConnection, 0);

        setupMapView();

        // Initialise the UI elements
        distance_tv = findViewById(R.id.distance_tv);
        pace_tv = findViewById(R.id.pace_tv);
        time_tv = findViewById(R.id.time_tv);
        startStopButton = findViewById(R.id.startStop_tb);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionHelper.G53MDP_REQUEST_PERMISSION_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                withPermiions();
            } else {
                Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    // TODO: rename
    void withPermiions() {

    }


    private void updateButtonState() {
        startStopButton.setText(locationService.isRecording() ? "Stop Recording" : "Begin Activity");
    }

    private void setupMapView() {
        // Load the mapview and set the tileset to Hike/Bike, set the zoom level
        // and enable pinch, zoom, and pan.
        mapView = findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.HIKEBIKEMAP);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(17);

        //Prepare the bitmap to be used as the user's icon on the map
        Bitmap myLocaton = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_location);

        // Tell the mapview to display the current location man on the map.
        this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
        mLocationOverlay.setPersonIcon(myLocaton);
        this.mLocationOverlay.enableMyLocation();

        mapView.getOverlays().add(this.mLocationOverlay);

        // Initialise the polyline which will be used to display the user's track.
        runningTackLine = new Polyline();
        runningTackLine.setPoints(runningTrackPoints);
        mapView.getOverlays().add(runningTackLine);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mBound) {
            Intent serviceIntent = new Intent(this, LocationService.class);
            bindService(serviceIntent, mConnection, 0);
        }
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            conformStopRecording();
        }
    }

    // Resume the UI state from the service
    private void resumeActivityState() {
        // Clear the old polyline from the UI and get a fresh copy from the service
        runningTrackPoints.clear();
        runningTrackPoints.addAll(locationService.getPoints());

        // Update the time elapsed count so the ui can continue counting.
        millis = locationService.getTimeElapsed();
        if (locationService.isRecording()) {
            startStopwatchThread();
        }
        updateButtonState();
    }

    public void beginClicked(View v) {
        if (!locationService.isRecording()) {
            locationService.startRecording();
            startStopwatchThread();
        } else {
            conformStopRecording();
        }
        updateButtonState();
    }

    @Override
    public void notifyLocationUpdated(GeoPoint newLocation, double pace, double distance) {
        mapView.getController().animateTo(newLocation);
        if (locationService.isRecording()) {
            // Add the new point to the traking line
            updateLine(newLocation);
            // Update the pace and distance textviews
            distance_tv.setText(ConversionHelper.distanceToString(distance));
            pace_tv.setText(ConversionHelper.paceToString(locationService.getPace()));
        }
    }

    // Takes a GeoPoint and adds it to the current line displayed by the UI
    private void updateLine(GeoPoint newLocation) {
        runningTrackPoints.add(newLocation);
        runningTackLine.setPoints(runningTrackPoints);
    }

    // Starts a stopwatch thread to keep the UI timer updated.
    private void startStopwatchThread() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateStopwatch();
            }
        }, 1, 1);
    }

    private void updateStopwatch() {
        this.runOnUiThread(stopwatchUpdater);
    }

    // Show an alert dialog to confirm that the user wants to finish and save their recording
    private void conformStopRecording() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int returnVal) {
                if (returnVal == DialogInterface.BUTTON_POSITIVE) {
                    locationService.stopRecording();
                    timer.cancel();

                    //Open the save activity
                    Intent i = new Intent(getApplicationContext(), SaveActivity.class);
                    startActivity(i);
                }

            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to end the session?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}

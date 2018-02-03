package com.psyrc3.runningman.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.psyrc3.runningman.R;
import com.psyrc3.runningman.StravaSyncHelper;
import com.psyrc3.runningman.activities.components.WorkoutDetailTable;
import com.psyrc3.runningman.providers.WorkoutEntry;
import com.psyrc3.runningman.providers.WorkoutProviderContract;
import com.psyrc3.runningman.providers.achievements.AchievementHelper;
import com.sweetzpot.stravazpot.authenticaton.ui.StravaLoginActivity;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/*
    This activity shows detail about user workouts (runs/walks/rides)
    and allows them to edit, delete or share them.
 */
public class ViewDetail extends AppCompatActivity {

    WorkoutEntry workoutEntry;
    MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_detail);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                int res_id = Integer.valueOf(extras.getString("res_id"));
                workoutEntry = new WorkoutEntry(res_id, getContentResolver());
                updateIUState();
            }
        }
    }

    private void updateIUState() {
        ((WorkoutDetailTable) findViewById(R.id.detailsTable)).setWorkout(workoutEntry);
        ((TextView) findViewById(R.id.title_tv)).setText(workoutEntry.title);
        ((TextView) findViewById(R.id.description_tv)).setText(getActivityDescription());
        ((TextView) findViewById(R.id.achievement_tv)).setText(
                AchievementHelper.stringifyAchievements(workoutEntry, this.getContentResolver()));

        setupMapView();
    }

    private void setupMapView() {
        // Load the mapview and set the tileset to Hike/Bike, set the zoom level
        // and enable pinch, zoom, and pan.
        mapView = findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.HIKEBIKEMAP);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15);

        //Setup the PolyLine which will display the route of the workout
        Polyline runningTrackOverlay = new Polyline();
        final List<GeoPoint> runningTrackPoints = new ArrayList<>();
        runningTrackPoints.addAll(workoutEntry.path.getGeoPointPath());
        runningTrackOverlay.setPoints(runningTrackPoints);
        mapView.getOverlays().add(runningTrackOverlay);

        // Center the screen on the workout
        if (workoutEntry.path.getGeoPointPath().size() > 0) {
            mapView.getController().setCenter(workoutEntry.path.getGeoPointPath().get(0));
        }
    }

    private String getActivityDescription() {
        Date date = new Date(workoutEntry.date);
        DateFormat displayFormat = new SimpleDateFormat("E MMMM d, yyyy HH:mm", Locale.ENGLISH);

        return String.format(Locale.ENGLISH, "A %s on %s",
                workoutEntry.type.toLowerCase(), displayFormat.format(date));
    }

    public void uploadClicked(View view) {
        // If the user has already authenticated, upload the workout, otherwise auth then upload
        StravaSyncHelper stravaSyncHelper = new StravaSyncHelper();
        if (stravaSyncHelper.isAuthenticated(this)) {
            stravaSyncHelper.shareWorkout(this, workoutEntry);
        } else {
            startActivityForResult(stravaSyncHelper.getLoginIntent(this),
                    StravaSyncHelper.STRAVA_LOGIN_INTENT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // If the result comes from the strava login, save the auth code and share the workout,
        // or show an auth failed toast.
        if (requestCode == StravaSyncHelper.STRAVA_LOGIN_INTENT) {
            if (resultCode == RESULT_OK && data != null) {
                StravaSyncHelper stravaSyncHelper = new StravaSyncHelper();
                stravaSyncHelper.saveAuthCode(this, data.getStringExtra(StravaLoginActivity.RESULT_CODE));
                stravaSyncHelper.shareWorkout(this, workoutEntry);
            } else {
                Toast.makeText(this, "Authentication failed!", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String title = data.getStringExtra("title");
                String type = data.getStringExtra("type");
                updateWorkoutProperties(workoutEntry, title, type);
            }
        }
    }


    public void editClicked(View view) {
        Intent i = new Intent(this, WorkoutProperties.class);
        startActivityForResult(i, 1);
    }

    private void updateWorkoutProperties(WorkoutEntry entry, String title, String type) {
        entry.title = title;
        entry.type = type;
        getContentResolver().update(WorkoutProviderContract.WORKOUT_URI,
                entry.toContentValues(), null, null);
        updateIUState();
    }

    public void deleteClicked(View view) {
        getContentResolver().delete(WorkoutProviderContract.WORKOUT_URI,
                String.valueOf(workoutEntry.id), null);
        Intent i = new Intent(this, ListWorkouts.class);
        startActivity(i);
    }
}

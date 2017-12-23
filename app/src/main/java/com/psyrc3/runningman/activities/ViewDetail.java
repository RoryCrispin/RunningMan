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
import com.psyrc3.runningman.activities.components.ActivityDetailTable;
import com.psyrc3.runningman.providers.ActivityEntry;
import com.psyrc3.runningman.providers.ActivityProviderContract;
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
    This activity shows detail about user Activities (runs/walks/rides)
    and allows them to edit, delete or share them.
 */
public class ViewDetail extends AppCompatActivity {

    ActivityEntry activityEntry;
    MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_detail);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                int res_id = Integer.valueOf(extras.getString("res_id"));
                activityEntry = new ActivityEntry(res_id, getContentResolver());
                updateIUState();
            }
        }
    }

    private void updateIUState() {
        ActivityDetailTable detailTable = findViewById(R.id.detailsTable);
        detailTable.setActivity(activityEntry);

        ((TextView) findViewById(R.id.title_tv)).setText(activityEntry.title);
        ((TextView) findViewById(R.id.description_tv)).setText(getActivityDescription());

        setupMapView();
    }

    private void setupMapView() {
        // Load the mapview and set the tileset to Hike/Bike, set the zoom level
        // and enable pinch, zoom, and pan.
        mapView = findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.HIKEBIKEMAP);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15);

        //Setup the PolyLine which will display the route of the activity
        Polyline runningTrackOverlay = new Polyline();
        final List<GeoPoint> runningTrackPoints = new ArrayList<>();
        runningTrackPoints.addAll(activityEntry.path.getGeoPointPath());
        runningTrackOverlay.setPoints(runningTrackPoints);
        mapView.getOverlays().add(runningTrackOverlay);

        // Center the screen on the activity
        mapView.getController().setCenter(activityEntry.path.getGeoPointPath().get(0));
    }

    private String getActivityDescription() {
        Date date = new Date(activityEntry.date);
        DateFormat displayFormat = new SimpleDateFormat("E MMMM d, yyyy HH:mm", Locale.ENGLISH);

        return String.format(Locale.ENGLISH, "A %s on %s",
                activityEntry.type.toLowerCase(), displayFormat.format(date));
    }

    public void uploadClicked(View view) {
        // If the user has already authenticated, upload the activity, otherwise auth then upload
        StravaSyncHelper stravaSyncHelper = new StravaSyncHelper();
        if (stravaSyncHelper.isAuthenticated(this)) {
            stravaSyncHelper.shareActivity(this, activityEntry);
        } else {
            startActivityForResult(stravaSyncHelper.getLoginIntent(this),
                    StravaSyncHelper.STRAVA_LOGIN_INTENT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // If the result comes from the strava login, save the auth code and share the activity,
        // or show an auth failed toast.
        if (requestCode == StravaSyncHelper.STRAVA_LOGIN_INTENT) {
            if (resultCode == RESULT_OK && data != null) {
                StravaSyncHelper stravaSyncHelper = new StravaSyncHelper();
                stravaSyncHelper.saveAuthCode(this, data.getStringExtra(StravaLoginActivity.RESULT_CODE));
                stravaSyncHelper.shareActivity(this, activityEntry);
            } else {
                Toast.makeText(this, "Authentication failed!", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String title = data.getStringExtra("title");
                String type = data.getStringExtra("type");
                updateActivityProperties(activityEntry, title, type);
            }
        }
    }


    public void editClicked(View view) {
        Intent i = new Intent(this, ActivityProperties.class);
        startActivityForResult(i, 1);
    }

    private void updateActivityProperties(ActivityEntry entry, String title, String type) {
        entry.title = title;
        entry.type = type;
        getContentResolver().update(ActivityProviderContract.ACTIVITY_URI,
                entry.toContentValues(), null, null);
        updateIUState();
    }

    public void deleteClicked(View view) {
        getContentResolver().delete(ActivityProviderContract.ACTIVITY_URI,
                String.valueOf(activityEntry.id), null);
        Intent i = new Intent(this, ListActivities.class);
        startActivity(i);
    }
}

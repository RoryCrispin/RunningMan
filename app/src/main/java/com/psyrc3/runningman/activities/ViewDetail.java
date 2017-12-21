package com.psyrc3.runningman.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.psyrc3.runningman.R;
import com.psyrc3.runningman.activities.components.ActivityDetailTable;
import com.psyrc3.runningman.providers.ActivityEntry;
import com.sweetzpot.stravazpot.authenticaton.api.AccessScope;
import com.sweetzpot.stravazpot.authenticaton.api.ApprovalPrompt;
import com.sweetzpot.stravazpot.authenticaton.api.StravaLogin;
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

    private void setupMapView() {
        // Load the mapview and set the tileset to Hike/Bike, set the zoom level
        // and enable pinch, zoom, and pan.
        mapView = findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.HIKEBIKEMAP);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(17);
        Polyline runningTrackOverlay = new Polyline();
        final List<GeoPoint> runningTrackPoints = new ArrayList<>();
        runningTrackPoints.addAll(activityEntry.path.getGeoPointPath());
        runningTrackOverlay.setPoints(runningTrackPoints);
        mapView.getOverlays().add(runningTrackOverlay);
        mapView.getController().setCenter(activityEntry.path.getGeoPointPath().get(0));
    }

    private void updateIUState() {
        ActivityDetailTable detailTable = findViewById(R.id.detailsTable);
        detailTable.setActivity(activityEntry);

        ((TextView) findViewById(R.id.title_tv)).setText(activityEntry.title);
        ((TextView) findViewById(R.id.description_tv)).setText(getActivityDescription());

        setupMapView();
    }

    private String getActivityDescription() {
        Date date = new Date(activityEntry.date);
        DateFormat displayFormat = new SimpleDateFormat("E MMMM d, yyyy HH:mm", Locale.ENGLISH);

        return String.format(Locale.ENGLISH, "A %s on %s",
                activityEntry.type.toLowerCase(), displayFormat.format(date));
    }

    public void uploadClicked(View view) {
        Intent intent = StravaLogin.withContext(this)
                .withClientID(22149)
                .withRedirectURI("http://rorycrispin.co.uk")
                .withApprovalPrompt(ApprovalPrompt.AUTO)
                .withAccessScope(AccessScope.VIEW_PRIVATE_WRITE)
                .makeIntent();
        startActivityForResult(intent, 1001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            Log.d("Strava code", data.getStringExtra(StravaLoginActivity.RESULT_CODE));
        }
    }


}

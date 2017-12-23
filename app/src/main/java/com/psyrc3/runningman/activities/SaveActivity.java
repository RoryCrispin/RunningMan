package com.psyrc3.runningman.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.psyrc3.runningman.R;
import com.psyrc3.runningman.activities.components.ActivityDetailTable;
import com.psyrc3.runningman.activities.components.PaceGraph;
import com.psyrc3.runningman.providers.ActivityEntry;
import com.psyrc3.runningman.providers.ActivityProviderContract;
import com.psyrc3.runningman.services.LocationService;

/*
    This activity is shown after recording an activity. It shows details about the activity and
    offers the user Saving and Discarding of the activity.
 */
public class SaveActivity extends AppCompatActivity {

    boolean mBound;
    LocationService locationService;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LocationService.LocationBinder binder = (LocationService.LocationBinder) iBinder;
            locationService = binder.getService();
            mBound = true;
            updateUIValues();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);
        // Start and bind the service
        Intent serviceIntent = new Intent(this, LocationService.class);
        bindService(serviceIntent, mConnection, 0);
    }

    private void updateUIValues() {
        ActivityDetailTable detailTable = findViewById(R.id.detailsTable);
        detailTable.setActivity(locationService.getPath());

        ((PaceGraph) findViewById(R.id.graph))
                .setPath(locationService.getPath());
    }

    public void saveClicked(View v) {
        Intent i = new Intent(this, ActivityProperties.class);
        startActivityForResult(i, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String title = data.getStringExtra("title");
                String type = data.getStringExtra("type");
                saveActivity(title, type);
            }
        }
    }

    private void saveActivity(String title, String type) {
        ActivityEntry activityEntry = new ActivityEntry(title, type, locationService.getPath());
        getContentResolver().insert(ActivityProviderContract.ACTIVITY_URI,
                activityEntry.toContentValues());
        closeServiceGoHome();
    }

    @Override
    public void onBackPressed() {
        closeServiceGoHome();
    }

    public void discardClicked(View view) {
        closeServiceGoHome();
    }

    private void closeServiceGoHome() {
        // Stop the service
        Intent service = new Intent(this, LocationService.class);
        stopService(service);
        // Return to the home screen
        Intent i = new Intent(this, ListActivities.class);
        startActivity(i);
    }

}

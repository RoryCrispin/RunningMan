package com.psyrc3.runningman.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.psyrc3.runningman.ConversionHelper;
import com.psyrc3.runningman.R;
import com.psyrc3.runningman.services.LocationService;

import java.util.List;

public class SaveActivity extends AppCompatActivity {

    boolean mBound;
    LocationService locationService;
    TextView speed_tv, distance_tv, time_tv;

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

        speed_tv = findViewById(R.id.speed_tv);
        distance_tv = findViewById(R.id.distance_tv);
        time_tv = findViewById(R.id.time_tv);
    }

    private void updateUIValues() {
        String distanceText = ConversionHelper.distanceToString(locationService.getDistance());
        String timeText = ConversionHelper.millisElapsedToTimer(locationService.getTimeElapsed());
        String speedText = ConversionHelper.paceToString(locationService.getAvgPace());

        distance_tv.setText(distanceText);
        time_tv.setText(timeText);
        speed_tv.setText(speedText);
        renderGraph();
    }

    // Render a graphView showing the activities speed over time
    private void renderGraph() {
        GraphView graph = findViewById(R.id.graph);
        List<Double> paceList = locationService.getIncrementalPace();
        DataPoint[] points = new DataPoint[paceList.size()];
        // Build a data structure of points that we can pass to the graphview
        int i = 0;
        for (Double d : paceList) {
            points[i] = new DataPoint(i, d);
            i++;
        }
        graph.setTitle("Speed: min/km");
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
        graph.addSeries(series);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
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
                Log.d("G53MDP", title + type);
            }
        }
    }


}

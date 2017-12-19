package com.psyrc3.runningman.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.psyrc3.runningman.R;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void newRecordingClicked(View v) {
        Intent i = new Intent(this, NewRecording.class);
        startActivity(i);
    }
}

package com.psyrc3.runningman.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.psyrc3.runningman.R;

/*
    This activity is used to set the workout type and name, returning them as Intent Extras.
    It's displayed as a popup view and used by the Save and Edit workflows.
 */

public class WorkoutProperties extends AppCompatActivity {

    EditText title_et;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_properties);

        // Setup the spinner with default values
        spinner = findViewById(R.id.workout_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.activity_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        title_et = findViewById(R.id.title_et);
    }

    private boolean validateFields() {
        return (!String.valueOf(title_et.getText()).equals(""));
    }

    public void saveClicked(View v) {
        if (validateFields()) {
            Intent result = new Intent();
            result.putExtra("title", String.valueOf(title_et.getText()));
            result.putExtra("type", spinner.getSelectedItem().toString());
            setResult(Activity.RESULT_OK, result);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Think of a more interesting title!",
                    Toast.LENGTH_SHORT).show();
        }
    }
}

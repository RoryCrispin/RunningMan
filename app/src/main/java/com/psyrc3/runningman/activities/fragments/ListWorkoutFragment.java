package com.psyrc3.runningman.activities.fragments;


import android.content.Intent;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.psyrc3.runningman.R;
import com.psyrc3.runningman.activities.ViewDetail;
import com.psyrc3.runningman.providers.WorkoutAdapter;
import com.psyrc3.runningman.providers.WorkoutProviderContract;

public class ListWorkoutFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "listActivitySection";
    ListView activity_lv;
    TextView noActivities;

    public ListWorkoutFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ListWorkoutFragment newInstance(int sectionNumber) {
        ListWorkoutFragment fragment = new ListWorkoutFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_workouts, container, false);
        noActivities = rootView.findViewById(R.id.emptylist_msg);
        activity_lv = rootView.findViewById(R.id.workouts_lv);
        activity_lv.setNestedScrollingEnabled(true);

        activity_lv.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Find the id of the element tapped and navigate to the view activity,
                // with element ID as Intent Extra
                String id = ((CursorWrapper) (adapterView).getItemAtPosition(i)).getString(0);
                Intent intent = new Intent(getActivity(), ViewDetail.class);
                intent.putExtra("res_id", id);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        populateListView();
    }

    private void populateListView() {

        Cursor activityCursor = getActivity().getContentResolver().query(WorkoutProviderContract.ALL_ACTIVITIES,
                null, workoutType(getArguments().getInt(ARG_SECTION_NUMBER)),
                null, null);
        WorkoutAdapter workoutAdapter = new WorkoutAdapter(getContext(), activityCursor, 0);
        activity_lv.setAdapter(workoutAdapter);

        if (activityCursor.getCount() > 0) {
            noActivities.setVisibility(View.INVISIBLE);
        }
    }

    String workoutType(int i) {
        switch (i) {
            case 1:
                return "*";
            case 2:
                return "Run";
            case 3:
                return "Walk";
            case 4:
                return "Cycle";
        }
        return null;
    }
}
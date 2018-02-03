package com.psyrc3.runningman.providers.achievements;

import android.content.ContentResolver;
import android.database.Cursor;

import com.psyrc3.runningman.providers.WorkoutEntry;
import com.psyrc3.runningman.providers.WorkoutProviderContract;

import java.util.ArrayList;

public class AchievementHelper {
    private static ArrayList<AchievementEnum> getAchievements(WorkoutEntry workoutEntry,
                                                              ContentResolver resolver){
        ArrayList<AchievementEnum> result = new ArrayList<>();
        Cursor workoutCursor = resolver.query(WorkoutProviderContract.ALL_ACTIVITIES,
                null, workoutEntry.type, null, null);
        workoutCursor.moveToPrevious();
        // Get max values from list.
        double maxDistance = 0;
        double maxPace = 0;
        long maxTimeElapsed = 0;
        // Iterate over the list of workouts and find the max val of each field.
        while (workoutCursor.moveToNext()) {
            double distance = workoutCursor.getDouble(WorkoutProviderContract._DISTANCE);
            double pace = workoutCursor.getDouble(WorkoutProviderContract._AVGPACE);
            long timeElapsed = workoutCursor.getLong(WorkoutProviderContract._TIMEELAPSED);

            if (maxDistance < distance) {
                maxDistance = distance;
            }
            if (maxPace < pace) {
                maxPace = pace;
            }
            if (maxTimeElapsed < timeElapsed) {
                maxTimeElapsed = timeElapsed;
            }
        }
        // Assign the achievements in the result list which correspond to this workout.
        if (workoutEntry.distance == maxDistance){ result.add(AchievementEnum.LONGESTDISTANCE); }
        if (workoutEntry.avgPace == maxPace){ result.add(AchievementEnum.FASTESTPACE); }
        if (workoutEntry.timeElapsed == maxTimeElapsed){ result.add(AchievementEnum.LONGESTTIME); }
        return result;
    }

    /* This function gets a list of achievements for a workout and returns a
    * loosely formatted string representing those achievements.
    * A better implementation would handle conjunctions better bit this is simple enough
    * for these purposes.
    */
    public static String stringifyAchievements(WorkoutEntry workoutEntry,
                                               ContentResolver resolver) {
        StringBuilder buffer  = new StringBuilder();
        for (AchievementEnum e : getAchievements(workoutEntry, resolver)) {
            switch (e) {
                case LONGESTDISTANCE:
                    buffer.append("Longest distance, ");
                    break;
                case FASTESTPACE:
                    buffer.append("Fastest pace, ");
                    break;
                case LONGESTTIME:
                    buffer.append("Longest time");
                    break;
            }
        }
        String outString = buffer.toString();
        if (outString.equals("")) {
            return "No Achievements on this workout";
        } else return outString;
    }
}

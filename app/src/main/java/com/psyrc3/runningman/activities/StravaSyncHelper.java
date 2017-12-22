package com.psyrc3.runningman.activities;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.psyrc3.runningman.providers.ActivityEntry;
import com.sweetzpot.stravazpot.authenticaton.api.AccessScope;
import com.sweetzpot.stravazpot.authenticaton.api.ApprovalPrompt;
import com.sweetzpot.stravazpot.authenticaton.api.AuthenticationAPI;
import com.sweetzpot.stravazpot.authenticaton.api.StravaLogin;
import com.sweetzpot.stravazpot.authenticaton.model.AppCredentials;
import com.sweetzpot.stravazpot.authenticaton.model.LoginResult;
import com.sweetzpot.stravazpot.common.api.AuthenticationConfig;
import com.sweetzpot.stravazpot.common.api.StravaConfig;
import com.sweetzpot.stravazpot.upload.api.UploadAPI;
import com.sweetzpot.stravazpot.upload.model.DataType;
import com.sweetzpot.stravazpot.upload.model.UploadActivityType;

import java.io.File;
import java.io.OutputStreamWriter;

class StravaSyncHelper {
    static final int STRAVA_LOGIN_INTENT = 1001;
    private static final String AUTH_CODE = "strava_auth_code";

    private static final int CLIENT_ID = 22149;
    private static final String CLIENT_SECRET = "c9aac058cefcc2224bfa803fe0efdcb18869348e";

    boolean isAuthenticated(Activity c) {
        SharedPreferences sharedPreferences = c.getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences.contains(AUTH_CODE);
    }

    Intent getLoginIntent(Context c) {
        return StravaLogin.withContext(c)
                .withClientID(CLIENT_ID)
                .withRedirectURI("http://rorycrispin.co.uk")
                .withApprovalPrompt(ApprovalPrompt.AUTO)
                .withAccessScope(AccessScope.VIEW_PRIVATE_WRITE)
                .makeIntent();
    }

    void saveAuthCode(Activity c, String authCode) {
        SharedPreferences sharedPreferences = c.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AUTH_CODE, authCode);
        editor.commit();
    }

    private String getAuthCode(Activity c) {
        SharedPreferences sharedPreferences = c.getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences.getString(AUTH_CODE, "");
    }

    private StravaConfig getConfig(Activity c) {
        AuthenticationConfig config = AuthenticationConfig.create()
                .debug()
                .build();
        AuthenticationAPI api = new AuthenticationAPI(config);
        LoginResult result = api.getTokenForApp(AppCredentials.with(CLIENT_ID, CLIENT_SECRET))
                .withCode(getAuthCode(c))
                .execute();

        return StravaConfig.withToken(result.getToken())
                .debug()
                .build();
    }

    void shareActivity(Activity c, final ActivityEntry activityEntry) {
        final Activity fc = c;
        // Run the network code off from the UI thread so that the UI remains smooth.
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Write to a temporary file before uploading
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fc.openFileOutput(
                            "upload.gpx", Context.MODE_PRIVATE));
                    outputStreamWriter.write(activityEntry.track);
                    outputStreamWriter.close();


                    // Upload the activity to Strava
                    UploadAPI uploadAPI = new UploadAPI(getConfig(fc));
                    uploadAPI.uploadFile(new File(fc.getFilesDir(), "upload.gpx"))
                            .withDataType(DataType.GPX)
                            .withActivityType(getType(activityEntry.type))
                            .withName(activityEntry.title)
                            .withDescription("No description")
                            .isPrivate(false)
                            .hasTrainer(false)
                            .isCommute(false)
                            .withExternalID("upload.gpx")
                            .execute();


                    fc.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(fc, "Upload complete!", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    fc.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(fc, "Upload Failed!", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
        t.start();
    }

    private UploadActivityType getType(String type) {
        switch (type) {
            case "Run":
                return UploadActivityType.RUN;
            case "Walk":
                return UploadActivityType.WALK;
            case "Ride":
                return UploadActivityType.RIDE;
        }
        return UploadActivityType.RUN;
    }
}

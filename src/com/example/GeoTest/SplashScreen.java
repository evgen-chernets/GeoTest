package com.example.GeoTest;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class SplashScreen extends FragmentActivity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        new Thread() {
            public void run() {
                if (isReady()) {
                    go();
                }
            }
        }.start();
    }

    private void go() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private boolean isReady() {
        if (servicesConnected()) {
            return true;
        }
        return false;
    }

    private boolean servicesConnected() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == resultCode) {
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        }
        return false;
    }
}

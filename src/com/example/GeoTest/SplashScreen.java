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
                if (servicesConnected()) {
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        }.start();
    }

    private boolean servicesConnected() {
        boolean result = false;
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        switch (resultCode) {
            case ConnectionResult.SUCCESS:
                Log.d("GeoTest", "ConnectionResult.SUCCESS");
                result = true;
                break;
            case ConnectionResult.SERVICE_DISABLED:
                Log.d("GeoTest", "ConnectionResult.SERVICE_DISABLED");
                break;
            case ConnectionResult.SERVICE_INVALID:
                Log.d("GeoTest", "ConnectionResult.SERVICE_INVALID");
                break;
            case ConnectionResult.SERVICE_MISSING:
                Log.d("GeoTest", "ConnectionResult.SERVICE_MISSING");
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                Log.d("GeoTest", "ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED");
                break;
        }
        return result;
    }
}

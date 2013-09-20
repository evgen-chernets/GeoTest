package com.example.GeoTest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashScreen extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        new Thread() {
            public void run() {
                prepare();
                onReady();
            }
        }.start();
    }

    private void onReady() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void prepare() {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

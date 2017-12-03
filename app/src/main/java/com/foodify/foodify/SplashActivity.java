package com.foodify.foodify;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class SplashActivity extends AppCompatActivity {
    static int SPLASH_TIME = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Thread myThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(SPLASH_TIME);
                    Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(homeIntent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();
    }
}

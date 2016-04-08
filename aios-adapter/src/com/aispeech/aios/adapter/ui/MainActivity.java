package com.aispeech.aios.adapter.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.aispeech.aios.adapter.R;
import com.aispeech.aios.adapter.service.FloatWindowService;

/**
 * @desc LauncherActivity
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class MainActivity extends Activity {

    public static final String TAG = "AIOS-Adapter-LauncherActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        startService(new Intent(MainActivity.this, FloatWindowService.class));
        finish();
    }
}
package com.example.bai7_1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class manhinh_b extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    Boolean is_play = false;
    private final int FILE_SELECT_CODE = 1;
    String TAG = "manhinh_b";
    VideoView mp;
    TextView lblInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manhinh_b);

        checkPermission();
        lblInfo = findViewById(R.id.lblInfo);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    //overriding two methods of SensorEventListener
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            startstopMusic(event);
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
//        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
//                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
//        sensorManager.unregisterListener(this);
    }

    private void startstopMusic(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];
        Log.d("",Float.toString(z));
        if (z > 0) {
            // Up
            if (!is_play)
            {
                if (mp!=null) {
                    mp.start();
                    is_play = true;
                }
            }
        } else {
            if (is_play)
            {
                if (mp!=null) {
                    mp.pause();
                    is_play = false;
                }
            }
        }
    }
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void playMusic(Uri filePath) {

        mp = findViewById(R.id.videoView);

            mp.setVideoURI(filePath);
            mp.requestFocus();
            mp.start();
            is_play = true;


    }

    void checkPermission() {
        Log.d("PERM", "RUN");
        // Send SMS to 5556

        String[] perm_array = {Manifest.permission.READ_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            List<String> permissions = new ArrayList<String>();

            for (int i=0;i<perm_array.length;i++)
            {
                if (checkSelfPermission(perm_array[i])!= PackageManager.PERMISSION_GRANTED)
                {
                    permissions.add(perm_array[i]);
                }
            }

            if (!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), 9999);
            } else {

            }
        } else {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    Uri fileUri = data.getData();
                    playMusic(fileUri);
                    lblInfo.setText(fileUri.getPath());
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void selectFile(android.view.View mv)
    {
        showFileChooser();
    }

    public void play(android.view.View mv)
    {
        if (!mp.isPlaying())
        {
            mp.start();
            is_play = true;
        }
    }


    public void stop(android.view.View mv)
    {
        if (mp.isPlaying())
        {
            mp.pause();
            is_play = false;
        }


    }
}
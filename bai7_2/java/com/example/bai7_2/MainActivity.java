package com.example.bai7_2;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    TextView lblInfo;
    ImageView imgIcon;
    String TAG = "";
    MediaRecorder recorder;
    boolean is_recording = false;
    File audiofile = null;
    SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;
    private static final int SHAKE_SLOP_TIME_MS = 500;
    private static final int SHAKE_COUNT_RESET_TIME_MS = 3000;

    private long mShakeTimestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();
        lblInfo = findViewById(R.id.lblInfo);
        imgIcon = findViewById(R.id.imgIcon);

        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

    }

    void checkPermission() {

        String[] perm_array = {Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
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
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // ignore
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!event.sensor.getStringType().equals( Sensor.STRING_TYPE_ACCELEROMETER)) return;

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float gX = x / SensorManager.GRAVITY_EARTH;
        float gY = y / SensorManager.GRAVITY_EARTH;
        float gZ = z / SensorManager.GRAVITY_EARTH;

        // gForce will be close to 1 when there is no movement.
        float gForce = (float)Math.sqrt(gX * gX + gY * gY + gZ * gZ);

        if (gForce > SHAKE_THRESHOLD_GRAVITY) {
            final long now = System.currentTimeMillis();
            // ignore shake events too close to each other (500ms)
            if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                return;
            }

            if (is_recording)
            {

                stop(null);
            }
            else {
                record(null);
            }

            mShakeTimestamp = now;

        }
    }

    public void record(android.view.View mv)
    {
        //Creating file
        File dir = Environment.getExternalStorageDirectory();
        try {
            audiofile = File.createTempFile("mysound", ".3gp", dir);
        } catch (IOException e) {
            Log.e(TAG, "external storage access error");
            e.printStackTrace();
            return;
        }
        //Creating MediaRecorder and specifying audio source, output format, encoder & output format
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(audiofile.getAbsolutePath());
        Log.d(TAG, audiofile.getAbsolutePath().toString());
        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();
        lblInfo.setText("Đang ghi");
        imgIcon.setImageResource(R.drawable.record);
        is_recording = true;

    }

    private void shareFile(File file) {

        Intent intentShareFile = new Intent(Intent.ACTION_SEND);

        intentShareFile.setType(URLConnection.guessContentTypeFromName(file.getName()));
        intentShareFile.putExtra(Intent.EXTRA_STREAM,
                Uri.parse("file://"+file.getAbsolutePath()));


        intentShareFile.putExtra(Intent.EXTRA_SUBJECT,"Sharing File Subject");
        intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File Description");

        startActivity(Intent.createChooser(intentShareFile, "Share File"));

    }

    public void share(android.view.View mv)
    {
        shareFile(audiofile);

    }
    public void stop(android.view.View mv)
    {
        recorder.stop();
        lblInfo.setText("Đã dừng");
        imgIcon.setImageResource(R.drawable.stop);
        is_recording = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}
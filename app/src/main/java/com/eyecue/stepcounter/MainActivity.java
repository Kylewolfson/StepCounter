package com.eyecue.stepcounter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.Console;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private Integer stepCounter = 0;
    private int counterSteps = 0;
    private int stepDetector = 0;
    TextView mStepCounter;
    private boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStepCounter = (TextView)findViewById(R.id.stepCounter);
    }


    public static boolean IsKitKatWithStepCounter(PackageManager pm) {

        // Require at least Android KitKat
        int currentApiVersion = (int) Build.VERSION.SDK_INT;
        // Check that the device supports the step counter and detector sensors
        return currentApiVersion >= 19
                && pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)
                && pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR);

    }

    public void onSensorChanged (SensorEvent e)
    {
        switch (e.sensor.getType()) {
            case Sensor.TYPE_STEP_DETECTOR:
                stepDetector++;
                System.out.println(stepDetector);
                break;
            case Sensor.TYPE_ACCELEROMETER:
                System.out.println(e.values[0]);
                break;
            case Sensor.TYPE_STEP_COUNTER:
                //Since it will return the total number since we registered we need to subtract the initial amount
                //for the current steps since we opened app
                if (counterSteps < 1) {
                    // initial value
                    counterSteps = (int)e.values [0];
                }

                // Calculate steps taken based on first counter value received.
                stepCounter = (int)e.values [0] - counterSteps;
                mStepCounter.setText(stepCounter.toString());
                break;
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        RegisterListeners();
        if (isRunning || !IsKitKatWithStepCounter(this.getPackageManager())) {
        return;
        } else {
            RegisterListeners();
        }
    }




    void RegisterListeners() {


        SensorManager mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        System.out.println(mSensorManager.getSensorList(-1));

        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        System.out.println("Sensor listener registered of type: " + Sensor.TYPE_STEP_COUNTER);
    }


    void UnregisterListeners() {

        SensorManager mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager.unregisterListener(this);
        System.out.println("Sensor listener unregistered.");

        isRunning = false;
    }

    @Override
    public void onDestroy ()
    {
        super.onDestroy ();
        UnregisterListeners ();
        isRunning = false;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

}

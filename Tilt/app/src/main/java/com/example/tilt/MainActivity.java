package com.example.tilt;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mGyroscope;
    private Sensor mAccelerometer;

    private double gyroTilt;
    private double accelTilt;
    private double compTilt;

    private float accelBiasX = -0.305250467f;
    private float accelBiasY = -0.142084112f;
    private float accelBiasZ = 10.06681121f;

    private double b = 0.98;

    private static final float NS2S = 1.0f / 1000000000.0f;

    private float gyroPrevTime = 0;

    private TextView gyroReadout;
    private TextView accelReadout;
    private TextView compReadout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED);
        mGyroscope     = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);

        gyroReadout = findViewById(R.id.gyroReadout);
        accelReadout = findViewById(R.id.accelReadout);
        compReadout = findViewById(R.id.compReadout);
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE_UNCALIBRATED) {
            if (gyroPrevTime == 0) {
                gyroPrevTime = event.timestamp;
            } else {
                float gyroX = event.values[0];
                float gyroCurrTime = event.timestamp;
                float dt = gyroCurrTime - gyroPrevTime;
                gyroPrevTime = gyroCurrTime;

                gyroTilt += gyroX * dt * NS2S;
                compTilt = b * gyroTilt + (1-b) * accelTilt;

                gyroReadout.setText(String.format("%.3f", gyroTilt));
                compReadout.setText(String.format("%.3f", compTilt));
            }
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER_UNCALIBRATED) {
            float accelY = event.values[1];
            accelTilt = Math.asin(accelY / SensorManager.GRAVITY_EARTH);
            accelReadout.setText(String.format("%.3f", accelTilt));
        }
    }
}
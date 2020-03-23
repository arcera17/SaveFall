package com.example.savefall;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView xText, yText, zText, rootText;
    double rootSquare;

    SensorManager sensorManager;
    Sensor sensor;
    boolean isPresent = false;
    String rootTextString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sensorManager =(SensorManager)getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);

        if(sensors.size() > 0){
            isPresent = true;
            sensor = sensors.get(0);
        }

        // orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(isPresent){
            sensorManager.registerListener(sel,sensor,SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(isPresent){
            sensorManager.unregisterListener( sel );
        }
    }

    SensorEventListener sel = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0],y = event.values[1], z = event.values[2];

            xText = (TextView) findViewById(R.id.x);
            yText = (TextView) findViewById(R.id.y);
            zText = (TextView) findViewById(R.id.z);
            rootText = (TextView) findViewById(R.id.rootSquare);

            rootSquare = 0;

            if ( x >= 0 ) {
                xText.setText("x: " + x);
                rootSquare += Math.pow( x,2);
            }else{
                xText.setText("x -");
            }

            if ( y >= 0 ) {
                yText.setText("y: " + y);
                rootSquare += Math.pow( y,2);
            }else{
                yText.setText("y -");
            }

            if ( z >= 0 ) {
                zText.setText("z: " + z);
                rootSquare += Math.pow( z,2);
            }else{
                zText.setText("z -");
            }

            rootSquare = Math.sqrt(rootSquare);

            rootTextString = "Root square :" + rootSquare;
            rootText.setText(rootTextString);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
}

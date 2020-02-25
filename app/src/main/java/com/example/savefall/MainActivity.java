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

    TextView xText, yText, zText, rootText, timeText1, timeText2, minValueText, maxValueText;
    double rootSquare, maxValue = 10, minValue = 9;

    SensorManager sensorManager;
    Sensor sensor;
    boolean isPresent = false;
    int Falls = 0;

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
            timeText1 = (TextView) findViewById(R.id.time1);

            maxValueText = (TextView) findViewById(R.id.maxValue);
            minValueText = (TextView) findViewById(R.id.minValue);

            rootSquare = 0;

            if ( x >= 0 ) {
                xText.setText("Pe x: " + x);
                rootSquare += Math.pow( x,2);
            }else{
                xText.setText("x ");
            }

            if ( y >= 0 ) {
                yText.setText("Pe y: " + y);
                rootSquare += Math.pow( y,2);
            }else{
                yText.setText("y ");
            }

            if ( z >= 0 ) {
                zText.setText("Pe z: " + z);
                rootSquare += Math.pow( z,2);
            }else{
                zText.setText("z");
            }

            rootSquare = Math.sqrt(rootSquare);

            if(maxValue <= rootSquare){
                maxValue = rootSquare;
            }
            if(minValue >= rootSquare){
                minValue = rootSquare;
            }

            maxValueText.setText("max value : " + maxValue);
            minValueText.setText("min value : " + minValue);

            rootText.setText("root square : " + rootSquare);

            String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
            timeText1.setText("start: " + currentDateTimeString);

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
}

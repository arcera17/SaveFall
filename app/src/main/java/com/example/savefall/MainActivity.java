package com.example.savefall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.nfc.cardemulation.CardEmulation;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    TextView xText, yText, zText, rootText, mTextViewResult, latTextView, lonTextView, appName, testOne, testTwo;
    double rootSquare;

    SensorManager sensorManager;
    Sensor sensor;
    boolean isPresent = false;
    String rootTextString = "";

    // Location
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;

    // for Http parameters
    String url = "http://167.71.59.142/api/";
    HashMap<String, Float> params = new HashMap<String, Float>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Views
        mTextViewResult = (TextView) findViewById(R.id.response_http);
        appName = (TextView) findViewById(R.id.textView);
        testOne = (TextView) findViewById(R.id.test_one);
        testTwo = (TextView) findViewById(R.id.test_two);

        // Lon & Lat of Location
        latTextView = findViewById(R.id.latTextView);
        lonTextView = findViewById(R.id.lonTextView);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getLastLocation();

        // Accelerometer sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);

        if (sensors.size() > 0) {
            isPresent = true;
            sensor = sensors.get(0);
        }

        // orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }


    // Add params to url
    private String urlParams(String url , HashMap<String, Float> params ) {
        Character and = new Character('&');
        Character lastChar = new Character(url.charAt(url.length()-1));

        StringBuilder sb = new StringBuilder();

        Iterator<?> iter = params.entrySet().iterator();
        while (iter.hasNext()) {
            if (sb.length() <= 0) {
                sb.append('?');
            }
            else{
                sb.append('&');
            }
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) iter.next();

            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }

        return sb.toString();
    }


    @Override
    protected void onResume() {

        // Sensor accelerometer
        super.onResume();

        if (isPresent) {
            sensorManager.registerListener(sel, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        // Location
        if (checkPermissions()) {
            getLastLocation();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (isPresent) {
            sensorManager.unregisterListener(sel);
        }
    }

    SensorEventListener sel = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0], y = event.values[1], z = event.values[2];

            xText = (TextView) findViewById(R.id.x);
            yText = (TextView) findViewById(R.id.y);
            zText = (TextView) findViewById(R.id.z);
            rootText = (TextView) findViewById(R.id.rootSquare);

            rootSquare = 0;

            if (x >= 0) {
                xText.setText("x: " + x);
                rootSquare += Math.pow(x, 2);
            } else {
                xText.setText("x -");
            }

            if (y >= 0) {
                yText.setText("y: " + y);
                rootSquare += Math.pow(y, 2);
            } else {
                yText.setText("y -");
            }

            if (z >= 0) {
                zText.setText("z: " + z);
                rootSquare += Math.pow(z, 2);
            } else {
                zText.setText("z -");
            }

            rootSquare = Math.sqrt(rootSquare);

            rootTextString = "Root square :" + rootSquare;
            rootText.setText(rootTextString);

            params.put("x", x);
            params.put("y", y);
            params.put("z", z);

            url += urlParams(url, params);

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            appName.setText(request.toString());

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    testOne.setText("Http: bad" + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if(response.isSuccessful()){
                        final String myResponse = response.body().toString();

                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                testOne.setText("Response: "+myResponse);
                            }
                        });
                    }
                }
            });
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    // all for location
    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    latTextView.setText("Lat: " + location.getLatitude()+"");
                                    lonTextView.setText("Lon :" + location.getLongitude()+"");
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latTextView.setText("Lat: " + mLastLocation.getLatitude()+"");
            lonTextView.setText("Lon :" + mLastLocation.getLongitude()+"");
        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }
}

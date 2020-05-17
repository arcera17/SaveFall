package com.example.savefall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomePage extends AppCompatActivity  {

    TextView xText, yText, zText, rootText, mTextViewResult, latTextView, lonTextView, appName, testOne, testTwo;
    float rootSqr;

    TextView testUserId, testUserPassword, testUserLogin, testUserName, testUserPrivacyPolicy, testUserBirthDate;
    Button logoutBtn;

    // Accelerometer
    SensorManager sensorManager;
    Sensor sensor;
    boolean isPresent = false;
    String rootTextString = "";
    // Accelerometer graph
    LineGraphSeries<DataPoint> valueOnX = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> valueOnY = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> valueOnZ = new LineGraphSeries<DataPoint>();
    private int lastX = 0;

    // Location
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;

    // locat database
    UserLocalStore userLocalStore;
    User loggedUser;
    int loggedUserID = 0;

    // for Http parameters
    public static String appURL = "http://167.71.59.142";
    String url = appURL+"/api";
    HashMap<String, Float> params = new HashMap<String, Float>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        appName = (TextView) findViewById(R.id.textView);
        userLocalStore = new UserLocalStore(this);

        // Views
        mTextViewResult = (TextView) findViewById(R.id.response_http);
        testOne = (TextView) findViewById(R.id.test_one);
        testTwo = (TextView) findViewById(R.id.test_two);

        // Lon & Lat of Location
        latTextView = (TextView) findViewById(R.id.latTextView);
        lonTextView = (TextView) findViewById(R.id.lonTextView);
        testUserId = (TextView) findViewById(R.id.testUserId);
        testUserLogin = (TextView) findViewById(R.id.testUserLogin);
        testUserPassword = (TextView) findViewById(R.id.testUserPassword);
        testUserName = (TextView) findViewById(R.id.testUserName);
        testUserPrivacyPolicy = (TextView) findViewById(R.id.testUserPrivacyPolicy);
        testUserBirthDate = (TextView) findViewById(R.id.testUserBirthDate);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Create graph view instance
        GraphView graph = (GraphView) findViewById(R.id.graph);

        getLastLocation();

        // orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Accelerometer sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);

        // Create graph and all those settings
        valueOnX.setColor(Color.BLUE);
        valueOnX.setTitle("axa-x");
        graph.addSeries(valueOnX);
        valueOnY.setColor(Color.GREEN);
        valueOnY.setTitle("axa-y");
        graph.addSeries(valueOnY);
        valueOnZ.setColor(Color.RED);
        valueOnZ.setTitle("axa-z");
        graph.addSeries(valueOnZ);
        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(-40);
        viewport.setMaxY(40);
        viewport.setMaxX(150);
        viewport.setScalable(true);
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setTextSize(30);


        if (sensors.size() > 0) {
            isPresent = true;
            sensor = sensors.get(0);
        }

        if(authentication() == true){

            // logout
            logoutBtn = (Button) findViewById(R.id.logOutBtn);

            logoutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logOut();
                }
            });

            loggedUser = userLocalStore.getLoggedUser();

            loggedUserID = loggedUser.id;
            testUserId.setText("User Id: " + loggedUserID);
            testUserLogin.setText("User login: "+loggedUser.login);
            testUserPassword.setText("User password: "+loggedUser.password);
            testUserName.setText("User name: "+loggedUser.name);
            testUserPrivacyPolicy.setText("User P&R: "+loggedUser.privacyPolicy);
            testUserBirthDate.setText("User BD: "+loggedUser.birthDate);
        }
        else{
            appName.setText("NOT Authenticated");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        appName = (TextView) findViewById(R.id.textView);

        if(authentication() == true){
            appName.setText("Authenticated");
        }
        else{
            appName.setText("NOT Authenticated");
        }
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

    // log out
    private void logOut(){
        userLocalStore.clearLoggedUserData();
        userLocalStore.setUserLogged(false);

        Intent welcomePage = new Intent(this, MainActivity.class);
        startActivity(welcomePage);
    }

    // check authentication
    private boolean authentication(){
        return userLocalStore.getUserLogged();
    }

    SensorEventListener sel = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0], y = event.values[1], z = event.values[2];

            xText = (TextView) findViewById(R.id.x);
            yText = (TextView) findViewById(R.id.y);
            zText = (TextView) findViewById(R.id.z);
            rootText = (TextView) findViewById(R.id.rootSquare);
            appName = (TextView) findViewById(R.id.textView);

            rootSqr = 0;

            if (x >= 0) {
                xText.setText("x: " + x);
                rootSqr += Math.pow(x, 2);
            } else {
                xText.setText("x -");
            }

            if (y >= 0) {
                yText.setText("y: " + y);
                rootSqr += Math.pow(y, 2);
            } else {
                yText.setText("y -");
            }

            if (z >= 0) {
                zText.setText("z: " + z);
                rootSqr += Math.pow(z, 2);
            } else {
                zText.setText("z -");
            }

            // Update graph
            valueOnX.appendData(new DataPoint(lastX, x),true,150);
            valueOnY.appendData(new DataPoint(lastX, y),true,150);
            valueOnZ.appendData(new DataPoint(lastX, z),true,150);
            lastX++;

            rootSqr = (float) Math.sqrt(rootSqr);
            rootTextString = "Root square :" + rootSqr;
            rootText.setText(rootTextString);

            // Paramds for url
            url += "?user_id=" +  loggedUser.id;
            params.put("x", x);
            params.put("y", y);
            params.put("z", z);
            params.put("rootSqr", rootSqr);

            // Add params to url
            url += urlParams(url, params);

            // Create client for Http request and to request URL
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();

//            appName.setText("");
//            appName.setText(request.toString());

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    testOne.setText("Http: bad" + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if(response.isSuccessful()){
                        final String myResponse = response.body().string();

                        HomePage.this.runOnUiThread(new Runnable() {
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


    // Add params to url
    public static String urlParams(String url , HashMap<String, Float> params ) {
        Character and = new Character('&');
        Character lastChar = new Character(url.charAt(url.length()-1));

        StringBuilder sb = new StringBuilder();

        Iterator<?> iter = params.entrySet().iterator();
        while (iter.hasNext()) {
            sb.append('&');
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) iter.next();

            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }

        return sb.toString();
    }

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

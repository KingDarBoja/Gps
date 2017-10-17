package com.example.augusto.gps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import android.widget.TextView;

// Import files related to sql connection on mariadb
import java.sql.Date;
import java.text.DateFormat;

public class MainActivity extends AppCompatActivity {

    private TextView tvLatitude, tvLongitude, tvTime;
    private LocationManager locationManager;
    private LocationListener listener;
    String lat,lon,tim,tim2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        tvLatitude = (TextView) findViewById(R.id.latitudeField);
        tvLongitude = (TextView) findViewById(R.id.longitudeField);
        tvTime = (TextView) findViewById(R.id.timeField);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lat = Double.toString(location.getLatitude());
                lon = Double.toString(location.getLongitude());
                Date date = new Date(location.getTime());
                DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
                DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(getApplicationContext());
                tim =  dateFormat.format(date) + "\n" + timeFormat.format(date);
                tim2 = dateFormat.format(date) + " " + timeFormat.format(date);
                tvLatitude.setText(lat);
                tvLongitude.setText(lon);
                tvTime.setText(tim);

                String method ="register";
                BackgroundTask backgroundTask = new BackgroundTask(getBaseContext());
                backgroundTask.execute(method,lat,lon,tim2);


            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        configure_button();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }


    void configure_button() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }

        // this code won'textView execute IF permissions are not allowed, because in the line above there is return statement.
        locationManager.requestLocationUpdates("gps", 5000, 0, listener);
    }
}
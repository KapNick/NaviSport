package com.example.navisport;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import classes.MyLocationListener;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap map;
    private Marker myLoc = null;
    private LatLng position;
    MyLocationListener list = new MyLocationListener();
    private LocationManager locationManager;

    private static final int REQUEST_LOCATION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnAddPoint = findViewById(R.id.PointMenu);
        btnAddPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(".AddPoint");
                startActivity(intent);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        if (!hasPermissions()) {
            requestPerms();
            onPause();
        } else {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            list.showLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hasPermissions()) {
            list.setLocationManager(locationManager);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 10, 10, list);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 10, list);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(hasPermissions() && list.checkEnabled()) {
            list.pause();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if(hasPermissions()) {
            list.setMap(map);
            list.setMyLoc(myLoc);
            list.setPosition(position);
        }
    }

    private boolean hasPermissions() {
        int res;
        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

        for (String perms : permissions) {
            res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)) {
                return false;
            }
        }
        return true;
    }

    private void requestPerms() {
        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, REQUEST_LOCATION);
        }
        this.finish();
    }

    public void onButtonLocationClicked(View view) {
    }

    public void onButtonUpdateClicked(View view) {
    }
}

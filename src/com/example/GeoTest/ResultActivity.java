package com.example.GeoTest;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created with IntelliJ IDEA.
 * User: e.chernets
 * Date: 19.09.13
 * Time: 0:24
 * To change this template use File | Settings | File Templates.
 */
public class ResultActivity extends FragmentActivity implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener/*, LocationListener */{

    private GoogleMap mapResult;
    private LocationClient locationClient;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);

        mapResult = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map_result)).getMap();
        if (mapResult != null) {
            mapResult.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }

        locationClient = new LocationClient(this, this, this);

        /*locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(1000);*/
    }


    @Override
    protected void onStart() {
        super.onStart();
        locationClient.connect();
    }

    @Override
    protected void onStop() {
        /*if (locationClient.isConnected()) {
            locationClient.removeLocationUpdates(this);
        }*/
        locationClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = locationClient.getLastLocation();
        if (location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mapResult.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        }
//        locationClient.requestLocationUpdates(locationRequest, this);
    }

    @Override
    public void onDisconnected() {
        //ToDo
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //ToDo
    }

    /*@Override
    public void onLocationChanged(Location location) {
       *//* if (location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mapFrom.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mapFrom.moveCamera(CameraUpdateFactory.newLatLng(latLng));*//*
    }*/
}
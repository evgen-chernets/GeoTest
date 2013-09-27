package com.example.GeoTest;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ProgressBar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: e.chernets
 * Date: 19.09.13
 * Time: 0:24
 * To change this template use File | Settings | File Templates.
 */
public class ResultActivity extends FragmentActivity implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

    private LatLng pointFrom, pointTo, currentLocation;
    private GoogleMap mapResult;
    private LocationClient locationClient;
    private LocationRequest locationRequest;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);

        if (mapResult == null) {
            mapResult = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map_result)).getMap();
            if (mapResult != null) {
                mapResult.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        }
        if (locationClient == null) {
            locationClient = new LocationClient(this, this, this);
        }
        if (locationRequest == null) {
            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(3000);
            locationRequest.setFastestInterval(1000);
        }

        Intent intent = getIntent();
        Double latFrom = intent.getDoubleExtra("from_lat", 0);
        Double lonFrom = intent.getDoubleExtra("from_lon", 0);
        Double latTo = intent.getDoubleExtra("to_lat", 0);
        Double lonTo = intent.getDoubleExtra("to_lon", 0);
        pointFrom = new LatLng(latFrom, lonFrom);
        pointTo = new LatLng(latTo, lonTo);
        String link = "http://maps.googleapis.com/maps/api/directions/json?";
        String params = String.format("origin=%f,%f&destination=%f,%f&sensor=false", latFrom, lonFrom, latTo, lonTo);
        String urlString = link + params;

        RequestRoute requestRoute = new RequestRoute();
        requestRoute.execute(urlString);

    }

    @Override
    protected void onStart() {
        super.onStart();
        locationClient.connect();
    }

    @Override
    protected void onStop() {
        if (locationClient.isConnected()) {
            locationClient.removeLocationUpdates(this);
        }
        locationClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = locationClient.getLastLocation();
        if (location != null) {
            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mapResult.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 10));
        }
        locationClient.requestLocationUpdates(locationRequest, this);
    }

    @Override
    public void onDisconnected() {
        //ToDo
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //ToDo
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        }
    }

    private class RequestRoute extends AsyncTask<String, Void, PolylineOptions> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ((ProgressBar)findViewById(R.id.result_progressBar)).setVisibility(View.VISIBLE);
        }

        @Override
        protected PolylineOptions doInBackground(String... params) {
            PolylineOptions polyOptions = new PolylineOptions();
            try {
                URL url = new URL(params[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                byte[] response = new byte[inputStream.available()];
                inputStream.read(response);
                JSONObject jsonObject = new JSONObject(new String(response));
                JSONArray routes = jsonObject.getJSONArray("routes");
                JSONArray legs = ((JSONObject)routes.get(0)).getJSONArray("legs");
                JSONArray steps = ((JSONObject)legs.get(0)).getJSONArray("steps");
                String encoded_poly = null;
                for (int i = 0; i < steps.length(); i++) {
                    encoded_poly = steps.getJSONObject(i).getJSONObject("polyline").getString("points");
                    polyOptions.addAll(decodePoints(encoded_poly));
                }
                httpURLConnection.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                polyOptions = null;
            } catch (IOException e) {
                e.printStackTrace();
                polyOptions = null;
            } catch (JSONException e) {
                e.printStackTrace();
                polyOptions = null;
            }
            return polyOptions;
        }

        @Override
        protected void onPostExecute(PolylineOptions polyOptions) {
            super.onPostExecute(polyOptions);
            if (polyOptions != null) {
                LatLng center = calculateCenterPoint(pointFrom, pointTo, currentLocation);
                mapResult.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 8));
                polyOptions.width(20);
                polyOptions.color(Color.BLACK);
                mapResult.addPolyline(polyOptions);
                ((ProgressBar)findViewById(R.id.result_progressBar)).setVisibility(View.INVISIBLE);
            }
        }

        private List<LatLng> decodePoints(String encoded_points){
            ArrayList<LatLng> poly = new ArrayList<LatLng>();
            int index = 0, len = encoded_points.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded_points.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded_points.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),(((double) lng / 1E5)));
                poly.add(p);
            }
            return poly;
        }

        private LatLng calculateCenterPoint(LatLng p1, LatLng p2, LatLng p3) {
            double x1 = p1.latitude;
            double y1 = p1.longitude;
            double x2 = p2.latitude;
            double y2 = p2.longitude;
            double x3 = p3.latitude;
            double y3 = p3.longitude;
            double x = (Math.max(x1, Math.max(x2,x3))+ Math.min(x1, Math.min(x2,x3))) / 2;
            double y = (Math.max(y1, Math.max(y2,y3))+ Math.min(y1, Math.min(y2,y3))) / 2;
            return new LatLng(x,y);
        }
    }
}
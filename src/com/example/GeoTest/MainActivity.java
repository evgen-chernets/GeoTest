package com.example.GeoTest;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: e.chernets
 * Date: 18.09.13
 * Time: 23:14
 * To change this template use File | Settings | File Templates.
 */
public class MainActivity extends FragmentActivity {

    private LatLng pointFrom, pointTo;
    private GoogleMap mapFrom, mapTo;
    private Geocoder geocoder;
    private List<Address> decodedAddresses;
    private InputMethodManager inputMethodManager;
    private TabHost tabHost;
    private ListView listViewFrom, listViewTo;
    private EditText editTextFrom, editTextTo;
    private Button route_button;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        inputMethodManager = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);

        tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec tabSpec;

        tabSpec = tabHost.newTabSpec("tab1");
        tabSpec.setIndicator(getResources().getString(R.string.from));
        tabSpec.setContent(R.id.tab_from);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tab2");
        tabSpec.setIndicator(getResources().getString(R.string.to));
        tabSpec.setContent(R.id.tab_to);
        tabHost.addTab(tabSpec);

        View.OnKeyListener onKeyListener = new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction() == KeyEvent.ACTION_DOWN) &&(keyCode == KeyEvent.KEYCODE_ENTER)) {
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    decodeAddress(((EditText) v).getText().toString());
                    return true;
                }
                return false;
            }
        };
        editTextTo = (EditText) findViewById(R.id.editText_to);
        editTextTo.setOnKeyListener(onKeyListener);
        editTextFrom = (EditText) findViewById(R.id.editText_from);
        editTextFrom.setOnKeyListener(onKeyListener);

        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goToAddressFromListByIndex(position);
            }
        };
        listViewTo = (ListView)findViewById(R.id.listView_to);
        listViewTo.setOnItemClickListener(onItemClickListener);
        listViewFrom = (ListView)findViewById(R.id.listView_from);
        listViewFrom.setOnItemClickListener(onItemClickListener);

        geocoder = new Geocoder(this);

        mapFrom = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map_from)).getMap();
        mapFrom.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mapTo = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map_from)).getMap();
        mapTo.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        route_button = (Button)findViewById(R.id.button_route);
        route_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goForResult();
            }
        });
    }

    private void decodeAddress(String initialString) {
        if (geocoder != null) {
            try {
                decodedAddresses = geocoder.getFromLocationName(initialString, 7);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String[] stringAddresses = new String[decodedAddresses.size()];
        Address adr;
        if (decodedAddresses != null) {
            for(int i = 0; i < decodedAddresses.size(); i++) {
                adr = decodedAddresses.get(i);
                stringAddresses[i] = "";
                for (int j = 0; j < adr.getMaxAddressLineIndex(); j++) {
                    if (adr.getAddressLine(j) != null) {
                        stringAddresses[i] = stringAddresses[i] + adr.getAddressLine(j) + " ";
                    }
                }
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stringAddresses);
        if (tabHost.getCurrentTab() == 0) {
            listViewFrom.setAdapter(adapter);
        } else {
            listViewTo.setAdapter(adapter);
        }

    }

    private void goToAddressFromListByIndex(int index) {
        if ((index >= 0)&&(index < 7)) {
            Address adr = decodedAddresses.get(index);
            LatLng target = new LatLng(adr.getLatitude(), adr.getLongitude());
            if (adr != null) {
                if (tabHost.getCurrentTab() == 0) {
                    pointFrom = target;
                } else {
                    pointTo = target;
                }

                if ((pointFrom != null)&&(pointTo != null)) {
                    if (route_button != null){
                        route_button.setEnabled(true);
                    }
                }

                GoogleMap map;
                if (tabHost.getCurrentTab() == 0) {
                    map = mapFrom;
                } else {
                    map = mapTo;
                }
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(target, 15));
                map.clear();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.draggable(false);
                markerOptions.position(target);
                map.addMarker(markerOptions);
            }
        }
    }

    private void goForResult() {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("from_lat", pointFrom.latitude);
        intent.putExtra("from_lon", pointFrom.longitude);
        intent.putExtra("to_lat", pointTo.latitude);
        intent.putExtra("to_lon", pointTo.longitude);
        startActivity(intent);
    }
}

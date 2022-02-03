package com.milplateaux.saibo_android;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.widget.TextView;

import com.khi.authlibrary.khiAuth;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapxus.map.mapxusmap.api.map.MapViewProvider;
import com.mapxus.map.mapxusmap.api.map.MapxusMap;
import com.mapxus.map.mapxusmap.api.map.interfaces.OnMapxusMapReadyCallback;
import com.mapxus.map.mapxusmap.api.map.model.MapxusMapOptions;
import com.mapxus.map.mapxusmap.impl.MapboxMapViewProvider;
import com.mapxus.map.mapxusmap.positioning.ErrorInfo;
import com.mapxus.map.mapxusmap.positioning.IndoorLocation;
import com.mapxus.map.mapxusmap.positioning.IndoorLocationProvider;
import com.mapxus.map.mapxusmap.positioning.IndoorLocationProviderListener;

public class MainActivity extends AppCompatActivity implements OnMapxusMapReadyCallback {
    private MapView mapView;
    private MapxusMap mapxusMap;
    private MapViewProvider mapViewProvider;
    private IndoorLocationProvider mapxusPositioningProvider;
    private TextView infoTextView;
    private Double longitude = 0.0;
    private Double latitude = 0.0;
    private Double oslongitude = 0.0;
    private Double oslatitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        khiAuth auth = new khiAuth();
        auth.MapInit(getApplicationContext(),"2011601020895A1140001005719001");

        setContentView(R.layout.activity_main);

        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        MapxusMapOptions mapxusMapOptions = new
                MapxusMapOptions().setBuildingId("b417724ce09f4828977b77ad35e932db").setFloor("2F");
        mapViewProvider = new MapboxMapViewProvider(this, mapView, mapxusMapOptions);
        mapViewProvider.getMapxusMapAsync(this);

        infoTextView = (TextView) findViewById(R.id.infoTextView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        if (mapxusMap != null) {
            mapxusMap.onResume();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        if (mapxusMap != null) {
            mapxusMap.onPause();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (mapViewProvider != null) {
            mapViewProvider.onDestroy();
        };
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onMapxusMapReady(MapxusMap mapxusMap) {
        this.mapxusMap = mapxusMap;
        mapxusPositioningProvider = new MapxusPositioningProvider(this, getApplicationContext());
        mapxusPositioningProvider.addListener(new IndoorLocationProviderListener() {
            @Override
            public void onProviderStarted() {
            }

            @Override
            public void onProviderStopped() {
            }

            @Override
            public void onProviderError(ErrorInfo error) {
            }

            @Override
            public void onIndoorLocationChange(IndoorLocation indoorLocation) {
                Double nowlatitude = indoorLocation.getLatitude();
                Double nowlongitude = indoorLocation.getLongitude();
                if (latitude != nowlatitude && longitude != nowlongitude) {
                    latitude = nowlatitude;
                    longitude = nowlongitude;
                    showInformation();
                }
            }

            @Override
            public void onCompassChanged(float angle, int sensorAccuracy) {
            }

        });
        mapxusMap.setLocationProvider(mapxusPositioningProvider);
    }

    private void showInformation() {
        infoTextView.setText(latitude.toString() + "," + longitude.toString() + "(" + oslatitude.toString() + "," + oslongitude.toString() + ")");
    }

    ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestMultiplePermissions(), result -> {
                        Boolean fineLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_FINE_LOCATION, false);
                        Boolean coarseLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_COARSE_LOCATION,false);
                        if (fineLocationGranted != null && fineLocationGranted) {
                            // Precise location access granted.
                        } else if (coarseLocationGranted != null && coarseLocationGranted) {
                            // Only approximate location access granted.
                        } else {
                            // No location access granted.
                        }
                    }
            );

}
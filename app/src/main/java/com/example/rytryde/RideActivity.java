package com.example.rytryde;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rytryde.utils.LocationHelper;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;


public class RideActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = RideActivity.class.getSimpleName();

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    EditText startLocET;
    EditText destinationLocET;
    Intent i;
    private GoogleMap map;
    private Location lastKnownLocation;
    private CameraPosition cameraPosition;
    private LatLng newCoordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride);

        getIntent();

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        startLocET = findViewById(R.id.et_start_address);
        destinationLocET = findViewById(R.id.et_destination_address);

        startLocET.setOnClickListener(v -> {
            i = new Intent(RideActivity.this, LocationActivity.class);
            i.putExtra("caller", "RideActivity-start");
            startActivity(i);
        });
        startLocET.setOnClickListener(v -> {
            i = new Intent(RideActivity.this, LocationActivity.class);
            i.putExtra("caller", "RideActivity-dest");
            startActivity(i);
        });
    }


    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        LocationHelper.locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationHelper.locationPermissionGranted = true;
                }
            }
        }
        LocationHelper.updateLocationUI(TAG, map, RideActivity.this);
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        this.map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.map_custom_info_contents,
                        (FrameLayout) findViewById(R.id.map), false);

                TextView title = infoWindow.findViewById(R.id.map_title);
                title.setText(marker.getTitle());

                TextView snippet = infoWindow.findViewById(R.id.snippet);
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });

        // Prompt the user for permission.
        LocationHelper.getLocationPermission(TAG, map, RideActivity.this);

        // Turn on the My Location layer and the related control on the map.
        LocationHelper.updateLocationUI(TAG, map, RideActivity.this);

        // Get the current location of the device and set the position of the map.
        lastKnownLocation = LocationHelper.getDeviceLocation(TAG, map, RideActivity.this);


    }

}
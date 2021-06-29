package com.example.rytryde;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.rytryde.adapters.PlacesAutoCompleteAdapter;
import com.example.rytryde.service.app.LocationService;
import com.example.rytryde.utils.LocationHelper;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = LocationActivity.class.getSimpleName();

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 22;
    private GoogleMap map;
    private Location lastKnownLocation;
    private LatLng pinLocation;
    private String addressLine;
    private EditText addressET;
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    private PlacesClient placesClient;
    private RelativeLayout mapLayout;
    private TextView addLocTV;
    private AutocompleteSessionToken token;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        String calledBy = getIntent().getStringExtra("caller");

        placesClient = LocationHelper.initialisePlaces(this);

        addressET = findViewById(R.id.et_location_address);
        mapLayout = findViewById(R.id.confirm_address_map_wrapper);
        addLocTV = findViewById(R.id.addLocTV);
        toolbar = findViewById(R.id.locationToolbar);

        setSupportActionBar(toolbar);


        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_locationActivity);
        mapFragment.getMapAsync(this);


        LocationHelper.updateLocationUI(TAG, map, LocationActivity.this);

        addLocTV.setOnClickListener(v -> {
            if (calledBy.equals(LocationHelper.destination))
                LocationService.saveDestLocation(new LatLng(map.getCameraPosition().target.latitude, map.getCameraPosition().target.longitude));
            else if (calledBy.equals(LocationHelper.start))
                LocationService.saveStartLocation(new LatLng(map.getCameraPosition().target.latitude, map.getCameraPosition().target.longitude));
            finish();
        });

    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        LocationHelper.locationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LocationHelper.locationPermissionGranted = true;
            }
        }

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
                        (FrameLayout) findViewById(R.id.map_locationActivity), false);

                TextView title = infoWindow.findViewById(R.id.map_title);
                title.setText(marker.getTitle());

                TextView snippet = infoWindow.findViewById(R.id.snippet);
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });


        // Prompt the user for permission.
        LocationHelper.getLocationPermission(TAG, map, this);

        // Turn on the My Location layer and the related control on the map.
        LocationHelper.updateLocationUI(TAG, map, this);

        // Get the current location of the device and set the position of the map.
        lastKnownLocation = LocationHelper.getDeviceLocation(TAG, map, this);
        if (lastKnownLocation != null) {
            pinLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            try {
                addressET.setText(LocationHelper.getAddress(TAG, pinLocation, this));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Get new coordinates if marker is moved
        LocationHelper.getNewLocation(TAG, map, this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.location_toolbar, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.search) {
            onSearchCalled();
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        } else return false;

    }


    public void onSearchCalled() {
        // Set the fields to specify which types of place data to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .setLocationBias(LocationHelper.getBounds(lastKnownLocation, 59000))
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);

                //Toast.makeText(LocationActivity.this, "ID: " + place.getId() + "address:" + place.getAddress() + "Name:" + place.getName() + " latlong: " + place.getLatLng(), Toast.LENGTH_LONG).show();
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), LocationHelper.DEFAULT_ZOOM));
                addressET.setText(place.getAddress());

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(LocationActivity.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

}
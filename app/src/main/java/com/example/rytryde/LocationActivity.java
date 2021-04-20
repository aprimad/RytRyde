package com.example.rytryde;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rytryde.utils.LocationHelper;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.IOException;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = LocationActivity.class.getSimpleName();

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GoogleMap map;
    private Location lastKnownLocation;
    private LatLng pinLocation;
    private String addressLine;
    private EditText addressET;
    private Button clearButton;
    private PlacesClient placesClient;
    private String apiKey = null;
    private ApplicationInfo applicationInfo = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        getIntent();

        addressET = findViewById(R.id.et_location_address);
        clearButton = findViewById(R.id.places_autocomplete_clear_button);

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_locationActivity);
        mapFragment.getMapAsync(this);


        addressET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    addressET.setSelection(addressET.getText().length());
                    clearButton.setVisibility(View.VISIBLE);
                } else {
                    clearButton.setVisibility(View.GONE);
                }
            }
        });

        clearButton.setOnClickListener(v -> {
            addressET.setText("");
        });

        LocationHelper.updateLocationUI(TAG, map, LocationActivity.this);

        try {
            applicationInfo = this.getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (applicationInfo != null) {
            apiKey = applicationInfo.metaData.getString("com.google.android.geo.API_KEY");
        }
        if (!Places.isInitialized() & apiKey != null) {
            Places.initialize(getApplicationContext(), apiKey);
            // Create a new Places client instance.
            placesClient = Places.createClient(this);
        }

        addressET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
                // and once again when the user makes a selection (for example when calling fetchPlace()).
                AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();


                // Use the builder to create a FindAutocompletePredictionsRequest.
                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                        .setTypeFilter(TypeFilter.ADDRESS)
                        .setSessionToken(token)
                        //.setLocationBias(lastKnownLocation)
                        .setQuery(s.toString())
                        .build();

                placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
                    for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                        Log.i(TAG, prediction.getPlaceId());
                        Log.i(TAG, prediction.getPrimaryText(null).toString());
                    }
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                    }
                });

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
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
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationHelper.locationPermissionGranted = true;
                }
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
}
package com.example.rytryde;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rytryde.adapters.PlacesAutoCompleteAdapter;
import com.example.rytryde.utils.LocationHelper;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.IOException;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback, PlacesAutoCompleteAdapter.ClickListener {

    private static final String TAG = LocationActivity.class.getSimpleName();

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GoogleMap map;
    private Location lastKnownLocation;
    private LatLng pinLocation;
    private String addressLine;
    private AutoCompleteTextView addressET;
    private Button clearButton;
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    private RecyclerView recyclerView;
    private PlacesClient placesClient;
    private RelativeLayout mapLayout;
    private AutocompleteSessionToken token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        getIntent();

        placesClient = LocationHelper.initialisePlaces(this);

        addressET = findViewById(R.id.et_location_address);
        clearButton = findViewById(R.id.places_autocomplete_clear_button);
        recyclerView = findViewById(R.id.places_recycler_view);
        mapLayout = findViewById(R.id.confirm_address_map_wrapper);

        KeyListener listener = addressET.getKeyListener();
        addressET.setKeyListener(null);


        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_locationActivity);
        mapFragment.getMapAsync(this);

        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        mAutoCompleteAdapter.setClickListener(this);
        recyclerView.setAdapter(mAutoCompleteAdapter);
        mAutoCompleteAdapter.notifyDataSetChanged();

        addressET.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                clearButton.setVisibility(View.VISIBLE);
                addressET.setKeyListener(listener);
            } else {
                clearButton.setVisibility(View.GONE);
                addressET.setKeyListener(null);
            }
        });

        clearButton.setOnClickListener(v -> {
            // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
            // and once again when the user makes a selection (for example when calling fetchPlace()).
            token = AutocompleteSessionToken.newInstance();
            addressET.setText("");
        });

        LocationHelper.updateLocationUI(TAG, map, LocationActivity.this);


        addressET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("") && addressET.getKeyListener() != null) {
                    mAutoCompleteAdapter.setToken(token);
                    mAutoCompleteAdapter.getFilter().filter(s.toString());
                    Log.e(TAG, "adapter count" + Integer.toString(mAutoCompleteAdapter.getItemCount()));

                    if (mAutoCompleteAdapter.getItemCount() > 0)
                        Log.i(TAG, mAutoCompleteAdapter.getItem(0).getPrimaryText());
                    if (recyclerView.getVisibility() == View.GONE) {
                        recyclerView.setVisibility(View.VISIBLE);
                        mapLayout.setVisibility(View.GONE);

                    }
                } else {
                    if (recyclerView.getVisibility() == View.VISIBLE) {
                        recyclerView.setVisibility(View.GONE);
                        mapLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

    }

    @Override
    public void click(Place place) {
        Toast.makeText(this, place.getAddress() + ", " + place.getLatLng().latitude + place.getLatLng().longitude, Toast.LENGTH_SHORT).show();
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
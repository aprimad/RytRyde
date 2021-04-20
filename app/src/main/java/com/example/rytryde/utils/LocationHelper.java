package com.example.rytryde.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.rytryde.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationHelper {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int DEFAULT_ZOOM = 15;
    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private static final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    public static boolean locationPermissionGranted;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private static Location lastKnownLocation;
    private static LatLng pinCoordinates;
    // The entry point to the Fused Location Provider.
    private static FusedLocationProviderClient fusedLocationProviderClient;

    private static String mTAG;
    private static Context mContext;
    private static GoogleMap googleMap;

    /**
     * Prompts the user for permission to use the device location.
     */
    public static void getLocationPermission(String TAG, GoogleMap map, Context context) {
        googleMap = map;
        mTAG = TAG;
        mContext = context;

        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(context.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    public static void updateLocationUI(String TAG, GoogleMap map, Context context) {
        if (map == null) {
            return;
        }
        try {
            if (LocationHelper.locationPermissionGranted) {
                Log.e("location", "granted");
                getDeviceLocation(TAG, map, context);
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                Log.e("location", "denied");
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                //getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    public static Location getDeviceLocation(String TAG, GoogleMap map, Context context) {
        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient((Activity) context);
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */

        try {
            if (LocationHelper.locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener((Activity) context, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                LatLng position = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }

        return lastKnownLocation;
    }

    /**
     * Gets the location of the pin change.
     */

    public static LatLng getNewLocation(String TAG, GoogleMap map, Context activityContext) {

        try {
            if (LocationHelper.locationPermissionGranted) {
                map.setOnCameraIdleListener(() -> {

                    pinCoordinates = new LatLng(map.getCameraPosition().target.latitude, map.getCameraPosition().target.longitude);
                    Log.e("new coords", Double.toString(pinCoordinates.longitude));
                    try {
                        EditText loc = ((Activity) activityContext).findViewById(R.id.et_location_address);
                        loc.setText(LocationHelper.getAddress(TAG, pinCoordinates, activityContext));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }

        return pinCoordinates;
    }

    /**
     * Gets the address from the LatLng.
     */
    public static String getAddress(String TAG, LatLng coordinates, Context context) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        String addressLine = "";
        geocoder = new Geocoder(context, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(coordinates.latitude, coordinates.longitude, 1);
            addressLine = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        return addressLine;
    }

}

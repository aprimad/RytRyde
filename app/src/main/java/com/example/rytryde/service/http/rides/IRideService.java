package com.example.rytryde.service.http.rides;

import com.example.rytryde.data.model.Waypoint;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.util.ArrayList;

import okhttp3.Response;

public interface IRideService {

    Response upcomingRides();

    Response offerRides(String pickup, String dropoff, LatLng pickupLL, LatLng dropoffLL, String rideDate, String rideTime, String rideType, String[] regularRideDate, ArrayList<Waypoint> waypoints) throws JSONException;

    Response parseRoutes(LatLng src, LatLng dest, String apiKey);
}

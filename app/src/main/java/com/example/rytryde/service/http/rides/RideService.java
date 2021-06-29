package com.example.rytryde.service.http.rides;

import android.util.Log;

import com.example.rytryde.App;
import com.example.rytryde.data.model.Waypoint;
import com.example.rytryde.service.app.AppService;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public class RideService implements IRideService {
    public static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static String domain = "https://rytryde.com";
    public static String route = "/api";
    public static String base = domain + route;
    public static String upcoming_rides = base + "/upcoming-rides";
    public static String offerRide = base + "/offer-ride";
    Response response = null;
    private OkHttpClient httpClient = App.getApp().getOkHttpClient();

    @Override
    public Response upcomingRides() {

        Log.e("auth", "hi" + AppService.getUser().getAuthorization());
        Request request = new Request.Builder()
                .url(upcoming_rides)
                .addHeader("authorization", "Bearer " + AppService.getUser().getAuthorization())
                .get()
                .build();

        try {
            response = httpClient.newCall(request).execute();

            if (response.isSuccessful()) {

                return response;
            } else Log.e("response failure", response.body().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public Response offerRides(String pickup, String dropoff, LatLng pickupLL, LatLng dropoffLL, String rideDate, String rideTime, String rideType, String[] regularRideDate, ArrayList<Waypoint> waypoints) throws JSONException {
        Gson gson = new Gson();

        JsonObject userinfo = new JsonObject();
        userinfo.addProperty("pick_up_address", pickup);
        userinfo.addProperty("drop_off_address", dropoff);
        userinfo.addProperty("pick_up_latitude", String.valueOf((pickupLL.latitude)));
        userinfo.addProperty("pick_up_longitude", String.valueOf((pickupLL.longitude)));
        userinfo.addProperty("drop_off_latitude", String.valueOf((dropoffLL.latitude)));
        userinfo.addProperty("drop_off_longitude", String.valueOf((dropoffLL.longitude)));
        userinfo.addProperty("ride_date", rideDate);
        userinfo.addProperty("ride_time", rideTime);
        userinfo.addProperty("ride_type", rideType);
        userinfo.add("regular_ride_dates", new Gson().toJsonTree(regularRideDate));
        userinfo.add("way_points", new Gson().toJsonTree(waypoints));


        Request request = new Request.Builder()
                .url(offerRide)
                .addHeader("authorization", "Bearer " + AppService.getUser().getAuthorization())
                .post(RequestBody.create(JSON, gson.toJson(userinfo)))
                .build();

        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            Log.e("req", buffer.readUtf8());
        } catch (final IOException e) {

        }

        try {
            response = httpClient.newCall(request).execute();
            return response;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public Response parseRoutes(LatLng src, LatLng dest, String GoogleMapsKey) {

        String sourceStr = +src.latitude + "," + src.longitude;
        String destStr = +dest.latitude + "," + dest.longitude;


        String url = "https://maps.googleapis.com/maps/api/directions/json?&origin=" + sourceStr + "&destination=" + destStr + "&mode=driving&key=" + GoogleMapsKey;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try {
            response = httpClient.newCall(request).execute();

            if (response.isSuccessful()) {

                return response;
            } else Log.e("response failure", response.body().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

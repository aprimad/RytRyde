package com.example.rytryde.service.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.text.TextUtils;

import com.example.rytryde.App;
import com.google.android.gms.maps.model.LatLng;

public class LocationService {
    private static final String SETTINGS_NAME = "RytRyde.settings";
    private static final String RECENT_LOCATION_LAT = "recentloclat";
    private static final String RECENT_LOCATION_LONG = "recentlong";
    private static final String START_LOCATION_LAT = "startlat";
    private static final String START_LOCATION_LONG = "startlong";
    private static final String DEST_LOCATION_LAT = "destloclat";
    private static final String DEST_LOCATION_LONG = "destlong";
    private static final String POINTS = "points";
    private static final String RECENT_LOCATION_PROVIDER = "provider";


    public static void saveRecentLocation(String latitude, String longitude, String provider) {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(RECENT_LOCATION_LAT, latitude);
        editor.putString(RECENT_LOCATION_LONG, longitude);
        editor.putString(RECENT_LOCATION_PROVIDER, provider);

        editor.apply();
    }

    public static Location getRecentLocation() {
        Location recentLocation;
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        String longitude = preferences.getString(RECENT_LOCATION_LONG, null);
        String latitude = preferences.getString(RECENT_LOCATION_LAT, null);
        String provider = preferences.getString(RECENT_LOCATION_PROVIDER, null);

        recentLocation = new Location(provider);
        recentLocation.setLongitude(Double.parseDouble(longitude));
        recentLocation.setLatitude(Double.parseDouble(latitude));

        return recentLocation;
    }

    public static void saveStartLocation(LatLng start) {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(START_LOCATION_LAT, Double.toString(start.latitude));
        editor.putString(START_LOCATION_LONG, Double.toString(start.longitude));

        editor.apply();

    }

    public static LatLng getStartLocation() {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        String longitude = preferences.getString(START_LOCATION_LONG, null);
        String latitude = preferences.getString(START_LOCATION_LAT, null);

        if (longitude == null || latitude == null)
            return null;
        else
            return new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
    }

    public static void saveDestLocation(LatLng start) {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(DEST_LOCATION_LAT, Double.toString(start.latitude));
        editor.putString(DEST_LOCATION_LONG, Double.toString(start.longitude));

        editor.apply();

    }

    public static LatLng getDestLocation() {

        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        String longitude = preferences.getString(DEST_LOCATION_LONG, null);
        String latitude = preferences.getString(DEST_LOCATION_LAT, null);
        if (longitude == null || latitude == null)
            return null;
        else
            return new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
    }

    public static void savePoints(String points) {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(POINTS, points);
        editor.apply();
    }

    public static String getPoints() {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        String sessionId = preferences.getString(POINTS, null);
        if (TextUtils.isEmpty(sessionId)) {
            return null;
        }
        return sessionId;
    }

    public static void clearStartLoc() {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        preferences.edit().remove(START_LOCATION_LONG).apply();
        preferences.edit().remove(START_LOCATION_LAT).apply();
    }

    public static void clearDestLoc() {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        preferences.edit().remove(DEST_LOCATION_LONG).apply();
        preferences.edit().remove(DEST_LOCATION_LAT).apply();
    }

}

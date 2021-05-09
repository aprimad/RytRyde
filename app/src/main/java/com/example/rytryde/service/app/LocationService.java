package com.example.rytryde.service.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.example.rytryde.App;

public class LocationService {
    private static final String SETTINGS_NAME = "RytRyde.settings";
    private static final String RECENT_LOCATION_LAT = "recentloclat";
    private static final String RECENT_LOCATION_LONG = "recentlong";
    private static final String RECENT_LOCATION_PROVIDER = "provider";


    public static void saveRecentLocation(String latitude, String longitude, String provider) {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(RECENT_LOCATION_LAT, latitude);
        editor.putString(RECENT_LOCATION_LONG, longitude);
        editor.putString(RECENT_LOCATION_PROVIDER, provider);

        editor.commit();
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

}

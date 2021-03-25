package com.example.rytryde.service.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.rytryde.App;
import com.example.rytryde.data.model.UpcomingRidesData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class UpcomingRidesService {
    private static final String SETTINGS_NAME = "RytRyde.settings";
    private static final String UPCOMING_RIDES = "Upcoming_Rides";

    private static Gson gson = new GsonBuilder().create();

    public static void saveUpcomingRides(String upcomingRides) {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(UPCOMING_RIDES, upcomingRides);
        editor.commit();
    }

    public static UpcomingRidesData getUpcomingRides() {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        String sessionId = preferences.getString(UPCOMING_RIDES, null);
        if (TextUtils.isEmpty(sessionId)) {
            return null;
        }
        return gson.fromJson(sessionId, new TypeToken<ArrayList<UpcomingRidesData>>() {
        }.getType());
    }
}

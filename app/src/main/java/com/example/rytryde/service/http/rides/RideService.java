package com.example.rytryde.service.http.rides;

import android.util.Log;

import com.example.rytryde.App;
import com.example.rytryde.service.app.AppService;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RideService implements IRideService {

    public static String domain = "https://rytryde.com";
    public static String route = "/api";
    public static String base = domain + route;
    public static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static String upcoming_rides = base + "/upcoming-rides";
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
}

package com.example.rytryde.service.http.vehicle;

import android.util.Log;

import com.example.rytryde.App;
import com.example.rytryde.service.app.AppService;
import com.example.rytryde.utils.Constants;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VehicleService implements IVehicleService {

    public static String vehicle_details = Constants.base + "/vehicle-details";

    Response response = null;
    private OkHttpClient httpClient = App.getApp().getOkHttpClient();

    @Override
    public Response getVehicleDetails() {
        Request request = new Request.Builder()
                .url(vehicle_details)
                .addHeader("authorization", "Bearer " + AppService.getUser().getAuthorization())
                .get()
                .build();

        try {

            response = httpClient.newCall(request).execute();

            if (response.isSuccessful()) {
                return response;
            } else Log.e("response failure terms", response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

package com.example.rytryde.service.http.cms;

import android.util.Log;

import com.example.rytryde.App;
import com.example.rytryde.service.app.AppService;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CmsSevice implements ICmsService {

    public static String domain = "https://rytryde.com";
    public static String route = "/api";
    public static String base = domain + route;
    public static String terms_and_conditions = base + "/terms-and-conditions";
    public static String privacy_policy = base + "/privacy-policy";
    public static String about_us = base + "/about-us";
    public static String faq = base + "/faqs";

    Response response = null;
    private OkHttpClient httpClient = App.getApp().getOkHttpClient();

    @Override
    public Response termsAndConditions() {
        Request request = new Request.Builder()
                .url(terms_and_conditions)
                .addHeader("authorization", "Bearer " + AppService.getUser().getAuthorization())
                .get()
                .build();

        try {
            Log.e("req", terms_and_conditions);
            response = httpClient.newCall(request).execute();

            if (response.isSuccessful()) {
                return response;
            } else Log.e("response failure terms", response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Response privacyPolicy() {
        Request request = new Request.Builder()
                .url(privacy_policy)
                .addHeader("authorization", "Bearer " + AppService.getUser().getAuthorization())
                .get()
                .build();

        try {
            response = httpClient.newCall(request).execute();

            if (response.isSuccessful()) {

                return response;
            } else Log.e("response failure", response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Response aboutUs() {
        Request request = new Request.Builder()
                .url(about_us)
                .addHeader("authorization", "Bearer " + AppService.getUser().getAuthorization())
                .get()
                .build();

        try {
            response = httpClient.newCall(request).execute();

            if (response.isSuccessful()) {

                return response;
            } else Log.e("response failure", response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Response faq(int page, int limit) {
        Request request = new Request.Builder()
                .url(faq)
                .addHeader("authorization", "Bearer " + AppService.getUser().getAuthorization())
                .addHeader("page", String.valueOf(page))
                .addHeader("limit", String.valueOf(limit))
                .get()
                .build();

        try {
            response = httpClient.newCall(request).execute();

            if (response.isSuccessful()) {

                return response;
            } else Log.e("response failure", response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Response loadNextFAQPage(String nextURL) {
        Request request = new Request.Builder()
                .url(nextURL)
                .addHeader("authorization", "Bearer " + AppService.getUser().getAuthorization())
                .get()
                .build();

        try {
            response = httpClient.newCall(request).execute();

            if (response.isSuccessful()) {

                return response;
            } else Log.e("response failure", response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

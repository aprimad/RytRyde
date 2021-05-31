package com.example.rytryde.utils;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.rytryde.service.http.account.AccountService.JSON;

public class AuthInterceptor implements Interceptor {

    private static final String TAG = "AuthInterceptor";


    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        request = request
                .newBuilder()
                .header("Content-Type", "application/json")
                .build();

        Response response = chain.proceed(request);
        // Unauthorized
        if (response.code() == 403) {
            // Get new token

            request = request
                    .newBuilder()
                    .build();

            response = chain.proceed(request);

        } else if (response.code() == 400) {
            Log.e(TAG, "inter");
            OkHttpClient okclient1 = new OkHttpClient.Builder()/*.addInterceptor(App.getApp().getStethoInterceptor())*/
                    .build();


            HashMap<String, String> token = new HashMap<>();
            // token.put("authToken", AppService.getAuthToken());

            Gson gson = new Gson();
            Request request1 = new Request.Builder()
                    .url(request.url())

                    .post(RequestBody.create(JSON, gson.toJson(token)))
                    .build();

            response = okclient1.newCall(request1).execute();
        }

        return response;
    }
}

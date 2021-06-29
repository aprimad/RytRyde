package com.example.rytryde.service.http.account;

import android.util.Log;

import com.example.rytryde.App;
import com.example.rytryde.service.app.AppService;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

import static com.example.rytryde.utils.Constants.base;

public class AccountService implements IAccountService {

    public static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient httpClient = App.getApp().getOkHttpClient();

    public static String login = base + "/login";
    public static String sendOTP = base + "/send-otp";
    public static String verifyOTP = base + "/otp-verification";
    public static String resendOTP = base + "/resend-otp";
    public static String signupVerifyOTP = base + "/verify-otp";
    public static String changePassword = base + "/change-password";
    public static String updateProfile = base + "/profile";
    public static String uploadMedia = base + "/upload-image";

    Response response = null;

    @Override
    public Response login(String email, String password) {
        HashMap<String, String> userinfo = new HashMap<>();
        userinfo.put("email", email);
        userinfo.put("password", password);
        userinfo.put("device_id", "null");
        userinfo.put("device_type", "android");


        Gson gson = new Gson();
        Request request = new Request.Builder()
                .url(login)
                .post(RequestBody.create(JSON, gson.toJson(userinfo)))
                .build();


        try {
            response = httpClient.newCall(request).execute();
            return response;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public Response sendOTP(String firstName, String lastName, String email, String dialCode, String phoneNumber) {
        HashMap<String, String> userinfo = new HashMap<>();
        userinfo.put("email", email);
        userinfo.put("first_name", firstName);
        userinfo.put("last_name", lastName);
        userinfo.put("country_code", dialCode);
        userinfo.put("phone_number", phoneNumber);


        Gson gson = new Gson();
        Request request = new Request.Builder()
                .url(sendOTP)
                .post(RequestBody.create(JSON, gson.toJson(userinfo)))
                .build();

        Log.e("Request", userinfo.toString());

        try {
            response = httpClient.newCall(request).execute();
            return response;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Response verifyOTP(String otpType, String OTP, String phoneNumber) {
        HashMap<String, String> userinfo = new HashMap<>();
        userinfo.put("otp_type", otpType);
        userinfo.put("otp", OTP);
        userinfo.put("phone_number", phoneNumber);
        userinfo.put("device_type", "android");


        Gson gson = new Gson();

        Request request = new Request.Builder()
                .url(verifyOTP)
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
    public Response resendOTP(String otpType, String phoneNumber) {
        HashMap<String, String> userinfo = new HashMap<>();

        userinfo.put("otp_type", otpType);
        userinfo.put("phone_number", phoneNumber);


        Gson gson = new Gson();
        Request request = new Request.Builder()
                .url(resendOTP)
                .post(RequestBody.create(JSON, gson.toJson(userinfo)))
                .build();

        Log.e("Request", userinfo.toString());

        try {
            response = httpClient.newCall(request).execute();
            return response;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Response signup(String firstName, String lastName, String otpCode, String email, String password, String dialCode, String phoneNumber) {
        JSONObject userinfo = new JSONObject();
        try {
            userinfo.put("email", email);
            userinfo.put("first_name", firstName);
            userinfo.put("last_name", lastName);
            userinfo.put("password", password);
            userinfo.put("country_code", dialCode);
            userinfo.put("otp_code", otpCode);
            userinfo.put("phone_number", phoneNumber);
            userinfo.put("gender", "");
            userinfo.put("dob", "");
            userinfo.put("search_radius", 0);
            userinfo.put("social_type", "");
            userinfo.put("social_id", "");
            userinfo.put("country_id", 0);
            userinfo.put("nationality_id", 0);
            userinfo.put("city_id", 0);
            userinfo.put("device_id", "");
            userinfo.put("device_type", "android");
            userinfo.put("state_id", 0);

        } catch (JSONException e) {

        }


        Request request = new Request.Builder()
                .url(signupVerifyOTP)
                .post(RequestBody.create(JSON, userinfo.toString()))
                .build();

        Log.e("Request", userinfo.toString());

        try {
            response = httpClient.newCall(request).execute();
            return response;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Response changePassword(String password, String confirmPassword) {
        HashMap<String, String> userinfo = new HashMap<>();
        userinfo.put("current_password", "\"\"");
        userinfo.put("confirm_password", confirmPassword);
        userinfo.put("password", password);

        Gson gson = new Gson();
        Request request = new Request.Builder()
                .url(changePassword)
                .addHeader("authorization", "Bearer " + AppService.getUser().getAuthorization())
                .post(RequestBody.create(JSON, gson.toJson(userinfo)))
                .build();

        Log.e("Request", userinfo.toString());

        try {
            response = httpClient.newCall(request).execute();
            return response;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Response updateProfile(String firstName, String lastName, String email, String dialCode, String phoneNumber, String gender, String dob, String searchRadius, String mediaID) {
        JSONObject userinfo = new JSONObject();
        try {
            userinfo.put("first_name", firstName);
            userinfo.put("last_name", lastName);
            userinfo.put("email", email);
            userinfo.put("country_code", dialCode);
            userinfo.put("phone_number", phoneNumber);
            userinfo.put("gender", "");
            userinfo.put("dob", "");
            userinfo.put("search_radius", searchRadius);
            userinfo.put("country_id", 0);
            userinfo.put("state_id", 0);
            userinfo.put("city_id", 0);
            userinfo.put("nationality_id", 0);
            userinfo.put("media_id", mediaID);


        } catch (JSONException e) {

        }


        Request request = new Request.Builder()
                .url(updateProfile)
                .addHeader("authorization", "Bearer " + AppService.getUser().getAuthorization())
                .post(RequestBody.create(JSON, userinfo.toString()))
                .build();

        Log.e("Request", userinfo.toString());

        try {
            response = httpClient.newCall(request).execute();
            return response;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Response uploadMedia(String mediaFor, File image) {
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("image", image.getName(),
                        RequestBody.create(MediaType.parse("image/png"), image))
                .addFormDataPart("image_type", mediaFor)
                .build();

        Request request = new Request.Builder()
                .url(uploadMedia)
                .addHeader("authorization", "Bearer " + AppService.getUser().getAuthorization())
                .post(requestBody)
                .build();

        try {
            response = httpClient.newCall(request).execute();
            return response;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}

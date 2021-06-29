package com.example.rytryde.service.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

import com.example.rytryde.App;
import com.example.rytryde.data.model.LoggedInUser;
import com.example.rytryde.data.model.Media;
import com.example.rytryde.utils.CustomKeyChain;
import com.facebook.crypto.Crypto;
import com.facebook.crypto.Entity;
import com.facebook.crypto.exception.CryptoInitializationException;
import com.facebook.crypto.exception.KeyChainException;
import com.facebook.crypto.util.SystemNativeCryptoLibrary;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

public class AppService {
    private static final String SETTINGS_NAME = "teache_reports.settings";
    private static final String AUTH_TOKEN = "auth_token";
    private static final String USER_DATA = "user_data";
    private static final String USERNAME_ENC = "username";
    private static final String PASSWORD_ENC = "password";
    private static final String MOBILE_NUMER = "mobile";
    private static final String DIAL_CODE = "dial";
    private static final String EMAIL = "email";
    private static final String FIRST_NAME = "fname";
    private static final String LAST_NAME = "fname";
    private static final String MEDIA = "media";
    private static LoggedInUser user;
    private static final String COUNTRY = "country";
    private static final String OTP = "otp";
    private static final String CONTACT_PERMISSION = "contact";


    public static void saveUserName(String name) {

        Crypto crypto = new Crypto(
                new CustomKeyChain("ahy83ba=9bJajqlc"),
                new SystemNativeCryptoLibrary());

        try {
            byte[] nameBytes = name.getBytes();
            Entity entity = new Entity(USERNAME_ENC);
            byte[] encryptedBytes = crypto.encrypt(nameBytes, entity);
            String encryptedString = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP);
            SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(USERNAME_ENC, encryptedString);
            editor.apply();

        } catch (CryptoInitializationException | IOException | KeyChainException e) {
            e.printStackTrace();
        }
    }


    public static String getUsername() {
        SharedPreferences settings = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);

        String encryptedDoctorString = settings.getString(USERNAME_ENC, null);
        if (!TextUtils.isEmpty(encryptedDoctorString)) {

            Crypto crypto = new Crypto(
                    new CustomKeyChain("ahy83ba=9bJajqlc"),
                    new SystemNativeCryptoLibrary());

            try {
                byte[] encryptedBytes = Base64.decode(encryptedDoctorString, Base64.NO_WRAP);
                byte[] decryptedBytes = crypto.decrypt(encryptedBytes, new Entity(USERNAME_ENC));
                return new String(decryptedBytes);

            } catch (CryptoInitializationException | IOException | KeyChainException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void saveUserPassword(String password) {

        Crypto crypto = new Crypto(
                new CustomKeyChain("ahy83ba=9bJajqlc"),
                new SystemNativeCryptoLibrary());

        try {
            byte[] passwordBytes = password.getBytes();
            Entity entity = new Entity(PASSWORD_ENC);
            byte[] encryptedBytes = crypto.encrypt(passwordBytes, entity);
            String encryptedPasswordString = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP);
            SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(PASSWORD_ENC, encryptedPasswordString);
            editor.apply();

        } catch (CryptoInitializationException | IOException | KeyChainException e) {
            e.printStackTrace();
        }
    }

    public static String getUserPassword() {

        SharedPreferences settings = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);

        String encryptedDoctorString = settings.getString(PASSWORD_ENC, null);
        if (!TextUtils.isEmpty(encryptedDoctorString)) {

            Crypto crypto = new Crypto(
                    new CustomKeyChain("ahy83ba=9bJajqlc"),
                    new SystemNativeCryptoLibrary());

            try {
                byte[] encryptedBytes = Base64.decode(encryptedDoctorString, Base64.NO_WRAP);
                byte[] decryptedBytes = crypto.decrypt(encryptedBytes, new Entity(PASSWORD_ENC));
                return new String(decryptedBytes);
            } catch (CryptoInitializationException | IOException | KeyChainException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public static void saveAuthToken(String path) {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(AUTH_TOKEN, path);
        editor.apply();
    }

    public static String getAuthToken() {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        String path = preferences.getString(AUTH_TOKEN, "");
        return path;
    }

    public static void saveMobileNumber(String mobile) {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(MOBILE_NUMER, mobile);
        editor.apply();
    }

    public static String getMobileNumber() {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        String path = preferences.getString(MOBILE_NUMER, "");
        return path;
    }

    public static void saveContactPermission(Boolean permission) {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(CONTACT_PERMISSION, permission);
        editor.apply();
    }

    public static Boolean getContactPermission() {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        Boolean path = preferences.getBoolean(CONTACT_PERMISSION, false);
        return path;
    }

    public static void saveDialCode(String code) {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(DIAL_CODE, code);
        editor.apply();
    }

    public static String getDialCode() {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        String path = preferences.getString(DIAL_CODE, "");
        return path;
    }

    public static void saveEmail(String email) {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(EMAIL, email);
        editor.apply();
    }

    public static String getEMAIL() {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        String path = preferences.getString(EMAIL, "");
        return path;
    }

    public static void saveFirstName(String firstName) {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(FIRST_NAME, firstName);
        editor.apply();
    }

    public static String getFirstName() {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        String path = preferences.getString(FIRST_NAME, "");
        return path;
    }

    public static void saveOTP(String otp) {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(OTP, otp);
        editor.apply();
    }

    public static String getOTP() {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        String path = preferences.getString(OTP, "");
        return path;
    }

    public static void saveLastName(String lastName) {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LAST_NAME, lastName);
        editor.apply();
    }

    public static String getLastName() {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        String path = preferences.getString(LAST_NAME, "");
        return path;
    }


    public static void saveUserInfo(LoggedInUser user) {

        Gson gson = new GsonBuilder().create();
        String userString = gson.toJson(user);

        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_DATA, userString);
        editor.apply();
    }

    public static LoggedInUser getUser() {

        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        String sessionId = preferences.getString(USER_DATA, null);
        LoggedInUser user = new Gson().fromJson(sessionId, LoggedInUser.class);
        if (TextUtils.isEmpty(sessionId)) {
            return null;
        }
        return user;
    }


    public static void saveMediaData(String data) {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(MEDIA, data);
        editor.apply();
    }

    public static Media getMedia() {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        String sessionId = preferences.getString(MEDIA, null);
        Media media = new Gson().fromJson(sessionId, Media.class);
        if (TextUtils.isEmpty(sessionId)) {
            return null;
        }
        return media;
    }

    public static void clearAll() {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        preferences.edit().remove(USER_DATA).apply();
        preferences.edit().remove(AUTH_TOKEN).apply();
        preferences.edit().remove(USERNAME_ENC).apply();
        preferences.edit().remove(PASSWORD_ENC).apply();
        user = null;
    }

}

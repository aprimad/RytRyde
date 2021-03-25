package com.example.rytryde.service.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

import com.example.rytryde.App;
import com.example.rytryde.data.model.LoggedInUser;
import com.example.rytryde.utils.CustomKeyChain;
import com.facebook.android.crypto.keychain.SharedPrefsBackedKeyChain;
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
    private static LoggedInUser user;

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
            editor.commit();

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
            editor.commit();

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
        editor.commit();
    }

    public static String getAuthToken() {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        String path = preferences.getString(AUTH_TOKEN, "");
        return path;
    }

    public static void saveUserInfo(LoggedInUser user) {
        if (user == null) {
            return;
        }

        Gson gson = new GsonBuilder().create();
        String veteranString = gson.toJson(user);

        Crypto crypto = new Crypto(
                new SharedPrefsBackedKeyChain(App.getApp()),
                new SystemNativeCryptoLibrary());

        try {
            byte[] doctorBytes = veteranString.getBytes();

            Entity entity = new Entity(USER_DATA);
            byte[] encryptedBytes = crypto.encrypt(doctorBytes, entity);

            String encryptedDoctorString = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP);

            SharedPreferences settings = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor settingsEditor = settings.edit();

            settingsEditor.putString(USER_DATA, encryptedDoctorString);
            settingsEditor.commit();


        } catch (CryptoInitializationException | IOException | KeyChainException e) {
            e.printStackTrace();
        }
    }

    public static LoggedInUser getUser() {

        if (user != null) {
            return user;
        }

        SharedPreferences settings = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);

        String encryptedDoctorString = settings.getString(USER_DATA, null);
        if (!TextUtils.isEmpty(encryptedDoctorString)) {

            Crypto crypto = new Crypto(
                    new SharedPrefsBackedKeyChain(App.getApp()),
                    new SystemNativeCryptoLibrary());

            try {
                byte[] encryptedBytes = Base64.decode(encryptedDoctorString, Base64.NO_WRAP);
                byte[] decryptedBytes = crypto.decrypt(encryptedBytes, new Entity(USER_DATA));

                Gson gson = new GsonBuilder().create();
                user = gson.fromJson(new String(decryptedBytes), LoggedInUser.class);

                return user;

            } catch (CryptoInitializationException | IOException | KeyChainException e) {
                e.printStackTrace();
            }
        }
        return user;
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

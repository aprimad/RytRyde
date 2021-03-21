package com.example.rytryde.service.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.rytryde.App;
import com.example.rytryde.data.model.TermsAndConditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CmsDataService {

    private static final String SETTINGS_NAME = "RytRyde.settings";
    private static final String TERMS_AND_CONDITIONS = "tnc";
    private static final String PRIVACY_POLICY = "pp";
    private static final String ABOUT_US = "ac";

    private static Gson gson = new GsonBuilder().create();

    public static void saveTnCData(String data) {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TERMS_AND_CONDITIONS, data);
        editor.commit();
    }

    public static String getTnCData() {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        String sessionId = preferences.getString(TERMS_AND_CONDITIONS, null);
        TermsAndConditions tnc = new Gson().fromJson(sessionId, TermsAndConditions.class);
        if (TextUtils.isEmpty(sessionId)) {
            return null;
        }
        return tnc.getContent();
    }

    public static String getTnCTitle() {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        String sessionId = preferences.getString(TERMS_AND_CONDITIONS, null);
        TermsAndConditions tnc = new Gson().fromJson(sessionId, TermsAndConditions.class);
        if (TextUtils.isEmpty(sessionId)) {
            return null;
        }
        return tnc.getTitle();
    }

    public static void savePrivacyData(String data) {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PRIVACY_POLICY, data);
        editor.commit();
    }

    public static String getPrivacyData() {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        String sessionId = preferences.getString(PRIVACY_POLICY, null);
        TermsAndConditions tnc = new Gson().fromJson(sessionId, TermsAndConditions.class);
        if (TextUtils.isEmpty(sessionId)) {
            return null;
        }
        return tnc.getContent();
    }

    public static String getPrivacyTitle() {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        String sessionId = preferences.getString(PRIVACY_POLICY, null);
        TermsAndConditions tnc = new Gson().fromJson(sessionId, TermsAndConditions.class);
        if (TextUtils.isEmpty(sessionId)) {
            return null;
        }
        return tnc.getTitle();
    }

    public static void saveAboutUsData(String data) {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ABOUT_US, data);
        editor.commit();
    }

    public static String getAboutUsData() {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        String sessionId = preferences.getString(ABOUT_US, null);
        TermsAndConditions tnc = new Gson().fromJson(sessionId, TermsAndConditions.class);
        if (TextUtils.isEmpty(sessionId)) {
            return null;
        }
        return tnc.getContent();
    }

    public static String getAboutUsTitle() {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        String sessionId = preferences.getString(ABOUT_US, null);
        TermsAndConditions tnc = new Gson().fromJson(sessionId, TermsAndConditions.class);
        if (TextUtils.isEmpty(sessionId)) {
            return null;
        }
        return tnc.getTitle();
    }
}

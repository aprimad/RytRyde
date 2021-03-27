package com.example.rytryde.service.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.example.rytryde.App;
import com.example.rytryde.data.model.FAQData;
import com.example.rytryde.data.model.FAQItemData;
import com.example.rytryde.data.model.TermsAndConditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

public class CmsDataService {

    private static final String SETTINGS_NAME = "RytRyde.settings";
    private static final String TERMS_AND_CONDITIONS = "tnc";
    private static final String PRIVACY_POLICY = "pp";
    private static final String ABOUT_US = "ac";
    private static final String FAQ = "faq";

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

    public static void saveFAQData(String data) {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(FAQ, data);
        editor.commit();
    }

    public static String getNextFAQPageURL() {
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        String sessionId = preferences.getString(FAQ, null);
        FAQData data = new Gson().fromJson(sessionId, FAQData.class);
        if (TextUtils.isEmpty(sessionId)) {
            return null;
        }
        return data.getNext_page_url();
    }

    public static List<FAQItemData> getFAQItems() {
        FAQData faqData;
        SharedPreferences preferences = App.getApp().getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        String sessionId = preferences.getString(FAQ, null);
        if (TextUtils.isEmpty(sessionId)) {
            return null;
        } else
            faqData = gson.fromJson(sessionId, FAQData.class);

        Log.e("faq", faqData.getFirst_page_url());

        return faqData.getData();
    }

}

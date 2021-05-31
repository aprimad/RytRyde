package com.example.rytryde.service.http.contacts;

import android.util.Log;

import com.example.rytryde.App;
import com.example.rytryde.data.model.Contacts;
import com.example.rytryde.data.model.SyncContact;
import com.example.rytryde.service.app.AppService;
import com.example.rytryde.utils.Constants;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public class ContactsService implements IContactsService {
    public static String syncContacts = Constants.base + "/user-sync-contacts";
    public static String myContacts = Constants.base + "/my-contacts";
    public static String addContacts = Constants.base + "/add-contacts";
    Response response = null;
    private OkHttpClient httpClient = App.getApp().getOkHttpClient();

    @Override
    public Response syncContacts(List<SyncContact> contactsList) {
        Gson gson = new Gson();
        HashMap<String, List<SyncContact>> userinfo = new HashMap<>();
        userinfo.put("contacts", contactsList);

        Request request = new Request.Builder()
                .url(syncContacts)
                .addHeader("authorization", "Bearer " + AppService.getUser().getAuthorization())
                .post(RequestBody.create(Constants.JSON, gson.toJson(userinfo)))
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
    public Response addContacts(List<Contacts> contactsList) {
        Gson gson = new Gson();
        HashMap<String, List<Contacts>> userinfo = new HashMap<>();
        userinfo.put("contacts", contactsList);

        Request request = new Request.Builder()
                .url(addContacts)
                .addHeader("authorization", "Bearer " + AppService.getUser().getAuthorization())
                .post(RequestBody.create(Constants.JSON, gson.toJson(userinfo)))
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
    public Response myContacts() {

        Request request = new Request.Builder()
                .url(syncContacts)
                .addHeader("authorization", "Bearer " + AppService.getUser().getAuthorization())
                //.addHeader("search", String.valueOf(limit))
                // .addHeader("page", String.valueOf(page))
                //.addHeader("limit", String.valueOf(limit))
                .get()
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

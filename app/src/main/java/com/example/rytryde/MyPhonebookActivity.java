package com.example.rytryde;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rytryde.adapters.ContactAdapter;
import com.example.rytryde.data.model.MyContacts;
import com.example.rytryde.data.model.SyncContact;
import com.example.rytryde.service.app.AppService;
import com.example.rytryde.service.http.contacts.ContactsService;
import com.example.rytryde.service.http.contacts.IContactsService;
import com.example.rytryde.utils.General;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Response;

public class MyPhonebookActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final String TAG = "MyPhoneBookActivity";
    protected Map<Long, List<String>> phones = new HashMap<>();
    private ContactAdapter adapter;
    private RecyclerView contactRV;
    private IContactsService httpUrlConnectionService = new ContactsService();
    private RecyclerView.LayoutManager layoutManager;
    private List<MyContacts> contactsList = new ArrayList<>();
    private List<SyncContact> syncContactList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_phonebook);

        contactRV = findViewById(R.id.rv_contacts);

        layoutManager = new LinearLayoutManager(this);

        if (AppService.getContactPermission()) {
            LoaderManager.getInstance(this).initLoader(0, null, this);
        }


    }


    private void setupAdapter() {
        if (contactsList != null) {

            adapter = new ContactAdapter(this, MyContacts.reaarangeList(contactsList));
            contactRV.setLayoutManager(layoutManager);
            contactRV.setAdapter(adapter);
        }


    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id,
                                         @Nullable Bundle args) {

        // Define the columns to retrieve
        String[] PROJECTION_NUMBERS = new String[]
                {ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                        ContactsContract.CommonDataKinds.Phone.NUMBER};
        String[] PROJECTION_DETAILS = new String[]
                {ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI};
        switch (id) {
            case 0:
                return new CursorLoader(MyPhonebookActivity.this,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, // URI
                        PROJECTION_NUMBERS, // projection fields
                        null, // the selection criteria
                        null, // the selection args
                        null // the sort order
                );
            default:
                return new CursorLoader(MyPhonebookActivity.this,
                        ContactsContract.Contacts.CONTENT_URI, // URI
                        PROJECTION_DETAILS, // projection fields
                        ContactsContract.Contacts.HAS_PHONE_NUMBER, // the selection criteria
                        null, // the selection args
                        null // the sort order
                );
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader,
                               Cursor data) {
        Map<String, String> photoMap = new HashMap<>();
        switch (loader.getId()) {
            case 0:
                phones = new HashMap<>();
                if (data != null) {
                    while (!data.isClosed() && data.moveToNext()) {
                        long contactId = data.getLong(0);
                        String phone = data.getString(1);

                        List<String> list;
                        if (phones.containsKey(contactId)) {
                            list = phones.get(contactId);
                        } else {
                            list = new ArrayList<>();
                            phones.put(contactId, list);
                        }
                        list.add(phone);
                    }
                    data.close();
                }
                LoaderManager.getInstance(MyPhonebookActivity.this)
                        .initLoader(1, null, this);
                break;
            case 1:
                if (data != null) {
                    while (!data.isClosed() && data.moveToNext()) {
                        long contactId = data.getLong(0);
                        String name = data.getString(1);
                        String photo = data.getString(2);
                        List<String> contactPhones = phones.get(contactId);

                        if (contactPhones != null) {
                            for (String phone :
                                    contactPhones) {
                                phone = phone.replace("+", "");
                                phone = phone.replace(" ", "");
                                if (!photoMap.containsKey(phone)) {
                                    photoMap.put(phone, photo);
                                    syncContactList.add(new SyncContact(getFirstName(name), getLastName(name), phone));
                                }
                            }
                        }
                    }
                    data.close();
                    if (syncContactList != null)
                        for (SyncContact contact : syncContactList)
                            Log.e(TAG, contact.getPhone_no() + contact.getFirst_name());
                    new AsyncSyncContacts(syncContactList, photoMap).execute();

                }
        }
    }

    // This method is triggered when the loader is being reset
    // and the loader data is no longer available. Called if the data
    // in the provider changes and the Cursor becomes stale.
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // Clear the Cursor we were using with another call to the swapCursor()


    }

    public String getFirstName(String fullName) {
        String firstName;
        if (fullName.split("\\w+").length > 1) {
            firstName = fullName.substring(0, fullName.lastIndexOf(' '));
        } else {
            firstName = fullName;
        }
        return firstName;
    }

    public String getLastName(String fullName) {
        String lastName;
        if (fullName.split("\\w+").length > 1) {

            lastName = fullName.substring(fullName.lastIndexOf(" ") + 1);
        } else {
            lastName = "";
        }
        return lastName;
    }

    public class AsyncSyncContacts extends AsyncTask<String, String, String> {

        List<SyncContact> toSynccontactsList;
        Map<String, String> photos;
        @SuppressLint("StaticFieldLeak")

        private Dialog loadingDialog;

        public AsyncSyncContacts(List<SyncContact> mcontactList, Map<String, String> _photos) {
            toSynccontactsList = mcontactList;
            photos = _photos;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (loadingDialog == null) {
                contactsList.clear();
                loadingDialog = General.loadingProgress(MyPhonebookActivity.this);
                loadingDialog.show();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String responseString = null;
            try {

                response = httpUrlConnectionService.syncContacts(toSynccontactsList);
                if (response != null) {
                    responseString = response.body().string();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return responseString;
        }


        @Override
        protected void onPostExecute(String response) {
            Gson gson = new GsonBuilder().create();
            if (response != null) {
                Log.e(" Sync response post", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean outcome = jsonObject.getBoolean("success");
                    Log.e("outcome", Boolean.toString(outcome));

                    if (loadingDialog != null && loadingDialog.isShowing())
                        loadingDialog.dismiss();
                    if (outcome) {
                        new AlertDialog.Builder(MyPhonebookActivity.this)
                                .setMessage(jsonObject.getString("message"))
                                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            contactsList = gson.fromJson(String.valueOf(jsonObject.getJSONArray("data")), new TypeToken<ArrayList<MyContacts>>() {
                                            }.getType());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        for (MyContacts contact : contactsList) {

                                            if (photos.containsKey(contact.getNumber()))
                                                contact.setPhotoURI(photos.get(contact.getNumber()));
                                        }

                                        setupAdapter();
                                    }
                                })
                                .show();
                    } else {
                        new AlertDialog.Builder(MyPhonebookActivity.this)
                                .setMessage(jsonObject.getString("message"))
                                .setPositiveButton(getResources().getString(R.string.ok), null)
                                .show();
                    }

                } catch (Exception e) {
                    Log.e("exception", e.getMessage());
                }

            }

        }

    }
}
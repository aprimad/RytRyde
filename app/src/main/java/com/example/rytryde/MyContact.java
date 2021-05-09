package com.example.rytryde;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.rytryde.service.app.AppService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MyContact extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_ACCESS_READ_CONTACTS = 1;
    FloatingActionButton addContactFB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_contact);

        addContactFB = findViewById(R.id.addcontactsFAB);

        addContactFB.setOnClickListener(v -> {
            if (AppService.getContactPermission())
                callPhoneBookActivity();
            else requestContactsPermission(MyContact.this);
        });
    }

    public void requestContactsPermission(Context context) {
        if (ContextCompat.checkSelfPermission(context.getApplicationContext(),
                android.Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            AppService.saveContactPermission(true);
        } else {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{android.Manifest.permission.READ_CONTACTS},
                    PERMISSIONS_REQUEST_ACCESS_READ_CONTACTS);
        }
    }

    /**
     * Handles the result of the request for contact permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AppService.saveContactPermission(true);
                    callPhoneBookActivity();

                }
            }
        }
    }

    private void callPhoneBookActivity() {

        Intent i = new Intent(MyContact.this, MyPhonebookActivity.class);
        i.putExtra("caller", "My contact");
        startActivity(i);
    }
}
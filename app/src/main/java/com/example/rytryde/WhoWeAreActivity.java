package com.example.rytryde;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class WhoWeAreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_who_we_are);

        getIntent();
    }
}
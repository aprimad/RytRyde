package com.example.rytryde;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ContactUsActivity extends AppCompatActivity {

    Toolbar TB_cms;
    TextView TV_cms_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        getIntent();

        TB_cms = findViewById(R.id.cmsToolbar);
        TV_cms_toolbar = findViewById(R.id.cmsToolbarTitle);

        setSupportActionBar(TB_cms);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        TV_cms_toolbar.setText(R.string.contact_us);
    }
}
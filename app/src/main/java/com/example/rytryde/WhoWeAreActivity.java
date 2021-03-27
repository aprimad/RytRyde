package com.example.rytryde;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.rytryde.service.app.CmsDataService;
import com.example.rytryde.utils.AsyncTnC;
import com.example.rytryde.utils.Constants;

public class WhoWeAreActivity extends AppCompatActivity {

    WebView wAwWV;
    TextView wAwTV;
    Toolbar wAwTB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_who_we_are);

        getIntent();
        getIntent();
        wAwWV = findViewById(R.id.wAwWV);
        wAwTV = findViewById(R.id.cmsToolbarTitle);
        wAwTB = findViewById(R.id.cmsToolbar);

        new AsyncTnC(this, Constants.ABOUT_US).execute();

        setSupportActionBar(wAwTB);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    public void loadWebView() {
        wAwTV.setText(getString(R.string.who_are_we));
        wAwWV.loadDataWithBaseURL(null, CmsDataService.getAboutUsData(), "text/html", "utf-8", null);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
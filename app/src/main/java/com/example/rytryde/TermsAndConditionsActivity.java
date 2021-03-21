package com.example.rytryde;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rytryde.service.app.CmsDataService;
import com.example.rytryde.utils.AsyncTnC;
import com.example.rytryde.utils.Constants;

public class TermsAndConditionsActivity extends AppCompatActivity {

    WebView tnCWB;
    TextView tnCTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);

        getIntent();
        tnCWB = findViewById(R.id.tncWV);
        tnCTV = findViewById(R.id.cmsToolbarTitle);

        new AsyncTnC(this, Constants.TERMS_AND_CONDITIONS).execute();

    }

    public void loadWebView() {
        tnCTV.setText(CmsDataService.getTnCTitle());
        tnCWB.loadDataWithBaseURL(null, CmsDataService.getTnCData(), "text/html", "utf-8", null);

    }
}
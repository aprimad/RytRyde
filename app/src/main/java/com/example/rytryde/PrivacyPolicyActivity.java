package com.example.rytryde;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rytryde.service.app.CmsDataService;
import com.example.rytryde.utils.AsyncTnC;
import com.example.rytryde.utils.Constants;

public class PrivacyPolicyActivity extends AppCompatActivity {
    WebView ppWV;
    TextView ppTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        ppWV = findViewById(R.id.ppWV);
        ppTV = findViewById(R.id.cmsToolbarTitle);

        new AsyncTnC(this, Constants.PRIVACY_POLICY).execute();

    }

    public void loadWebView() {
        ppTV.setText(CmsDataService.getPrivacyTitle());
        ppWV.loadDataWithBaseURL(null, CmsDataService.getPrivacyData(), "text/html", "utf-8", null);
        ppWV.setVerticalScrollBarEnabled(true);
    }
}
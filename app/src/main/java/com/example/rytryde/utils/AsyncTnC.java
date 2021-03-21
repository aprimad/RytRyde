package com.example.rytryde.utils;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.rytryde.PrivacyPolicyActivity;
import com.example.rytryde.R;
import com.example.rytryde.TermsAndConditionsActivity;
import com.example.rytryde.WhoWeAreActivity;
import com.example.rytryde.data.model.UpcomingRidesData;
import com.example.rytryde.service.app.CmsDataService;
import com.example.rytryde.service.http.cms.CmsSevice;
import com.example.rytryde.service.http.cms.ICmsService;

import org.json.JSONObject;

import okhttp3.Response;

public class AsyncTnC extends AsyncTask<String, String, String> {

    private ICmsService cmsService = new CmsSevice();
    private UpcomingRidesData upcomingRidesData;
    @SuppressLint("StaticFieldLeak")
    private Context context;
    private String type;

    private ProgressDialog loadingDialog;

    public AsyncTnC(Context mContext, String mType) {
        context = mContext;
        type = mType;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (loadingDialog == null) {
            loadingDialog = new ProgressDialog(context);
            loadingDialog.setMessage(context.getString(R.string.loading));
            loadingDialog.setCancelable(false);
            loadingDialog.show();
        }

    }

    @Override
    protected String doInBackground(String... params) {
        Response response = null;
        String responseString = null;
        try {
            if (type.equals(Constants.TERMS_AND_CONDITIONS))
                response = cmsService.termsAndConditions();
            else if (type.equals(Constants.PRIVACY_POLICY))
                response = cmsService.privacyPolicy();
            else if (type.equals(Constants.ABOUT_US))
                response = cmsService.aboutUs();

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
        if (response != null) {
            Log.e("response post", response);

            try {
                JSONObject jsonObject = new JSONObject(response);
                Boolean outcome = jsonObject.getBoolean("success");
                Log.e("outcome", Boolean.toString(outcome));

                if (loadingDialog != null && loadingDialog.isShowing())
                    loadingDialog.dismiss();
                if (outcome) {
                    if (type.equals(Constants.TERMS_AND_CONDITIONS)) {
                        CmsDataService.saveTnCData(jsonObject.getString("data"));
                        ((TermsAndConditionsActivity) context).loadWebView();
                    }
                    if (type.equals(Constants.PRIVACY_POLICY)) {
                        CmsDataService.savePrivacyData(jsonObject.getString("data"));
                        ((PrivacyPolicyActivity) context).loadWebView();
                    }
                    if (type.equals(Constants.ABOUT_US)) {
                        CmsDataService.saveAboutUsData(jsonObject.getString("data"));
                        ((WhoWeAreActivity) context).loadWebView();
                    }


                }

            } catch (Exception e) {
                Log.e("exception", e.getMessage());
            }

        }

    }

}


package com.example.rytryde;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.rytryde.data.model.Login;
import com.example.rytryde.service.app.AppService;
import com.example.rytryde.service.http.account.AccountService;
import com.example.rytryde.service.http.account.IAccountService;
import com.example.rytryde.utils.Constants;
import com.example.rytryde.utils.General;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import okhttp3.Response;

public class VerifyMobileActivity extends AppCompatActivity {

    EditText otpET;
    TextView enterMobileTV, otpEmtyTV;
    CardView continueCV;
    TextView resendCodeTV;
    private IAccountService httpUrlConnectionService = new AccountService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_mobile);

        otpET = findViewById(R.id.et_otp);
        enterMobileTV = findViewById(R.id.enter_phone_number_tv);
        continueCV = findViewById(R.id.cv_continue);
        otpEmtyTV = findViewById(R.id.otpEmptyTV);
        resendCodeTV = findViewById(R.id.resendCodeTV);

        String mobileText = enterMobileTV.getText() + " " + AppService.getDialCode() + AppService.getMobileNumber();

        enterMobileTV.setText(mobileText);

        continueCV.setOnClickListener(v -> {
            String OTP = otpET.getText().toString();
            if (OTP.equals(""))
                otpEmtyTV.setVisibility(View.VISIBLE);
            else {
                otpEmtyTV.setVisibility(View.GONE);
                new AsyncVerifyOTP(OTP).execute();

            }

        });

        resendCodeTV.setOnClickListener(v -> {
            new AsyncResendOTP().execute();
        });
    }

    public class AsyncVerifyOTP extends AsyncTask<String, String, String> {

        Dialog progressDialog;
        @SuppressLint("StaticFieldLeak")
        private String otpType, OTP, mobileNumber;
        private Login loginDetails;

        public AsyncVerifyOTP(String mOTP) {
            OTP = mOTP;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog == null) {
                progressDialog = General.loadingProgress(VerifyMobileActivity.this);
                progressDialog.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String responseString = null;
            try {

                response = httpUrlConnectionService.signup(AppService.getFirstName(), AppService.getLastName(), OTP, AppService.getEMAIL(), AppService.getUserPassword(), AppService.getDialCode(), AppService.getMobileNumber());
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
                    Gson gson = new GsonBuilder().create();
                    loginDetails = gson.fromJson(response, Login.class);

                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    if (loginDetails.getSuccess()) {
                        AppService.saveOTP(OTP);
                        AppService.saveUserInfo(loginDetails.getData());
                        Intent i = new Intent(VerifyMobileActivity.this, MainActivity.class);
                        i.putExtra("caller", "VerifyMobileActivity");
                        startActivity(i);
                        finish();
                    } else {
                        new AlertDialog.Builder(VerifyMobileActivity.this)
                                .setMessage("Please retry entering your pin.")
                                .setPositiveButton(getResources().getString(R.string.ok), null)
                                .show();
                    }

                } catch (Exception e) {
                    Log.e("exception", e.getMessage());
                }

            }

        }

    }

    public class AsyncResendOTP extends AsyncTask<String, String, String> {

        Dialog progressDialog;
        @SuppressLint("StaticFieldLeak")
        private String otpType, OTP, mobileNumber;
        private ProgressDialog loadingDialog;
        private Login loginDetails;

        public AsyncResendOTP() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog == null) {
                progressDialog = General.loadingProgress(VerifyMobileActivity.this);
                progressDialog.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String responseString = null;
            try {

                response = httpUrlConnectionService.resendOTP(Constants.VERIFICATION_OTP_TYPE_SIGNUP, AppService.getMobileNumber());
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
                    if (outcome) {
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                    } else {
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                        new AlertDialog.Builder(VerifyMobileActivity.this)
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
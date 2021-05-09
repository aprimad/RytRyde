package com.example.rytryde;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.rytryde.service.app.AppService;
import com.example.rytryde.service.http.account.AccountService;
import com.example.rytryde.service.http.account.IAccountService;
import com.example.rytryde.utils.Constants;
import com.example.rytryde.utils.General;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Response;

public class CompleteDetailsActivity extends AppCompatActivity {

    private EditText passwordET, confirmPasswordET;
    private TextView passwordWarningTV, confirmPasswordWarningTV;
    private IAccountService httpUrlConnectionService = new AccountService();
    private CardView cv_signup;
    private ImageButton showPasswordButton;
    private boolean passwordHidden = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_details);

        passwordET = findViewById(R.id.passwordET);
        confirmPasswordET = findViewById(R.id.confirmPasswordET);
        passwordWarningTV = findViewById(R.id.passwordWarningTV);
        confirmPasswordWarningTV = findViewById(R.id.confirmPasswordWarningTV);
        cv_signup = findViewById(R.id.cv_signup);
        showPasswordButton = findViewById(R.id.showPasswordButton);

        showPasswordButton.setOnClickListener(v -> {
            if (passwordHidden) {
                passwordET.setTransformationMethod(null);
                passwordHidden = false;
            } else {
                passwordET.setTransformationMethod(new PasswordTransformationMethod());
                passwordHidden = true;
            }

        });

        passwordET.setOnFocusChangeListener((v, hasFocus) -> {
            String password = passwordET.getText().toString().trim();
            if (!hasFocus && !password.equals("") && !isStrongPassword(password)) {
                passwordWarningTV.setVisibility(View.VISIBLE);
                passwordWarningTV.setText(getString(R.string.strong_password));
            } else {
                passwordWarningTV.setVisibility(View.GONE);
                passwordWarningTV.setText(getString(R.string.should_not_be_empty));
            }
        });

        confirmPasswordET.setOnFocusChangeListener((v, hasFocus) -> {
            String password = passwordET.getText().toString();
            String confirmPassword = confirmPasswordET.getText().toString();
            if (!hasFocus) {
                if (!password.equals("") && !confirmPassword.equals("")) {
                    if (!password.equals(confirmPassword)) {
                        confirmPasswordWarningTV.setVisibility(View.VISIBLE);
                        confirmPasswordWarningTV.setText(getResources().getString(R.string.password_doesnt_match));
                    }
                }
            } else {
                confirmPasswordWarningTV.setVisibility(View.GONE);
                confirmPasswordWarningTV.setText(getString(R.string.should_not_be_empty));
            }
        });

        cv_signup.setOnClickListener(v -> {
            String password = passwordET.getText().toString();
            String confirmpassword = confirmPasswordET.getText().toString();

            if (passwordWarningTV.getVisibility() == View.VISIBLE || confirmPasswordWarningTV.getVisibility() == View.VISIBLE) {

            } else if (password.equals(""))
                passwordWarningTV.setVisibility(View.VISIBLE);
            else if (confirmpassword.equals(""))
                confirmPasswordWarningTV.setVisibility(View.VISIBLE);
            else {
                new AsyncAddPassword(password, confirmpassword).execute();
            }

        });


    }

    private boolean isStrongPassword(String password) {
        Pattern pattern;
        Matcher matcher;

        pattern = Pattern.compile(Constants.PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }

    public class AsyncAddPassword extends AsyncTask<String, String, String> {

        Dialog progressDialog;
        @SuppressLint("StaticFieldLeak")
        private String password;
        private String confirmPassword;

        public AsyncAddPassword(String mpassword, String mconfirmPassword) {

            password = mpassword;
            confirmPassword = mconfirmPassword;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog == null) {
                progressDialog = General.loadingProgress(CompleteDetailsActivity.this);
                progressDialog.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String responseString = null;
            try {

                response = httpUrlConnectionService.signup(AppService.getFirstName(), AppService.getLastName(), AppService.getOTP(), AppService.getEMAIL(), password, AppService.getDialCode(), AppService.getMobileNumber());
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
                    Log.e("outcome", AppService.getUser().getAuthorization());

                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    if (outcome) {
                        Intent i = new Intent(CompleteDetailsActivity.this, MainActivity.class);
                        i.putExtra("caller", "CompleteDetailsActivity");
                        startActivity(i);
                    } else {
                        new AlertDialog.Builder(CompleteDetailsActivity.this)
                                .setMessage(jsonObject.getJSONObject("error").getString("message"))
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
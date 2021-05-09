package com.example.rytryde;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.rytryde.service.app.AppService;
import com.example.rytryde.service.http.account.AccountService;
import com.example.rytryde.service.http.account.IAccountService;
import com.example.rytryde.ui.login.LoginActivity;
import com.example.rytryde.utils.Constants;
import com.example.rytryde.utils.General;
import com.hbb20.CountryCodePicker;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Response;

public class SignUpActivity extends AppCompatActivity {

    EditText firstNameET, emailET, mobileNumberET, lastNameET;
    String firstName, lastName, email, dialCode, mobibleNumber, password, confirmPassword;
    TextView firstNameEmptyTV, lastNameEmptyTV, emailEmptyTV, mobileEmptyTV;
    CardView signUpButton;
    CheckBox tncCB;
    CountryCodePicker countryCodePicker;
    private IAccountService httpUrlConnectionService = new AccountService();
    private EditText passwordET, confirmPasswordET;
    private TextView passwordWarningTV, confirmPasswordWarningTV, tNcTV, signInTV;
    private ImageButton showPasswordButton;
    private boolean passwordHidden = true;

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firstNameET = findViewById(R.id.firstNameET);
        emailET = findViewById(R.id.emailET);
        mobileNumberET = findViewById(R.id.et_mobile_number);
        firstNameEmptyTV = findViewById(R.id.firstNameEmptyTV);
        lastNameEmptyTV = findViewById(R.id.lastNameEmptyTV);
        emailEmptyTV = findViewById(R.id.emailEmptyTV);
        mobileEmptyTV = findViewById(R.id.mobileEmptyTV);
        lastNameET = findViewById(R.id.lastNameET);
        signUpButton = findViewById(R.id.cv_continue);
        countryCodePicker = findViewById(R.id.country_picker);
        passwordET = findViewById(R.id.passwordET);
        confirmPasswordET = findViewById(R.id.confirmPasswordET);
        passwordWarningTV = findViewById(R.id.passwordWarningTV);
        confirmPasswordWarningTV = findViewById(R.id.confirmPasswordWarningTV);
        showPasswordButton = findViewById(R.id.showPasswordButton);
        tncCB = findViewById(R.id.cb_terms_condition);
        tNcTV = findViewById(R.id.tncTV);
        signInTV = findViewById(R.id.tv_sign_in);

        firstNameET.setText("");
        lastNameET.setText("");
        emailET.setText("");
        mobileNumberET.setText("");

        firstNameET.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && firstNameET.getText().toString().equals(""))
                firstNameEmptyTV.setVisibility(View.VISIBLE);
            else firstNameEmptyTV.setVisibility(View.GONE);
        });

        lastNameET.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && lastNameET.getText().toString().equals(""))
                lastNameEmptyTV.setVisibility(View.VISIBLE);
            else lastNameEmptyTV.setVisibility(View.GONE);
        });

        mobileNumberET.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && mobileNumberET.getText().toString().equals(""))
                mobileEmptyTV.setVisibility(View.VISIBLE);
            else mobileEmptyTV.setVisibility(View.GONE);
        });

        emailET.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (!isValidEmail(emailET.getText().toString())) {
                    emailEmptyTV.setVisibility(View.VISIBLE);
                    emailEmptyTV.setText("Please enter valid email");
                } else {
                    emailEmptyTV.setVisibility(View.GONE);
                    emailEmptyTV.setText(getResources().getString(R.string.Needs_to_be_filled));
                }
            }
        });

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

        tNcTV.setOnClickListener(v -> {
            Intent i = new Intent(SignUpActivity.this, TermsAndConditionsActivity.class);
            i.putExtra("caller", "SignUpActivity");
            startActivity(i);
        });

        signUpButton.setOnClickListener(v -> {
            password = passwordET.getText().toString();
            confirmPassword = confirmPasswordET.getText().toString();

            if (firstNameET.getText().toString().equals("")) {
                firstNameEmptyTV.setVisibility(View.VISIBLE);
            } else if (lastNameET.getText().toString().equals("")) {
                lastNameEmptyTV.setVisibility(View.VISIBLE);
            } else if (emailET.getText().toString().equals("")) {
                emailEmptyTV.setVisibility(View.VISIBLE);
            } else if (mobileNumberET.getText().toString().equals("")) {
                mobileEmptyTV.setVisibility(View.VISIBLE);
            } else if (password.equals(""))
                passwordWarningTV.setVisibility(View.VISIBLE);
            else if (confirmPassword.equals(""))
                confirmPasswordWarningTV.setVisibility(View.VISIBLE);
            else if (!tncCB.isChecked()) {
                new AlertDialog.Builder(SignUpActivity.this)
                        .setMessage(R.string.please_accept_terms)
                        .setPositiveButton(getResources().getString(R.string.ok), null)
                        .show();
            } else if (!warningsVisible()) {
                firstNameEmptyTV.setVisibility(View.GONE);
                lastNameEmptyTV.setVisibility(View.GONE);
                emailEmptyTV.setVisibility(View.GONE);
                mobileEmptyTV.setVisibility(View.GONE);
                firstName = firstNameET.getText().toString();
                lastName = lastNameET.getText().toString();
                email = emailET.getText().toString();
                mobibleNumber = mobileNumberET.getText().toString();
                dialCode = "+" + countryCodePicker.getSelectedCountryCode();

                new AsyncSendOTP(firstName, lastName, email, dialCode, mobibleNumber, password).execute();
            }
        });

        signInTV.setOnClickListener(v -> {
            Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
            i.putExtra("caller", "SignUpActivity");
            startActivity(i);
            finish();
        });


    }

    private boolean isStrongPassword(String password) {
        Pattern pattern;
        Matcher matcher;

        pattern = Pattern.compile(Constants.PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }

    private boolean warningsVisible() {
        return mobileEmptyTV.getVisibility() == View.VISIBLE ||
                firstNameEmptyTV.getVisibility() == View.VISIBLE ||
                lastNameEmptyTV.getVisibility() == View.VISIBLE ||
                emailEmptyTV.getVisibility() == View.VISIBLE ||
                passwordWarningTV.getVisibility() == View.VISIBLE ||
                confirmPasswordWarningTV.getVisibility() == View.VISIBLE;
    }

    public class AsyncSendOTP extends AsyncTask<String, String, String> {

        @SuppressLint("StaticFieldLeak")
        private String firstName, lastName, email, dialCode, mobileNumber, password;

        private Dialog loadingDialog;

        public AsyncSendOTP(String mfirstName, String mlasName, String memail, String mdialCode, String mmobileNumber, String mpassword) {
            firstName = mfirstName;
            lastName = mlasName;
            email = memail;
            dialCode = mdialCode;
            mobileNumber = mmobileNumber;
            password = mpassword;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (loadingDialog == null) {
                loadingDialog = General.loadingProgress(SignUpActivity.this);
                loadingDialog.show();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String responseString = null;
            try {

                response = httpUrlConnectionService.sendOTP(firstName, lastName, email, dialCode, mobileNumber);
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
                        AppService.saveFirstName(firstName);
                        AppService.saveLastName(lastName);
                        AppService.saveEmail(email);
                        AppService.saveMobileNumber(mobibleNumber);
                        AppService.saveDialCode(dialCode);
                        AppService.saveUserPassword(password);
                        Intent i = new Intent(SignUpActivity.this, VerifyMobileActivity.class);
                        i.putExtra("caller", "SignUpActivity");
                        startActivity(i);
                        finish();
                    } else {
                        new AlertDialog.Builder(SignUpActivity.this)
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
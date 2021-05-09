package com.example.rytryde.ui.login;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rytryde.MainActivity;
import com.example.rytryde.R;
import com.example.rytryde.data.model.Login;
import com.example.rytryde.service.app.AppService;
import com.example.rytryde.service.http.account.AccountService;
import com.example.rytryde.service.http.account.IAccountService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    //private LoginViewModel loginViewModel;
    private IAccountService httpUrlConnectionService = new AccountService();
    private View decorView;
    private ProgressDialog dialog = null;
    String username, password;
    private EditText usernameET, passwordET;
    private ImageButton loginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = getIntent();

        usernameET = findViewById(R.id.username);
        passwordET = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);

        Log.e("username", usernameET.getText().toString());

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncLogin(usernameET.getText().toString(), passwordET.getText().toString()).execute();

            }
        });
    }

    public void dismissProgressDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    private void showLoginFailedMessage() {
        final AlertDialog.Builder failureMessageBuilder = new AlertDialog.Builder(this);
        failureMessageBuilder.setTitle(getString(R.string.authenticationFailed));
        failureMessageBuilder.setMessage(getString(R.string.authenticationFailed_message));
        failureMessageBuilder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        LoginActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog alertDialog = failureMessageBuilder.create();
                alertDialog.show();
            }
        });

    }

    private class AsyncLogin extends AsyncTask<String, String, String> {


        private String username, password;
        private Login loginDetails;

        protected AsyncLogin(String mUsername, String mPassword) {
            username = mUsername;
            password = mPassword;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (dialog == null) {
                dialog = new ProgressDialog(LoginActivity.this);
                dialog.setMessage(getString(R.string.logging_in));
                dialog.setCancelable(false);
                dialog.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String responseString = null;
            try {

                response = httpUrlConnectionService.login(username, password);
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
                Log.e("response post", "hi" + response);
                if (!LoginActivity.this.isFinishing()) {
                    dismissProgressDialog();
                }

                Gson gson = new GsonBuilder().create();

                try {
                  loginDetails = gson.fromJson(response, Login.class);

                    if (loginDetails.getSuccess()) {
                        AppService.saveUserInfo(loginDetails.getData());
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        i.putExtra("caller", "LoginActivity");
                        startActivity(i);
                        finish();
                    } else if (response.equals("")) {
                        new AlertDialog.Builder(LoginActivity.this)
                                .setMessage(getString(R.string.no_user))
                                .setPositiveButton(getResources().getString(R.string.ok), null)
                                .show();
                    } else {
                        new AlertDialog.Builder(LoginActivity.this)
                                .setMessage(loginDetails.getMessage())
                                .setPositiveButton(getResources().getString(R.string.ok), null)
                                .show();

                    }
                    if (!LoginActivity.this.isFinishing()) {
                        showLoginFailedMessage();
                        dismissProgressDialog();
                    }

                } catch (Exception e) {
                    Log.e("exception",e.getMessage());
                }


            }

        }
    }

}
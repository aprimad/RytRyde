package com.example.rytryde;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rytryde.service.http.vehicle.IVehicleService;
import com.example.rytryde.service.http.vehicle.VehicleService;
import com.example.rytryde.utils.General;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import okhttp3.Response;

public class MyVehicleActivity extends AppCompatActivity {

    RelativeLayout noVehicleRL;
    LinearLayout vehicleLL;
    FloatingActionButton vehicleFAB;
    private IVehicleService httpUrlConnectionService = new VehicleService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_vehicle);
        noVehicleRL = findViewById(R.id.no_vehicle);
        vehicleLL = findViewById(R.id.vehicle_ll);
        vehicleFAB = findViewById(R.id.vehicleFAB);
        if (noVehicleRL.getVisibility() == View.VISIBLE)
            vehicleFAB.setOnClickListener(v -> {
                Intent i = new Intent(MyVehicleActivity.this, AddVehicleDetailsActivity.class);
                i.putExtra("caller", "MyVehicle");
                startActivity(i);
            });

        new AsyncGetVechileDetails().execute();
    }


    @SuppressLint("StaticFieldLeak")
    public class AsyncGetVechileDetails extends AsyncTask<String, String, String> {

        private Dialog loadingDialog;

        public AsyncGetVechileDetails() {


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (loadingDialog == null) {
                loadingDialog = General.loadingProgress(MyVehicleActivity.this);
                loadingDialog.show();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String responseString = null;
            try {

                response = httpUrlConnectionService.getVehicleDetails();
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
                Log.e("response post ", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean outcome = jsonObject.getBoolean("success");
                    Log.e("outcome", Boolean.toString(outcome));

                    if (loadingDialog != null && loadingDialog.isShowing())
                        loadingDialog.dismiss();
                    if (outcome) {
                        //AppService.saveMediaData(jsonObject.getJSONObject("data").toString());
                        if (jsonObject.getString("message").equals("Not found")) {
                            noVehicleRL.setVisibility(View.VISIBLE);
                            vehicleLL.setVisibility(View.GONE);
                        } else {
                            noVehicleRL.setVisibility(View.GONE);
                            vehicleLL.setVisibility(View.VISIBLE);
                        }


                    } else {
                        new AlertDialog.Builder(MyVehicleActivity.this)
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
package com.example.rytryde.utils;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.example.rytryde.R;
import com.example.rytryde.data.model.UpcomingRidesData;
import com.example.rytryde.fragments.UpcomingRidesFragment;
import com.example.rytryde.service.app.UpcomingRidesService;
import com.example.rytryde.service.http.rides.IRideService;
import com.example.rytryde.service.http.rides.RideService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.Response;

public class AsyncUpcomingRides extends AsyncTask<String, String, String> {

    private IRideService rideService = new RideService();
    private UpcomingRidesData upcomingRidesData;
    private Fragment fragment;

    private ProgressDialog loadingDialog;

    public AsyncUpcomingRides(Fragment mFragment) {
        this.fragment = mFragment;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (loadingDialog == null) {
            loadingDialog = new ProgressDialog(fragment.getContext());
            loadingDialog.setMessage(fragment.getString(R.string.loading));
            loadingDialog.setCancelable(false);
            loadingDialog.show();
        }

    }

    @Override
    protected String doInBackground(String... params) {
        Response response = null;
        String responseString = null;
        try {

            response = rideService.upcomingRides();
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

            Gson gson = new GsonBuilder().create();

            try {
                upcomingRidesData = gson.fromJson(response, UpcomingRidesData.class);

                if (upcomingRidesData.isSuccess()) {
                    UpcomingRidesService.saveUpcomingRides(response);
                    if (loadingDialog != null && loadingDialog.isShowing())
                        loadingDialog.dismiss();
                    ((UpcomingRidesFragment) fragment).refreshFragmentView();
                    ;
                } else {

                }

            } catch (Exception e) {
                Log.e("exception", e.getMessage());
            }


        }


    }

}

package com.example.rytryde;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.rytryde.data.model.Waypoint;
import com.example.rytryde.service.app.LocationService;
import com.example.rytryde.service.http.rides.IRideService;
import com.example.rytryde.service.http.rides.RideService;
import com.example.rytryde.utils.General;
import com.example.rytryde.utils.LocationHelper;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.Response;


public class RideActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = RideActivity.class.getSimpleName();
    public static IRideService httpUrlConnectionService = new RideService();
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    final Calendar myCalendar = Calendar.getInstance();
    EditText startLocET, destinationLocET, dateOnetimeET, timeOnetimeET;
    ArrayList<Waypoint> waypoints = new ArrayList<>();

    Intent i;
    CardView submitCV;
    private RadioGroup rideTypeRG;
    private GoogleMap map;
    private Location lastKnownLocation;
    private CameraPosition cameraPosition;
    private LatLng newCoordinates;
    private LinearLayout onetimeLL, regularLL;
    private String rideType = "single";
    private String rideDate;
    private String[] regularRideDates;
    private Dialog loadingDialog;

    public static int nearestElement(Float average, List<Float> coordinates) {
        int index = 0;
        while (coordinates.get(index) <= average) {
            index += 1;
            if (index >= coordinates.size()) {
                break;
            }

        }
        return index;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride);

        getIntent();

        //clear any previous locations saved
        LocationService.clearStartLoc();
        LocationService.clearDestLoc();

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);

        startLocET = findViewById(R.id.et_start_address);
        destinationLocET = findViewById(R.id.et_destination_address);
        rideTypeRG = findViewById(R.id.rideTypeRG);
        onetimeLL = findViewById(R.id.rideTypeOnetimeLL);
        regularLL = findViewById(R.id.rideTypeRegularLL);
        dateOnetimeET = findViewById(R.id.et_date_onetime);
        timeOnetimeET = findViewById(R.id.et_time_onetime);
        submitCV = findViewById(R.id.ride_submitCV);

        startLocET.setOnClickListener(v -> {
            i = new Intent(RideActivity.this, LocationActivity.class);
            i.putExtra("caller", LocationHelper.start);
            startActivity(i);
        });
        destinationLocET.setOnClickListener(v -> {
            i = new Intent(RideActivity.this, LocationActivity.class);
            i.putExtra("caller", LocationHelper.destination);
            startActivity(i);
        });

        rideTypeRG.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.onetimeRB) {
                onetimeLL.setVisibility(View.VISIBLE);
                regularLL.setVisibility(View.GONE);
                rideType = "single";
            } else {
                onetimeLL.setVisibility(View.GONE);
                regularLL.setVisibility(View.VISIBLE);
                rideType = "regular";
            }
        });

        dateOnetimeET.setOnClickListener(v -> setUpDatePicker());

        timeOnetimeET.setOnClickListener(v -> setUpTimePicker());

        submitCV.setOnClickListener(v -> {
            if (validateForm())
                getWayPoints();

        });


    }


    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        LocationHelper.locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationHelper.locationPermissionGranted = true;
                }
            }
        }
        LocationHelper.updateLocationUI(TAG, map, RideActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "On resume");
        if (LocationService.getStartLocation() != null) {
            try {
                startLocET.setText(LocationHelper.getAddress(TAG, LocationService.getStartLocation(), this));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (LocationService.getDestLocation() != null) {
            try {
                destinationLocET.setText(LocationHelper.getAddress(TAG, LocationService.getDestLocation(), this));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        this.map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.map_custom_info_contents,
                        (FrameLayout) findViewById(R.id.map), false);

                TextView title = infoWindow.findViewById(R.id.map_title);
                title.setText(marker.getTitle());

                TextView snippet = infoWindow.findViewById(R.id.snippet);
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });

        // Prompt the user for permission.
        LocationHelper.getLocationPermission(TAG, map, RideActivity.this);

        // Turn on the My Location layer and the related control on the map.
        LocationHelper.updateLocationUI(TAG, map, RideActivity.this);

        // Get the current location of the device and set the position of the map.
        lastKnownLocation = LocationHelper.getDeviceLocation(TAG, map, RideActivity.this);


    }

    private void setUpDatePicker() {


        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };

        // TODO Auto-generated method stub
        DatePickerDialog datePickerDialog = new DatePickerDialog(RideActivity.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();


    }

    private void setUpTimePicker() {


        if (dateOnetimeET.getText().toString().equals("")) {
            new AlertDialog.Builder(RideActivity.this)
                    .setMessage(R.string.please_select_date)
                    .setPositiveButton(getResources().getString(R.string.ok), null)
                    .show();
        } else {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(RideActivity.this, (timePicker, selectedHour, selectedMinute) -> {

                String am_pm = "";

                Calendar datetime = Calendar.getInstance();
                datetime.set(Calendar.HOUR_OF_DAY, selectedHour);
                datetime.set(Calendar.MINUTE, selectedMinute);

                if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                    am_pm = "AM";
                else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
                    am_pm = "PM";

                String strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ? "12" : datetime.get(Calendar.HOUR) + "";
                String time = strHrsToShow + ":" + datetime.get(Calendar.MINUTE) + " " + am_pm;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa", Locale.getDefault());

                try {
                    timeOnetimeET.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(simpleDateFormat.parse(time)));

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }, hour, minute, false);//Yes 24 hour time

            mTimePicker.show();
        }


    }

    private void updateLabel() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        String apiFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf2 = new SimpleDateFormat(apiFormat, Locale.US);

        dateOnetimeET.setText(sdf.format(myCalendar.getTime()));
        rideDate = sdf2.format(myCalendar.getTime());
    }

    private boolean validateForm() {
        boolean flag = false;
        String startLoc = startLocET.getText().toString();
        String destLoc = destinationLocET.getText().toString();
        if (startLoc.equals(""))
            General.showAlert(this, getResources().getString(R.string.please_enter_start_add), null);
        else if (destLoc.equals(""))
            General.showAlert(this, getResources().getString(R.string.please_enter_dest_add), null);
        else if (rideTypeRG.getCheckedRadioButtonId() == R.id.onetimeRB && dateOnetimeET.getText().toString().equals(""))
            General.showAlert(this, getResources().getString(R.string.please_select_ride_date), null);
        else if (rideTypeRG.getCheckedRadioButtonId() == R.id.onetimeRB && timeOnetimeET.getText().toString().equals(""))
            General.showAlert(this, getResources().getString(R.string.please_select_ride_time), null);
        else if (startLoc.equals(destLoc))
            General.showAlert(this, getResources().getString(R.string.please_select_add_cantbesame), null);
        else flag = true;

        return flag;
    }

    private void getWayPoints() {
        new AsyncParseRoute(this, LocationService.getStartLocation(), LocationService.getDestLocation()).execute();

    }

    public class AsyncParseRoute extends AsyncTask<String, String, String> {

        String sourceStr, destStr;
        LatLng src, dest;
        String apiKey = null;
        @SuppressLint("StaticFieldLeak")


        private Context context;

        public AsyncParseRoute(Context mcontext, LatLng msource, LatLng mdest) {
            src = msource;
            dest = mdest;
            context = mcontext;

            ApplicationInfo applicationInfo = null;
            try {
                applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (applicationInfo != null) {
                apiKey = applicationInfo.metaData.getString("com.google.android.geo.API_KEY");
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (loadingDialog == null) {

                loadingDialog = General.loadingProgress(context);
                loadingDialog.show();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String responseString = null;
            try {

                response = httpUrlConnectionService.parseRoutes(src, dest, apiKey);
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
            Gson gson = new GsonBuilder().create();
            if (response != null) {
                Log.e(" Sync response post", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String outcome = jsonObject.getString("status");


                    if (outcome.equals("OK")) {
                        JSONArray routesArray = jsonObject.getJSONArray("routes");
                        JSONObject route = routesArray.getJSONObject(0);
                        JSONObject polyline = route.getJSONObject("overview_polyline");
                        String points = polyline.getString("points");
                        List<LatLng> decodedPoints = PolyUtil.decode(points);
                        Log.e("decoded", "null " + decodedPoints.size());


                        List<Float> coords = new ArrayList<>();

                        Location start = new Location("");
                        start.setLatitude(decodedPoints.get(0).latitude);
                        start.setLongitude(decodedPoints.get(0).longitude);

                        if (decodedPoints.size() > 1) {
                            for (int i = 0; i < decodedPoints.size(); i++) {

                                Location nextDest = new Location("");
                                nextDest.setLatitude(decodedPoints.get(i).latitude);
                                nextDest.setLongitude(decodedPoints.get(i).longitude);

                                float distance = start.distanceTo(nextDest);

                                coords.add(i, distance / 1000);

                            }
                            for (float coord : coords)
                                Log.e("distance", "coord" + coord);
                            float lastVal = coords.get(coords.size() - 1);
                            float averageVal = lastVal / 10;
                            List<Integer> indexArray = new ArrayList<>();
                            float average = averageVal;

                            while (average <= lastVal) {
                                indexArray.add(nearestElement(average, coords));
                                average += averageVal;
                            }
                            waypoints.add(new Waypoint(String.valueOf(decodedPoints.get(0).latitude), String.valueOf(decodedPoints.get(0).longitude)));

                            for (int index : indexArray) {
                                if (index >= decodedPoints.size())
                                    break;
                                waypoints.add(new Waypoint(String.valueOf(decodedPoints.get(index).latitude), String.valueOf(decodedPoints.get(index).longitude)));
                            }
                        }
                        waypoints.add(new Waypoint(String.valueOf(decodedPoints.get(decodedPoints.size() - 1).latitude), String.valueOf(decodedPoints.get(decodedPoints.size() - 1).longitude)));

                        new AsyncOfferRide().execute();

                    } else {
                        new AlertDialog.Builder(context)
                                .setMessage(outcome)
                                .setPositiveButton(context.getResources().getString(R.string.ok), null)
                                .show();
                    }

                } catch (Exception e) {
                    Log.e("exception", e.getMessage());
                }

            }

        }

    }

    public class AsyncOfferRide extends AsyncTask<String, String, String> {

        @SuppressLint("StaticFieldLeak")
        private String firstName, lastName, email, dialCode, mobileNumber, password;


        public AsyncOfferRide() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String responseString = null;
            try {

                response = httpUrlConnectionService.offerRides(startLocET.getText().toString(), destinationLocET.getText().toString(), LocationService.getStartLocation(),
                        LocationService.getDestLocation(), rideDate, timeOnetimeET.getText().toString(), rideType, regularRideDates, waypoints);
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

                    } else {
                        new AlertDialog.Builder(RideActivity.this)
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
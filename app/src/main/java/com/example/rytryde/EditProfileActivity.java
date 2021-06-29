package com.example.rytryde;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.rytryde.data.model.LoggedInUser;
import com.example.rytryde.service.app.AppService;
import com.example.rytryde.service.http.account.AccountService;
import com.example.rytryde.service.http.account.IAccountService;
import com.example.rytryde.utils.CircleImageView;
import com.example.rytryde.utils.General;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hbb20.CountryCodePicker;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import okhttp3.Response;

public class EditProfileActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 0;
    private static final int REQUEST_GALLERY = 1;
    final Calendar myCalendar = Calendar.getInstance();
    String firstName, lastName, emailAddress, dialcode, mobile, gender, dob, rideRadius;
    CountryCodePicker countryCodePicker;
    TextView saveChangesTV;
    ImageView uploadImageIV;
    CircleImageView displayImageIV;
    private EditText firstNameET, lastNameET, emailAddressET, mobileET, dobET, rideRadiusET;
    private AutoCompleteTextView genderTV;
    private IAccountService httpUrlConnectionService = new AccountService();
    private String mediaID = "";
    private File imageFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        firstNameET = findViewById(R.id.firstName);
        lastNameET = findViewById(R.id.lastName);
        emailAddressET = findViewById(R.id.et_emailAddress);
        countryCodePicker = findViewById(R.id.et_country_code);
        mobileET = findViewById(R.id.mobileET);
        genderTV = findViewById(R.id.et_gender);
        dobET = findViewById(R.id.et_dob);
        rideRadiusET = findViewById(R.id.et_ride_match_radius);
        saveChangesTV = findViewById(R.id.save_button);
        uploadImageIV = findViewById(R.id.uploadImageIV);
        displayImageIV = findViewById(R.id.iv_user_image);

        LoggedInUser user = AppService.getUser();
        if (user != null) {
            Log.e("Edit Profile Activity", AppService.getUser().getFirst_name());
            firstNameET.setText(user.getFirst_name());
            lastNameET.setText(user.getLast_name());
            emailAddressET.setText(user.getEmail());
            countryCodePicker.setCountryPreference(user.getCountry_code());
            mobileET.setText(user.getPhone_number());
            genderTV.setText(user.getGender());
            dobET.setText(user.getDob());
            new General.DownloadImageTask(displayImageIV)
                    .execute(user.getMedia().getPath());
        }

        DatePickerDialog.OnDateSetListener date = createDateDialog();

        setupGenderTextView();

        rideRadiusET.setText("5");

        dobET.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            new DatePickerDialog(EditProfileActivity.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        saveChangesTV.setOnClickListener(v -> {
            firstName = firstNameET.getText().toString();
            lastName = lastNameET.getText().toString();
            emailAddress = emailAddressET.getText().toString();
            dialcode = countryCodePicker.getSelectedCountryCode();
            mobile = mobileET.getText().toString();
            gender = genderTV.getText().toString();
            dob = dobET.getText().toString();
            rideRadius = rideRadiusET.getText().toString();

            if (firstName.equals(""))
                General.showAlert(this, getResources().getString(R.string.please_enter_first_name), null);
            else if (lastName.equals(""))
                General.showAlert(this, getResources().getString(R.string.please_enter_last_name), null);
            else if (emailAddress.equals(""))
                General.showAlert(this, getResources().getString(R.string.please_enter_email), null);
            else if (mobile.equals(""))
                General.showAlert(this, getResources().getString(R.string.please_enter_your_mobile), null);
            else if (rideRadius.equals("") || rideRadius.equals("0"))
                General.showAlert(this, getResources().getString(R.string.ride_radius_threshold), null);
            else updateProfile(mediaID);
        });

        uploadImageIV.setOnClickListener(v -> {

            try {
                if (ActivityCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_GALLERY);
                } else {
                    setupimageOptionDialog();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

    }


    private void setupGenderTextView() {
        String[] gender = {"Male", "Female"};

        //Creating the instance of ArrayAdapter containing list of fruit names
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, gender);

        genderTV.setThreshold(0);//will start working from first character
        genderTV.setAdapter(adapter);
    }

    private DatePickerDialog.OnDateSetListener createDateDialog() {
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };
        return date;
    }

    private void setupimageOptionDialog() {
        new AlertDialog.Builder(EditProfileActivity.this)
                .setMessage(getResources().getString(R.string.camera_or_gallery))
                .setPositiveButton(getResources().getString(R.string.camera), (dialog, which) -> {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    try {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    } catch (ActivityNotFoundException e) {
                        // display error state to the user
                    }
                })
                .setNegativeButton(getResources().getString(R.string.gallery), (dialog, which) -> {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, REQUEST_GALLERY);
                })
                .setNeutralButton(getResources().getString(R.string.cancel), null)
                .show();
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dobET.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateProfile(String mediaID) {
        if (imageFile == null)
            new AsyncUpdateProfile(firstName, lastName, emailAddress, dialcode, mobile, gender, dob, rideRadius, mediaID).execute();
        else {
            new AsyncUploadImage(this, "user", imageFile).execute();

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_GALLERY:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupimageOptionDialog();
                } else {
                    //do something like displaying a message that he didn`t allow the app to access gallery and you wont be able to let him select from gallery
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    Bundle extras = imageReturnedIntent.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    displayImageIV.setImageBitmap(imageBitmap);

                    // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                    Uri tempUri = General.getImageUri(getApplicationContext(), imageBitmap);

                    // CALL THIS METHOD TO GET THE ACTUAL PATH
                    imageFile = new File(General.getPathCamera(getApplicationContext(), tempUri));
                }
                break;
            case REQUEST_GALLERY:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    displayImageIV.setImageURI(selectedImage);

                    String imagepath = General.getPathGallery(this, selectedImage);
                    imageFile = new File(imagepath);
                }

                break;
        }
    }

    public class AsyncUploadImage extends AsyncTask<String, String, String> {

        @SuppressLint("StaticFieldLeak")
        private String mediaFor;
        private File file;
        private Context context;

        private Dialog loadingDialog;

        public AsyncUploadImage(Context mcontext, String mmediaFor, File mimage) {
            mediaFor = mmediaFor;
            file = mimage;
            context = mcontext;

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

                response = httpUrlConnectionService.uploadMedia(mediaFor, file);
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
                Log.e("response post image", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Boolean outcome = jsonObject.getBoolean("success");
                    Log.e("outcome", Boolean.toString(outcome));

                    if (loadingDialog != null && loadingDialog.isShowing())
                        loadingDialog.dismiss();
                    if (outcome) {
                        AppService.saveMediaData(jsonObject.getJSONObject("data").toString());
                        new AsyncUpdateProfile(firstName, lastName, emailAddress, dialcode, mobile, gender, dob, rideRadius, Integer.toString(Objects.requireNonNull(AppService.getMedia()).getId())).execute();
                    } else {
                        new AlertDialog.Builder(context)
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

    public class AsyncUpdateProfile extends AsyncTask<String, String, String> {

        @SuppressLint("StaticFieldLeak")
        private String firstName, lastName, email, dialCode, mobileNumber, radius, gender, dob, mediaID;

        private Dialog loadingDialog;

        public AsyncUpdateProfile(String mfirstName, String mlasName,
                                  String memail, String mdialCode, String mmobileNumber,
                                  String mdob, String mgender, String mradius, String mmediaID) {
            firstName = mfirstName;
            lastName = mlasName;
            email = memail;
            dialCode = mdialCode;
            mobileNumber = mmobileNumber;
            gender = mgender;
            dob = mdob;
            radius = mradius;
            mediaID = mmediaID;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (loadingDialog == null) {
                loadingDialog = General.loadingProgress(EditProfileActivity.this);
                loadingDialog.show();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String responseString = null;
            try {

                response = httpUrlConnectionService.updateProfile(firstName, lastName, email, dialCode, mobileNumber, gender, dob, radius, mediaID);
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

                        Gson gson = new GsonBuilder().create();

                        LoggedInUser updatedUser = gson.fromJson(jsonObject.getJSONObject("data").toString(), LoggedInUser.class);
                        Objects.requireNonNull(AppService.getUser()).setFirst_name(updatedUser.getFirst_name());
                        Objects.requireNonNull(AppService.getUser()).setLast_name(updatedUser.getLast_name());
                        Objects.requireNonNull(AppService.getUser()).setEmail(updatedUser.getEmail());
                        Objects.requireNonNull(AppService.getUser()).setGender(updatedUser.getGender());
                        Objects.requireNonNull(AppService.getUser()).setDob(updatedUser.getDob());
                        Objects.requireNonNull(AppService.getUser()).setSearch_radius(updatedUser.getSearch_radius());
                        Objects.requireNonNull(AppService.getUser()).setPhone_number(updatedUser.getPhone_number());
                        Objects.requireNonNull(AppService.getUser()).setCountry_code(updatedUser.getCountry_code());
                        Objects.requireNonNull(AppService.getUser()).setMedia(updatedUser.getMedia());
                        Log.e("edit profile act", AppService.getUser().getFirst_name());

                        new AlertDialog.Builder(EditProfileActivity.this)
                                .setMessage(jsonObject.getString("message"))
                                .setPositiveButton(getResources().getString(R.string.ok), (dialog, which) -> {
                                    finish();
                                })
                                .show();
                    } else {
                        new AlertDialog.Builder(EditProfileActivity.this)
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
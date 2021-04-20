package com.example.rytryde;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.rytryde.service.app.CmsDataService;
import com.example.rytryde.service.http.cms.CmsSevice;
import com.example.rytryde.service.http.cms.ICmsService;
import com.example.rytryde.utils.Constants;

import org.json.JSONObject;

import java.util.List;

import okhttp3.Response;

public class ContactUsActivity extends AppCompatActivity {

    Toolbar TB_cms;
    TextView TV_cms_toolbar;
    Spinner subject_spinner;
    CardView submitCV;
    EditText messageET;
    int selectedSubjectID = -1;
    String message = "";

    private ICmsService cmsService = new CmsSevice();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        getIntent();

        TB_cms = findViewById(R.id.cmsToolbar);
        TV_cms_toolbar = findViewById(R.id.cmsToolbarTitle);
        subject_spinner = findViewById(R.id.subjectSpinner);
        submitCV = findViewById(R.id.submitCV);
        messageET = findViewById(R.id.contactMessageET);

        setSupportActionBar(TB_cms);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        TV_cms_toolbar.setText(R.string.contact_us);


        submitCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = messageET.getText().toString();
                if (selectedSubjectID < 0 || message.equals("")) {
                    inflateDialog(getResources().getString(R.string.incompletedata), getResources().getString(R.string.entermessagechoosesubject));


                } else {
                    new AsyncContactUs("ContactUs", selectedSubjectID, message).execute();
                }
            }
        });

        new AsyncContactUs(Constants.SUBJECTS).execute();
    }

    public void setSubject_spinner() {

        List<String> subjectList = CmsDataService.getContactSubject();
        subjectList.add(0, getResources().getString(R.string.select_your_subject));

        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.item_contactus_spinner, subjectList) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(getResources().getColor(R.color.darker_gray));
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.item_contactus_spinner);
        subject_spinner.setAdapter(spinnerArrayAdapter);

        subject_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if (position > 0) {
                    selectedSubjectID = CmsDataService.getContactSubjectID(position - 1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void inflateDialog(String title, String desc) {
        LayoutInflater factory = LayoutInflater.from(this);
        final View dialogView = factory.inflate(R.layout.dialog_ok, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(this).create();
        deleteDialog.setView(dialogView);
        ((TextView) dialogView.findViewById(R.id.dialog_title)).setText(title);
        ((TextView) dialogView.findViewById(R.id.dialog_desc)).setText(desc);
        dialogView.findViewById(R.id.okCV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
            }
        });

        deleteDialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public class AsyncContactUs extends AsyncTask<String, String, String> {

        String nextURL;
        @SuppressLint("StaticFieldLeak")
        private Context context;
        private String type;
        private int subjectID;
        private String message;

        private ProgressDialog loadingDialog;

        public AsyncContactUs(String mReqType) {
            type = mReqType;

        }

        public AsyncContactUs(String mReqType, int mSubjectID, String mMessage) {
            type = mReqType;
            subjectID = mSubjectID;
            message = mMessage;


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (loadingDialog == null) {
                loadingDialog = new ProgressDialog(ContactUsActivity.this);
                loadingDialog.setMessage(getString(R.string.loading));
                loadingDialog.setCancelable(false);
                loadingDialog.show();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String responseString = null;
            try {
                if (type.equals(Constants.SUBJECTS))
                    response = cmsService.getSubjectList();
                else {
                    response = cmsService.contactUs(subjectID, message);
                }

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

                    if (loadingDialog != null && loadingDialog.isShowing())
                        loadingDialog.dismiss();
                    if (outcome & type.equals(Constants.SUBJECTS)) {
                        CmsDataService.saveContactSubject(jsonObject.getString("data"));
                        setSubject_spinner();
                    }
                    if (outcome & type.equals("ContactUs")) {
                        inflateDialog(getResources().getString(R.string.success), getResources().getString(R.string.thankyou));
                    }

                } catch (Exception e) {
                    Log.e("exception", e.getMessage());
                }

            }

        }


    }
}
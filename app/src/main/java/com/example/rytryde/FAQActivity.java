package com.example.rytryde;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rytryde.adapters.FAQItemAdapter;
import com.example.rytryde.service.app.CmsDataService;
import com.example.rytryde.service.http.cms.CmsSevice;
import com.example.rytryde.service.http.cms.ICmsService;
import com.example.rytryde.utils.PageNationScrollListener;

import org.json.JSONObject;

import java.util.Objects;

import okhttp3.Response;

public class FAQActivity extends AppCompatActivity {

    Toolbar tb_cms;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = 1;
    private FAQItemAdapter faqItemAdapter;
    private ICmsService cmsService = new CmsSevice();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_f_a_q);

        getIntent();

        RecyclerView rv_faq = findViewById(R.id.rv_faq);
        tb_cms = findViewById(R.id.cmsToolbar);

        setSupportActionBar(tb_cms);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FAQActivity.this, LinearLayoutManager.VERTICAL, false);
        faqItemAdapter = new FAQItemAdapter(FAQActivity.this);
        rv_faq.setLayoutManager(linearLayoutManager);
        rv_faq.setAdapter(faqItemAdapter);

        rv_faq.addOnScrollListener(new PageNationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                loadNextPage();
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        loadFirstPage();
    }

    private void loadFirstPage() {
        new AsyncFAQ(true, 1, 20).execute();
    }

    private void loadNextPage() {
        new AsyncFAQ(false, CmsDataService.getNextFAQPageURL());

    }

    public class AsyncFAQ extends AsyncTask<String, String, String> {

        String nextURL;
        @SuppressLint("StaticFieldLeak")
        private Context context;
        private String type;
        private int page;
        private int limit;
        private boolean firstPage;

        private ProgressDialog loadingDialog;

        public AsyncFAQ(boolean mFirstPage, int mPage, int mLimit) {
            page = mPage;
            limit = mLimit;
            firstPage = mFirstPage;

        }

        public AsyncFAQ(boolean mFirstPage, String mnextURL) {
            nextURL = mnextURL;
            firstPage = mFirstPage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (loadingDialog == null) {
                loadingDialog = new ProgressDialog(FAQActivity.this);
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
                if (firstPage)
                    response = cmsService.faq(page, limit);
                else
                    response = cmsService.loadNextFAQPage(nextURL);

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
                        CmsDataService.saveFAQData(jsonObject.getString("data"));
                        faqItemAdapter.addAll(Objects.requireNonNull(CmsDataService.getFAQItems()));
                        if (CmsDataService.getNextFAQPageURL() == null)
                            isLastPage = true;
                        else isLastPage = false;

                    }

                } catch (Exception e) {
                    Log.e("exception", e.getMessage());
                }

            }

        }


    }
}
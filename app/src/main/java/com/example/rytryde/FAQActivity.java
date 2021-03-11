package com.example.rytryde;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.rytryde.adapters.FAQItemAdapter;

public class FAQActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_f_a_q);

        RecyclerView rv_faq = findViewById(R.id.rv_faq);
        FAQItemAdapter faqItemAdapter = new FAQItemAdapter(FAQActivity.this);
        rv_faq.setLayoutManager(new LinearLayoutManager(FAQActivity.this));
        rv_faq.setAdapter(faqItemAdapter);

    }
}
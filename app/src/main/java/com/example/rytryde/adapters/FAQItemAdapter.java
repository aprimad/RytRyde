package com.example.rytryde.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.example.rytryde.R;

import org.jetbrains.annotations.NotNull;

public class FAQItemAdapter extends RecyclerView.Adapter<FAQItemAdapter.ViewHolder> {

    public Activity activity;

    public FAQItemAdapter(Activity activity) {
        this.activity = activity;
    }

    @NotNull
    @Override
    public FAQItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.fqa_list_item, parent, false);
        return new ViewHolder(contactView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(@NotNull FAQItemAdapter.ViewHolder holder, int position) {

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return 3;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @SuppressLint("ResourceAsColor")
        public ViewHolder(View itemView) {
            super(itemView);

            TextView tv_faq_detail = itemView.findViewById(R.id.tv_faq_detail);
            LinearLayout ll_faq_item = itemView.findViewById(R.id.ll_faq_item);
            TextView tv_faq_title = itemView.findViewById(R.id.tv_faq_title);

            tv_faq_title.setOnClickListener(view -> {
                if (tv_faq_detail.getVisibility() == View.VISIBLE) {

                    TransitionManager.beginDelayedTransition(ll_faq_item,
                            new AutoTransition());
                    tv_faq_detail.setVisibility(View.GONE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        tv_faq_title.setCompoundDrawables(null, null, activity.getDrawable(R.drawable.ic_add), null);
                    }
                } else {

                    TransitionManager.beginDelayedTransition(ll_faq_item,
                            new AutoTransition());
                    tv_faq_detail.setVisibility(View.VISIBLE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        tv_faq_title.setCompoundDrawables(null, null, activity.getDrawable(R.drawable.ic_remove), null);
                    }
                }
            });


        }
    }
}
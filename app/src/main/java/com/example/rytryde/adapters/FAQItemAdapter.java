package com.example.rytryde.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rytryde.R;
import com.example.rytryde.data.model.FAQItemData;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class FAQItemAdapter extends RecyclerView.Adapter<FAQItemAdapter.ViewHolder> {

    public Activity activity;
    private List<FAQItemData> faqList;

    public FAQItemAdapter(Activity activity) {

        this.activity = activity;
        faqList = new LinkedList<>();
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

        FAQItemData FAQitem = faqList.get(position);
        ViewHolder faqViewHolder = (ViewHolder) holder;
        faqViewHolder.tv_faq_title.setText(FAQitem.getQuestion());
        faqViewHolder.tv_faq_detail.setText(FAQitem.getAnswer());

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return faqList == null ? 0 : faqList.size();
    }

    public void add(FAQItemData faq) {
        faqList.add(faq);
        notifyItemInserted(faqList.size() - 1);
    }

    public void addAll(List<FAQItemData> faqResults) {
        for (FAQItemData result : faqResults) {
            add(result);
        }
    }

    public FAQItemData getItem(int position) {
        return faqList.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_faq_detail;
        private TextView tv_faq_title;
        private LinearLayout ll_faq_item;

        @SuppressLint("ResourceAsColor")
        public ViewHolder(View itemView) {
            super(itemView);

            tv_faq_detail = itemView.findViewById(R.id.tv_faq_detail);
            ll_faq_item = itemView.findViewById(R.id.ll_faq_item);
            tv_faq_title = itemView.findViewById(R.id.tv_faq_title);

            tv_faq_title.setOnClickListener(view -> {
                if (tv_faq_detail.getVisibility() == View.VISIBLE) {

                    tv_faq_detail.setVisibility(View.GONE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        tv_faq_title.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(activity, R.drawable.ic_add), null);
                    }
                } else {
                    tv_faq_detail.setVisibility(View.VISIBLE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        tv_faq_title.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(activity, R.drawable.ic_remove), null);
                    }
                }
            });


        }
    }
}
package com.example.rytryde.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rytryde.MyPhonebookActivity;
import com.example.rytryde.R;
import com.example.rytryde.data.model.Contacts;
import com.example.rytryde.data.model.MyContacts;
import com.example.rytryde.service.http.contacts.ContactsService;
import com.example.rytryde.service.http.contacts.IContactsService;
import com.example.rytryde.utils.CircleImageView;
import com.example.rytryde.utils.General;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Response;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {


    private Context context;
    private List<MyContacts> contactsList;
    private List<Contacts> addContacts;
    private TextView addContactsTV;
    private IContactsService httpUrlConnectionService = new ContactsService();
    private Map<String, Contacts> contactsMap = new HashMap<>();

    public ContactAdapter(Context context, List<MyContacts> mcontactsList) {

        this.context = context;
        this.contactsList = mcontactsList;

        addContactsTV = ((MyPhonebookActivity) context).findViewById(R.id.tv_add_contacts);
        addContactsTV.setOnClickListener(v -> {
            Collection<Contacts> contacts = contactsMap.values();
            addContacts = new ArrayList<>(contacts);

            if (addContacts != null) new AsyncAddContacts(addContacts).execute();
        });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_phonebook_items, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final MyContacts contact = contactsList.get(position);
        if (contact.getFullName() != null) holder.nameTV.setText(contact.getFullName());
        if (contact.getPhotoURI() != null)
            holder.circleImageView.setImageURI(Uri.parse(contact.getPhotoURI()));
        else holder.circleImageView.setImageResource(R.drawable.ic_baseline_account_circle_24);

        if (contact.getAppUserId() == null) {
            holder.checkBox.setEnabled(false);
            holder.cardView.setEnabled(false);
            holder.item_ll.getBackground().setColorFilter(context.getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP);
        } else {
            holder.checkBox.setEnabled(true);
            holder.cardView.setEnabled(true);
            holder.item_ll.getBackground().setColorFilter(context.getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        }

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isChecked()) {
                if (!contactsMap.containsKey(contact.getId()))
                    contactsMap.put(contact.getId(), new Contacts(contact.getAppUserId(), contact.getFirst_name(), contact.getLast_name(), contact.getNumber(), contact.getId()));
            } else {
                contactsMap.remove(contact.getId());
            }
        });


    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTV;
        public CircleImageView circleImageView;
        private CardView cardView;
        private CheckBox checkBox;
        private LinearLayout item_ll;


        public ViewHolder(View v) {
            super(v);

            nameTV = v.findViewById(R.id.tv_contact_title);
            ;
            circleImageView = v.findViewById(R.id.contact_image);
            cardView = v.findViewById(R.id.contact_CV);
            checkBox = v.findViewById(R.id.contact_CB);
            item_ll = v.findViewById(R.id.ll_faq_item);

        }

    }

    public class AsyncAddContacts extends AsyncTask<String, String, String> {

        List<Contacts> addContactsList;
        Map<String, String> photos;
        @SuppressLint("StaticFieldLeak")

        private Dialog loadingDialog;

        public AsyncAddContacts(List<Contacts> mcontactList) {
            addContactsList = mcontactList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (loadingDialog == null) {
                contactsList.clear();
                loadingDialog = General.loadingProgress(context);
                loadingDialog.show();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            Response response = null;
            String responseString = null;
            try {

                response = httpUrlConnectionService.addContacts(addContactsList);
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
                    Boolean outcome = jsonObject.getBoolean("success");
                    Log.e("outcome", Boolean.toString(outcome));

                    if (loadingDialog != null && loadingDialog.isShowing())
                        loadingDialog.dismiss();
                    if (outcome) {
                        new AlertDialog.Builder(context)
                                .setMessage(jsonObject.getString("message"))
                                .setPositiveButton(context.getResources().getString(R.string.ok), null)
                                .show();
                    } else {
                        new AlertDialog.Builder(context)
                                .setMessage(jsonObject.getString("message"))
                                .setPositiveButton(context.getString(R.string.ok), null)
                                .show();
                    }

                } catch (Exception e) {
                    Log.e("exception", e.getMessage());
                }

            }

        }

    }
}

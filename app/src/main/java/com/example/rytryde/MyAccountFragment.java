package com.example.rytryde;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.rytryde.data.model.LoggedInUser;
import com.example.rytryde.service.app.AppService;
import com.example.rytryde.utils.CircleImageView;
import com.example.rytryde.utils.General;


public class MyAccountFragment extends Fragment {

    Intent i;
    private ImageView editProfileIV;
    private CircleImageView profileImage;
    private LinearLayout myContactsLL, offeredRidesLL, requestedRidesLL, savedAddressLL, myVehicleLL, myGroupsLL, contactUsLL, joinGroupLL,
            rateUsLL, tAndCLL, priPolLL, faqLL, whoareweLL;
    private TextView name, number, carbonsave;

    public MyAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        myContactsLL = (LinearLayout) view.findViewById(R.id.ll_mycontacts);
        offeredRidesLL = (LinearLayout) view.findViewById(R.id.ll_offeredRides);
        requestedRidesLL = (LinearLayout) view.findViewById(R.id.ll_requestedrides);
        savedAddressLL = (LinearLayout) view.findViewById(R.id.ll_savedaddress);
        myVehicleLL = (LinearLayout) view.findViewById(R.id.ll_myvehicle);
        myGroupsLL = (LinearLayout) view.findViewById(R.id.ll_mygroups);
        contactUsLL = (LinearLayout) view.findViewById(R.id.ll_contactus);
        joinGroupLL = (LinearLayout) view.findViewById(R.id.ll_joingroup);
        rateUsLL = (LinearLayout) view.findViewById(R.id.ll_rateus);
        tAndCLL = (LinearLayout) view.findViewById(R.id.ll_termsnconditions);
        priPolLL = (LinearLayout) view.findViewById(R.id.ll_privacypolicy);
        faqLL = (LinearLayout) view.findViewById(R.id.ll_faq);
        whoareweLL = (LinearLayout) view.findViewById(R.id.ll_whoarewe);
        editProfileIV = (ImageView) view.findViewById(R.id.iv_edit_profile);
        profileImage = (CircleImageView) view.findViewById(R.id.iv_user_profile);
        name = (TextView) view.findViewById(R.id.tv_user_name);
        carbonsave = (TextView) view.findViewById(R.id.tv_total_carbon_saved);
        number = (TextView) view.findViewById(R.id.tv_phoneNumber);

        LoggedInUser user = AppService.getUser();
        if (user != null) {
            String fullname = user.getFirst_name() + " " + user.getLast_name();
            name.setText(fullname);
            new General.DownloadImageTask(profileImage)
                    .execute(user.getMedia().getPath());
        }
        editProfileIV.setOnClickListener(v -> {
            i = new Intent(getContext(), EditProfileActivity.class);
            i.putExtra("caller", "MainActivity");
            startActivity(i);
        });
        myContactsLL.setOnClickListener(v -> {
            i = new Intent(getContext(), MyContactActivity.class);
            i.putExtra("caller", "MainActivity");
            startActivity(i);
        });
        offeredRidesLL.setOnClickListener(v -> {
          /*  i = new Intent(getContext(), EditProfileActivity.class);
            i.putExtra("caller", "MainActivity");
            startActivity(i);*/
        });
        requestedRidesLL.setOnClickListener(v -> {
          /*  i = new Intent(getContext(), EditProfileActivity.class);
            i.putExtra("caller", "MainActivity");
            startActivity(i);*/
        });
        savedAddressLL.setOnClickListener(v -> {
          /*  i = new Intent(getContext(), EditProfileActivity.class);
            i.putExtra("caller", "MainActivity");
            startActivity(i);*/
        });
        myVehicleLL.setOnClickListener(v -> {
            i = new Intent(getContext(), MyVehicle.class);
            i.putExtra("caller", "MainActivity");
            startActivity(i);
        });
        myGroupsLL.setOnClickListener(v -> {
            i = new Intent(getContext(), MyGroupActivity.class);
            i.putExtra("caller", "MainActivity");
            startActivity(i);
        });
        contactUsLL.setOnClickListener(v -> {
            i = new Intent(getContext(), ContactUsActivity.class);
            i.putExtra("caller", "MainActivity");
            startActivity(i);
        });
        joinGroupLL.setOnClickListener(v -> {
            i = new Intent(getContext(), MyGroupActivity.class);
            i.putExtra("caller", "MainActivity");
            startActivity(i);
        });
        rateUsLL.setOnClickListener(v -> {
            i = new Intent(getContext(), ContactUsActivity.class);
            i.putExtra("caller", "MainActivity");
            startActivity(i);
        });
        tAndCLL.setOnClickListener(v -> {
            i = new Intent(getContext(), TermsAndConditionsActivity.class);
            i.putExtra("caller", "MainActivity");
            startActivity(i);
        });
        faqLL.setOnClickListener(v -> {
            i = new Intent(getContext(), FAQActivity.class);
            i.putExtra("caller", "MainActivity");
            startActivity(i);
        });
        priPolLL.setOnClickListener(v -> {
            i = new Intent(getContext(), PrivacyPolicyActivity.class);
            i.putExtra("caller", "MainActivity");
            startActivity(i);
        });
        whoareweLL.setOnClickListener(v -> {
            i = new Intent(getContext(), WhoWeAreActivity.class);
            i.putExtra("caller", "MainActivity");
            startActivity(i);
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_account, container, false);
    }

}
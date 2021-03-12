package com.example.rytryde.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.rytryde.R;
import com.example.rytryde.data.model.UpcomingRidesData;


public class UpcomingRides extends Fragment {
    UpcomingRidesData upcomingRidesData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // upcomingRidesData= UpcomingRidesService.getUpcomingRides();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upcoming_rides, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout noRidesLL = (LinearLayout) view.findViewById(R.id.no_upcomingrides);
        if (upcomingRidesData != null)
            if (upcomingRidesData.getData().isEmpty())
                noRidesLL.setVisibility(View.VISIBLE);

    }
}
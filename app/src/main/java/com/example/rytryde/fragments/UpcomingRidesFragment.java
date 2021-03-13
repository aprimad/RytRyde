package com.example.rytryde.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rytryde.R;
import com.example.rytryde.adapters.UpcomingRideItemAdapter;
import com.example.rytryde.data.model.Ride;
import com.example.rytryde.service.app.UpcomingRidesService;
import com.example.rytryde.utils.AsyncUpcomingRides;

import java.util.List;


public class UpcomingRidesFragment extends Fragment {
    public RecyclerView.LayoutManager mLayoutManager;
    public RecyclerView mRecyclerView;
    public UpcomingRideItemAdapter rideItemAdapter;
    List<Ride> upcomingRidesData;
    LinearLayout noRidesLL;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        new AsyncUpcomingRides(this).execute();

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

        noRidesLL = (LinearLayout) view.findViewById(R.id.no_upcomingrides);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.upcomingRidesRV);
        refreshFragmentView();


    }

    public void refreshFragmentView() {
        upcomingRidesData = UpcomingRidesService.getUpcomingRidesData();


        if (upcomingRidesData == null)
            noRidesLL.setVisibility(View.VISIBLE);
        else {
            mLayoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
            rideItemAdapter = new UpcomingRideItemAdapter(getContext(), upcomingRidesData);

            mRecyclerView.setAdapter(rideItemAdapter);
        }
    }
}
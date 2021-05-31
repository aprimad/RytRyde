package com.example.rytryde;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.rytryde.adapters.TabAdapter;
import com.example.rytryde.fragments.RatingandReviewFragment;
import com.example.rytryde.fragments.UpcomingRidesFragment;
import com.example.rytryde.utils.AsyncUpcomingRides;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    Fragment upcomingRides, ratingReview, account;
    CardView submenuCV;
    FloatingActionButton rideFAB;
    private boolean fabExpanded = false;
    LinearLayout ridesLL, accountLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getIntent();

        BottomAppBar bottomAppBar = (BottomAppBar) findViewById(R.id.bottomAppBar);
        Toolbar topAppBar = findViewById(R.id.toolbar);
        /*NavigationView mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);*/
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        TabAdapter adapter = new TabAdapter(getSupportFragmentManager());
        CardView offerRide = findViewById(R.id.offerRideCV);
        CardView requestRide = findViewById(R.id.requestRidesCV);
        rideFAB = findViewById(R.id.rideFAB);
        submenuCV = findViewById(R.id.fabSubmenuCV);
        ridesLL = findViewById(R.id.ridesLL);
        accountLL = findViewById(R.id.accountLL);
        //SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);

        upcomingRides = new UpcomingRidesFragment();
        ratingReview = new RatingandReviewFragment();
        account = new MyAccountFragment();

        setSupportActionBar(topAppBar);

        /*ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, bottomAppBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setItemIconTintList(null);
        mNavigationView.setNavigationItemSelectedListener(this);*/


        adapter.addFragment(upcomingRides, "Upcoming Rides");
        adapter.addFragment(ratingReview, "Rating & Review");

        /*pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFragments();
                pullToRefresh.setRefreshing(false);
            }
        });*/

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        accountLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment, account)
                        .addToBackStack(null)
                        .commit();
            }
        });
        ridesLL.setOnClickListener(v -> getSupportFragmentManager().popBackStack());


        rideFAB.setOnClickListener(v -> {
            if (fabExpanded)
                closeFab();
            else expandFab();
        });

        offerRide.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, RideActivity.class);
            i.putExtra("caller", "Offer");
            startActivity(i);
        });

        requestRide.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, RideActivity.class);
            i.putExtra("caller", "Request");
            startActivity(i);
        });

    }

    public void expandFab() {

        submenuCV.setVisibility(View.VISIBLE);
        rideFAB.setImageResource(R.mipmap.ic_close);
        fabExpanded = true;

    }

    public void closeFab() {
        submenuCV.setVisibility(View.GONE);
        rideFAB.setImageResource(R.mipmap.ic_ride);
        fabExpanded = false;

    }


    public void refreshFragments() {
        new AsyncUpcomingRides(upcomingRides).execute();
    }
}
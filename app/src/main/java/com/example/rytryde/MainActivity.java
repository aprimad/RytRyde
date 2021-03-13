package com.example.rytryde;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.rytryde.fragments.RatingandReviewFragment;
import com.example.rytryde.fragments.UpcomingRidesFragment;
import com.example.rytryde.utils.TabAdapter;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getIntent();

        BottomAppBar bottomAppBar = (BottomAppBar) findViewById(R.id.bottomAppBar);
        Toolbar topAppBar = findViewById(R.id.toolbar);
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        TabAdapter adapter = new TabAdapter(getSupportFragmentManager());

        setSupportActionBar(topAppBar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, bottomAppBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setItemIconTintList(null);
        mNavigationView.setNavigationItemSelectedListener(this);

        adapter.addFragment(new UpcomingRidesFragment(), "Upcoming Rides");
        adapter.addFragment(new RatingandReviewFragment(), "Rating & Review");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent i;

        if (id == R.id.nav_contacts) {

        } else if (id == R.id.nav_offer) {

        } else if (id == R.id.nav_request) {

        } else if (id == R.id.nav_address) {

        } else if (id == R.id.nav_vehicle) {

        } else if (id == R.id.nav_rate) {

        } else if (id == R.id.nav_groups) {

        } else if (id == R.id.nav_join_group) {

        } else if (id == R.id.nav_contact_us) {
            Log.e("menu", "clicked");
            i = new Intent(MainActivity.this, ContactUsActivity.class);
            i.putExtra("caller", "ContactUsActivity");
            startActivity(i);

        } else if (id == R.id.nav_tnc) {
            i = new Intent(MainActivity.this, TermsAndConditionsActivity.class);
            i.putExtra("caller", "TnCActivity");
            startActivity(i);

        } else if (id == R.id.nav_privacy) {
            i = new Intent(MainActivity.this, PrivacyPolicyActivity.class);
            i.putExtra("caller", "PrivacyPolicyActivity");
            startActivity(i);

        } else if (id == R.id.nav_faq) {
            i = new Intent(MainActivity.this, FAQActivity.class);
            i.putExtra("caller", "FAQActivity");
            startActivity(i);

        } else if (id == R.id.nav_whoarewe) {
            i = new Intent(MainActivity.this, WhoWeAreActivity.class);
            i.putExtra("caller", "WhoWeAreActivity");
            startActivity(i);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
package com.example.rytryde;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.rytryde.ui.login.LoginActivity;


public class OnboarderActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    ImageButton mNextBtn, mFinishTV;
    TextView mSkipTV;

    ImageView zero, one, two;
    ImageView[] indicators;

    int lastLeftValue = 0;

    int page = 0;   //  to track page position

    SectionsPagerAdapter mSectionsPagerAdapter;
    CoordinatorLayout mCoordinator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarder);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mNextBtn = findViewById(R.id.intro_btn_next);
        mSkipTV = findViewById(R.id.intro_btn_skip);
        mFinishTV =  findViewById(R.id.intro_btn_finish);

        zero =  findViewById(R.id.intro_indicator_0);
        one =  findViewById(R.id.intro_indicator_1);
        two =  findViewById(R.id.intro_indicator_2);

        mCoordinator = (CoordinatorLayout) findViewById(R.id.main_content);


        indicators = new ImageView[]{zero, one, two};

        mViewPager = findViewById(R.id.view_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setCurrentItem(page);
        updateIndicators(page);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {

                page = position;

                updateIndicators(page);

                switch (position) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                }


                mNextBtn.setVisibility(position == 2 ? View.GONE : View.VISIBLE);
                mFinishTV.setVisibility(position == 2 ? View.VISIBLE : View.GONE);


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page += 1;
                mViewPager.setCurrentItem(page, true);
            }
        });

        mSkipTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences =
                        getSharedPreferences("my_preferences", MODE_PRIVATE);

                preferences.edit()
                        .putBoolean("onboarding_complete",true).apply();

                Intent main = new Intent(OnboarderActivity.this, LoginActivity.class);
                startActivity(main);

                finish();
            }
        });

        mFinishTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //  update 1st time pref
                SharedPreferences preferences =
                        getSharedPreferences("my_preferences", MODE_PRIVATE);

                preferences.edit()
                        .putBoolean("onboarding_complete",true).apply();

                Intent main = new Intent(OnboarderActivity.this, LoginActivity.class);
                startActivity(main);

                finish();

            }
        });

    }


    void updateIndicators(int position) {
        for (int i = 0; i < indicators.length; i++) {
            indicators[i].setBackgroundResource(
                    i == position ? R.drawable.indicator_selected : R.drawable.indicator_unselected
            );
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        ImageView img;
        TextView titleOnboarderTV;
        TextView descOnboarderTV;

        int[] bgs = new int[]{R.mipmap.bg_walk01, R.mipmap.bg_walk02, R.mipmap.bg_walk03};


        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_onboarder, container, false);

            String[] title = getResources().getStringArray(R.array.onboarder_title);
            String[] desc = getResources().getStringArray(R.array.onboarder_desc);

            titleOnboarderTV = (TextView) rootView.findViewById(R.id.section_label);
            titleOnboarderTV.setText(title[getArguments().getInt(ARG_SECTION_NUMBER)-1]);

            descOnboarderTV = (TextView) rootView.findViewById(R.id.section_desc);
            descOnboarderTV.setText(desc[getArguments().getInt(ARG_SECTION_NUMBER)-1]);

            img = (ImageView) rootView.findViewById(R.id.section_img);
            img.setBackgroundResource(bgs[getArguments().getInt(ARG_SECTION_NUMBER) - 1]);


            return rootView;
        }


    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }


    }

}
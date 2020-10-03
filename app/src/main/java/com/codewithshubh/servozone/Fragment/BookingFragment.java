package com.codewithshubh.servozone.Fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.codewithshubh.servozone.Activity.MainActivity;
import com.codewithshubh.servozone.R;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;
/**
 * A simple {@link Fragment} subclass.
 */
public class BookingFragment extends Fragment {
    private TextView toolbar_title;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Adapter adapter;
    private boolean allowRefresh = false;

    public BookingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_booking, container, false);

        //Initialize variables
        Init(view);

        // Setting ViewPager for each Tabs
        SetupViewPager(viewPager);

        // Set Tabs inside Toolbar
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    private void SetupViewPager(ViewPager viewPager) {
        adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new OnGoingBookingFragment(), "ON GOING");
        adapter.addFragment(new CompletedBookingFragment(), "HISTORY");
        viewPager.setAdapter(adapter);
    }

    private void Init(View view) {
        //setting toolbar title
        toolbar_title = ((MainActivity)getActivity()).findViewById(R.id.activity_main_toolbar_tv);
        toolbar_title.setText(getResources().getString(R.string.booking_fragment_toolbar_title));
        tabLayout = view.findViewById(R.id.fragment_booking_tabs);
        viewPager = view.findViewById(R.id.fragment_booking_viewpager);
    }


    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void Refresh(){
        // Setting ViewPager for each Tabs
        SetupViewPager(viewPager);

        // Set Tabs inside Toolbar
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Initialize();
        if(allowRefresh){
            allowRefresh=false;
            //call your initialization code here
            Refresh();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!allowRefresh)
            allowRefresh = true;
    }
}

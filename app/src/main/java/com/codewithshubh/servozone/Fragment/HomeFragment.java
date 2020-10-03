package com.codewithshubh.servozone.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.codewithshubh.servozone.Activity.MainActivity;
import com.codewithshubh.servozone.Adapter.BannerAdapter;
import com.codewithshubh.servozone.Adapter.ServiceGroupAdapter;
import com.codewithshubh.servozone.Adapter.ServiceHeaderAdapter;
import com.codewithshubh.servozone.Interface.IFirebaseLoadListener;
import com.codewithshubh.servozone.Model.BannerItemModel;
import com.codewithshubh.servozone.Model.ServiceGroup;
import com.codewithshubh.servozone.R;
import com.codewithshubh.servozone.Utils.NetworkCheck;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import cn.pedant.SweetAlert.SweetAlertDialog;
import dmax.dialog.SpotsDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements IFirebaseLoadListener {

    private TextView toolbar_title;
    private ViewPager2 viewPager2;
    private List<BannerItemModel> bannerItemModelList;
    private List<BannerItemModel> newBannerItemModelList;
    private BannerAdapter bannerAdapter;
    private AlertDialog dialog;
    private IFirebaseLoadListener iFirebaseLoadListener;
    private RecyclerView rv_service_group, rv_service_header;
    private DatabaseReference mydata;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<ServiceGroup> serviceGroups;
    private ServiceGroupAdapter adapter;
    private ServiceHeaderAdapter serviceHeaderAdapter;
    private Handler bannerHandler;
    private SweetAlertDialog noInternetDialog;
    private LinearLayout indicatorLayout;
    private Activity mActivity;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //Init
        Init(view);

        //Load and show banner slide
        SetupBanner();

        //Load data
        GetFirebaseData();

        swipeRefreshLayout.setColorSchemeResources(R.color.azureBlue);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SetupBanner();
                GetFirebaseData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        return view;
    }

    private void Init(View view) {
        //setting toolbar title
        toolbar_title = ((MainActivity)getActivity()).findViewById(R.id.activity_main_toolbar_tv);
        toolbar_title.setText(getResources().getString(R.string.home_fragment_toolbar_title));

        //Init
        mydata = FirebaseDatabase.getInstance().getReference("Services");
        dialog = new SpotsDialog.Builder().setContext(getActivity()).setCancelable(false).build();
        iFirebaseLoadListener = this;

        swipeRefreshLayout = view.findViewById(R.id.swipe);
        rv_service_group = view.findViewById(R.id.rv_service_group);
        rv_service_group.setHasFixedSize(true);
        rv_service_header = view.findViewById(R.id.rv_service_header);

        //View
        serviceGroups = new ArrayList<>();
        adapter = new ServiceGroupAdapter(getActivity(), serviceGroups);
        serviceHeaderAdapter = new ServiceHeaderAdapter(getActivity(), serviceGroups);

        rv_service_group.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rv_service_header.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        rv_service_group.setAdapter(adapter);
        rv_service_header.setAdapter(serviceHeaderAdapter);
        rv_service_header.setNestedScrollingEnabled(false);

        //viewpager2
        viewPager2 = view.findViewById(R.id.viewpager_banner);
        bannerItemModelList = new ArrayList<>();
        newBannerItemModelList = new ArrayList<>();

        indicatorLayout = view.findViewById(R.id.slide_indicator_lin);
        bannerHandler = new Handler();
    }

    private void SetupBanner() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Banner");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bannerItemModelList.clear();
                newBannerItemModelList.clear();
                for (DataSnapshot d: dataSnapshot.getChildren()){
                    BannerItemModel bannerItemModel = new BannerItemModel();
                    bannerItemModel.setImageUrl(d.child("imageUrl").getValue(true).toString());
                    bannerItemModelList.add(bannerItemModel);
                }
                newBannerItemModelList.addAll(bannerItemModelList);
                newBannerItemModelList.add(0, bannerItemModelList.get(bannerItemModelList.size()-1));
                newBannerItemModelList.add(bannerItemModelList.get(0));
                //setting viewpager2
                viewPager2.setClipToPadding(false);
                viewPager2.setClipChildren(false);
                viewPager2.setOffscreenPageLimit(3);
                viewPager2.setCurrentItem(1, false);

                viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

                CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
                compositePageTransformer.addTransformer(new MarginPageTransformer(20));
                compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
                    @Override
                    public void transformPage(@NonNull View page, float position) {
                        float r = 1 - Math.abs(position);
                        page.setScaleY(0.85f + r * 0.15f);
                    }
                });
                setUpIndicator(newBannerItemModelList);
                setCurrentIndicator(0);
                viewPager2.setPageTransformer(compositePageTransformer);
                viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        setCurrentIndicator(position-1);
                        //Log.d("Position: ", String.valueOf(position)+" ---CurrentItem: "+viewPager2.getCurrentItem());
                        bannerHandler.removeCallbacks(bannerRunnableForward);
                        bannerHandler.postDelayed(bannerRunnableForward, 3000);
                        // skip fake page (first), go to last page
                        if (position == 0) {
                            viewPager2.setCurrentItem(newBannerItemModelList.size()-2, false);
                            setCurrentIndicator(newBannerItemModelList.size()-3);
                        }
                        // skip fake page (last), go to first page
                        if (position == newBannerItemModelList.size()-1) {
                            viewPager2.setCurrentItem(1, true); //notice how this jumps to position 1, and not position 0. Position 0 is the fake page!
                            setCurrentIndicator(0);
                        }
                    }
                });
                bannerAdapter = new BannerAdapter(newBannerItemModelList, viewPager2);
                viewPager2.setAdapter(bannerAdapter);
                //bannerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUpIndicator(List bannerItemModelList){
        indicatorLayout.removeAllViews();
        ImageView[] indicators = new ImageView[bannerItemModelList.size()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8,0,8,0);
        for (int i=0; i<indicators.length-2;i++){
            indicators[i] = new ImageView(mActivity.getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    mActivity.getApplicationContext(),
                    R.drawable.indicator_inactive
            ));
            indicators[i].setLayoutParams(layoutParams);
            indicatorLayout.addView(indicators[i]);
        }
    }

    private void setCurrentIndicator(int index){
        int childCount = indicatorLayout.getChildCount();
        for (int i = 0; i<childCount; i++){
            ImageView imageView = (ImageView) indicatorLayout.getChildAt(i);
            if (i==index){
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        mActivity.getApplicationContext(),
                        R.drawable.indicator_active
                ));
            }
            else {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        mActivity.getApplicationContext(),
                        R.drawable.indicator_inactive
                ));
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        bannerHandler.removeCallbacks(bannerRunnableForward);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            mActivity =(Activity) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        bannerHandler.postDelayed(bannerRunnableForward, 3000);
    }

    private Runnable bannerRunnableForward = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
            //Log.d("TAG", "run: "+viewPager2.getCurrentItem());
        }
    };

    private void GetFirebaseData() {
        dialog.show();
        mydata.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //List<ServiceGroup> serviceGroups = new ArrayList<>();
                serviceGroups.clear();
                for(DataSnapshot groupSnapshot:dataSnapshot.getChildren())
                {
                    ServiceGroup serviceGroup = groupSnapshot.getValue(ServiceGroup.class);
                    Log.d("HomeFragment", "onDataChange: "+serviceGroup.isActiveStatus());
                    if(serviceGroup.isActiveStatus())
                        serviceGroups.add(serviceGroup);
                }
                iFirebaseLoadListener.onFirebaseLoadSuccess(serviceGroups);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                iFirebaseLoadListener.onFirebaseLoadFailed(databaseError.getMessage());
            }
        });
    }

    @Override
    public void onFirebaseLoadSuccess(List<ServiceGroup> serviceGroupList) {
        //ServiceGroupAdapter adapter = new ServiceGroupAdapter(getActivity(), serviceGroupList);
        //ServiceHeaderAdapter serviceHeaderAdapter = new ServiceHeaderAdapter(getActivity(), serviceGroupList);
        adapter.notifyDataSetChanged();
        serviceHeaderAdapter.notifyDataSetChanged();
        //rv_service_group.setAdapter(adapter);
        //rv_service_header.setAdapter(serviceHeaderAdapter);
        //rv_service_header.setNestedScrollingEnabled(false);
        dialog.dismiss();
    }


    @Override
    public void onFirebaseLoadFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }

}

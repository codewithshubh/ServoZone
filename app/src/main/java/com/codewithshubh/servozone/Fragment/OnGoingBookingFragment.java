package com.codewithshubh.servozone.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codewithshubh.servozone.Adapter.OnGoingBookingAdapter;
import com.codewithshubh.servozone.Model.BookingDetail;
import com.codewithshubh.servozone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

import static com.codewithshubh.servozone.Constant.Constants.CONFIRMED;
import static com.codewithshubh.servozone.Constant.Constants.PENDING;
import static com.codewithshubh.servozone.Constant.Constants.SM_ASSIGNED;


/**
 * A simple {@link Fragment} subclass.
 */
public class OnGoingBookingFragment extends Fragment {
    private List<BookingDetail> bookingDetailList;
    private OnGoingBookingAdapter onGoingBookingAdapter;
    private AlertDialog dialog;
    private DatabaseReference myRefBooking;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rv_bookingdetail;
    private FirebaseAuth mAuth;
    private String uid;
    private String parentNode;
    private TextView tv_msg;
    private boolean allowRefresh = false;

    public OnGoingBookingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_on_going_booking, container, false);
        
        Init(view);
        tv_msg.setVisibility(View.INVISIBLE);


        //Setting Recyclerview
        rv_bookingdetail.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rv_bookingdetail.setAdapter(onGoingBookingAdapter);

        //Load data
        GetFirebaseData();

        if(!bookingDetailList.isEmpty()) {
            rv_bookingdetail.setVisibility(View.VISIBLE);
        }
        else{
            rv_bookingdetail.setVisibility(View.INVISIBLE);
            tv_msg.setVisibility(View.VISIBLE);
        }

        swipeRefreshLayout.setColorSchemeResources(R.color.azureBlue);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetFirebaseData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }

    private void GetFirebaseData() {
        myRefBooking
                .orderByChild("bookingTimeStamp")/*orderByChild("userUID").equalTo(uid)*/
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dialog.show();
                bookingDetailList.clear();
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    if (child.exists()) {
                        parentNode = child.getKey();
                        BookingDetail bookingDetail = child.getValue(BookingDetail.class);
                        if (bookingDetail.getUserUID().equals(uid) && (bookingDetail.getBookingStatus().equals(PENDING) ||
                                bookingDetail.getBookingStatus().equals(CONFIRMED) || bookingDetail.getBookingStatus().equals(SM_ASSIGNED))) {
                            bookingDetailList.add(bookingDetail);
                        }
                    }
                }
                Collections.reverse(bookingDetailList);
                onGoingBookingAdapter.notifyDataSetChanged();
                if(!bookingDetailList.isEmpty()) {
                    rv_bookingdetail.setVisibility(View.VISIBLE);
                    tv_msg.setVisibility(View.INVISIBLE);
                }
                else{
                    rv_bookingdetail.setVisibility(View.INVISIBLE);
                    tv_msg.setVisibility(View.VISIBLE);
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Init(View view) {
        //Init
        tv_msg = view.findViewById(R.id.fragment_ongoing_msg_tv);
        dialog = new SpotsDialog.Builder().setContext(getActivity()).setCancelable(false).build();
        swipeRefreshLayout = view.findViewById(R.id.fragment_ongoing_swipe);
        rv_bookingdetail = view.findViewById(R.id.fragment_ongoing_rv);
        rv_bookingdetail.setHasFixedSize(true);

        //View
        bookingDetailList = new ArrayList<>();
        onGoingBookingAdapter = new OnGoingBookingAdapter(getActivity(), bookingDetailList);

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();
        myRefBooking = FirebaseDatabase.getInstance().getReference("Bookings");

    }

    @Override
    public void onResume() {
        super.onResume();
        //Initialize();
        if(allowRefresh){
            allowRefresh=false;
            //call your initialization code here
            GetFirebaseData();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!allowRefresh)
            allowRefresh = true;
    }

}


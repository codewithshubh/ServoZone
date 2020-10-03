package com.codewithshubh.servozone.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.codewithshubh.servozone.Fragment.BookingFragment;
import com.codewithshubh.servozone.Fragment.HelpFragment;
import com.codewithshubh.servozone.Fragment.HomeFragment;
import com.codewithshubh.servozone.Fragment.ProfileFragment;
import com.codewithshubh.servozone.Model.Notification;
import com.codewithshubh.servozone.R;
import com.codewithshubh.servozone.Utils.AutoStartHelper;
import com.codewithshubh.servozone.Utils.CheckIfLoggedIn;
import com.codewithshubh.servozone.Utils.NetworkCheck;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.codewithshubh.servozone.Constant.Constants.MY_PREFERENCES;
import static com.codewithshubh.servozone.Constant.Constants.REGISTERED_USERS;
import static com.codewithshubh.servozone.Constant.Constants.UNREGISTERED_USERS;

public class MainActivity extends AppCompatActivity {

    public static int HOME=1, BOOKING=2, PROFILE=3, BOOK_REQ = 4;
    public BottomNavigationView bnv;    //declaring variable for bottom navbar
    private Fragment home = new HomeFragment();         //declaring Fragment variable for Home fragment
    private Fragment help = new HelpFragment();         //declaring Fragment variable for Help fragment
    private Fragment booking = new BookingFragment();   //declaring Fragment variable for Booking fragment
    private Fragment profile = new ProfileFragment();   //declaring Fragment variable for Profile fragment
    private FirebaseAuth mAuth;     //declaring FirabaseAuth variable for Firebase authentication
    private FirebaseUser currentUser;       //declaring FirebaseUser variable for checking logged in user
    private boolean doubleBackToExitPressedOnce = false;
    private Toast toast;
    private SharedPreferences sharedpreferences;
    private String autoStart;
    private ImageView iv_notification;
    private TextView tv_notification_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedpreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        autoStart = sharedpreferences.getString("autoStart", "");
        if (autoStart.equals("")) {
            AutoStartHelper.getInstance().getAutoStartPermission(this);
        }

        //Initialization
        Init();

        CountNotificationNumber();

        iv_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MyNotificationActivity.class));
            }
        });

        openFragment(home);     //setting Home fragment as default fragment to show when MainActivity is opened

        new NetworkCheck(this).noInternetDialog();
    }

    private void CountNotificationNumber() {
        String uid = mAuth.getUid();
        DatabaseReference myRefNotifCount = FirebaseDatabase.getInstance().getReference("Notification");
        myRefNotifCount.orderByChild("notificationUid").equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot child: snapshot.getChildren()){
                    Notification notification = child.getValue(Notification.class);
                    if (!notification.isRead())
                        count++;
                }
                //setting badge
                if (count==0)
                    tv_notification_count.setVisibility(View.INVISIBLE);
                else{
                    tv_notification_count.setVisibility(View.VISIBLE);
                    tv_notification_count.setText(String.valueOf(count));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //callback method: refresh fragment on the basis of next activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BOOKING) { //handle if user click on Booking nav menu without logging in, loginactivity will open and when login done, redirect to booking fragment with reload
            if(resultCode == RESULT_OK) {
                String key = data.getStringExtra("key");
                if(key.equals("success")){
                    openFragmentWithRefresh(booking);
                    bnv.setSelectedItemId(R.id.bottom_navbar_booking);
                }
            }
        }
        if (requestCode == PROFILE) {//handle if user click on Profile nav menu without logging in, loginactivity will open and when login done, redirect to booking fragment with reload
            if(resultCode == RESULT_OK) {
                String key = data.getStringExtra("key");
                if(key.equals("success")){
                    openFragmentWithRefresh(profile);
                    bnv.setSelectedItemId(R.id.bottom_navbar_profile);
                }
            }
        }
        if(requestCode == BOOK_REQ){ //handle home fragment refresh, once booking done and booking activity is closed then refresh home fragment and show dialog
            if(resultCode == RESULT_OK) {
                String key = data.getStringExtra("key");
                if(key.equals("success")){
                    SweetAlertDialog s =  new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                    s.setCanceledOnTouchOutside(false);
                    s.setCancelable(false);
                    s.setTitleText("Booking Successful!");
                    s.setContentText(getString(R.string.booking_successful_msg));
                    s.setConfirmText("OK");
                    s.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                            openFragmentWithRefresh(home);
                            bnv.setSelectedItemId(R.id.bottom_navbar_home);
                        }
                    }).show();
                }
            }
        }
    }

    //Open fragment without re-loading
    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    //Open fragment with re-loading
    public void openFragmentWithRefresh(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.detach(fragment);
        transaction.attach(fragment);
        transaction.replace(R.id.container, fragment);
        //transaction.addToBackStack(null);
        transaction.commit();
    }

    //setting up two times back click within 2 seconds to exit from app
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        toast.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    protected void onStop () {
        super.onStop();
        toast.cancel();
    }

    //selection listener on bottom navigation menu
    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.bottom_navbar_home:
                            openFragment(home);
                            return true;
                        case R.id.bottom_navbar_help:
                            openFragment(help);
                            return true;
                        case R.id.bottom_navbar_booking:
                            currentUser = mAuth.getCurrentUser();
                            if(currentUser != null)
                                openFragment(booking);
                            else {
                                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                                startActivityForResult(i, BOOKING);
                                return false;
                            }
                            return true;
                        case R.id.bottom_navbar_profile:
                            currentUser = mAuth.getCurrentUser();
                            if(currentUser!=null)
                                openFragment(profile);
                            else {
                                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                                startActivityForResult(i, PROFILE);
                                return false;
                            }
                            return true;
                    }
                    return false;
                }
            };

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null)
            FirebaseMessaging.getInstance().subscribeToTopic(UNREGISTERED_USERS);
        else
            FirebaseMessaging.getInstance().subscribeToTopic(REGISTERED_USERS);
    }

    private void Init(){
        bnv = findViewById(R.id.bottom_navigation); //initializing bottom navbar
        bnv.setItemIconTintList(null);
        bnv.setSelectedItemId(R.id.bottom_navbar_home);    //default icon selected is home
        bnv.setOnNavigationItemSelectedListener(navigationItemSelectedListener);    //setting listener to bottom nav
        mAuth = FirebaseAuth.getInstance(); //initializing FirebaseAuth
        toast = Toast.makeText(this, getResources().getString(R.string.toast_backpress), Toast.LENGTH_SHORT);    //initialing toast message

        iv_notification = findViewById(R.id.activity_main_toolbar_notification);
        tv_notification_count = findViewById(R.id.activity_main_toolbar_badge);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new NetworkCheck(this).noInternetDialog();
    }

}

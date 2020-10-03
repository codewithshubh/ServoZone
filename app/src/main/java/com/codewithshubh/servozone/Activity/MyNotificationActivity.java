package com.codewithshubh.servozone.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codewithshubh.servozone.Adapter.NotificationAdapter;
import com.codewithshubh.servozone.Model.Notification;
import com.codewithshubh.servozone.R;
import com.codewithshubh.servozone.Utils.CheckIfLoggedIn;
import com.codewithshubh.servozone.Utils.MySwipeHelper;
import com.codewithshubh.servozone.Utils.NetworkCheck;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dmax.dialog.SpotsDialog;

public class MyNotificationActivity extends AppCompatActivity {
    private ImageView iv_back, iv_clearall;
    private RecyclerView recyclerView;
    private TextView tv_bg;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;
    private FirebaseAuth mAuth;
    private DatabaseReference myRefNotification;
    private String uid;
    private AlertDialog dialog;
    private MySwipeHelper swipeHelper;
    private String parentkey;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_notification);
        iv_back = findViewById(R.id.activity_notification_toolbar_icon);

        Init();

        setUpSwipeDelete();

        ResetNotificationCount();

        LoadNotificationData();

        //listening on back button click
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.azureBlue);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoadNotificationData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        iv_clearall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllNotification();
            }
        });

        new NetworkCheck(this).noInternetDialog();
    }

    private void clearAllNotification() {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(MyNotificationActivity.this, SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog.setTitleText("Are you sure?");
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.setCanceledOnTouchOutside(false);
        sweetAlertDialog.setContentText("Do you want to clear all your notifications?")
                .setConfirmText("Yes, Delete!")
                .setCancelText("Cancel")
                .setCancelButtonBackgroundColor(ContextCompat.getColor(MyNotificationActivity.this,R.color.dark_gray))
                .setConfirmButtonBackgroundColor(ContextCompat.getColor(MyNotificationActivity.this,R.color.red))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        dialog.show();
                        String uid = FirebaseAuth.getInstance().getUid();
                        DatabaseReference myRefClearAll = FirebaseDatabase.getInstance().getReference("Notification");
                        myRefClearAll.orderByChild("notificationUid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    for (DataSnapshot child: snapshot.getChildren()){
                                       myRefClearAll.child(child.getKey()).removeValue();
                                    }
                                    dialog.dismiss();
                                    SweetAlertDialog s = new SweetAlertDialog(MyNotificationActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                                    s.setCancelable(false);
                                    s.setCanceledOnTouchOutside(false);
                                    s.setTitleText("Done!")
                                            .setContentText("All notifications are deleted successfully.")
                                            .setConfirmText("OK")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    sweetAlertDialog.dismissWithAnimation();
                                                    finish();
                                                    startActivity(getIntent());
                                                }
                                            })
                                            .show();
                                }
                                dialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                dialog.dismiss();
                            }
                        });
                    }
                }).show();
    }

    private void LoadNotificationData() {
        dialog.show();
        myRefNotification.orderByChild("timeStamp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notificationList.clear();
                for (DataSnapshot child: snapshot.getChildren()){
                    Notification notification = child.getValue(Notification.class);
                    if (notification.getNotificationUid().equals(uid)) {
                        notificationList.add(notification);
                    }
                }
                if (notificationList.size()>0){
                    tv_bg.setVisibility(View.INVISIBLE);
                    Collections.reverse(notificationList);
                    notificationAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
                else {
                    tv_bg.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });
    }

    private void Init() {
        tv_bg = findViewById(R.id.activity_my_notification_tv);
        iv_clearall = findViewById(R.id.activity_my_notification_clearall);
        swipeRefreshLayout = findViewById(R.id.activity_my_notification_swipe_refresh_layout);
        dialog = new SpotsDialog.Builder().setContext(MyNotificationActivity.this).setCancelable(false).build();
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();
        myRefNotification = FirebaseDatabase.getInstance().getReference("Notification");
        recyclerView = findViewById(R.id.activity_my_notification_rv);
        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(this, notificationList);

        //Setting Recyclerview
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(notificationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void ResetNotificationCount() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getUid();
        DatabaseReference myRefNotifCount = FirebaseDatabase.getInstance().getReference("Notification");
        myRefNotifCount.orderByChild("notificationUid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child: snapshot.getChildren()){
                    Map readUpdate = new HashMap();
                    readUpdate.put("read", true);
                    myRefNotifCount.child(child.getKey()).updateChildren(readUpdate);

                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancelAll();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setUpSwipeDelete(){
        swipeHelper = new MySwipeHelper(this) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new MySwipeHelper.UnderlayButton(
                        "Delete",
                        0,
                        Color.parseColor("#D11A2A"),
                        new MySwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(final int pos) {
                                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(MyNotificationActivity.this, SweetAlertDialog.WARNING_TYPE);
                                sweetAlertDialog.setTitleText("Are you sure?");
                                sweetAlertDialog.setCancelable(false);
                                sweetAlertDialog.setCanceledOnTouchOutside(false);
                                sweetAlertDialog.setContentText("Won't be able to retrieve once deleted!")
                                        .setConfirmText("Yes, Delete!")
                                        .setCancelText("Cancel")
                                        .setCancelButtonBackgroundColor(ContextCompat.getColor(MyNotificationActivity.this,R.color.dark_gray))
                                        .setConfirmButtonBackgroundColor(ContextCompat.getColor(MyNotificationActivity.this,R.color.red))
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                dialog.show();
                                                String notificationID = notificationList.get(pos).getNotificationId();
                                                DatabaseReference myRefDel = FirebaseDatabase.getInstance().getReference("Notification");
                                                myRefDel.orderByChild("notificationId").equalTo(notificationID).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.exists()){
                                                            for (DataSnapshot child: snapshot.getChildren()){
                                                                parentkey = child.getKey();
                                                                break;
                                                            }
                                                            myRefDel.child(parentkey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    dialog.dismiss();
                                                                    SweetAlertDialog s = new SweetAlertDialog(MyNotificationActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                                                                    s.setCancelable(false);
                                                                    s.setCanceledOnTouchOutside(false);
                                                                    s.setTitleText("Done!")
                                                                            .setContentText("Notification deleted successfully.")
                                                                            .setConfirmText("OK")
                                                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                                @Override
                                                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                                    sweetAlertDialog.dismissWithAnimation();
                                                                                    finish();
                                                                                    startActivity(getIntent());
                                                                                }
                                                                            })
                                                                            .show();
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    dialog.dismiss();
                                                                }
                                                            });
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }
                                        }).show();
                            }
                        }
                ));
            }
        };
        swipeHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new NetworkCheck(this).noInternetDialog();
        new CheckIfLoggedIn(this).CheckForUser();
    }

    @Override
    protected void onStart() {
        super.onStart();
        new CheckIfLoggedIn(this).CheckForUser();
    }
}

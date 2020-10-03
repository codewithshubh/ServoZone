package com.codewithshubh.servozone.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.codewithshubh.servozone.API.NotificationClientAPI;
import com.codewithshubh.servozone.Model.BookingDetail;
import com.codewithshubh.servozone.Model.ServiceCategory;
import com.codewithshubh.servozone.Model.ServiceGroup;
import com.codewithshubh.servozone.Model.User;
import com.codewithshubh.servozone.R;
import com.codewithshubh.servozone.Utils.CheckIfLoggedIn;
import com.codewithshubh.servozone.Utils.NetworkCheck;
import com.codewithshubh.servozone.Utils.NotificationUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import cn.pedant.SweetAlert.SweetAlertDialog;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Response;
import static com.codewithshubh.servozone.Constant.Constants.BOOKING_ID;
import static com.codewithshubh.servozone.Constant.Constants.CANCELLED;
import static com.codewithshubh.servozone.Constant.Constants.COMPLETED;
import static com.codewithshubh.servozone.Constant.Constants.CONFIRMED;
import static com.codewithshubh.servozone.Constant.Constants.NA;
import static com.codewithshubh.servozone.Constant.Constants.PENDING;
import static com.codewithshubh.servozone.Constant.Constants.SM_ASSIGNED;

public class BookingDetailActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_CALL = 1;
    private Calendar c;
    private int px0, px10;
    private ViewGroup.MarginLayoutParams layoutParamsComplete, layoutParamsSM;
    private String BookingIdParent, BookingID, ServiceName, ServiceId, ServiceCategoryName, ServiceCategoryId, ServiceProvider, ContactPerson, BookingAddress,
            ContactNumber, DateOfBooking, TimeOfBooking, SerivceDateSlot,ServiceTimeSlot, Query, UserUID, UserEmail, UserMobile,
            BookingStatus, ServiceManName, ServiceManContact, BookingImgUrl, BookingFinalDate, BookingConfirmDate, BookingSMAssignedDate;
    private TextView tv_booking_id, tv_service_name, tv_category_name, tv_service_provider, tv_status, tv_booking_received_title,
            tv_booking_received_time, tv_booking_confirmed_title, tv_booking_confirmed_time, tv_booking_completed_title,
            tv_booking_completed_time, tv_address_name, tv_address, tv_address_contact, tv_query, tv_serviceman_name, tv_serviceman_contact,
            tv_booking_slot, tv_booking_sm_assigned_time, tv_booking_sm_assigned_title;
    private ImageView iv_booking_img, iv_confirm_dot, iv_completed_dot, iv_confirm_line, iv_back, iv_sm_dot, iv_sm_line;
    private RelativeLayout rel_complete, rel_confirm, rel_sm;
    private LinearLayout lin_cancel, lin_help;
    private CardView cv_serviceman;
    private Button btn_callserviceman;
    private FirebaseAuth mAuth;
    private AlertDialog dialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String uid, userName, deviceToken;
    private DatabaseReference myRefUser;
    private String whatsappNumber, messengerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);

        Resources r = this.getResources();
        px0 = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 0, r.getDisplayMetrics()));
        px10 = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10, r.getDisplayMetrics()));

        //Initialize
        Init();

        //GetIntent
        GetIntent();

        //Getting data from database
        GetBookingDetail();

        GetUserDetail();

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
                GetBookingDetail();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        //cancel booking
        lin_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetBookingDetail();
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(BookingDetailActivity.this, SweetAlertDialog.WARNING_TYPE);
                sweetAlertDialog.setTitleText("Are you sure?");
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.setCanceledOnTouchOutside(false);
                sweetAlertDialog.setContentText("Do you really want to cancel this booking!")
                        .setConfirmText("Yes, Cancel!")
                        .setCancelText("No")
                        .setCancelButtonBackgroundColor(ContextCompat.getColor(BookingDetailActivity.this, R.color.dark_gray))
                        .setConfirmButtonBackgroundColor(ContextCompat.getColor(BookingDetailActivity.this, R.color.red))
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(final SweetAlertDialog sDialog) {
                                if (BookingStatus.equals(PENDING)) {
                                    DatabaseReference myRefBooking = FirebaseDatabase.getInstance().getReference("Bookings")
                                            .child(BookingIdParent);
                                    Map bookingUpdate = new HashMap();
                                    bookingUpdate.put("bookingFinalDate", GetCurrentDate() + ", " + GetCurrentTime());
                                    bookingUpdate.put("bookingStatus", CANCELLED);
                                    //myRefBooking.child("bookingFinalDate").setValue(GetCurrentDate() + ", " + GetCurrentTime());
                                    myRefBooking.updateChildren(bookingUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            sDialog.dismiss();
                                            //notification payload
                                            String title = "Booking cancelled! :(";
                                            String userArr[] = userName.split(" ");
                                            String message = "Hi "+userArr[0]+"! "+"Your booking with Booking ID "
                                                    +BookingID+" has been cancelled as per your request";
                                            JsonObject payload = new NotificationUtils(BookingDetailActivity.this)
                                                    .buildNotificationPayload(deviceToken, title, message,
                                                    BookingImgUrl, uid);
                                            // send notification to receiver ID
                                            NotificationClientAPI.getAPIService().sendNotification(payload).enqueue(
                                                    new retrofit2.Callback<JsonObject>() {
                                                        @Override
                                                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                                            if (response.isSuccessful()) {

                                                            }
                                                        }
                                                        @Override public void onFailure(Call<JsonObject> call, Throwable t) { }
                                                    });
                                            SweetAlertDialog s = new SweetAlertDialog(BookingDetailActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                                            s.setCancelable(false);
                                            s.setCanceledOnTouchOutside(false);
                                            s.setTitleText("Cancelled!")
                                                    .setConfirmButtonBackgroundColor(ContextCompat.getColor(BookingDetailActivity.this, R.color.main_green_color))
                                                    .setContentText("You booking with booking ID " + BookingID + " has been cancelled successfully.")
                                                    .setConfirmText("OK")
                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                            sweetAlertDialog.dismissWithAnimation();
                                                            GetBookingDetail();
                                                        }
                                                    })
                                                    .show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            sDialog.dismiss();
                                            Toasty.error(BookingDetailActivity.this, "Something went wrong... Try again!", Toasty.LENGTH_SHORT, true).show();
                                        }
                                    });
                                } else {
                                    sDialog.dismiss();
                                    SweetAlertDialog s = new SweetAlertDialog(BookingDetailActivity.this, SweetAlertDialog.WARNING_TYPE);
                                    s.setCancelable(false);
                                    s.setCanceledOnTouchOutside(false);
                                    s.setTitleText("Booking Confirmed!")
                                            //.setConfirmButtonBackgroundColor(ContextCompat.getColor(BookingDetailActivity.this,R.color.main_green_color))
                                            .setContentText("You booking with booking ID " + BookingID + " has been confirmed and is under process. So you cannot cancel this booking. For more info, please contact our customer executive.")
                                            .setConfirmText("OK")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    sweetAlertDialog.dismissWithAnimation();
                                                }
                                            })
                                            .show();
                                }
                            }
                        })
                        .show();
            }
        });

        //call serviceman
        btn_callserviceman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = tv_serviceman_contact.getText().toString();
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + number));
                if (ContextCompat.checkSelfPermission(BookingDetailActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    ActivityCompat.requestPermissions(BookingDetailActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            MY_PERMISSIONS_CALL);
                    return;
                }
                startActivity(callIntent);
            }
        });

        lin_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnHelpClick(v);
            }
        });

        new NetworkCheck(this).noInternetDialog();

    }



    private void GetUserDetail() {
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();
        myRefUser = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        myRefUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                userName = user.getName();
                deviceToken = user.getDeviceToken();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //requesting permission for calling
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_CALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    String number = tv_serviceman_contact.getText().toString();
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + number));
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(callIntent);

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    //setting fetched data to views
    private void SettingView() {
        tv_booking_id.setText(BookingID);
        tv_service_name.setText(ServiceName);
        tv_category_name.setText(ServiceCategoryName);
        tv_address_name.setText(ContactPerson);
        tv_address.setText(BookingAddress);
        tv_address_contact.setText(ContactNumber);
        tv_booking_received_time.setText(DateOfBooking+", "+TimeOfBooking);
        tv_service_provider.setText("Provider: "+ServiceProvider);

        if (BookingStatus.equals(PENDING))
            tv_status.setText("Pending");
        else if (BookingStatus.equals(CONFIRMED))
            tv_status.setText("Confirmed");
        else if (BookingStatus.equals(SM_ASSIGNED))
            tv_status.setText("In Progress");
        else if (BookingStatus.equals(COMPLETED))
            tv_status.setText("Completed");
        else if (BookingStatus.equals(CANCELLED))
            tv_status.setText("Cancelled");
        else
            tv_status.setText("NA");

        tv_query.setText(Query);
        tv_serviceman_name.setText(ServiceManName);
        tv_serviceman_contact.setText(ServiceManContact);
        tv_booking_slot.setText(SerivceDateSlot+", "+ServiceTimeSlot);

        Picasso.get().load(BookingImgUrl).placeholder(R.drawable.default_image)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(iv_booking_img, new Callback() {
                    @Override
                    public void onSuccess() {
                        //holder.progress.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(BookingImgUrl).placeholder(R.drawable.default_image)
                                .into(iv_booking_img);
                    }
                });

        if(BookingStatus.equals(PENDING)){
            rel_complete.setVisibility(View.INVISIBLE);
            rel_sm.setVisibility(View.INVISIBLE);
            iv_confirm_line.setVisibility(View.INVISIBLE);
            layoutParamsComplete =
                    (ViewGroup.MarginLayoutParams) rel_complete.getLayoutParams();
            layoutParamsComplete.height = px0;
            layoutParamsSM = (ViewGroup.MarginLayoutParams) rel_sm.getLayoutParams();
            layoutParamsSM.height = px0;
            tv_booking_confirmed_time.setText("Booking is yet to be confirmed");
            iv_confirm_dot.setImageResource(R.drawable.dot_orange);
        }
        if(BookingStatus.equals(CONFIRMED)){
            lin_cancel.setVisibility(View.GONE);
            rel_complete.setVisibility(View.INVISIBLE);
            rel_sm.setVisibility(View.VISIBLE);
            layoutParamsComplete = (ViewGroup.MarginLayoutParams) rel_complete.getLayoutParams();
            layoutParamsComplete.height = px0;
            layoutParamsSM = (ViewGroup.MarginLayoutParams) rel_sm.getLayoutParams();
            layoutParamsSM.height = layoutParamsSM.WRAP_CONTENT;
            tv_status.setBackgroundResource(R.drawable.btn_confirmed);
            iv_confirm_line.setVisibility(View.VISIBLE);
            tv_booking_confirmed_time.setText(BookingConfirmDate);
            iv_confirm_dot.setImageResource(R.drawable.dot_green);
            iv_sm_dot.setImageResource(R.drawable.dot_orange);
            iv_sm_line.setVisibility(View.INVISIBLE);
            tv_booking_sm_assigned_time.setText("Serviceman is yet to be assigned");
        }
        if (BookingStatus.equals(SM_ASSIGNED)){
            lin_cancel.setVisibility(View.GONE);
            rel_complete.setVisibility(View.VISIBLE);
            cv_serviceman.setVisibility(View.VISIBLE);
            layoutParamsComplete = (ViewGroup.MarginLayoutParams) rel_complete.getLayoutParams();
            layoutParamsComplete.height = layoutParamsComplete.WRAP_CONTENT;
            layoutParamsSM = (ViewGroup.MarginLayoutParams) rel_sm.getLayoutParams();
            layoutParamsSM.height = layoutParamsSM.WRAP_CONTENT;
            tv_status.setBackgroundResource(R.drawable.btn_confirmed);
            iv_confirm_line.setVisibility(View.VISIBLE);
            tv_booking_confirmed_time.setText(BookingConfirmDate);
            tv_booking_sm_assigned_time.setText(BookingSMAssignedDate);
            iv_confirm_dot.setImageResource(R.drawable.dot_green);
            rel_sm.setVisibility(View.VISIBLE);
            iv_sm_line.setVisibility(View.VISIBLE);
            iv_sm_dot.setImageResource(R.drawable.dot_green);
            iv_completed_dot.setImageResource(R.drawable.dot_orange);
            tv_booking_completed_time.setText("Service is not completed yet!");

        }
        if(BookingStatus.equals(CANCELLED)){
            lin_cancel.setVisibility(View.GONE);
            rel_confirm.setVisibility(View.GONE);
            rel_sm.setVisibility(View.GONE);
            rel_complete.setVisibility(View.VISIBLE);
            layoutParamsComplete =
                    (ViewGroup.MarginLayoutParams) rel_complete.getLayoutParams();
            layoutParamsComplete.height = layoutParamsComplete.WRAP_CONTENT;
            tv_booking_completed_title.setText("Cancelled");
            tv_booking_completed_time.setText(BookingFinalDate);
            tv_status.setBackgroundResource(R.drawable.btn_red);
            iv_completed_dot.setImageResource(R.drawable.dot_red);
        }
        if(BookingStatus.equals(COMPLETED)){
            lin_cancel.setVisibility(View.GONE);
            rel_complete.setVisibility(View.VISIBLE);
            cv_serviceman.setVisibility(View.VISIBLE);
            layoutParamsComplete = (ViewGroup.MarginLayoutParams) rel_complete.getLayoutParams();
            layoutParamsComplete.height = layoutParamsComplete.WRAP_CONTENT;
            layoutParamsSM = (ViewGroup.MarginLayoutParams) rel_sm.getLayoutParams();
            layoutParamsSM.height = layoutParamsSM.WRAP_CONTENT;
            iv_confirm_line.setVisibility(View.VISIBLE);
            iv_sm_line.setVisibility(View.VISIBLE);
            tv_booking_completed_time.setText(BookingFinalDate);
            tv_booking_confirmed_time.setText(BookingConfirmDate);
            tv_booking_sm_assigned_time.setText(BookingConfirmDate);
            tv_status.setBackgroundResource(R.drawable.btn_confirmed);
            iv_completed_dot.setImageResource(R.drawable.dot_green);
            iv_sm_dot.setImageResource(R.drawable.dot_green);
            iv_confirm_dot.setImageResource(R.drawable.dot_green);
            iv_confirm_line.setVisibility(View.VISIBLE);
        }

        if(ServiceManName.equals(NA)){
            cv_serviceman.setVisibility(View.INVISIBLE);
            layoutParamsComplete =
                    (ViewGroup.MarginLayoutParams) cv_serviceman.getLayoutParams();
            layoutParamsComplete.height = px0;
            layoutParamsComplete.bottomMargin = px0;
        }
        else {
            cv_serviceman.setVisibility(View.VISIBLE);
            layoutParamsComplete =
                    (ViewGroup.MarginLayoutParams) cv_serviceman.getLayoutParams();
            layoutParamsComplete.height = layoutParamsComplete.WRAP_CONTENT;
            layoutParamsComplete.bottomMargin = px10;
        }
        dialog.dismiss();
    }

    //getting all the booking details from firebase
    private void GetBookingDetail() {
        dialog.show();
        DatabaseReference myRefBooking = FirebaseDatabase.getInstance().getReference("Bookings").child(BookingIdParent);
        myRefBooking.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    BookingDetail bookingDetail = dataSnapshot.getValue(BookingDetail.class);
                    ServiceId = bookingDetail.getServiceId();
                    ServiceCategoryId = bookingDetail.getServiceCategoryId();
                    ServiceProvider = bookingDetail.getServiceProvider();
                    ContactPerson = bookingDetail.getContactPerson();
                    BookingAddress = bookingDetail.getBookingAddress();
                    ContactNumber = bookingDetail.getContactNumber();
                    DateOfBooking = bookingDetail.getDateOfBooking();
                    TimeOfBooking = bookingDetail.getTimeOfBooking();
                    SerivceDateSlot = bookingDetail.getServiceDateSlot();
                    ServiceTimeSlot = bookingDetail.getServiceTimeSlot();
                    Query = bookingDetail.getQuery();
                    UserUID = bookingDetail.getUserUID();
                    UserEmail = bookingDetail.getUserEmail();
                    UserMobile = bookingDetail.getUserMobile();
                    BookingStatus = bookingDetail.getBookingStatus();
                    ServiceManName = bookingDetail.getServiceManName();
                    ServiceManContact = bookingDetail.getServiceManContact();
                    BookingImgUrl = bookingDetail.getBookingImgUrl();
                    BookingFinalDate = bookingDetail.getBookingFinalDate();
                    BookingConfirmDate = bookingDetail.getBookingConfirmDate();
                    BookingID = bookingDetail.getBookingID();
                    BookingSMAssignedDate = bookingDetail.getSmAssignedDate();
                    Log.d("ServiceId", "onDataChange: "+ServiceId);
                    Log.d("ServiceCategoryId", "onDataChange: "+ServiceCategoryId);
                    //Getting Service Name
                    DatabaseReference myServiceRef = FirebaseDatabase.getInstance().getReference().child("Services");
                    myServiceRef.orderByChild("id").equalTo(ServiceId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                for (DataSnapshot child: dataSnapshot.getChildren()){
                                    ServiceGroup serviceGroup = child.getValue(ServiceGroup.class);
                                    ServiceName = serviceGroup.getServiceName();
                                    tv_service_name.setText(ServiceName);
                                    //Log.d("ServiceName", "onDataChange: "+ServiceName);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    //Getting Category Name
                    DatabaseReference myCategoryServiceRef = FirebaseDatabase.getInstance().getReference().child("ServiceCategory");
                    myCategoryServiceRef.orderByChild("id").equalTo(ServiceCategoryId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                for (DataSnapshot child: dataSnapshot.getChildren()){
                                    ServiceCategory serviceCategory = child.getValue(ServiceCategory.class);
                                    ServiceCategoryName = serviceCategory.getCategoryName();
                                    tv_category_name.setText(ServiceCategoryName);
                                    //Log.d("CategoryName", "onDataChange: "+ServiceCategoryName);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    //Log.d("Service Name", ServiceName);
                    SettingView();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toasty.error(BookingDetailActivity.this, "Something went wrong...", Toasty.LENGTH_SHORT, true).show();
            }
        });
    }

    //getting booking node from previous activity
    private void GetIntent() {
        BookingIdParent = getIntent().getStringExtra(BOOKING_ID);
    }

    private void Init() {
        c = Calendar.getInstance();
        tv_booking_id = findViewById(R.id.activity_booking_detail_bookingid_tv);
        tv_service_name = findViewById(R.id.activity_booking_detail_service_tv);
        tv_category_name = findViewById(R.id.activity_booking_detail_category_tv);
        tv_service_provider = findViewById(R.id.activity_booking_detail_provider_tv);
        tv_status = findViewById(R.id.activity_booking_detail_status_tv);
        tv_booking_received_title = findViewById(R.id.activity_booking_detail_receive_title);
        tv_booking_received_time = findViewById(R.id.activity_booking_detail_receive_time);
        tv_booking_confirmed_title = findViewById(R.id.activity_booking_detail_confirm_title);
        tv_booking_confirmed_time = findViewById(R.id.activity_booking_detail_confirm_time);
        tv_booking_completed_title = findViewById(R.id.activity_booking_detail_complete_title);
        tv_booking_completed_time = findViewById(R.id.activity_booking_detail_complete_time);
        tv_booking_sm_assigned_title = findViewById(R.id.activity_booking_detail_sm_title);
        tv_booking_sm_assigned_time = findViewById(R.id.activity_booking_detail_sm_time);
        tv_address_name = findViewById(R.id.activity_booking_detail_address_name_tv);
        tv_address = findViewById(R.id.activity_booking_detail_address_address_full_tv);
        tv_address_contact = findViewById(R.id.activity_booking_detail_address_contact_tv);
        tv_query = findViewById(R.id.activity_booking_detail_query_tv);
        tv_serviceman_name = findViewById(R.id.activity_booking_detail_serviceboy_name_tv);
        tv_serviceman_contact = findViewById(R.id.activity_booking_detail_serviceboy_contact_tv);
        tv_booking_slot = findViewById(R.id.activity_booking_detail_slot_tv);

        swipeRefreshLayout = findViewById(R.id.activity_booking_detail_swipe);

        btn_callserviceman = findViewById(R.id.activity_booking_detail_serviceboy_call);

        cv_serviceman = findViewById(R.id.activity_booking_detail_cv4);

        iv_booking_img = findViewById(R.id.activity_booking_detail_image_iv);
        iv_confirm_dot = findViewById(R.id.activity_booking_detail_confirm_bullet);
        iv_completed_dot = findViewById(R.id.activity_booking_detail_completed_bullet);
        iv_sm_dot = findViewById(R.id.activity_booking_detail_sm_bullet);
        iv_confirm_line = findViewById(R.id.activity_booking_detail_confirm_line);
        iv_sm_line = findViewById(R.id.activity_booking_detail_sm_line);
        iv_back = findViewById(R.id.activity_booking_detail_toolbar_icon);

        rel_complete = findViewById(R.id.activity_booking_detail_completed_rel);
        rel_confirm = findViewById(R.id.activity_booking_detail_confirm_rel);
        rel_sm = findViewById(R.id.activity_booking_detail_sm_rel);
        lin_cancel = findViewById(R.id.activity_booking_detail_cancel_lin);
        lin_help = findViewById(R.id.activity_booking_detail_needhelp_lin);
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        mAuth = FirebaseAuth.getInstance();

        //getting whatsapp number and messenger link from server
        DatabaseReference myRefWp = FirebaseDatabase.getInstance().getReference("ChatSupport").child("Whatsapp").child("whatsappNumber");
        DatabaseReference myRefFb = FirebaseDatabase.getInstance().getReference("ChatSupport").child("Facebook").child("messengerID");

        myRefWp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                whatsappNumber = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        myRefFb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messengerID = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //getting current date
    private String GetCurrentTime() {
        int h, m;
        String currentTime, format;
        h = c.get(Calendar.HOUR_OF_DAY);
        m = c.get(Calendar.MINUTE);

        if (h == 0) {
            h += 12;
            format = "AM";
        } else if (h == 12) {
            format = "PM";
        } else if (h > 12) {
            h -= 12;
            format = "PM";
        } else {
            format = "AM";
        }

        if(h<10){
            if(m<10){
                currentTime = "0"+h+":"+"0"+m+" "+format;
            }
            else {
                currentTime = "0"+h+":"+m+" "+format;
            }
        }
        else {
            if(m<10){
                currentTime = h+":"+"0"+m+" "+format;
            }
            else {
                currentTime = h+":"+m+" "+format;
            }
        }
        return currentTime;
    }

    //getting current date
    private String GetCurrentDate() {
        String currentdate;
        int y, m, d;
        y = c.get(Calendar.YEAR);
        m = c.get(Calendar.MONTH)+1;
        d = c.get(Calendar.DAY_OF_MONTH);

        if(m <10){
            if(d <10)
                currentdate = "0"+d+"/"+"0"+m+"/"+y;
            else
                currentdate = d+"/"+"0"+m+"/"+y;
        }
        else{
            if(d<10)
                currentdate = "0"+d+"/"+m+"/"+y;
            else
                currentdate = d+"/"+m+"/"+y;
        }
        return currentdate;
    }

    private void OnHelpClick(View v) {
        //init the wrapper with style
        Context wrapper = new ContextThemeWrapper(this, R.style.MyPopupStyle);
        //init the popup
        PopupMenu popup = new PopupMenu(wrapper, v);

        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //inflate menu
        popup.getMenuInflater().inflate(R.menu.support_menu, popup.getMenu());

        //implement click events
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.support_facebook:
                        if (messengerID.isEmpty() || messengerID==null)
                            Toasty.error(BookingDetailActivity.this, "Something went wrong! Please try again...", Toasty.LENGTH_SHORT, true).show();
                        else {
                            Uri uri = Uri.parse(messengerID); // missing 'http://' will cause crashed
                            try{
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                                //Toast.makeText(getActivity(), "Facebook clicked", Toast.LENGTH_SHORT).show();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        break;
                    case R.id.support_whatsapp:
                        if (whatsappNumber.isEmpty() || wrapper==null){
                            Toasty.error(BookingDetailActivity.this, "Something went wrong! Please try again...", Toasty.LENGTH_SHORT, true).show();
                        }
                        else {
                            String message = "Need assistance regarding my booking ID: "+BookingID;
                            String url = null;
                            try {
                                url = "https://api.whatsapp.com/send?phone=" + whatsappNumber + "&text=" + URLEncoder.encode(message, "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                        }
                        //Toast.makeText(getActivity(), "Whatsapp clicked", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
        popup.show();
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

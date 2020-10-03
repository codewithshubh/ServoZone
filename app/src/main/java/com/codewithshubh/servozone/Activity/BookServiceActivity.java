package com.codewithshubh.servozone.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codewithshubh.servozone.API.NotificationClientAPI;
import com.codewithshubh.servozone.Model.BookingDetail;
import com.codewithshubh.servozone.Model.ServiceCategory;
import com.codewithshubh.servozone.Model.ServiceGroup;
import com.codewithshubh.servozone.Model.ServiceableArea;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.codewithshubh.servozone.Constant.Constants.NA;
import static com.codewithshubh.servozone.Constant.Constants.PENDING;
import static com.codewithshubh.servozone.Constant.Constants.SERVICE_CATEGORY_ID;
import static com.codewithshubh.servozone.Constant.Constants.SERVICE_ID;
import static com.codewithshubh.servozone.Constant.Constants.USER_ADDRESS;
import static com.codewithshubh.servozone.Constant.Constants.USER_NAME;
import static com.codewithshubh.servozone.Constant.Constants.USER_PHONE;
import static com.codewithshubh.servozone.Constant.Constants.USER_PIN;

public class BookServiceActivity extends AppCompatActivity {
    private static final int REQ = 1;
    private static final int DIALOG_ID = 0;
    private TextView tv_category, tv_service, tv_add_msg, tv_address_name, tv_address_full, tv_address_no, tv_charges, tv_provider;
    private ImageView iv_back;
    private String serviceCategoryId;
    private String serviceId;
    private LinearLayout lin_address;
    private Button btn_select_address, btn_book;
    private EditText et_query, et_date_slot, et_time_slot;
    private int year_x, month_x, day_x, y, m, d;
    private Calendar c;
    private AlertDialog dialog;
    private FirebaseAuth mAuth;
    private String BookingParentID, BookingID, ServiceName, ServiceCategoryName, ServiceProvider, ContactPerson, BookingAddress,
            ContactNumber, DateOfBooking, TimeOfBooking, SerivceDateSlot, ServiceTimeSlot, Query, UserUID, UserEmail, UserMobile,
            BookingStatus, ServiceManName, ServiceManContact, BookingImgUrl, BookingFinalDate, BookingConfirmDate, BookingCharge;
    private String uid;
    private long count = 0;
    private String servicablePinCode;
    private boolean isPinAvailable = false;
    private String deviceToken, userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_service);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Initialize
        Init();

        //Counting total bookings number from firebase db and saving in count variable
        GetBookingsCount();

        //getting current user details
        GetUserDetails();

        //back button click
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //choose date slot
        et_date_slot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DIALOG_ID);
            }
        });

        //choose time slot
        et_time_slot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(BookServiceActivity.this, et_time_slot);
                popup.getMenuInflater().inflate(R.menu.time_slot_menu, popup.getMenu());
                popup.show();

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.timeslot1:
                                et_time_slot.setText(menuItem.getTitle().toString());
                                break;
                            case R.id.timeslot2:
                                et_time_slot.setText(menuItem.getTitle().toString());
                                break;
                            case R.id.timeslot3:
                                et_time_slot.setText(menuItem.getTitle().toString());
                                break;
                            case R.id.timeslot4:
                                et_time_slot.setText(menuItem.getTitle().toString());
                                break;
                        }
                        return true;
                    }
                });
            }
        });

        //choose address
        btn_select_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookServiceActivity.this, SelectAddressActivity.class);
                startActivityForResult(intent, REQ);
            }
        });

        //click action on booking button
        btn_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnBookClick();
            }
        });

        new NetworkCheck(this).noInternetDialog();

    }

    private void GetUserDetails() {
        final DatabaseReference myRefUser = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        myRefUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                UserEmail = user.getEmail();
                UserMobile = user.getPhone();
                deviceToken = user.getDeviceToken();
                userName = user.getName();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();
            }
        });
    }

    private void GetBookingsCount() {
        final DatabaseReference myRefBooking = FirebaseDatabase.getInstance().getReference("Bookings");
        myRefBooking.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    count = dataSnapshot.getChildrenCount();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void OnBookClick() {
        hideKeyboard(this);
        if(tv_address_name.getText().toString().isEmpty() || tv_address_full.getText().toString().isEmpty() ||
        tv_address_no.getText().toString().isEmpty()){
            Toasty.warning(BookServiceActivity.this, "Please select your address", Toasty.LENGTH_SHORT, true).show();
            return;
        }
        if(!validateDateSlot())
            return;
        if (!validateTimeSlot())
            return;
        if(!validateQuery())
            return;
        //Checking if pincode is available for service
        DatabaseReference myPinRef = FirebaseDatabase.getInstance().getReference().child("ServiceableArea");
        myPinRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dialog.show();
                if (dataSnapshot.exists()){
                    for (DataSnapshot child: dataSnapshot.getChildren()){
                        ServiceableArea serviceableArea = child.getValue(ServiceableArea.class);
                        //Log.d("BookingServiceActivity", "onDataChange: "+serviceableArea.getPinCode());
                        if (serviceableArea.getPinCode().equals(servicablePinCode)){
                            isPinAvailable = true;
                            break;
                        }
                    }
                    if (isPinAvailable){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                SendBookingData();
                            }
                        }, 1000);
                    }
                    else {
                        Toasty.warning(BookServiceActivity.this, getResources().getString(R.string.out_of_service_area),
                                Toasty.LENGTH_LONG, true).show();
                        dialog.dismiss();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toasty.warning(BookServiceActivity.this, databaseError.getMessage(), Toasty.LENGTH_SHORT, true).show();
            }
        });
    }

    private void SendBookingData() {
        ServiceName = tv_service.getText().toString();
        ServiceCategoryName = tv_category.getText().toString();
        ServiceProvider = tv_provider.getText().toString();
        ContactPerson = tv_address_name.getText().toString();
        BookingAddress = tv_address_full.getText().toString();
        ContactNumber = tv_address_no.getText().toString();
        SerivceDateSlot = et_date_slot.getText().toString();
        ServiceTimeSlot = et_time_slot.getText().toString();
        Query = et_query.getText().toString();
        DateOfBooking = GetCurrentDate();
        TimeOfBooking = GetCurrentTime();
        UserUID = uid;
        BookingStatus = PENDING;
        ServiceManName = "NA";
        ServiceManContact = "NA";
        BookingID = "OD" + (10000000+count+1);
        BookingParentID = UUID.randomUUID().toString();

        BookingFinalDate = "NA";
        BookingConfirmDate = "NA";
        BookingCharge = tv_charges.getText().toString();


        final DatabaseReference myRefBookings = FirebaseDatabase.getInstance().getReference("Bookings");
        BookingDetail bookingDetail = new BookingDetail(BookingParentID, BookingID, serviceId, serviceCategoryId, ServiceProvider,
                ContactPerson, BookingAddress, ContactNumber, DateOfBooking, TimeOfBooking, SerivceDateSlot, ServiceTimeSlot, Query, UserUID,
                UserEmail, UserMobile, BookingStatus, ServiceManName, ServiceManContact, BookingImgUrl, BookingFinalDate,
                BookingConfirmDate, BookingCharge, NA);


        myRefBookings.child(BookingParentID).setValue(bookingDetail).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //dialog.dismiss();
                Intent intent = new Intent();
                intent.putExtra("key", "success");
                setResult(RESULT_OK, intent);

                //notification payload
                String title = "Yay! Booking received";
                String userArr[] = userName.split(" ");
                String message = "Hi "+userArr[0]+"! "+"Your booking with Booking ID "
                        +BookingID+" has been received. You'll be notified once your booking is confirmed.";
                JsonObject payload = new NotificationUtils(BookServiceActivity.this)
                        .buildNotificationPayload(deviceToken, title, message, BookingImgUrl, uid);
                // send notification to receiver ID
                NotificationClientAPI.getAPIService().sendNotification(payload).enqueue(
                        new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                if (response.isSuccessful()) {

                                }
                            }
                            @Override public void onFailure(Call<JsonObject> call, Throwable t) { }
                        });

                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toasty.error(BookServiceActivity.this, "Something went wrong! Please try again...",
                        Toasty.LENGTH_SHORT, true).show();
            }
        });
        Map timestamp = new HashMap();
        timestamp.put("bookingTimeStamp", ServerValue.TIMESTAMP);
        myRefBookings.child(BookingParentID).updateChildren(timestamp);
    }

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

    @Override
    protected Dialog onCreateDialog(int id) {
        if(id == DIALOG_ID) {
            y = c.get(Calendar.YEAR);
            m = c.get(Calendar.MONTH);
            d = c.get(Calendar.DAY_OF_MONTH)+1;
            DatePickerDialog _date = new DatePickerDialog(this, dpickerListner, y, m, d){
                @Override
                public void onDateChanged(@NonNull DatePicker view, int year, int month, int dayOfMonth) {
                    if (year < y)
                        view.updateDate(y, m, d);

                    if (month < m && year == y)
                        view.updateDate(y, m, d);

                    if (dayOfMonth < d && year == y && month == m)
                        view.updateDate(y, m, d);
                }
            };
            return _date;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener dpickerListner = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            year_x = year;
            month_x = month+1;
            day_x = dayOfMonth;
            if(month_x <10){
                if(day_x <10)
                    et_date_slot.setText("0"+day_x+"/"+"0"+month_x+"/"+year_x);
                else
                    et_date_slot.setText(day_x+"/"+"0"+month_x+"/"+year_x);
            }
            else{
                if(day_x<10)
                    et_date_slot.setText("0"+day_x+"/"+month_x+"/"+year_x);
                else
                    et_date_slot.setText(day_x+"/"+month_x+"/"+year_x);
            }
        }
    };

    private void Init() {
        tv_category = findViewById(R.id.activity_book_servicecategory_tv_title);
        iv_back = findViewById(R.id.activity_book_service_toolbar_icon);
        tv_service = findViewById(R.id.activity_book_service_tv_title);
        lin_address = findViewById(R.id.activity_book_service_address_lin);
        tv_add_msg = findViewById(R.id.activity_book_service_address_msg);
        tv_address_name = findViewById(R.id.activity_book_service_address_name);
        tv_address_full = findViewById(R.id.activity_book_service_address_full);
        tv_address_no = findViewById(R.id.activity_book_service_address_contact);
        tv_charges = findViewById(R.id.activity_book_service_tv_charges);
        tv_provider = findViewById(R.id.activity_book_service_tv_provider);
        btn_select_address = findViewById(R.id.activity_book_service_btn_select_address);
        et_query = findViewById(R.id.activity_book_service_et_query);
        btn_book = findViewById(R.id.activity_book_service_btn_book);
        et_date_slot = findViewById(R.id.activity_book_service_et_date);
        et_time_slot = findViewById(R.id.activity_book_service_et_time);
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();
        c = Calendar.getInstance();
        serviceId = getIntent().getStringExtra(SERVICE_ID);
        serviceCategoryId = getIntent().getStringExtra(SERVICE_CATEGORY_ID);

        //setting Service category details and setting the textviews
        DatabaseReference myServiceCategoryRef = FirebaseDatabase.getInstance().getReference().child("ServiceCategory");
        myServiceCategoryRef.orderByChild("id").equalTo(serviceCategoryId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        ServiceCategory serviceCategory = child.getValue(ServiceCategory.class);
                        tv_category.setText(serviceCategory.getCategoryName());
                        ServiceCategoryName = serviceCategory.getCategoryName();
                        BookingImgUrl = serviceCategory.getImageUrl();
                        tv_charges.setText(serviceCategory.getCharges());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //getting Service name by using serviceId
        DatabaseReference myServiceRef = FirebaseDatabase.getInstance().getReference().child("Services");
        myServiceRef.orderByChild("id").equalTo(serviceId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()){
                    ServiceGroup serviceGroup = child.getValue(ServiceGroup.class);
                    tv_service.setText(serviceGroup.getServiceName());
                    break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void hideKeyboard(Activity activity){
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if(view == null)
            view = new View(activity);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //Validate date Slot
    private boolean validateDateSlot(){
        if(et_date_slot.getText().toString().isEmpty()){
            et_date_slot.setError("This field cannot be empty");
            Toasty.warning(this, "Please select date", Toasty.LENGTH_SHORT, true).show();
            requestFocus(et_date_slot);
            return false;
        }
        else{
            et_date_slot.setError(null);
        }
        return true;
    }

    //Validate time Slot
    private boolean validateTimeSlot(){
        if(et_time_slot.getText().toString().isEmpty()){
            et_time_slot.setError("This field cannot be empty");
            Toasty.warning(this, "Please select your time slot", Toasty.LENGTH_SHORT, true).show();
            requestFocus(et_time_slot);
            return false;
        }
        else{
            et_date_slot.setError(null);
        }
        return true;
    }

    //Validating requirement string
    private boolean validateQuery() {
        if(et_query.getText().toString().isEmpty()){
            et_query.setError("This field cannot be empty");
            requestFocus(et_query);
            return false;
        }
        else{
            et_query.setError(null);
        }
        return true;
    }

    //Focus on edit text
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ){
            if(resultCode == RESULT_OK) {
                String name = data.getStringExtra(USER_NAME);
                String address = data.getStringExtra(USER_ADDRESS);
                String phone = data.getStringExtra(USER_PHONE);
                servicablePinCode = data.getStringExtra(USER_PIN);

                tv_add_msg.setVisibility(View.INVISIBLE);
                lin_address.setVisibility(View.VISIBLE);
                tv_address_name.setText(name);
                tv_address_full.setText(address);
                tv_address_no.setText(phone);
                btn_select_address.setText("Change Address");
            }
        }
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

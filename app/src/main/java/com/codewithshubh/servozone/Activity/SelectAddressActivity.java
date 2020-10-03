package com.codewithshubh.servozone.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.codewithshubh.servozone.Adapter.SelectAddressAdapter;
import com.codewithshubh.servozone.Model.Address;
import com.codewithshubh.servozone.R;
import com.codewithshubh.servozone.Utils.CheckIfLoggedIn;
import com.codewithshubh.servozone.Utils.NetworkCheck;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import cn.pedant.SweetAlert.SweetAlertDialog;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

import static com.codewithshubh.servozone.Constant.Constants.ADDRESS_DETAIL;
import static com.codewithshubh.servozone.Constant.Constants.USER_ADDRESS;
import static com.codewithshubh.servozone.Constant.Constants.USER_NAME;
import static com.codewithshubh.servozone.Constant.Constants.USER_PHONE;
import static com.codewithshubh.servozone.Constant.Constants.USER_PIN;

public class SelectAddressActivity extends AppCompatActivity {
    private static final int REQ_CODE = 1;
    private int count;
    private TextView tv_count;
    private RelativeLayout addNewAddress;
    private RecyclerView rv_myaddress;
    private List<Address> addressList;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef, myRefSelectAddress;
    private SelectAddressAdapter selectAddressAdapter;
    private String uid;
    private String parent;
    private AlertDialog dialog;
    private ImageView iv_back;
    private TextView toolbar_title;
    private Button btn_select;
    private String name = "", address = "", phone = "", nameFirst = "", addressFirst = "", phoneFirst = "";
    private String pinCode;
    private String pinCodeFirst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_address);

        //setting toolbar title
        toolbar_title = findViewById(R.id.activity_selectaddress_toolbar_tv);
        toolbar_title.setText(getResources().getString(R.string.select_address_activity_titlebar_title)); //setting titlebar title

        //Initialize view
        Init();

        //Load Recyclerview data from firebase
        LoadData();

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                if(!name.isEmpty()) {
                    intent.putExtra(USER_NAME, name);
                    intent.putExtra(USER_ADDRESS, address);
                    intent.putExtra(USER_PHONE, phone);
                    intent.putExtra(USER_PIN, pinCode);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else if(!addressList.isEmpty()){
                    intent.putExtra(USER_NAME, nameFirst);
                    intent.putExtra(USER_ADDRESS, addressFirst);
                    intent.putExtra(USER_PHONE, phoneFirst);
                    intent.putExtra(USER_PIN, pinCodeFirst);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else {
                    Toasty.error(SelectAddressActivity.this, "No Address Found!", Toasty.LENGTH_SHORT, true).show();
                }
            }
        });

        //on add new address click
        addNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(SelectAddressActivity.this, AddNewAddressActivity.class), REQ_CODE);
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(ADDRESS_DETAIL));

        new NetworkCheck(this).noInternetDialog();
    }

    public void LoadData() {
        dialog.show();
        myRefSelectAddress = FirebaseDatabase.getInstance().getReference().child("UserAddress");
        myRefSelectAddress.orderByChild("creationTimeStamp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    count = 0;
                    addressList.clear();
                    for (DataSnapshot child : dataSnapshot.getChildren()){
                        final Address address = child.getValue(Address.class);
                        if (address.getUserId().equals(uid)){
                            addressList.add(address);
                        }
                    }
                    count = addressList.size();
                    if(count>0){
                        tv_count.setText(count + " SAVED ADDRESSES");
                        toolbar_title.setText("Select Address (" + count + ")");
                    }
                    else{
                        tv_count.setText("NO SAVED ADDRESSES");
                        toolbar_title.setText("Select Address (0)");
                    }
                    Collections.reverse(addressList);
                    if(!addressList.isEmpty()) {
                        nameFirst = addressList.get(0).getName();
                        if (!addressList.get(0).getLandmark().equals("")) {
                            addressFirst = addressList.get(0).getHouseNo() + ", " + addressList.get(0).getArea() + ", (Landmark: "
                                    + addressList.get(0).getLandmark() + "), " +
                                    addressList.get(0).getCity() + ", " + addressList.get(0).getState() + " - " +
                                    addressList.get(0).getPincode();
                        } else {
                            addressFirst = addressList.get(0).getHouseNo() + ", " + addressList.get(0).getArea() + ", " +
                                    addressList.get(0).getCity() + ", " + addressList.get(0).getState() + " - " +
                                    addressList.get(0).getPincode();
                        }
                        phoneFirst = addressList.get(0).getContact();
                        pinCodeFirst = addressList.get(0).getPincode();
                        Log.d("name: ", nameFirst);
                        Log.d("address: ", addressFirst);
                        Log.d("phone: ", phoneFirst);
                    }
                    selectAddressAdapter.notifyDataSetChanged();
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toasty.error(SelectAddressActivity.this, databaseError.getMessage(), Toasty.LENGTH_SHORT, true).show();
                dialog.dismiss();
            }
        });
    }

    private void Init() {
        addNewAddress = findViewById(R.id.activity_selectaddress_add_new_address);
        rv_myaddress = findViewById(R.id.activity_selectaddress_rv);
        tv_count = findViewById(R.id.activity_selectaddress_count_tv);
        addressList = new ArrayList<>();
        selectAddressAdapter = new SelectAddressAdapter(this, addressList);
        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference("Users");
        uid = mAuth.getUid();
        dialog = new SpotsDialog.Builder().setContext(SelectAddressActivity.this).setCancelable(false).build();
        iv_back = findViewById(R.id.activity_selectaddress_toolbar_icon);
        btn_select = findViewById(R.id.activity_selectaddress_select_btn);

        //Setting Recyclerview
        rv_myaddress.setHasFixedSize(true);
        rv_myaddress.setAdapter(selectAddressAdapter);
        rv_myaddress.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE){
            if(resultCode == RESULT_OK) {
                String key = data.getStringExtra("key");
                if(key.equals("success")){
                    SweetAlertDialog s =  new SweetAlertDialog(SelectAddressActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                    s.setCanceledOnTouchOutside(false);
                    s.setCancelable(false);
                    s.setTitleText("Success!");
                    s.setContentText("Your address has been saved successfully.");
                    s.setConfirmText("OK");
                    s.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                            LoadData();
                        }
                    }).show();
                }
            }
        }
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            name = intent.getStringExtra(USER_NAME);
            address = intent.getStringExtra(USER_ADDRESS);
            phone = intent.getStringExtra(USER_PHONE);
            pinCode = intent.getStringExtra(USER_PIN);
        }
    };

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

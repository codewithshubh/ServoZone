package com.codewithshubh.servozone.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.codewithshubh.servozone.Adapter.MyAddressAdapter;
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

public class MyAddressActivity extends AppCompatActivity {
    private static final int REQ_CODE = 1;
    private int count;
    private TextView tv_count;
    private RelativeLayout addNewAddress;
    private RecyclerView rv_myaddress;
    private List<Address> addressList;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private MyAddressAdapter myAddressAdapter;
    private String uid;
    private AlertDialog dialog;
    private ImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_address);

        //Initialize
        Init();

        //Load Recyclerview data from firebase
        LoadData();


        //on back icon click
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //on add new address click
        addNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MyAddressActivity.this, AddNewAddressActivity.class), REQ_CODE);
            }
        });

        new NetworkCheck(this).noInternetDialog();
    }

    public void LoadData() {
        dialog.show();
        myRef.orderByChild("creationTimeStamp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    count = 0;
                    addressList.clear();
                    for (DataSnapshot child: dataSnapshot.getChildren()){
                        final Address address = child.getValue(Address.class);
                        if (address.getUserId().equals(uid)){
                            addressList.add(address);
                        }
                    }
                    count = addressList.size();
                    if(count>0)
                        tv_count.setText(count + " SAVED ADDRESSES");
                    else
                        tv_count.setText("NO SAVED ADDRESSES");
                    Collections.reverse(addressList);
                    myAddressAdapter.notifyDataSetChanged();
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toasty.error(MyAddressActivity.this, databaseError.getMessage(), Toasty.LENGTH_SHORT, true).show();
                dialog.dismiss();
            }
        });
    }

    private void Init() {
        addNewAddress = findViewById(R.id.activity_myaddress_add_new_address);
        rv_myaddress = findViewById(R.id.activity_myaddress_rv);
        tv_count = findViewById(R.id.activity_myaddress_count_tv);
        addressList = new ArrayList<>();
        myAddressAdapter = new MyAddressAdapter(this, addressList);
        mAuth = FirebaseAuth.getInstance();
        //myRef = FirebaseDatabase.getInstance().getReference("Users");
        myRef = FirebaseDatabase.getInstance().getReference("UserAddress");
        uid = mAuth.getUid();
        dialog = new SpotsDialog.Builder().setContext(MyAddressActivity.this).setCancelable(false).build();
        iv_back = findViewById(R.id.activity_myaddress_toolbar_icon);

        //Setting Recyclerview
        rv_myaddress.setHasFixedSize(true);
        rv_myaddress.setAdapter(myAddressAdapter);
        rv_myaddress.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE){
            if(resultCode == RESULT_OK) {
                String key = data.getStringExtra("key");
                if(key.equals("success")){
                    SweetAlertDialog s =  new SweetAlertDialog(MyAddressActivity.this, SweetAlertDialog.SUCCESS_TYPE);
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

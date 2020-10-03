package com.codewithshubh.servozone.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.codewithshubh.servozone.Adapter.ServiceBookAdapter;
import com.codewithshubh.servozone.Model.ServiceCategory;
import com.codewithshubh.servozone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import cn.pedant.SweetAlert.SweetAlertDialog;
import dmax.dialog.SpotsDialog;
import static com.codewithshubh.servozone.Constant.Constants.SERVICE_ID;
import static com.codewithshubh.servozone.Constant.Constants.SERVICE_NAME;

public class ServiceActivity extends AppCompatActivity {

    private TextView toolbar_title;
    private String serviceNode;
    public String serviceId, serviceName;
    private AlertDialog dialog;
    private RecyclerView rv_service_booking;
    private DatabaseReference mydata;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ServiceBookAdapter serviceBookAdapter;
    private List<ServiceCategory> serviceCategories;
    private ImageView iv_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        //Initialize
        Init();

        //Load data
        GetFirebaseData();

        //swipe to refresh
        swipeRefreshLayout.setColorSchemeResources(R.color.azureBlue);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetFirebaseData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void Init() {
        //get service name value from previous activity
        serviceName = getIntent().getStringExtra(SERVICE_NAME);
        serviceId = getIntent().getStringExtra(SERVICE_ID);

        //setting toolbar title
        toolbar_title = findViewById(R.id.activity_service_toolbar_tv);
        toolbar_title.setText(serviceName);
        swipeRefreshLayout = findViewById(R.id.activity_service_swipe);
        iv_back = findViewById(R.id.activity_service_toolbar_icon);

        //Init
        mydata = FirebaseDatabase.getInstance().getReference("ServiceCategory");
        dialog = new SpotsDialog.Builder().setContext(ServiceActivity.this).setCancelable(false).build();

        //View
        serviceCategories = new ArrayList<>();
        serviceBookAdapter = new ServiceBookAdapter(this, serviceCategories);

        rv_service_booking = findViewById(R.id.activity_service_rv);
        rv_service_booking.setHasFixedSize(true);

        rv_service_booking.setAdapter(serviceBookAdapter);
        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        rv_service_booking.setLayoutManager(gridLayoutManager);
    }

    private void GetFirebaseData() {
        dialog.show();
        mydata.orderByChild("serviceId").equalTo(serviceId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    serviceCategories.clear();
                    for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                        ServiceCategory serviceCategory = groupSnapshot.getValue(ServiceCategory.class);
                        if (serviceCategory.isActiveStatus())
                            serviceCategories.add(serviceCategory);
                    }
                    serviceBookAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                String key = data.getStringExtra("key");
                if(key.equals("success")){
                    SweetAlertDialog s =  new SweetAlertDialog(ServiceActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                    s.setCanceledOnTouchOutside(false);
                    s.setCancelable(false);
                    s.setTitleText("Booking Successful!");
                    s.setContentText(getString(R.string.booking_successful_msg));
                    s.setConfirmText("OK");
                    s.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                            finish();
                        }
                    }).show();
                }
            }
        }
    }
}

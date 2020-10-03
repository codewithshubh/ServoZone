package com.codewithshubh.servozone.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.codewithshubh.servozone.Model.Address;
import com.codewithshubh.servozone.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import java.util.HashMap;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import static com.codewithshubh.servozone.Constant.Constants.ADDRESS_ID;

public class EditAddressActivity extends AppCompatActivity {
    private EditText et_pin, et_house_no, et_area, et_city, et_state, et_landmark, et_contact_name, et_contact_no;
    private Button btn_save;
    private String pinCode, houseNo, area, city, state, landmark, contactName, contactNo, uid;
    private FirebaseAuth mAuth;
    private AlertDialog dialog;
    private ImageView iv_back;
    private String addressId;
    private CountryCodePicker ccp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);

        //Initialize views
        Init();

        GetData();

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnSaveAddressClick();
            }
        });
    }

    private void GetData() {
        dialog.show();
        final DatabaseReference myRefEditAddress = FirebaseDatabase.getInstance().getReference("UserAddress");
        Log.d("EditAddressActivity", "GetData: "+addressId);
        myRefEditAddress.orderByChild("id").equalTo(addressId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot child: dataSnapshot.getChildren()){
                        final Address address = child.getValue(Address.class);
                        et_pin.setText(address.getPincode());
                        et_house_no.setText(address.getHouseNo());
                        et_area.setText(address.getArea());
                        et_city.setText(address.getCity());
                        et_state.setText(address.getState());
                        et_landmark.setText(address.getLandmark());
                        et_contact_name.setText(address.getName());
                        ccp.setFullNumber(address.getContact());
                        break;
                    }
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();
                Toasty.error(EditAddressActivity.this, databaseError.getMessage(), Toasty.LENGTH_SHORT, true).show();
            }
        });
    }

    private void OnSaveAddressClick() {
        hideKeyboard(EditAddressActivity.this);
        pinCode = et_pin.getText().toString();
        houseNo = et_house_no.getText().toString();
        area = et_area.getText().toString();
        city = et_city.getText().toString();
        state = et_state.getText().toString();
        landmark = et_landmark.getText().toString();
        contactName = et_contact_name.getText().toString();
        contactNo = ccp.getFullNumberWithPlus();

        if(!validatePincode(pinCode))
            return;
        if (!validateHouseNo(houseNo))
            return;
        if (!validateArea(area))
            return;
        if (!validateCity(city))
            return;
        if (!validateState(state))
            return;
        if (!validateContactname(contactName))
            return;
        if (!validateContactNo())
            return;
        dialog.show();
        final DatabaseReference myRefSaveAddress = FirebaseDatabase.getInstance().getReference("UserAddress").child(addressId);
        HashMap <String, Object> update = new HashMap<>();
        update.put("pincode", pinCode);
        update.put("houseNo", houseNo);
        update.put("area", area);
        update.put("city", city);
        update.put("state", state);
        update.put("landmark", (landmark.equals("")?"":landmark));
        update.put("name", contactName);
        update.put("contact", contactNo);
        myRefSaveAddress.updateChildren(update).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dialog.dismiss();
                Toasty.success(EditAddressActivity.this, "Address saved successfully", Toasty.LENGTH_SHORT, true).show();
                Intent intent = new Intent();
                intent.putExtra("key", "success");
                setResult(RESULT_OK, intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toasty.error(EditAddressActivity.this, "Something went wrong...Try again!", Toasty.LENGTH_SHORT, true).show();

            }
        });
    }

    private void Init() {
        et_pin = findViewById(R.id.activity_editaddress_pincode);
        et_house_no = findViewById(R.id.activity_editaddress_houseno);
        et_area = findViewById(R.id.activity_editaddress_area);
        et_city = findViewById(R.id.activity_editaddress_city);
        et_state = findViewById(R.id.activity_editaddress_state);
        et_landmark = findViewById(R.id.activity_editaddress_landmark);
        et_contact_name = findViewById(R.id.activity_editaddress_contactname);
        ccp = findViewById(R.id.ccp_edit_address);
        et_contact_no = findViewById(R.id.activity_editaddress_contactno);
        ccp.registerCarrierNumberEditText(et_contact_no);
        btn_save = findViewById(R.id.activity_editaddress_btn_save);
        iv_back = findViewById(R.id.activity_editaddress_toolbar_icon);
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();
        addressId = getIntent().getStringExtra(ADDRESS_ID);
    }

    //validate Pincode
    private boolean validatePincode(String pincode){
        if(pincode.isEmpty()){
            et_pin.setError("This field cannot be empty");
            requestFocus(et_pin);
            return false;
        }
        else if(pincode.isEmpty() || pincode.contains(" ")){
            et_pin.setError("Enter a valid pincode");
            requestFocus(et_pin);
            return false;
        }
        else{
            et_pin.setError(null);
        }
        return true;
    }

    //validate House No
    private boolean validateHouseNo(String houseno){
        if(houseno.isEmpty()){
            et_house_no.setError("This field cannot be empty");
            requestFocus(et_house_no);
            return false;
        }
        else{
            et_house_no.setError(null);
        }
        return true;
    }

    //validate Area
    private boolean validateArea(String area){
        if(area.isEmpty()){
            et_area.setError("This field cannot be empty");
            requestFocus(et_area);
            return false;
        }
        else{
            et_area.setError(null);
        }
        return true;
    }

    //validate City
    private boolean validateCity(String city){
        if(city.isEmpty()){
            et_city.setError("This field cannot be empty");
            requestFocus(et_city);
            return false;
        }
        else{
            et_city.setError(null);
        }
        return true;
    }

    //validate State
    private boolean validateState(String state){
        if(state.isEmpty()){
            et_state.setError("This field cannot be empty");
            requestFocus(et_state);
            return false;
        }
        else{
            et_state.setError(null);
        }
        return true;
    }

    //validate Landmark
    private boolean validateLandmark(String landmark){
        if(landmark.isEmpty()){
            et_landmark.setError("This field cannot be empty");
            requestFocus(et_landmark);
            return false;
        }
        else{
            et_landmark.setError(null);
        }
        return true;
    }

    //validate Name
    private boolean validateContactname(String name){
        if(name.isEmpty()){
            et_contact_name.setError("This field cannot be empty");
            requestFocus(et_contact_name);
            return false;
        }
        else{
            et_contact_name.setError(null);
        }
        return true;
    }

    //validate Phone
    private boolean validateContactNo(){
        if(!ccp.isValidFullNumber()){
            et_contact_no.setError("Enter a valid number");
            requestFocus(et_contact_no);
            return false;
        }
        else{
            et_contact_no.setError(null);
        }
        return true;
    }

    //Focus on edit text
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public static void hideKeyboard(Activity activity){
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if(view == null)
            view = new View(activity);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}

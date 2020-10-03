package com.codewithshubh.servozone.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.codewithshubh.servozone.Model.Address;
import com.codewithshubh.servozone.R;
import com.codewithshubh.servozone.Utils.CheckIfLoggedIn;
import com.codewithshubh.servozone.Utils.NetworkCheck;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.hbb20.CountryCodePicker;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class AddNewAddressActivity extends AppCompatActivity {
    private EditText et_pin, et_house_no, et_area, et_city, et_state, et_landmark, et_contact_name, et_contact_no;
    private Button btn_save;
    private ImageView iv_back;
    private String pinCode, houseNo, area, city, state, landmark, contactName, contactNo, uid;
    private FirebaseAuth mAuth;
    private AlertDialog dialog;
    private CountryCodePicker ccp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_address);

        //Initialize views
        Init();

        //Listening on save button click
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnSaveAddressClick();
            }
        });

        //listening on back button click
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        new NetworkCheck(this).noInternetDialog();
    }

    //This method will add a new address under UserAddress node
    private void OnSaveAddressClick() {
        hideKeyboard(AddNewAddressActivity.this);
        pinCode = et_pin.getText().toString();
        houseNo = et_house_no.getText().toString();
        area = et_area.getText().toString();
        city = et_city.getText().toString();
        state = et_state.getText().toString();
        landmark = et_landmark.getText().toString();
        contactName = et_contact_name.getText().toString();
        contactNo = ccp.getFullNumberWithPlus();

        if(!validatePincode(pinCode))   //validating pinCode/zipCode (cannot be empty and greater than 5 digits)
            return;
        if (!validateHouseNo(houseNo))  //validating house number (cannot be empty)
            return;
        if (!validateArea(area))    //validating area (cannot be empty)
            return;
        if (!validateCity(city))    //validating city (cannot be empty)
            return;
        if (!validateState(state))  //validating state (cannot be empty)
            return;
        if (!validateContactname(contactName))  //validating contact number (cannot be empty)
            return;
        if (!validateContactNo())   //validating contact number (if a valid phone number)
            return;
        dialog.show();  // if all the validation pass, then the call will continue from this line

        //getting the firebase database reference which refers to UserAddress node
        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("UserAddress");
        Address address;    //Address model instance/object
        String address_id = UUID.randomUUID().toString();   //generating a unique string ID for address ID
        if(et_landmark.getText().toString().isEmpty())
            address = new Address(address_id, pinCode, houseNo, area, city, state, "", contactName, contactNo, uid);    //adding values to address instance if landmark value is not provided
        else
            address = new Address(address_id, pinCode, houseNo, area, city, state, landmark, contactName, contactNo, uid);  //adding values to address instance if landmark value is provided

        //storing the address object data with parent node of address id under UserAddress in firebase realtime db
        myRef.child(address_id).setValue(address).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) { //if storing data to firebase successful, this callback method will be called
                dialog.dismiss();
                Intent intent = new Intent();
                intent.putExtra("key", "success");
                setResult(RESULT_OK, intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {   //if any error occurred while uploading data to firebase, then this callback method will be called
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toasty.error(AddNewAddressActivity.this, e.getMessage(), Toasty.LENGTH_SHORT, true).show();
            }
        });
        Map timestamp = new HashMap();  //creating a Hashmap to store timestamp
        timestamp.put("creationTimeStamp", ServerValue.TIMESTAMP);
        myRef.child(address_id).updateChildren(timestamp);  //adding timestamp of creation of the address.

    }

    private void Init() {
        et_pin = findViewById(R.id.activity_addnewaddress_pincode);
        et_house_no = findViewById(R.id.activity_addnewaddress_houseno);
        et_area = findViewById(R.id.activity_addnewaddress_area);
        et_city = findViewById(R.id.activity_addnewaddress_city);
        et_state = findViewById(R.id.activity_addnewaddress_state);
        et_landmark = findViewById(R.id.activity_addnewaddress_landmark);
        et_contact_name = findViewById(R.id.activity_addnewaddress_contactname);
        ccp = findViewById(R.id.ccp_add_address);
        et_contact_no = findViewById(R.id.activity_addnewaddress_contactno);
        ccp.registerCarrierNumberEditText(et_contact_no);
        btn_save = findViewById(R.id.activity_addnewaddress_btn_save);
        iv_back = findViewById(R.id.activity_addnewaddress_toolbar_icon);
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();
    }

    //validate Pincode/zipcode
    private boolean validatePincode(String pincode){
        if(pincode.isEmpty()){
            et_pin.setError("This field cannot be empty");
            requestFocus(et_pin);
            return false;
        }
        else if(pincode.length()<5 || pincode.contains(" ")){
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

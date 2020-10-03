package com.codewithshubh.servozone.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.codewithshubh.servozone.R;
import com.codewithshubh.servozone.Utils.CheckIfLoggedIn;
import com.codewithshubh.servozone.Utils.NetworkCheck;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import static com.codewithshubh.servozone.Constant.Constants.USER_NUMBER;

public class CreateProfileActivity extends AppCompatActivity {
    private ImageView back;
    private EditText et_name, et_phone, et_mail;
    private Button btn_save;
    private String number;
    private AlertDialog dialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        //Initializing
        Init();

        //on back click
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnBackClick();
            }
        });

        //On save profile button click
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateProfile();
            }
        });

        new NetworkCheck(this).noInternetDialog();
    }

    private void Init() {
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        back = findViewById(R.id.activity_create_profile_toolbar_icon);
        et_name = findViewById(R.id.activity_create_profile_et_name);
        et_phone = findViewById(R.id.activity_create_profile_et_phone);
        et_mail = findViewById(R.id.activity_create_profile_et_email);
        btn_save = findViewById(R.id.activity_create_profile_btn_save);
        mAuth = FirebaseAuth.getInstance();
        number = getIntent().getStringExtra(USER_NUMBER);
        et_phone.setText(number);
    }

    private void CreateProfile() {
        hideKeyboard(CreateProfileActivity.this);
        if (!validateName())
            return;
        if (!validateEmail())
            return;
        dialog.show();
        String uid = mAuth.getUid();
        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users");
        Map<String, Object> user = new HashMap<>();
        user.put("name", et_name.getText().toString());
        user.put("phone", number);
        user.put("email", et_mail.getText().toString());
        myRef.child(uid).updateChildren(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.putExtra("key", "success");
                setResult(RESULT_OK, intent);
                Toasty.success(CreateProfileActivity.this, "Profile saved successfully", Toasty.LENGTH_SHORT, true).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toasty.error(CreateProfileActivity.this, "Something went wrong. Try again.", Toasty.LENGTH_SHORT, true).show();
            }
        });

    }

    private void OnBackClick() {
        Intent intent = new Intent();
        intent.putExtra("key", "back");
        setResult(RESULT_OK, intent);
        Toasty.success(CreateProfileActivity.this, "SignUp Successful", Toasty.LENGTH_SHORT, true).show();
        finish();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("key", "back");
        setResult(RESULT_OK, intent);
        Toasty.success(CreateProfileActivity.this, "SignUp Successful", Toasty.LENGTH_SHORT, true).show();
        finish();
    }

    public static void hideKeyboard(Activity activity){
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if(view == null)
            view = new View(activity);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //Validate Name
    private boolean validateName(){
        if(et_name.getText().toString().isEmpty()){
            et_name.setError("This field cannot be empty");
            requestFocus(et_name);
            return false;
        }
        else if(et_name.getText().toString().length()<3){
            et_name.setError("Enter a valid name");
            requestFocus(et_name);
            return false;
        }
        else{
            et_name.setError(null);
        }
        return true;
    }

    //Validating Email
    private boolean validateEmail() {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if(et_mail.getText().toString().isEmpty()){
            et_mail.setError("This field cannot be empty");
            requestFocus(et_mail);
            return false;
        }
        else if(!et_mail.getText().toString().matches(emailPattern)){
            et_mail.setError("Enter a valid email address");
            requestFocus(et_mail);
            return false;
        }
        else{
            et_mail.setError(null);
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

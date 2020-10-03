package com.codewithshubh.servozone.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.codewithshubh.servozone.Model.User;
import com.codewithshubh.servozone.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class EditProfileActivity extends AppCompatActivity {
    private ImageView back;
    private EditText et_name, et_phone, et_mail;
    private Button btn_save;
    private AlertDialog dialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //Initializing variables
        Init();

        //fetching and setting firebase data to views
        SetData();

        //on back click
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnBackClick();
            }
        });

        //on save click
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveProfile();
            }
        });
    }

    private void OnBackClick() {
        finish();
    }

    private void SaveProfile() {
        hideKeyboard(EditProfileActivity.this);
        if (!validateName())
            return;
        if (!validateEmail())
            return;
        dialog.show();
        String uid = mAuth.getUid();
        final DatabaseReference myRefSaveProfile = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        Map<String, Object> user = new HashMap<>();
        user.put("name", et_name.getText().toString());
        user.put("email", et_mail.getText().toString());
        myRefSaveProfile.updateChildren(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dialog.dismiss();
                Toasty.success(EditProfileActivity.this, "Profile saved successfully", Toasty.LENGTH_SHORT, true).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toasty.error(EditProfileActivity.this, "Something went wrong. Try again.", Toasty.LENGTH_SHORT, true).show();
            }
        });
    }

    private void Init() {
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        back = findViewById(R.id.activity_edit_profile_toolbar_icon);
        et_name = findViewById(R.id.activity_edit_profile_et_name);
        et_phone = findViewById(R.id.activity_edit_profile_et_phone);
        et_mail = findViewById(R.id.activity_edit_profile_et_email);
        btn_save = findViewById(R.id.activity_edit_profile_btn_save);
        mAuth = FirebaseAuth.getInstance();
    }

    private void SetData() {
        dialog.show();
        String uid = mAuth.getUid();
        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                et_phone.setText(user.getPhone());
                et_name.setText(user.getName());
                et_mail.setText(user.getEmail());
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toasty.error(EditProfileActivity.this, databaseError.getMessage(), Toasty.LENGTH_SHORT, true).show();
                dialog.dismiss();
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
}

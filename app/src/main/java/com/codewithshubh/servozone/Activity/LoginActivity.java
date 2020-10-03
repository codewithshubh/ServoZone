package com.codewithshubh.servozone.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.codewithshubh.servozone.Model.User;
import com.codewithshubh.servozone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.hbb20.CountryCodePicker;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import es.dmoral.toasty.Toasty;
import in.aabhasjindal.otptextview.OtpTextView;
import static com.codewithshubh.servozone.Constant.Constants.USER_NUMBER;

public class LoginActivity extends AppCompatActivity {

    private EditText et_number;
    private TextView tv_number, tv_countdown, tv_edit, tv_resend;
    private OtpTextView otp;
    private Button btn_send_code, btn_verify;
    private LinearLayout lin_resend_msg, lin_fetch_msg, lin_countdown_msg;
    private ScrollView sv_send_code, sv_verify;
    private AVLoadingIndicatorView avLoadingIndicatorView;
    private FirebaseAuth mAuth;
    private CountDownTimer countDownTimer;
    private ImageView iv_back;
    private String mVarificationId;
    private CountryCodePicker ccp;
    private String userNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialize variables
        Init();

        //On back icon click
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnBackClick();
            }
        });

        //On Send code button click
        btn_send_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnSendCodeClick();
            }
        });

        //On Verify button click
        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnVerifyCodeClick();
            }
        });

        //On Edit click
        tv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnEditNumberClick();
            }
        });

        //On re-send code click
        tv_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnResendCick();
            }
        });
    }

    private void OnResendCick() {
        hideKeyboard(LoginActivity.this);
        avLoadingIndicatorView.setVisibility(View.VISIBLE);
        btn_send_code.setVisibility(View.VISIBLE);
        btn_verify.setVisibility(View.INVISIBLE);
        btn_send_code.setText("");
        btn_send_code.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                userNumber = ccp.getFullNumberWithPlus();
                tv_number.setText(userNumber);
                btn_verify.setBackgroundResource(R.drawable.btn_gray);
                btn_verify.setEnabled(false);
                btn_send_code.setVisibility(View.INVISIBLE);
                btn_verify.setVisibility(View.VISIBLE);
                lin_countdown_msg.setVisibility(View.VISIBLE);
                lin_fetch_msg.setVisibility(View.VISIBLE);
                lin_resend_msg.setVisibility(View.INVISIBLE);
                countDownTimer.start();
                avLoadingIndicatorView.setVisibility(View.INVISIBLE);
                Toasty.success(LoginActivity.this, "OTP re-sent", Toasty.LENGTH_SHORT, true).show();
                SendVerificationCode(userNumber);

            }
        }, 1000);

    }

    private void OnEditNumberClick() {
        finish();
        startActivity(getIntent());
    }

    private void OnVerifyCodeClick() {
        hideKeyboard(LoginActivity.this);
        btn_verify.setText("");
        btn_verify.setEnabled(false);
        avLoadingIndicatorView.setVisibility(View.VISIBLE);
        final String code = otp.getOTP();
        if(code.isEmpty() || code.length()<6){
            otp.showError();
            btn_verify.setText("VERIFY THE CODE");
            btn_verify.setEnabled(true);
            avLoadingIndicatorView.setVisibility(View.INVISIBLE);
            Toasty.error(LoginActivity.this, "OTP is invalid", Toasty.LENGTH_SHORT, true).show();
        }
        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    VerifyVerificationCode(code);
                }
            }, 1000);
        }
    }

    private void OnSendCodeClick() {
        hideKeyboard(LoginActivity.this);
        avLoadingIndicatorView.setVisibility(View.VISIBLE);
        btn_send_code.setText("");
        btn_send_code.setEnabled(false);
        if (!ccp.isValidFullNumber()){  //validating phone number
            avLoadingIndicatorView.setVisibility(View.INVISIBLE);
            btn_send_code.setText("GET THE CODE");
            btn_send_code.setEnabled(true);
            et_number.setError("Please enter a valid number");
        }
        else{
            et_number.setError(null);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //String mobile = et_code.getText().toString()+et_number.getText().toString();
                    userNumber = ccp.getFullNumberWithPlus();
                    tv_number.setText(userNumber);
                    sv_send_code.setVisibility(View.INVISIBLE);
                    btn_send_code.setVisibility(View.INVISIBLE);
                    sv_verify.setVisibility(View.VISIBLE);
                    btn_verify.setVisibility(View.VISIBLE);
                    btn_verify.setBackgroundResource(R.drawable.btn_gray);
                    btn_verify.setEnabled(false);
                    lin_countdown_msg.setVisibility(View.VISIBLE);
                    lin_fetch_msg.setVisibility(View.VISIBLE);
                    countDownTimer.start();
                    avLoadingIndicatorView.setVisibility(View.INVISIBLE);
                    Toasty.success(LoginActivity.this, "OTP sent", Toasty.LENGTH_SHORT, true).show();
                    SendVerificationCode(userNumber);

                }
            }, 1000);
        }
    }

    private void SendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            //Getting the code sent by sms
            final String code = phoneAuthCredential.getSmsCode();
            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if(code != null){
                tv_edit.setClickable(false);
                tv_edit.setTextColor(getResources().getColor(R.color.grey));
                otp.setOTP(code);
                countDownTimer.cancel();
                lin_countdown_msg.setVisibility(View.INVISIBLE);
                lin_fetch_msg.setVisibility(View.INVISIBLE);
                lin_resend_msg.setVisibility(View.VISIBLE);
                btn_verify.setText("");
                btn_verify.setEnabled(false);
                btn_verify.setBackgroundResource(R.drawable.btn_blue);
                avLoadingIndicatorView.setVisibility(View.VISIBLE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //verify the code
                        VerifyVerificationCode(code);
                    }
                }, 1000);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            btn_verify.setText("VERIFY THE CODE");
            btn_verify.setEnabled(true);
            avLoadingIndicatorView.setVisibility(View.INVISIBLE);
            tv_edit.setClickable(true);
            Toasty.error(LoginActivity.this, e.getMessage(), Toasty.LENGTH_LONG, true).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            //storing the verification id that is sent to the user
            mVarificationId = s;
        }
    };

    private void VerifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVarificationId, code);

        //signing the user
        SignInWithPhoneAuthCredential(credential, userNumber);
    }

    private void SignInWithPhoneAuthCredential(PhoneAuthCredential credential, final String number) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    final FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    FirebaseUser user = task.getResult().getUser();
                    long creationTimestamp = user.getMetadata().getCreationTimestamp();
                    //long lastSignInTimestamp = user.getMetadata().getLastSignInTimestamp();
                    if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                        //do create new user
                        otp.showSuccess();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                SendUserData(number, "John Doe", "johndoe@example.com", "default", creationTimestamp);
                                GetDeviceToken();
                            }
                        },1000);

                    }
                    else {
                        otp.showSuccess();
                        String uid = FirebaseAuth.getInstance().getUid();
                        //checking if user is active
                        DatabaseReference myRefIsActive = FirebaseDatabase.getInstance().getReference("Users");
                        myRefIsActive.orderByChild("uid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    for (DataSnapshot child: dataSnapshot.getChildren()){
                                        User user = child.getValue(User.class);
                                        if (user.isActiveStatus()){
                                            //user is exists, just do login
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Intent intent = new Intent();
                                                    intent.putExtra("key", "success");
                                                    setResult(RESULT_OK, intent);
                                                    Toasty.success(LoginActivity.this, "Login Successful",
                                                            Toasty.LENGTH_SHORT, true).show();
                                                    GetDeviceToken();
                                                    finish();

                                                }
                                            },1000);
                                        }
                                        else {
                                            mAuth.signOut();
                                            Toasty.warning(LoginActivity.this, getResources().getString(R.string.inactive_user_msg),
                                                    Toasty.LENGTH_LONG, true).show();
                                            finish();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
                else {
                    //verification unsuccessful
                    tv_edit.setClickable(true);
                    tv_edit.setTextColor(getResources().getColor(R.color.azureBlue));
                    otp.showError();
                    btn_verify.setText("VERIFY THE CODE");
                    btn_verify.setEnabled(true);
                    avLoadingIndicatorView.setVisibility(View.INVISIBLE);
                    String msg = "Something is wrong, we will fix it soon...";
                    if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                        msg = "OTP is invalid";
                    }
                    Toasty.error(LoginActivity.this, msg, Toasty.LENGTH_LONG, true).show();
                }
            }
        });
    }

    private void GetDeviceToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()){
                            String deviceToken = task.getResult().getToken();
                            SaveToken(deviceToken);
                        }
                    }
                });
    }

    private void SaveToken(String deviceToken) {
        String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference myRefToken = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        Map token = new HashMap();
        token.put("deviceToken", deviceToken);
        myRefToken.updateChildren(token);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                String key = data.getStringExtra("key");
                if(key.equals("success") || key.equals("back")){
                    Intent intent = new Intent();
                    intent.putExtra("key", "success");
                    setResult(RESULT_OK, intent);
                    Toasty.success(LoginActivity.this, "SignUp Successful", Toasty.LENGTH_SHORT, true).show();
                    finish();
                }
            }
        }
    }

    private void SendUserData(final String number, String name, String email, String imageUrl, long creationTimestamp) {
        String uid = mAuth.getUid();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("Users");
        User user = new User(name, number, email, imageUrl, uid, true, "user", creationTimestamp, "NA");
        myRef.child(uid).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent i = new Intent(LoginActivity.this, CreateProfileActivity.class);
                i.putExtra(USER_NUMBER, number);
                startActivityForResult(i,1);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toasty.error(LoginActivity.this, e.getMessage(), Toasty.LENGTH_SHORT, true).show();
            }
        });
    }

    private void OnBackClick() {
        finish();
        //startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }

    private void Init() {
        iv_back = findViewById(R.id.activity_login_back);
        ccp = findViewById(R.id.ccp_login);
        et_number = findViewById(R.id.activity_login_mobile_no);
        ccp.registerCarrierNumberEditText(et_number);
        otp = findViewById(R.id.activity_login_otp);
        btn_send_code = findViewById(R.id.activity_login_btn_send);
        btn_verify = findViewById(R.id.activity_login_btn_verify);
        avLoadingIndicatorView = findViewById(R.id.activity_login_avi);
        tv_number = findViewById(R.id.activity_login_tv_number);
        tv_edit = findViewById(R.id.activity_login_btn_edit);
        tv_resend = findViewById(R.id.activity_login_btn_resend);
        sv_send_code = findViewById(R.id.sv1);
        sv_verify = findViewById(R.id.sv2);
        tv_countdown = findViewById(R.id.activity_login_tv_count_down);

        lin_countdown_msg = findViewById(R.id.activity_login_lin_countdown_msg);
        lin_fetch_msg = findViewById(R.id.activity_login_lin_fetching_msg);
        lin_resend_msg = findViewById(R.id.activity_login_lin_resend_msg);

        sv_send_code.setVisibility(View.VISIBLE);
        sv_verify.setVisibility(View.INVISIBLE);
        avLoadingIndicatorView.setVisibility(View.INVISIBLE);

        btn_send_code.setVisibility(View.VISIBLE);
        btn_verify.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();

        //setting 15 second counter to wait for otp
        countDownTimer = new CountDownTimer(15000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int sec = (int)(millisUntilFinished/1000);
                if(sec<10)
                    tv_countdown.setText("00:0"+sec);
                else
                    tv_countdown.setText("00:"+sec);
            }

            @Override
            public void onFinish() {
                btn_verify.setBackgroundResource(R.drawable.btn_blue);
                btn_verify.setEnabled(true);
                lin_countdown_msg.setVisibility(View.INVISIBLE);
                lin_fetch_msg.setVisibility(View.INVISIBLE);
                lin_resend_msg.setVisibility(View.VISIBLE);
            }
        };
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public static void hideKeyboard(Activity activity){
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if(view == null)
            view = new View(activity);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}

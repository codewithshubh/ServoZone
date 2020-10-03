package com.codewithshubh.servozone.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import com.codewithshubh.servozone.R;
import com.google.firebase.messaging.FirebaseMessaging;

import static com.codewithshubh.servozone.Constant.Constants.ALL_USERS;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //subscribe to global topic for notification
        FirebaseMessaging.getInstance().subscribeToTopic(ALL_USERS);

        //removing toolbar and status bar to show splash screen in full screen mode
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // It will show splash screen for 1.5 seconds and will be closed after opening our MainActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, 1500);
    }
}

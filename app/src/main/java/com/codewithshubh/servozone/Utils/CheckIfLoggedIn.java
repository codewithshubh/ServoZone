package com.codewithshubh.servozone.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.codewithshubh.servozone.Activity.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CheckIfLoggedIn {
    Context context;

    public CheckIfLoggedIn(Context context) {
        this.context = context;
    }

    public void CheckForUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            context.startActivity(new Intent(context, LoginActivity.class));
            ((Activity)context).finish();
        }
    }
}

package com.codewithshubh.servozone.Interface;

import com.codewithshubh.servozone.BuildConfig;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface NotificationAPI {
    @Headers({
            "Authorization: key="+ BuildConfig.FCM_SERVER_KEY ,
            "Content-Type: application/json"
    })
    @POST("fcm/send")
    Call<JsonObject> sendNotification(@Body JsonObject payload);
}

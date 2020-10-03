package com.codewithshubh.servozone.API;

import com.codewithshubh.servozone.BuildConfig;
import com.codewithshubh.servozone.Interface.NotificationAPI;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotificationClientAPI {
    public static NotificationAPI getAPIService(){
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.FCM_BASE_URL)
                .client(provideClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NotificationAPI.class);
    }

    private static OkHttpClient provideClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder().addInterceptor(interceptor).addInterceptor(chain -> {
            Request request = chain.request();
            return chain.proceed(request);
        }).build();
    }
}

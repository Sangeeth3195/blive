package com.blive.service;

import com.blive.constant.Constants_api;
import com.blive.session.SessionLogin;
import com.blive.session.SessionUser;
import com.facebook.AccessToken;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sans on 25-Nov-19.
 **/
public class ServiceGenerator {
    private static Retrofit retrofit = null;
    private static Retrofit retrofit2 = null;

    public static Retrofit getClient() {
        if(SessionLogin.getLoginSession()){

            if (retrofit2 == null) {
                OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
                if(SessionLogin.getLoginSession()){
                    okHttpClient.addInterceptor(chain -> {
                        Request request = chain.request();

                        Request.Builder newRequest = request.newBuilder().header("Authorization", "Bearer " + "8f9839c2883e717ef40978c751fdfa030debab3c");
//                        newRequest.header("Content-Type", "application/json");
                        return chain.proceed(newRequest.build());
                    });
                }
                okHttpClient.readTimeout(15, TimeUnit.SECONDS)
                        .connectTimeout(15, TimeUnit.SECONDS);
                retrofit2 = new Retrofit.Builder()
                        .baseUrl("https://api-ssl.bitly.com/v4/")
                        .client(okHttpClient.build())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return retrofit2;
        }else {
            if (retrofit == null) {
                OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
                okHttpClient.readTimeout(15, TimeUnit.SECONDS)
                        .connectTimeout(15, TimeUnit.SECONDS);
                retrofit = new Retrofit.Builder()
                        .baseUrl("https://api-ssl.bitly.com/v4/")
                        .client(okHttpClient.build())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
        }
        return retrofit;
    }
}
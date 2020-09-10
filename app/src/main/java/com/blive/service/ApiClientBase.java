package com.blive.service;

import com.blive.constant.Constants_api;
import com.blive.session.SessionLogin;
import com.blive.session.SessionUser;

import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by sans on 04-04-2019.
 **/

public class ApiClientBase {

    private static Retrofit retrofit = null;
    private static Retrofit retrofit2 = null;

    public static Retrofit getClient() {
        if(SessionLogin.getLoginSession()){

                if (retrofit2 == null) {
                OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
                if(SessionLogin.getLoginSession()){
                    okHttpClient.addInterceptor(chain -> {
                        Request request = chain.request();
                        Request.Builder newRequest = request.newBuilder()
                                .addHeader("Connection","close")
                                .header("Authorization", "Bearer " + SessionUser.getUser().getActivation_code());
                        return chain.proceed(newRequest.build());
                    });
                }
                okHttpClient.readTimeout(15, TimeUnit.SECONDS)
                        .connectTimeout(15, TimeUnit.SECONDS);
                retrofit2 = new Retrofit.Builder()
                        .baseUrl(Constants_api.domain)
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
                        .baseUrl(Constants_api.domain)
                        .client(okHttpClient.build())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
        }
        return retrofit;
    }

}

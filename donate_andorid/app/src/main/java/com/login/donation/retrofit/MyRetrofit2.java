package com.login.donation.retrofit;

import android.util.Log;

import com.login.donation.info.info;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyRetrofit2 {
    public static final String URL  = info.servcer_ip;
    static Retrofit mRetrofit;

    public static Retrofit getRetrofit2(){



        if(mRetrofit == null){

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.connectTimeout(1, TimeUnit.MINUTES);
            httpClient.readTimeout(30, TimeUnit.SECONDS);
            httpClient.writeTimeout(15, TimeUnit.SECONDS);
            httpClient.addInterceptor(logging);

            mRetrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return mRetrofit;
    }
}

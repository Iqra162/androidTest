package com.example.bilal.movieslist.rest;

import android.content.Context;


import com.google.gson.GsonBuilder;
import com.example.bilal.movieslist.GlobalClass;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {

    public static String BASE_URL = GlobalClass.baseUrl;
    private static Retrofit retrofit = null;
    public static Retrofit getClient(Context context) {

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()

                    .baseUrl(BASE_URL)


                    .client(getOkHttpClient())
                    /*.addConverterFactory(new ToStringConverterFactory())*/
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().serializeNulls().setLenient().create()))
                    .build();
        }
        return retrofit;
    }

    private static OkHttpClient getOkHttpClient() {
        try {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.MINUTES)
                    .readTimeout(5, TimeUnit.MINUTES)
                    .addInterceptor(interceptor)
                    .build();
            return client;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

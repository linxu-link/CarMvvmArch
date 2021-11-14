package com.mvvm.hmi.full.model.remote;

import android.util.Log;

import com.mvvm.hmi.full.api.Webservice;
import com.mvvm.hmi.full.factory.LiveDataCallAdapterFactory;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpService {

    private static final HttpService HTTP_SERVICE = new HttpService();
    private final Webservice mWebservice;

    public HttpService() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(message -> Log.i("TAG", "log: " + message));
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.tianapi.com/caipu/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(LiveDataCallAdapterFactory.create())
                .client(client)
                .build();
        mWebservice = retrofit.create(Webservice.class);
    }

    public static HttpService get() {
        return HTTP_SERVICE;
    }

    public Webservice getWebservice() {
        return mWebservice;
    }
}

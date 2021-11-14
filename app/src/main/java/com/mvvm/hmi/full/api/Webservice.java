package com.mvvm.hmi.full.api;

import androidx.lifecycle.LiveData;

import com.mvvm.hmi.full.bean.biz.BaseEntity;
import com.mvvm.hmi.full.bean.biz.MenuEntity;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Webservice {

    @GET("index")
    LiveData<ApiResponse<BaseEntity<List<MenuEntity>>>> searchMenu(@Query("key") String key, @Query("word") String word);

}

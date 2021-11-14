package com.mvvm.hmi.full.model;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mvvm.hmi.full.api.ApiResponse;
import com.mvvm.hmi.full.api.DefaultNetworkBoundResource;
import com.mvvm.hmi.full.bean.biz.BaseEntity;
import com.mvvm.hmi.full.bean.biz.MenuEntity;
import com.mvvm.hmi.full.bean.Resource;
import com.mvvm.hmi.full.model.local.LocalService;
import com.mvvm.hmi.full.api.Webservice;
import com.mvvm.hmi.full.tools.AppExecutors;

import java.util.ArrayList;
import java.util.List;

public class MenuRepository implements IRepository {

    public static final String TAG = MenuRepository.class.getSimpleName();

    private final LocalService mLocalService;
    private final Webservice mRemoteService;
    private final AppExecutors mAppExecutors;

    public MenuRepository(LocalService localService,
                          Webservice remoteService,
                          AppExecutors appExecutors) {
        mLocalService = localService;
        mRemoteService = remoteService;
        mAppExecutors = appExecutors;
    }

    @Override
    @WorkerThread
    public LiveData<Resource<List<MenuEntity>>> searchMenu(String menuName) {
        return new DefaultNetworkBoundResource<List<MenuEntity>, BaseEntity<List<MenuEntity>>>(mAppExecutors) {

            @Override
            protected void saveCallResult(BaseEntity<List<MenuEntity>> item) {
                Log.e(TAG, "saveCallResult: " + item.toString());
            }

            @Override
            protected boolean shouldFetch(@Nullable List<MenuEntity> data) {
                Log.e(TAG, "shouldFetch: " + data);
                return true;
            }

            @Override
            protected LiveData<List<MenuEntity>> loadFromCache() {
                Log.e(TAG, "loadFromCache: ");
                MutableLiveData<List<MenuEntity>> liveData = new MutableLiveData<>();
                liveData.setValue(new ArrayList<>());
                return liveData;
            }

            @Override
            protected LiveData<ApiResponse<BaseEntity<List<MenuEntity>>>> createCall() {
                return mRemoteService.searchMenu(KEY, menuName);
            }
        }.asLiveData();
    }
}

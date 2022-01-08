package com.mvvm.hmi.ipc.ui;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mvvm.fwk.utils.AppExecutors;
import com.mvvm.fwk.utils.LogUtils;
import com.mvvm.fwk.utils.eventbus.LiveDataBus;
import com.mvvm.fwk.viewmodel.BaseViewModel;
import com.mvvm.hmi.ipc.CarApp;
import com.mvvm.hmi.ipc.model.HvacCallback;
import com.mvvm.hmi.ipc.model.HvacRepository;

public class HvacViewModel extends BaseViewModel<HvacRepository> {

    private static final String TAG = CarApp.TAG_HVAC + HvacViewModel.class.getSimpleName();

    private final HvacRepository mRepository;
    // 线程池框架。某些场景，ViewModel访问Repository中的方法可能会需要切换到子线程。
    private final AppExecutors mAppExecutors;
    private MutableLiveData<String> mTempLive;

    private final HvacCallback mHvacCallback = new HvacCallback() {
        @Override
        public void onTemperatureChanged(String temp) {
            LogUtils.logI(TAG, "[onTemperatureChanged] " + temp);
            getTempLive().postValue(temp);
        }
    };

    public HvacViewModel(HvacRepository repository, AppExecutors executors) {
        super(repository);
        mRepository = repository;
        mAppExecutors = executors;
        mRepository.setHvacListener(mHvacCallback);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mRepository.removeHvacListener(mHvacCallback);
        mRepository.release();
    }

    /**
     * 请求页面数据
     */
    public void requestTemperature() {
        mRepository.requestTemperature();
    }

    /**
     * 将温度数据设定到Service中
     *
     * @param view
     */
    public void setTemperature(View view) {
        mRepository.setTemperature(getTempLive().getValue());
    }

    public MutableLiveData<String> getTempLive() {
        if (mTempLive == null) {
            mTempLive = new MutableLiveData<>();
        }
        return mTempLive;
    }
}

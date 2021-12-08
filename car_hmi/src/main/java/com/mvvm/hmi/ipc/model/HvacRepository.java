package com.mvvm.hmi.ipc.model;

import android.text.TextUtils;

import com.fwk.sdk.hvac.HvacManager;
import com.fwk.sdk.hvac.IHvacCallback;
import com.mvvm.fwk.model.BaseRepository;
import com.mvvm.fwk.utils.LogUtils;
import com.mvvm.hmi.ipc.IpcApp;

import java.util.ArrayList;
import java.util.List;

public class HvacRepository extends BaseRepository {

    private static final String TAG = IpcApp.TAG_HVAC + HvacRepository.class.getSimpleName();

    private final HvacManager mHvacManager;
    private HvacCallback mHvacViewModelCallback;

    private final IHvacCallback mHvacCallback = new IHvacCallback() {
        @Override
        public void onTemperatureChanged(double temp) {
            if (mHvacViewModelCallback != null) {
                // 处理远程数据，将它转换为应用中需要的数据格式或内容
                String value = String.valueOf(temp);
                mHvacViewModelCallback.onTemperatureChanged(value);
            }
        }
    };

    public HvacRepository(HvacManager hvacManager) {
        mHvacManager = hvacManager;
        mHvacManager.registerCallback(mHvacCallback);
    }

    public void release() {
        mHvacManager.unregisterCallback(mHvacCallback);
    }

    public void requestTemperature() {
        LogUtils.logI(TAG, "[requestTemperature]");
        HvacManager.getInstance().requestTemperature();
    }

    public void setTemperature(String temperature) {
        LogUtils.logI(TAG, "[setTemperature] " + temperature);
        if (temperature == null || TextUtils.isEmpty(temperature)) {
            return;
        }
        mHvacManager.setTemperature(Integer.parseInt(temperature));
    }

    public void setHvacListener(HvacCallback callback) {
        LogUtils.logI(TAG, "[setHvacListener] " + callback);
        mHvacViewModelCallback = callback;
    }

    public void removeHvacListener(HvacCallback callback) {
        LogUtils.logI(TAG, "[removeHvacListener] " + callback);
        mHvacViewModelCallback = null;
    }

}

package com.mvvm.hmi.ipc.ui;

import androidx.lifecycle.Observer;

import com.mvvm.fwk.ui.BaseMvvmActivity;
import com.mvvm.fwk.utils.LogUtils;
import com.mvvm.hmi.ipc.BR;
import com.mvvm.hmi.ipc.IpcApp;
import com.mvvm.hmi.ipc.R;
import com.mvvm.hmi.ipc.databinding.ActivityHvacBinding;
import com.mvvm.hmi.ipc.factory.AppInjection;

public class HvacActivity extends BaseMvvmActivity<HvacViewModel, ActivityHvacBinding> {

    private static final String TAG = IpcApp.TAG_HVAC + HvacActivity.class.getSimpleName();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_hvac;
    }

    @Override
    protected Object getViewModelOrFactory() {
        return AppInjection.getViewModelFactory();
    }

    @Override
    protected int getViewModelVariable() {
        return BR.viewModel;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initObservable(HvacViewModel viewModel) {
        viewModel.getTempLive().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String temp) {
                LogUtils.logI(TAG, "[onChanged] " + temp);
            }
        });
    }

    @Override
    protected void loadData(HvacViewModel viewModel) {
        viewModel.requestTemperature();
    }
}

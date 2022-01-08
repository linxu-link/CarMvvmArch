package com.mvvm.hmi.ipc.ui;

import android.content.Intent;

import androidx.lifecycle.Observer;

import com.mvvm.fwk.ui.BaseMvvmActivity;
import com.mvvm.fwk.utils.LogUtils;
import com.mvvm.fwk.utils.eventbus.LiveDataBus;
import com.mvvm.hmi.ipc.BR;
import com.mvvm.hmi.ipc.CarApp;
import com.mvvm.hmi.ipc.R;
import com.mvvm.hmi.ipc.databinding.ActivityHvacBinding;
import com.mvvm.hmi.ipc.factory.AppInjection;

public class HvacActivity extends BaseMvvmActivity<HvacViewModel,ActivityHvacBinding>{

    @Override
    protected int getLayoutId() {
        return R.layout.activity_hvac;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected Object getViewModelOrFactory() {
        return AppInjection.getViewModelFactory();
    }

    @Override
    protected int getViewModelVariable() {
        return 0;
    }

    @Override
    protected void initObservable(HvacViewModel viewModel) {

    }

    @Override
    protected void loadData(HvacViewModel viewModel) {

    }
}

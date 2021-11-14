package com.mvvm.hmi.ipc.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.mvvm.fwk.utils.AppExecutors;
import com.mvvm.hmi.ipc.model.HvacRepository;
import com.mvvm.hmi.ipc.ui.HvacViewModel;

import java.lang.reflect.InvocationTargetException;

// default 权限，不对外部公开此类
class AppViewModelFactory implements ViewModelProvider.Factory {

    // 创建 viewModel 实例
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            if (modelClass == HvacViewModel.class) {
                return modelClass.getConstructor(HvacRepository.class, AppExecutors.class)
                        .newInstance(AppInjection.getHvacRepository(), AppExecutors.get());
            } else {
                throw new RuntimeException(modelClass.getSimpleName() + "create failed");
            }
        } catch (NoSuchMethodException | IllegalAccessException
                | InstantiationException | InvocationTargetException exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }
}

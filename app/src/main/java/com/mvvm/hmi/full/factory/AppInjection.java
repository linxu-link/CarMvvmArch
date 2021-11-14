package com.mvvm.hmi.full.factory;

import androidx.lifecycle.ViewModel;

import com.mvvm.hmi.full.model.MenuRepository;
import com.mvvm.hmi.full.model.local.LocalService;
import com.mvvm.hmi.full.model.remote.HttpService;
import com.mvvm.hmi.full.tools.AppExecutors;

// 统一生成APP中 ViewModel & Repository
public class AppInjection {

    private final static AppViewModelFactory mViewModelFactory = new AppViewModelFactory();
    private static volatile MenuRepository mRepository;

    public static <T extends ViewModel> T getViewModel(Class<T> clazz) {
        return mViewModelFactory.create(clazz);
    }

    // 受保护的权限，只对factory包中的类公开使用
    protected static MenuRepository getMenuRepository() {
        if (mRepository == null) {
            synchronized (AppInjection.class) {
                if (mRepository == null) {
                    mRepository = new MenuRepository(
                            LocalService.get(),
                            HttpService.get().getWebservice(),
                            AppExecutors.get()
                    );
                }
            }
        }
        return mRepository;
    }

}

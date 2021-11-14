package com.mvvm.hmi.ipc.factory;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.fwk.sdk.hvac.HvacManager;
import com.mvvm.hmi.ipc.model.HvacRepository;

/**
 * 统一生成APP中 ViewModel & Repository.
 * 统一管理APP中的所有单例类.
 *
 * @author WuJia
 * @version 1.0
 * @date 2021/11/14
 */
public class AppInjection {

    // ViewModel 工厂
    private final static AppViewModelFactory mViewModelFactory = new AppViewModelFactory();

    public static <T extends ViewModel> T getViewModel(ViewModelStoreOwner store, Class<T> clazz) {
        return new ViewModelProvider(store, mViewModelFactory).get(clazz);
    }

    public static AppViewModelFactory getViewModelFactory() {
        return mViewModelFactory;
    }

    /**
     * 受保护的权限,除了ViewModel，其它模块不应该需要Model层的实例
     *
     * @return {@link HvacRepository}
     */
    protected static HvacRepository getHvacRepository() {
        return new HvacRepository(getHvacManager());
    }

    public static HvacManager getHvacManager() {
        return HvacManager.getInstance();
    }

}

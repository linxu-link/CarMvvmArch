package com.mvvm.hmi.full.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.mvvm.hmi.full.bean.biz.MenuEntity;
import com.mvvm.hmi.full.bean.Resource;
import com.mvvm.hmi.full.model.MenuRepository;
import com.mvvm.hmi.full.tools.AppExecutors;

import java.util.List;

public class MenuViewModel extends ViewModel {

    private final MenuRepository mRepository;
    private final AppExecutors mAppExecutors;

    public MenuViewModel(MenuRepository repository, AppExecutors appExecutors) {
        mRepository = repository;
        mAppExecutors = appExecutors;
    }

    public LiveData<Resource<List<MenuEntity>>> searchMenu(String menuName) {
        return mRepository.searchMenu(menuName);
    }
}

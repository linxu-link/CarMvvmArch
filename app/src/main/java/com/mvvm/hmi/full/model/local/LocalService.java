package com.mvvm.hmi.full.model.local;

import androidx.lifecycle.LiveData;

import com.mvvm.hmi.full.bean.biz.MenuEntity;
import com.mvvm.hmi.full.bean.Resource;
import com.mvvm.hmi.full.model.IRepository;

import java.util.List;

public class LocalService implements IRepository {

    private static final LocalService LOCAL_SERVICE = new LocalService();

    public static LocalService get() {
        return LOCAL_SERVICE;
    }

    @Override
    public LiveData<Resource<List<MenuEntity>>> searchMenu(String menuName) {
        return null;
    }
}

package com.mvvm.hmi.full.model;

import androidx.lifecycle.LiveData;

import com.mvvm.hmi.full.bean.biz.MenuEntity;
import com.mvvm.hmi.full.bean.Resource;

import java.util.List;

public interface IRepository {

    String KEY = "3518f22948d760fcaa7d3f5dc133008e";

    LiveData<Resource<List<MenuEntity>>> searchMenu(String menuName);

}

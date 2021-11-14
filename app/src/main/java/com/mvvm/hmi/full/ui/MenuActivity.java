package com.mvvm.hmi.full.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.util.Log;

import com.mvvm.hmi.full.bean.Resource;
import com.mvvm.hmi.full.factory.AppInjection;
import com.mvvm.hmi.full.R;
import com.mvvm.hmi.full.bean.biz.MenuEntity;
import com.mvvm.hmi.full.databinding.ActivityMainBinding;
import com.mvvm.hmi.full.viewmodel.MenuViewModel;

import java.util.List;

public class MenuActivity extends AppCompatActivity {

    protected MenuViewModel mMenuViewModel;
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mMenuViewModel = AppInjection.getViewModel(MenuViewModel.class);

        mMenuViewModel.searchMenu("黄瓜")
                .observe(MenuActivity.this, new Observer<Resource<List<MenuEntity>>>() {
                    @Override
                    public void onChanged(Resource<List<MenuEntity>> listResource) {
                        Log.e("TAG", "onChanged: " + listResource.mData.toString());
//                        mBinding.setMenu(listResource.mData.get(0));
                    }
                });
    }
}
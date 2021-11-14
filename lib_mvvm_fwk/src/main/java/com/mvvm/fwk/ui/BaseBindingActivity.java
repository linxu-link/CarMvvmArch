package com.mvvm.fwk.ui;

import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

public abstract class BaseBindingActivity<V extends ViewDataBinding> extends BaseActivity {

    protected V mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getLayoutId() == 0) {
            throw new RuntimeException("getLayout() must be not null");
        }
        mBinding = DataBindingUtil.setContentView(this, getLayoutId());
        mBinding.setLifecycleOwner(this);
        mBinding.executePendingBindings();
        initView();
    }

    @LayoutRes
    protected abstract int getLayoutId();

    public V getBinding() {
        return mBinding;
    }

    protected abstract void initView();
}

package com.mvvm.fwk.ui;

import static com.mvvm.fwk.utils.LogUtils.TAG_FWK;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.mvvm.fwk.utils.LogUtils;

public abstract class BaseBindingFragment<V extends ViewDataBinding> extends BaseFragment {

    private static final String TAG = TAG_FWK + BaseBindingFragment.class.getSimpleName();

    protected V mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        LogUtils.logV(TAG, "[onCreateView]");
        if (getLayoutId() == 0) {
            throw new RuntimeException("getLayout() must be not null");
        }
        mBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        mBinding.setLifecycleOwner(this);
        mBinding.executePendingBindings();
        initView();
        return mBinding.getRoot();
    }

    protected abstract void initView();

    @LayoutRes
    protected abstract int getLayoutId();

    public V getBinding() {
        return mBinding;
    }
}

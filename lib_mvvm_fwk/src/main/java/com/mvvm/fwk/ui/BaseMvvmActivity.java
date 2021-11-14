package com.mvvm.fwk.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.mvvm.fwk.viewmodel.BaseViewModel;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class BaseMvvmActivity<Vm extends BaseViewModel, V extends ViewDataBinding> extends BaseBindingActivity<V> {

    protected Vm mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initViewModel();
        super.onCreate(savedInstanceState);
        if (getViewModelVariable() != 0) {
            mBinding.setVariable(getViewModelVariable(), mViewModel);
        }
        mBinding.executePendingBindings();
        initObservable(mViewModel);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadData(mViewModel);
    }

    private void initViewModel() {
        Class<Vm> modelClass;
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            modelClass = (Class<Vm>) ((ParameterizedType) type).getActualTypeArguments()[0];
        } else {
            modelClass = (Class<Vm>) BaseViewModel.class;
        }
        Object  object = getViewModelOrFactory();
        if (object instanceof BaseViewModel){
            mViewModel = (Vm) object;
        }else if (object instanceof ViewModelProvider.Factory){
            mViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) object)
                    .get(modelClass);
        }else {
            mViewModel = new ViewModelProvider(this,
                    new ViewModelProvider.NewInstanceFactory()).get(modelClass);
        }
    }

    protected abstract Object getViewModelOrFactory();

    protected abstract int getViewModelVariable();

    protected abstract void initObservable(Vm viewModel);

    protected abstract void loadData(Vm viewModel);

    protected Vm getViewModel() {
        return mViewModel;
    }
}

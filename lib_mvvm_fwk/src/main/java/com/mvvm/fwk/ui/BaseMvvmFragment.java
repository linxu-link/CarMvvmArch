package com.mvvm.fwk.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.mvvm.fwk.utils.LogUtils;
import com.mvvm.fwk.viewmodel.BaseViewModel;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class BaseMvvmFragment<Vm extends BaseViewModel, V extends ViewDataBinding> extends BaseBindingFragment<V> {

    protected Vm mViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initViewModel();
        View view = super.onCreateView(inflater, container, savedInstanceState);
        initObservable(mViewModel);
        if (getViewModelVariable() != 0) {
            mBinding.setVariable(getViewModelVariable(), mViewModel);
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadData(getViewModel());
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
        if (object instanceof ViewModel){
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

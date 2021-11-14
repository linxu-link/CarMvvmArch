package com.mvvm.fwk.viewmodel;

import static com.mvvm.fwk.utils.LogUtils.TAG_FWK;

import androidx.lifecycle.ViewModel;

import com.mvvm.fwk.model.BaseRepository;
import com.mvvm.fwk.utils.LogUtils;

public abstract class BaseViewModel<M extends BaseRepository> extends ViewModel {

    private final String TAG = TAG_FWK + getClass().getSimpleName();

    protected M mRepository;

    public BaseViewModel(M repository) {
        mRepository = repository;
    }

    public M getRepository() {
        return mRepository;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        LogUtils.logV(TAG, "[onCleared]");
    }
}

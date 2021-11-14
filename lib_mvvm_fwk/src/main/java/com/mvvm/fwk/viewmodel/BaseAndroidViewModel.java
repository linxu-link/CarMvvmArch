package com.mvvm.fwk.viewmodel;

import static com.mvvm.fwk.utils.LogUtils.TAG_FWK;

import android.app.Application;

import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;

import com.mvvm.fwk.model.BaseRepository;
import com.mvvm.fwk.utils.LogUtils;

public abstract class BaseAndroidViewModel<M extends BaseRepository> extends AndroidViewModel {

    private final String TAG = TAG_FWK + getClass().getSimpleName();

    protected M mRepository;

    public BaseAndroidViewModel(Application application, @Nullable M repository) {
        super(application);
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

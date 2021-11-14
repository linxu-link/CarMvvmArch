package com.mvvm.hmi.full.tools;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

// app 中统一管理的线程池
public class AppExecutors {

    private static final AppExecutors APP_EXECUTORS = new AppExecutors();

    private final Executor mDiskIO;
    private final Executor mNetworkIO;
    private final Executor mMainThread;

    public AppExecutors() {
        mDiskIO = Executors.newSingleThreadExecutor();
        mNetworkIO = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        mMainThread = new MainThreadExecutor();
    }

    public static AppExecutors get() {
        return APP_EXECUTORS;
    }

    public Executor diskIO() {
        return mDiskIO;
    }

    public Executor networkIO() {
        return mNetworkIO;
    }

    public Executor mainThread() {
        return mMainThread;
    }

    private static class MainThreadExecutor implements Executor {
        private final Handler mMainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable runnable) {
            mMainThreadHandler.post(runnable);
        }
    }

}

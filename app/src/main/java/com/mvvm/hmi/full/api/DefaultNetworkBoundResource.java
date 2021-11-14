package com.mvvm.hmi.full.api;

import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.mvvm.hmi.full.bean.Resource;
import com.mvvm.hmi.full.tools.AppExecutors;

// 一个泛型类，可以提供数据库和网络支持的资源。
public abstract class DefaultNetworkBoundResource<Result, Request> {

    private static final String TAG = DefaultNetworkBoundResource.class.getSimpleName();

    private final MediatorLiveData<Resource<Result>> mResult = new MediatorLiveData<>();
    protected final AppExecutors mAppExecutors;

    public DefaultNetworkBoundResource(AppExecutors appExecutors) {
        mAppExecutors = appExecutors;
        // 创建一个 loading 状态的resource
        mResult.setValue(Resource.loading(null));
        // 取出 cache 数据源
        LiveData<Result> dbSource = loadFromCache();

        // 监听本地数据源
        mResult.addSource(dbSource, data -> {
            // 停止监听本地数据源
            mResult.removeSource(dbSource);
            if (shouldFetch(data)) {
                // 如果需要更新数据，从网络获取最新的数据
                fetchFromNetwork(dbSource);
            } else {
                // 将 dbSource 作为一个新源重新连接，它将快速发送其最新值
                // 如果不需要更新数据，则从本地获取数据
                mResult.addSource(dbSource, result -> {
                    mResult.setValue(Resource.success(result));
                });
            }
        });
    }

    public LiveData<Resource<Result>> asLiveData() {
        return mResult;
    }

    private void fetchFromNetwork(LiveData<Result> dbSource) {
        LiveData<ApiResponse<Request>> apiResponse = createCall();
        // 将 dbSource 作为一个新源重新连接，它将快速发送其最新值
        mResult.addSource(dbSource, result -> {
            mResult.setValue(Resource.loading(result));
        });
        // 将 dbSource 作为一个新源重新连接，它将快速发送其最新值
        mResult.addSource(apiResponse, new Observer<ApiResponse<Request>>() {
            @Override
            public void onChanged(ApiResponse<Request> response) {
                mResult.removeSource(apiResponse);
                mResult.removeSource(dbSource);

                if (response instanceof ApiResponse.ApiSuccessResponse) {
                    Log.e(TAG, "onChanged2: " + ((ApiResponse.ApiSuccessResponse<Request>) response).mBody.toString());
                    mAppExecutors.diskIO().execute(() -> {
                        saveCallResult(
                                processResponse((ApiResponse.ApiSuccessResponse<Request>) response)
                        );
                        mAppExecutors.mainThread().execute(() -> {
                            // 请求一个新的实时数据，否则我们将立即得到最后一个缓存值，该值可能不会用从网络收到的最新结果进行更新。
                            mResult.addSource(loadFromCache(), result -> {
                                mResult.setValue(Resource.success(result));
                            });
                        });
                    });
                } else if (response instanceof ApiResponse.ApiErrorResponse) {
                    onFetchFailed();
                    mResult.addSource(dbSource, new Observer<Result>() {
                        @Override
                        public void onChanged(Result result) {
                            mResult.setValue(
                                    Resource.error(
                                            result,
                                            ((ApiResponse.ApiErrorResponse<Request>) response).mErrorMessage)
                            );
                        }
                    });
                } else if (response instanceof ApiResponse.ApiEmptyResponse) {
                    mAppExecutors.mainThread().execute(() -> {
                        mResult.addSource(loadFromCache(), result -> {
                            mResult.setValue(Resource.success(result));
                        });
                    });
                } else {
                    // do nothing.
                }
            }
        });
    }

    @WorkerThread
    protected Request processResponse(ApiResponse.ApiSuccessResponse<Request> response) {
        return response.mBody;
    }

    // 将最新的数据保存到本地数据源
    @WorkerThread
    protected abstract void saveCallResult(Request item);

    // 是否应该更新数据
    @MainThread
    protected abstract boolean shouldFetch(@Nullable Result data);

    // 从本地获取数据
    @MainThread
    protected abstract LiveData<Result> loadFromCache();

    // 创建网络调用
    @MainThread
    protected abstract LiveData<ApiResponse<Request>> createCall();

    // 在提取失败时调用。子类可能希望重置诸如速率限制器之类的组件。
    protected void onFetchFailed() {

    }

}

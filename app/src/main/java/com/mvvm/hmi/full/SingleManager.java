package com.fwk.sdk.sinagle;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.fwk.sdk.AppGlobal;
import com.fwk.sdk.ISampleCallback;
import com.fwk.sdk.ISampleInterface;
import com.fwk.sdk.listener.IServiceConnectListener;
import com.fwk.sdk.utils.Remote;
import com.fwk.sdk.utils.SdkLogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class SingleManager {

    private static final String TAG = SdkLogUtils.TAG_FWK + SingleManager.class.getSimpleName();

    public static final String SERVICE_PACKAGE = "com.fwk.service";
    public static final String SERVICE_CLASSNAME = "com.fwk.service.SimpleService";
    public static final String SERVICE_ACTION = "com.fwk.service.bind.action";
    private static final long RETRY_TIME = 5000L;

    private static volatile SingleManager sSingleManager;
    private final Application mApplication;
    private IServiceConnectListener mServiceListener;
    private ISampleInterface mSampleProxy;
    private final List<SampleCallback> mCallbacks = new ArrayList<>();

    private final Handler mHandler;
    private final LinkedBlockingQueue<Runnable> mTaskQueue = new LinkedBlockingQueue<>();
    private final Runnable mBindServiceTask = new Runnable() {
        @Override
        public void run() {

        }
    };
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SdkLogUtils.logI(TAG, "[onServiceConnected]");
            mSampleProxy = ISampleInterface.Stub.asInterface(service);
            Remote.tryExec(() -> {
                service.linkToDeath(mDeathRecipient, 0);
            });
            if (mServiceListener != null) {
                mServiceListener.onServiceConnected();
            }
            handleTask();
            mHandler.removeCallbacks(mBindServiceTask);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            SdkLogUtils.logI(TAG, "[onServiceDisconnected]");
            mSampleProxy = null;
            if (mServiceListener != null) {
                mServiceListener.onServiceDisconnected();
            }

        }
    };
    private final IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            SdkLogUtils.logI(TAG, "[binderDied]");
            if (mServiceListener != null) {
                mServiceListener.onBinderDied();
            }

            if (mSampleProxy != null) {
                mSampleProxy.asBinder().unlinkToDeath(mDeathRecipient, 0);
                mSampleProxy = null;
            }

            attemptToRebindService();
        }
    };

    private final ISampleCallback.Stub mSampleCallback = new ISampleCallback.Stub() {
        @Override
        public void onDateChanged(long date) throws RemoteException {
            SdkLogUtils.logI(TAG, "[onDateChanged] date " + date);
            for (SampleCallback callback : mCallbacks) {
                callback.onDateChanged(date);
            }
        }
    };

    private SingleManager() {
        HandlerThread thread = new HandlerThread("bindService", 10);
        thread.start();
        mHandler = new Handler(thread.getLooper());
        mApplication = AppGlobal.getApplication();
    }

    public static SingleManager getInstance() {
        if (sSingleManager == null) {
            synchronized (SingleManager.class) {
                if (sSingleManager == null) {
                    sSingleManager = new SingleManager();
                }
            }
        }
        return sSingleManager;
    }

    public static void init() {
        SdkLogUtils.logV(TAG, "[init]");
        getInstance().bindService();
    }

    private void bindService() {
        if (mSampleProxy == null) {
            SdkLogUtils.logV(TAG, "[bindService] start");
            Intent intent = new Intent();
            intent.setAction(SERVICE_ACTION);
            intent.setPackage(SERVICE_PACKAGE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mApplication.startForegroundService(intent);
            } else {
                mApplication.startService(intent);
            }
            boolean connected = mApplication.bindService(intent, mServiceConnection,
                    Context.BIND_AUTO_CREATE);
            SdkLogUtils.logE(TAG, "[bindService] result " + connected);
            if (!connected) {
                attemptToRebindService();
            }
        } else {
            SdkLogUtils.logV(TAG, "[bindService] not need");
        }
    }

    private void attemptToRebindService() {
        SdkLogUtils.logV(TAG, "[attemptToRebindService]");
        mHandler.postDelayed(mBindServiceTask, RETRY_TIME);
    }

    private void handleTask() {
        Runnable task;
        while ((task = mTaskQueue.poll()) != null) {
            SdkLogUtils.logV(TAG, "[handleTask] poll task form task queue");
            mHandler.post(task);
        }
    }

    public boolean isServiceConnected() {
        return isServiceConnected(false);
    }

    public boolean isServiceConnected(boolean tryConnect) {
        SdkLogUtils.logV(TAG, "[isServiceConnected] tryConnect" + tryConnect + ";isConnected " + (mSampleProxy == null));
        if (mSampleProxy == null && tryConnect) {
            attemptToRebindService();
        }
        return this.mSampleProxy != null;
    }

    public void release() {
        SdkLogUtils.logV(TAG, "[release]");
        if (this.isServiceConnected()) {
            this.mSampleProxy.asBinder().unlinkToDeath(this.mDeathRecipient, 0);
            this.mSampleProxy = null;
            this.mApplication.unbindService(mServiceConnection);
        }
    }

    public void setStateListener(IServiceConnectListener listener) {
        SdkLogUtils.logV(TAG, "[setStateListener]" + listener);
        mServiceListener = listener;
    }

    public void removeStateListener() {
        SdkLogUtils.logV(TAG, "[removeStateListener]");
        mServiceListener = null;
    }

    /******************/

    public void requestDate() {
        Remote.tryExec(() -> {
            if (isServiceConnected(true)) {
                mSampleProxy.requestDate();
            } else {
                mTaskQueue.offer(this::requestDate);
            }
        });
    }

    public boolean registerCallback(SampleCallback callback) {
        return Remote.exec(() -> {
            if (isServiceConnected(true)) {
                boolean result = mSampleProxy.registerCallback(mSampleCallback);
                if (result) {
                    mCallbacks.remove(callback);
                    mCallbacks.add(callback);
                }
                return result;
            } else {
                mTaskQueue.offer(() -> {
                    registerCallback(callback);
                });
                return false;
            }
        });
    }

    public boolean unregisterCallback(SampleCallback callback) {
        return Remote.exec(() -> {
            if (isServiceConnected(true)) {
                boolean result = mSampleProxy.unregisterCallback(mSampleCallback);
                if (result) {
                    mCallbacks.remove(callback);
                }
                return result;
            } else {
                mTaskQueue.offer(() -> {
                    unregisterCallback(callback);
                });
                return false;
            }
        });
    }

}

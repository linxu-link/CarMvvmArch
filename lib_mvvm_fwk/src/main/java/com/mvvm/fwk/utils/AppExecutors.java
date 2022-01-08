package com.mvvm.fwk.utils;

import static com.mvvm.fwk.utils.LogUtils.TAG_FWK;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.IntRange;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class AppExecutors {
    private static final String TAG = TAG_FWK + AppExecutors.class.getSimpleName();

    private static final AppExecutors APP_EXECUTORS = new AppExecutors();
    public static final long KEEP_ALIVE = 30L;
    public static final int MAX_PRIORITY = 10;
    public static final int MIN_PRIORITY = 0;
    public static final int MIDDLE_PRIORITY = 5;

    private boolean mIsPause;
    private final ThreadPoolExecutor mPoolExecutor;
    private final ReentrantLock mLock = new ReentrantLock();
    private final Condition mPauseCondition;
    private final Handler mMainHandler;

    public AppExecutors() {
        mMainHandler = new Handler(Looper.getMainLooper());
        mPauseCondition = mLock.newCondition();
        int cpuCount = Runtime.getRuntime().availableProcessors();
        int corePoolSize = cpuCount + 1;
        int maxPoolSize = Integer.MAX_VALUE;
        PriorityBlockingQueue<Runnable> blockingQueue = new PriorityBlockingQueue<>();

        AtomicLong seq = new AtomicLong();

        ThreadFactory factory = runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("FWK_TASK#" + seq.getAndIncrement());
            return thread;
        };

        mPoolExecutor = new ThreadPoolExecutor(
                corePoolSize, maxPoolSize, KEEP_ALIVE, TimeUnit.SECONDS, blockingQueue, factory) {
            @Override
            protected void beforeExecute(Thread t, Runnable r) {
                super.beforeExecute(t, r);
                if (mIsPause) {
                    mLock.lock();
                    try {
                        mPauseCondition.await();
                    } catch (InterruptedException exception) {
                        LogUtils.logE(TAG, "beforeExecute:" + exception.toString());
                    } finally {
                        mLock.unlock();
                    }
                }
            }

            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                LogUtils.logV(TAG, "this thread priority is " + ((PriorityRunnable) r).mPriority);
            }
        };

    }

    public static AppExecutors get() {
        return APP_EXECUTORS;
    }

    public void execute(@IntRange(from = MIN_PRIORITY, to = MAX_PRIORITY) int priority,
                        Runnable runnable) {
        mPoolExecutor.execute(new PriorityRunnable(priority, runnable));
    }

    public void execute(Runnable runnable) {
        execute(MIDDLE_PRIORITY, runnable);
    }

    public void post(Runnable runnable) {
        mMainHandler.post(runnable);
    }

    public void postDelay(Runnable runnable, long delayMills) {
        mMainHandler.postDelayed(runnable, delayMills);
    }

    public void postRemove(Runnable runnable) {
        mMainHandler.removeCallbacks(runnable);
    }

    /**
     * Pause thread pool.
     */
    public void pause() {
        mLock.lock();
        try {
            mIsPause = true;
            LogUtils.logW(TAG, "thread pool is paused");
        } finally {
            mLock.unlock();
        }
    }

    /**
     * Resume thread pool.
     */
    public void resume() {
        mLock.lock();
        try {
            mIsPause = false;
            mPauseCondition.signalAll();
        } finally {
            mLock.unlock();
        }
        LogUtils.logW(TAG, "thread pool is resumed");
    }

    private static class MainThreadExecutor implements Executor {
        private final Handler mMainThreadHandler = new Handler(Looper.getMainLooper());

        public void removeCallbacksAndMessages(Object token) {
            mMainThreadHandler.removeCallbacksAndMessages(token);
        }

        @Override
        public void execute(Runnable runnable) {
            mMainThreadHandler.post(runnable);
        }
    }

    public abstract class Callable<T> implements Runnable {

        @Override
        public void run() {
            mMainHandler.post(this::onPrepare);
            T background = onBackground();
            mMainHandler.removeCallbacksAndMessages(null);
            mMainHandler.post(() -> onCompleted(background));
        }

        protected void onPrepare() {
            // do nothing.
        }

        protected abstract T onBackground();

        protected abstract void onCompleted(T background);
    }

    public static class PriorityRunnable implements Runnable, Comparable<PriorityRunnable> {

        private final int mPriority;
        private final Runnable mRunnable;

        public PriorityRunnable(int priority, Runnable runnable) {
            mPriority = priority;
            mRunnable = runnable;
        }

        @Override
        public int compareTo(PriorityRunnable runnable) {
            return Integer.compare(runnable.mPriority, this.mPriority);
        }

        @Override
        public void run() {
            mRunnable.run();
        }
    }

}

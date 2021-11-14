package com.mvvm.fwk.utils;

import android.util.Log;

public class LogUtils {

    public static final String TAG_FWK = "FWK_MVVM_";

    private static boolean DEBUG = true;
    private static boolean VERBOSE = true;
    private static boolean WARN = true;
    private static boolean INFO = true;
    private static boolean ERROR = true;

    public static void init(boolean debug, boolean verbose,
                            boolean warn, boolean info, boolean error) {
        DEBUG = debug;
        VERBOSE = verbose;
        WARN = warn;
        INFO = info;
        ERROR = error;
    }

    /**
     * Print debug log info.
     *
     * @param tag  title
     * @param info description
     */
    public static void logD(String tag, String info) {
        if (DEBUG) {
            Log.d(tag, "[thread:" + Thread.currentThread().getName() + "] - " + info);
        }
    }

    /**
     * Print verbose log info.
     *
     * @param tag  title
     * @param info description
     */
    public static void logV(String tag, String info) {
        if (VERBOSE) {
            Log.v(tag, "[thread:" + Thread.currentThread().getName() + "] - " + info);
        }
    }

    /**
     * Print info log info.
     *
     * @param tag  title
     * @param info description
     */
    public static void logI(String tag, String info) {
        if (INFO) {
            Log.i(tag, "[thread:" + Thread.currentThread().getName() + "] - " + info);
        }
    }

    /**
     * Print warn log info.
     *
     * @param tag  title
     * @param info description
     */
    public static void logW(String tag, String info) {
        if (WARN) {
            Log.w(tag, "[thread:" + Thread.currentThread().getName() + "] - " + info);
            StackTraceElement[] stackTraceElement = Thread.currentThread().getStackTrace();
            for (StackTraceElement traceElement : stackTraceElement) {
                Log.w(tag, "[thread:" + Thread.currentThread().getName()
                        + " - class:" + traceElement.getClassName()
                        + "." + traceElement.getMethodName() + "]");
            }
        }
    }

    /**
     * Print error log info.
     *
     * @param tag  title
     * @param info description
     */
    public static void logE(String tag, String info) {
        if (ERROR) {
            Log.e(tag, "[thread:" + Thread.currentThread().getName() + "] - " + info);
        }
    }
}

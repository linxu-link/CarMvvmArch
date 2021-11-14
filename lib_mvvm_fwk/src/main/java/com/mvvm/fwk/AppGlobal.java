package com.mvvm.fwk;

import android.app.Application;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.core.content.res.ResourcesCompat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AppGlobal {
    private static final String TAG = AppGlobal.class.getSimpleName();
    public static final String CLASS_FOR_NAME = "android.app.ActivityThread";
    public static final String CURRENT_APPLICATION = "currentApplication";
    public static final String GET_INITIAL_APPLICATION = "getInitialApplication";

    private AppGlobal() {

    }

    /**
     * Get application.
     *
     * @return application context.
     */
    public static Application getApplication() {
        Application application = null;
        try {
            Class atClass = Class.forName(CLASS_FOR_NAME);
            Method method = atClass.getDeclaredMethod(CURRENT_APPLICATION);
            method.setAccessible(true);
            application = (Application) method.invoke(null);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException | ClassNotFoundException exception) {
            Log.e(TAG, "exception:" + exception.toString());
        }

        if (application != null) {
            return application;
        }

        try {
            Class atClass = Class.forName(CLASS_FOR_NAME);
            Method method = atClass.getDeclaredMethod(GET_INITIAL_APPLICATION);
            method.setAccessible(true);
            application = (Application) method.invoke(null);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException | ClassNotFoundException exception) {
            Log.e(TAG, "exception:" + exception.toString());
        }

        return application;
    }

    public static Resources getResource() {
        return getApplication().getResources();
    }

    public static String getString(@StringRes int resId) {
        return getApplication().getResources().getString(resId);
    }

    public static int getDimension(@DimenRes int resId) {
        return getApplication().getResources().getDimensionPixelOffset(resId);
    }

    public static Drawable getDrawable(@DrawableRes int resId) {
        return ResourcesCompat.getDrawable(getResource(), resId, null);
    }

    public static int getColor(@ColorRes int resId) {
        return ResourcesCompat.getColor(getResource(), resId, null);
    }

}

package com.mvvm.fwk.ui;

import static com.mvvm.fwk.utils.LogUtils.TAG_FWK;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mvvm.fwk.utils.LogUtils;

public abstract class BaseFragment extends Fragment {

    private final String TAG = TAG_FWK + getClass().getSimpleName();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtils.logV(TAG, "[onActivityCreated]");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.logV(TAG, "[onCreate]");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.logV(TAG, "[onCreateView]");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        LogUtils.logV(TAG, "[onAttach] content " + context);
    }

    @Override
    public void onAttachFragment(@NonNull Fragment childFragment) {
        super.onAttachFragment(childFragment);
        LogUtils.logV(TAG, "[onAttachFragment] childFragment " + childFragment);
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtils.logV(TAG, "[onStart]");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.logV(TAG, "[onResume]");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.logV(TAG, "[onActivityResult] requestCode:" + requestCode + ";"
                + "resultCode:" + requestCode);
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.logV(TAG, "[onPause]");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        LogUtils.logV(TAG, "[onSaveInstanceState]");
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtils.logV(TAG, "[onStop]");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LogUtils.logV(TAG, "[onDetach]");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtils.logV(TAG, "[onDestroyView]");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.logV(TAG, "[onDestroy]");
    }
}

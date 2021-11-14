package com.mvvm.hmi.ipc.model;

public interface HvacCallback {

    default void onTemperatureChanged(String temp){

    }

}

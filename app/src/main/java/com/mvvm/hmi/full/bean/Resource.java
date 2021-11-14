package com.mvvm.hmi.full.bean;

public class Resource<T> {

    public Status mStatus;
    public T mData;
    public String mMessage;

    public Resource(Status status, T data, String message) {
        mStatus = status;
        mData = data;
        mMessage = message;
    }

    public static <T> Resource<T> success(T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    public static <T> Resource<T> error(T data,String msg) {
        return new Resource<>(Status.ERROR, data, msg);
    }

    public static <T> Resource<T> loading(T data){
        return new Resource<>(Status.LOADING, data, null);
    }


}

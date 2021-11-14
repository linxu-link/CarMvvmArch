package com.mvvm.hmi.full.bean.biz;

import com.google.gson.annotations.SerializedName;

public class BaseEntity<T> {

    @SerializedName("code")
    private Integer mCode;
    @SerializedName("msg")
    private String mMsg;
    @SerializedName("newslist")
    private T mNewsList;

    public Integer getCode() {
        return mCode;
    }

    public void setCode(Integer code) {
        mCode = code;
    }

    public String getMsg() {
        return mMsg;
    }

    public void setMsg(String msg) {
        mMsg = msg;
    }

    public T getNewsList() {
        return mNewsList;
    }

    public void setNewsList(T newsList) {
        mNewsList = newsList;
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "mCode=" + mCode +
                ", mMsg='" + mMsg + '\'' +
                ", mNewsList=" + mNewsList +
                '}';
    }
}

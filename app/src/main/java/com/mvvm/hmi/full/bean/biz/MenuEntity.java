package com.mvvm.hmi.full.bean.biz;

import com.google.gson.annotations.SerializedName;

public class MenuEntity {

    @SerializedName("id")
    private Integer mId;
    @SerializedName("type_id")
    private Integer mTypeId;
    @SerializedName("type_name")
    private String mTypeName;
    @SerializedName("cp_name")
    private String mCpName;
    @SerializedName("zuofa")
    private String mZuoFa;
    @SerializedName("texing")
    private String mTexing;
    @SerializedName("tishi")
    private String mTiShi;
    @SerializedName("tiaoliao")
    private String mTiaoLiao;
    @SerializedName("yuanliao")
    private String mYuanLiao;

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        mId = id;
    }

    public Integer getTypeId() {
        return mTypeId;
    }

    public void setTypeId(Integer typeId) {
        mTypeId = typeId;
    }

    public String getTypeName() {
        return mTypeName;
    }

    public void setTypeName(String typeName) {
        mTypeName = typeName;
    }

    public String getCpName() {
        return mCpName;
    }

    public void setCpName(String cpName) {
        mCpName = cpName;
    }

    public String getZuoFa() {
        return mZuoFa;
    }

    public void setZuoFa(String zuoFa) {
        mZuoFa = zuoFa;
    }

    public String getTexing() {
        return mTexing;
    }

    public void setTexing(String texing) {
        mTexing = texing;
    }

    public String getTiShi() {
        return mTiShi;
    }

    public void setTiShi(String tiShi) {
        mTiShi = tiShi;
    }

    public String getTiaoLiao() {
        return mTiaoLiao;
    }

    public void setTiaoLiao(String tiaoLiao) {
        mTiaoLiao = tiaoLiao;
    }

    public String getYuanLiao() {
        return mYuanLiao;
    }

    public void setYuanLiao(String yuanLiao) {
        mYuanLiao = yuanLiao;
    }

    @Override
    public String toString() {
        return "MenuEntity{" +
                "mId=" + mId +
                ", mTypeId=" + mTypeId +
                ", mTypeName='" + mTypeName + '\'' +
                ", mCpName='" + mCpName + '\'' +
                ", mZuoFa='" + mZuoFa + '\'' +
                ", mTexing='" + mTexing + '\'' +
                ", mTiShi='" + mTiShi + '\'' +
                ", mTiaoLiao='" + mTiaoLiao + '\'' +
                ", mYuanLiao='" + mYuanLiao + '\'' +
                '}';
    }
}

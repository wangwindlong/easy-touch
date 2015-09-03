package xyz.template.material.menu.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by wangyl on 15-9-3.
 */
public class LoginData extends BaseData {
    @SerializedName("imtoken")
    private String mToken;

    @SerializedName("nickname")
    private String mNickname;

    @SerializedName("portraituri")
    private String mPortraituri;

    @SerializedName("phone")
    private String mPhone;

    public String getToken() {
        return mToken;
    }

    public void setToken(String mToken) {
        this.mToken = mToken;
    }

    public String getNickname() {
        return mNickname;
    }

    public void setNickname(String mNickname) {
        this.mNickname = mNickname;
    }

    public String getPortraituri() {
        return mPortraituri;
    }

    public void setPortraituri(String mPortraituri) {
        this.mPortraituri = mPortraituri;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    @Override
    public String toString() {
        return "LoginData{" +
                "mToken='" + mToken + '\'' +
                ", mNickname='" + mNickname + '\'' +
                ", mPortraituri='" + mPortraituri + '\'' +
                ", mPhone='" + mPhone + '\'' +
                ", mResult='" + getResult() + '\'' +
                '}';
    }
}

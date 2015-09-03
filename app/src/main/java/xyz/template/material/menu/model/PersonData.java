package xyz.template.material.menu.model;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wangyl on 15-9-3.
 */
public class PersonData implements Serializable {
    @SerializedName("object_or_array")
    private String mType;

    @SerializedName("empty")
    private boolean mIsEmpty;

    @SerializedName("parse_time_nanoseconds")
    private long mNanoseconds;

    @SerializedName("validate")
    private boolean mIsValid;


    public String getType() {
        return mType;
    }


    public boolean isIsEmpty() {
        return mIsEmpty;
    }


    public long getNanoseconds() {
        return mNanoseconds;
    }


    public boolean isIsValid() {
        return mIsValid;
    }

}

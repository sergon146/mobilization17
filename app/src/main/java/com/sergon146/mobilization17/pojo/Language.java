package com.sergon146.mobilization17.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class Language implements Parcelable {
    private int id;
    private String name;
    private String code = "";
    private boolean isSource = false;
    private boolean isTarget = false;

    public  Language() {
    }

    private Language(Parcel in) {
        id = in.readInt();
        name = in.readString();
        code = in.readString();
        isSource = in.readByte() != 0;
        isTarget = in.readByte() != 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isSource() {
        return isSource;
    }

    public void setSource(boolean source) {
        isSource = source;
    }

    public boolean isTarget() {
        return isTarget;
    }

    public void setTarget(boolean target) {
        isTarget = target;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(code);
        dest.writeByte((byte) (isSource() ? 1 : 0));
        dest.writeByte((byte) (isTarget() ? 1 : 0));
    }

    public static final Creator<Language> CREATOR = new Creator<Language>() {
        @Override
        public Language createFromParcel(Parcel in) {
            return new Language(in);
        }

        @Override
        public Language[] newArray(int size) {
            return new Language[size];
        }
    };
}

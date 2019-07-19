package com.ct7liang.pictureselector;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class ImageBean {

    public String load;

    public int width;

    public int height;

    public ImageBean(String load, int width, int height) {
        this.load = load;
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return "ImageBean{" +
                "load='" + load + '\'' +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}

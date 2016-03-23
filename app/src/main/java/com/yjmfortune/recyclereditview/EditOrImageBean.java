package com.yjmfortune.recyclereditview;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by lixian on 2016/3/22.
 */
public class EditOrImageBean implements Serializable {

    public Bitmap bitmap;
    public String bitmapUrl;
    public String type;
    public String text;


    public EditOrImageBean( Bitmap bitmap, String type) {
        this.bitmap = bitmap;
        this.type = type;
    }


    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getBitmapUrl() {
        return bitmapUrl;
    }

    public void setBitmapUrl(String bitmapUrl) {
        this.bitmapUrl = bitmapUrl;
    }
}

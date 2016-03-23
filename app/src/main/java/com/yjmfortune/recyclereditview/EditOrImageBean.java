package com.yjmfortune.recyclereditview;

import android.graphics.Bitmap;
import android.view.Gravity;

import java.io.Serializable;

/**
 * Created by lixian on 2016/3/22.
 */
public class EditOrImageBean implements Serializable {

    public Bitmap bitmap;
    public String bitmapUrl;
    public String type;
    public String text;

    public int textGravity = Gravity.LEFT;
    public int textColor = R.color.black;
    public int textSize =25;


    public int getTextGravity() {
        return textGravity;
    }

    public void setTextGravity(int textGravity) {
        this.textGravity = textGravity;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

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

package com.lynnsion.lmnpuht.bean;

import java.io.Serializable;

/**
 * Created by dongjiang on 2017/8/19.
 */

public class PointBean implements Serializable{
    private float pointX;
    private float pointY;

    public float getPointX() {
        return pointX;
    }

    public void setPointX(float pointX) {
        this.pointX = pointX;
    }

    public float getPointY() {
        return pointY;
    }

    public void setPointY(float pointY) {
        this.pointY = pointY;
    }

    @Override
    public String toString() {
        return "PointBean{" +
                "pointX=" + pointX +
                ", pointY=" + pointY +
                '}';
    }
}

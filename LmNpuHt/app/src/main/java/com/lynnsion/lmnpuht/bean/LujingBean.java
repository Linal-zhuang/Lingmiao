package com.lynnsion.lmnpuht.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by dongjiang on 2017/8/19.
 */

public class LujingBean implements Serializable {
    private ArrayList<ArrayList<PointBean>> pathPointList;

    public ArrayList<ArrayList<PointBean>> getPathPointList() {
        return pathPointList;
    }

    public void setPathPointList(ArrayList<ArrayList<PointBean>> pathPointList) {
        this.pathPointList = pathPointList;
    }
}

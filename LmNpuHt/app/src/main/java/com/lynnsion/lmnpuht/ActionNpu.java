package com.lynnsion.lmnpuht;


import wizrobo_npu.Pose3D;

/**
 * Created by ZLX on 2018/3/26.
 * <p>
 * ti is a Action class of Npu
 */

public interface ActionNpu  {

    public void connectNpu();

    public void gotoPose(Pose3D pose);


}

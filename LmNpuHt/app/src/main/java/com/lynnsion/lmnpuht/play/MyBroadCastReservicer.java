package com.lynnsion.lmnpuht.play;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by ZLX on 2018/3/26.
 */

public class MyBroadCastReservicer extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"音乐播放结束",Toast.LENGTH_SHORT).show();
    }
}

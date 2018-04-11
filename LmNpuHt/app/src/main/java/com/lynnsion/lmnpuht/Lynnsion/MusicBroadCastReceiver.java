package com.lynnsion.lmnpuht.Lynnsion;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Lynnsion on 2018/4/11.
 */

public class MusicBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("MusicBroadCastReceiver"," play music over");
    }
}

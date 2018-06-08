package com.lynnsion.lmnpuht.Lynnsion;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lynnsion.lmnpuht.WizRoboNpu;

public class MusicBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LoadPictureAndMusic.isMusicPlayOver = true;
//        WizRoboNpu.isMusicPlayOver = true;

        Log.e("MusicBroadCastReceiver"," play music over");
    }
}

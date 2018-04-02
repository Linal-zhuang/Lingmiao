package com.lynnsion.lmnpuht;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.lynnsion.lmnpuht.utils.WifiUtils;
import com.wizrobonpu.NpuIceI;
import com.wizrobonpu.WizRoboNpuUdp;

import wizrobo_npu.NpuException;
import wizrobo_npu.Pose3D;

/**
 * Created by ZLX on 2018/3/26.
 */

public class Activity_main extends Activity implements ActionNpu {

    private static NpuIceI mNpu = new NpuIceI();

    /**
     * 规定开始音乐、暂停音乐、结束音乐的标志
     */
    public static final int PLAT_MUSIC = 1;
    public static final int PAUSE_MUSIC = 2;
    public static final int STOP_MUSIC = 3;

    private static NpuIceI myNpu = new NpuIceI();
    private WizRoboNpuUdp udpSendIp = new WizRoboNpuUdp();

    private static int wifiStrength =0;

    private Button btnAll;
    private boolean
            displayOnce = false,
            wifiIsConnected = false;

    private int pathPoseNum = 0, countTime = 0;
    public static String strServerIP = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnAll = (Button) findViewById(R.id.btn_clear);
        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectNPU();
            }
        });
        ConnectNPU();

    }

    private void CheckWifiStatus() {
        if (new WifiUtils().checkNetWorkConnection(this).isConnected()) {
            String wserviceName = Context.WIFI_SERVICE;
            WifiManager wm = (WifiManager) getSystemService(wserviceName);
            WifiInfo info1 = wm.getConnectionInfo();
            int strength = info1.getRssi();
            wifiStrength = strength;
            if (strength < -85)
                return;
            wifiIsConnected = true;
        } else {
            wifiIsConnected = false;
            return;
        }
    }


    private void ConnectNPU() {
        countTime = 0;
        new Thread() {
            public void run() {
                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
                displayOnce = false;
                boolean isNpuInitOK;
                CheckWifiStatus();
                if (wifiIsConnected) {
                    strServerIP = udpSendIp.GetIp();
                    if (strServerIP == null || strServerIP.length() <= 0)
                        return;
                    Log.d("getIp", "ip="+strServerIP);
                    System.out.println(strServerIP);
                    try {
                        isNpuInitOK = myNpu.NpuInit(strServerIP);
                        if (isNpuInitOK) {
                            updateUIOnce = true;
                            displayPointsHandler.post(displayPoints);
                            updateUiHandler.post(updateUi);
                            loopHandler.post(loopRunnable);
                        }
                    } catch (NpuException e) {
                        e.printStackTrace();
                    }

                }
            }
        }.start();
    }

    @Override
    public void connectNpu() {

    }

    @Override
    public void gotoPose(Pose3D pose) {
        String mapId = "";
        String stationId = "";
        mNpu.GotoStation(mapId, stationId);
    }


}

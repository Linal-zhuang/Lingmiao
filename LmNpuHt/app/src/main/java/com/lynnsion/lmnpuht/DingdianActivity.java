package com.lynnsion.lmnpuht;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.permission.FloatWindowManager;
import com.wizrobonpu.NpuIceI;
import com.wizrobonpu.WizRoboNpuUdp;

import wizrobo_npu.ActionState;
import wizrobo_npu.CoreParam;
import wizrobo_npu.ImgPose;
import wizrobo_npu.NpuException;
import wizrobo_npu.NpuIcePrx;
import wizrobo_npu.ServerState;

/**
 * Created by Lynnsion on 2018/4/4.
 */

public class DingdianActivity extends AppCompatActivity implements JoystickView.JoystickListener{

    private JoystickView joystickViewDingdian;

    public static String strServerIP = new String();
    public static NpuIceI mynpu = new NpuIceI();
    WizRoboNpuUdp udpSendIp = new WizRoboNpuUdp();

    public static ServerState serverState;

    public static boolean isJoystickTriggered = false,
            isTimeout = false,
            toGetParam=false,
            isSettingInitialPose = false,
            isSettingGoalPose = false,
            isSettingFreePath = false,
            isSettingPathpose = false,
            displayOnce = false,
            wifiIsConnected = false,
            toConnectNpu = true,
            isInited = false,
            isSlam = false,
            isNavi = false,
            toRunOnce = true;

    public static float linScale = 0, angScale = 0;

    private static Handler getDataHandler, setManualVelHandler;
    private static Handler getDataHandler1;

    private final static int MESSAGECODE = 1;

    private float initImgposeTheta,setImgposeTheta;
    static float resolution = 0.07f, redarrowYaw = 0;
    private static int  pathPoseNum = 0;

    private int  countTime = 0;

    ImgPose[] imgPathPose = new ImgPose[100];   //Limited

    PowerManager pm = null;
    PowerManager.WakeLock wl = null;

    private TextProgressBar pg_my_progressbar;
    public  static DisplayMetrics dm = new DisplayMetrics();

    static NpuException npuException;

    private TextView tv_map_info, tv_act_vel, tv_cmd_vel, tv_act_posex, tv_robot_status, tv_act_motor_spd,
            tv_cmd_motor_spd, tv_ip, tv_wifi_strength, tv_act_enc, tv_alert, tv_npu_version,tv_imu_data;


    public static String mapname = "0",str_npu_version="npu_v1.0";


    Handler updateUiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("value");
            switch (val) {
                case "ConnectSuccess":
                    ConnectSuccess();
                    break;
//                case "SelectMap":
//                    SelectMapNotice();
//                    break;
//                case "ChangeWifiUI":
//                    ChangeWifiUI();
//                    break;
//                case "ChangePathStationUI":
//                    ChangePathStationUI();
//                    break;
//                case "ChangePathUI":
//                    ChangePathUI();
//                    break;
//                case "ChangeTaskUI":
//                    ChangeTaskUI();
//                    break;
//                case "ChangeNaviUI":
//                    ChangeNaviUI();
//                    break;
//                case "ChangeSlamUI":
//                    ChangeSlamUI();
//                    break;
//                case "ChangeVirtualWallUI":
//                    ChangeVirtualWallUI();
//                    break;
//                case "NpuException":
//                    NpuExceptionAlert(npuException);
//                    break;
//                case "CheckSensorStatus":
//                    DisPlaySensorStatus();
//                    break;
//                case "ChangeProgressBar":
//                    ChangeProgressBar();
//                    break;
//                case "GetSensorStatus":
//                    toGetSensorStatus=false;
//                    GetSensorStatus();
//                    break;
//                case "SetMapInfos":
//                    SetMapInfoSucceed();
//                    break;
//                case "GetMapInfos":
//                    GetMapInfos();
//                    break;
//                case "GetServerVersion":
//                    GetServerVersion();
//                    break;

                default:
                    break;
            }

        }
    };

    Runnable updateUiRunnable = new Runnable() {

        public void run() {
//            UpdateRobotStatus();
            serverState = mynpu.serverState;
//            if(togetFileInfo)
//            {
//                SendMessageToUI("GetFile");
//            }
//
//            if(toGetSensorStatus)
//            {
//                SendMessageToUI("GetSensorStatus");
//            }
//
//            if(toReboot)
//            {
//                SendMessageToUI("Reboot");
//            }
//
//            if(toShutDown)
//            {
//                SendMessageToUI("ShutDown");
//            }
//
//            if(toSendFile)
//            {
//                SendMessageToUI("SendFile");
//            }

            if (!wifiIsConnected) {
                getDataHandler1.removeCallbacksAndMessages(null);
                getDataHandler.removeCallbacksAndMessages(null);
                setManualVelHandler.removeCallbacksAndMessages(null);
                updateUiHandler.removeCallbacksAndMessages(null);
//                ConnectFailed();
                return;
            }

            if (serverState == ServerState.TIMEOUT && wifiIsConnected) {
                Toast toast = Toast.makeText(getApplicationContext(), mynpu.timeoutMethodName + "连接超时，正在重新连接...", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                isNavi = false;
                isSlam = false;
                isTimeout = true;
                isInited = false;
                getDataHandler1.removeCallbacksAndMessages(null);
                getDataHandler.removeCallbacksAndMessages(null);
                setManualVelHandler.removeCallbacksAndMessages(null);
                updateUiHandler.removeCallbacksAndMessages(null);
                toConnectNpu = true;
            }

//            UpdateCmdVel();
//            UpdateActVel();
//            UpdateCmdMotorSpd();
//            UpdateActMotorSpd();
//            UpdateActEnc();
//            UpdateActPose();
//            UpdateImudata();
//
//            if (isNavi || isSlam ) {
//                try {
//                    toGetSensorData=false;
//                    if (mynpu.isInited) {
//                        Display();
//                        UpdateCmdVel();
//                        UpdateActVel();
//                        UpdateCmdMotorSpd();
//                        UpdateActMotorSpd();
//                        UpdateActEnc();
//                        UpdateActPose();
//                        UpdateImudata();
//                        if (toChangeChildView) {
//                            toChangeChildView = false;
//                            adapter_operate.getChildView(0, 1, true, tempView, tempParent);
//                        }
//                    }
//                } catch (Exception e) {
//                    ExceptionAlert(e);
//                }
//            }
//
//            if(toGetSensorData&&!isNavi&&!isSlam)
//            {
//                UpdateActEnc();
//                DisplayLidar();
//
//            }
//
//            if (!mynpu.isInited && !displayOnce && wifiIsConnected) {
//                countTime++;
//                if (countTime == 8) {
//                    displayOnce = true;
//                    ConnectFailed();
//                }
//            }

            if (isSlam || isNavi)
                updateUiHandler.postDelayed(updateUiRunnable, 200);
            else
                updateUiHandler.postDelayed(updateUiRunnable, 1000);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dingdian_layout);



        setWifiDormancy();
        setWifiNeverSleep();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        wl.setReferenceCounted(false);
        wl.acquire();

        initLayout();

        pg_my_progressbar.setProgress(0);
        pg_my_progressbar.setVisibility(View.INVISIBLE);
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        FloatWindowManager.getInstance().askPermission(this);

        getDataHandler1 = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        break;
                    default:
                        break;
                }
            }
        };



    }

    private void initLayout() {
        joystickViewDingdian = (JoystickView) findViewById(R.id.joystickDingdian);

        tv_ip = (TextView) findViewById(R.id.tv_ip);
        tv_map_info = (TextView) findViewById(R.id.tv_mapinfo);
        tv_act_posex = (TextView) findViewById(R.id.tv_act_pose);
        tv_act_vel = (TextView) findViewById(R.id.tv_act_vel);
        tv_cmd_vel = (TextView) findViewById(R.id.tv_cmd_vel);
        tv_act_motor_spd = (TextView) findViewById(R.id.tv_act_motor_spd);
        tv_cmd_motor_spd = (TextView) findViewById(R.id.tv_cmd_motor_spd);
        tv_robot_status = (TextView) findViewById(R.id.tv_robot_status);
        tv_wifi_strength = (TextView) findViewById(R.id.tv_wifi_strength);
        tv_act_enc = (TextView) findViewById(R.id.tv_act_enc);
        tv_alert = (TextView) findViewById(R.id.tv_alert);
        tv_alert.setVisibility(View.INVISIBLE);
        tv_npu_version=(TextView) findViewById(R.id.tv_npu_version);
        tv_imu_data=(TextView) findViewById(R.id.tv_imu_data);

        pg_my_progressbar = (TextProgressBar) findViewById(R.id.pg_my_progressbar);
    }

    @Override
    public void onJoystickMoved(float xPercent, float yPercent, int id) {
        switch (id) {
            case R.id.joystickLeft:

                linScale = xPercent;
                angScale = -yPercent;
                System.out.println(linScale);
                System.out.println(angScale);

                if (isSettingInitialPose || isSettingGoalPose) {
                    if (linScale == 0 && angScale == 0)
                        return;
                    redarrowYaw = (float) (Math.atan2(linScale, -angScale));
                    initImgposeTheta = redarrowYaw;
                    setImgposeTheta = redarrowYaw;
                    if (pathPoseNum == 0)
                        return;
                    if (isSettingFreePath)
                        imgPathPose[pathPoseNum - 1].theta = redarrowYaw;
                } else if (isSettingPathpose) {
                    if (linScale == 0 && angScale == 0)
                        return;
                    redarrowYaw = (float) (Math.atan2(linScale, -angScale));
                    if (pathPoseNum == 0)
                        return;
                    imgPathPose[pathPoseNum - 1].theta = redarrowYaw;
                }
                else {
                    isJoystickTriggered = true;
                    if (Math.abs(linScale) < 0.3)
                        linScale = 0;
                    if (Math.abs(angScale) < 0.3)
                        angScale = 0;
                    if (linScale == 0 && angScale == 0) {
                        isJoystickTriggered = false;
                    }
                }
                break;
            default:
                break;
        }
    }


    private static class SetManualVelRunnable implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.currentThread().sleep(100);
                    if (!isTimeout&&!toGetParam) {
                        try {
                            int newTimeout = 30000;
                            NpuIcePrx newProxy = (NpuIcePrx)mynpu.npu.ice_timeout(newTimeout);
//                            if (cb_cruise_control.isChecked()) {
//                                newProxy.SetManualVel(0.99f, 0);
                            //定速功能
//                            }

                            if (isJoystickTriggered && serverState != ServerState.TIMEOUT) {
                                toRunOnce = true;
                                newProxy.SetManualVel(linScale, angScale);
                            } else {
                                if (toRunOnce) {
                                    newProxy.SetManualVel(0, 0);
                                    toRunOnce = false;
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                    setManualVelHandler.sendEmptyMessage(MESSAGECODE);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Throwable e) {
                    return;
                }
            }
        }
    }

    public void setWifiDormancy() {
        int value = Settings.System.getInt(getContentResolver(), Settings.System.WIFI_SLEEP_POLICY, Settings.System.WIFI_SLEEP_POLICY_DEFAULT);
        final SharedPreferences prefs = getSharedPreferences("wifi_sleep_policy", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("WIFI_SLEEP_POLICY_DEFAULT", value);
        editor.commit();

        if (Settings.System.WIFI_SLEEP_POLICY_NEVER != value) {
            Settings.System.putInt(getContentResolver(), Settings.System.WIFI_SLEEP_POLICY, Settings.System.WIFI_SLEEP_POLICY_NEVER);
        }
    }


    private void setWifiNeverSleep() {
        int wifiSleepPolicy = 0;
        wifiSleepPolicy = Settings.System.getInt(getContentResolver(),
                android.provider.Settings.System.WIFI_SLEEP_POLICY,
                Settings.System.WIFI_SLEEP_POLICY_DEFAULT);
        System.out.println("---> 修改前的Wifi休眠策略值 WIFI_SLEEP_POLICY=" + wifiSleepPolicy);


        Settings.System.putInt(getContentResolver(),
                android.provider.Settings.System.WIFI_SLEEP_POLICY,
                Settings.System.WIFI_SLEEP_POLICY_NEVER_WHILE_PLUGGED);


        wifiSleepPolicy = Settings.System.getInt(getContentResolver(),
                android.provider.Settings.System.WIFI_SLEEP_POLICY,
                Settings.System.WIFI_SLEEP_POLICY_DEFAULT);
        System.out.println("---> 修改后的Wifi休眠策略值 WIFI_SLEEP_POLICY=" + wifiSleepPolicy);
    }


    private void ConnectSuccess()
    {
        tv_ip.setText(strServerIP);
        Toast toast = Toast.makeText(getApplicationContext(), "连接成功！", Toast.LENGTH_LONG);
        toast.show();
    }

    private void ConnectNPU() {
        try {
            countTime = 0;
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
            displayOnce = false;
            mynpu.isInited = false;
            boolean a;
            if (wifiIsConnected) {
                strServerIP = udpSendIp.GetIp();
                if (strServerIP == null || strServerIP.length() <= 0)
                    return;
                System.out.println(strServerIP);

                a = mynpu.NpuInit(strServerIP);
                if (!a) {
                    System.out.println("Connect ICE Failed");
                    return;
                } else {
                    SendMessageToUI("ConnectSuccess");
                    updateUiHandler.post(updateUiRunnable);
                    CheckNpuStatus();
                    toConnectNpu = false;
                }
            }
        }

        catch (NpuException e) {

            toConnectNpu=false;
            System.out.println("NpuException:"+e.msg);
            npuException=e;
            SendMessageToUI("NpuException");
        }
    }

    private void CheckNpuStatus() {
        if (mynpu.isInited) {
            try {
                isInited = true;
                isTimeout = false;
                str_npu_version=mynpu.GetServerVersion();
                str_npu_version=str_npu_version.substring(0,23);
                SendMessageToUI("GetServerVersion");
                ActionState actionState = mynpu.GetActionState();
                if (actionState == ActionState.SLAM_ACTION) {
                    isSlam = false;
//                    Slam();
                }

                if (actionState == ActionState.NAVI_ACTION) {
                    CoreParam coreParam = mynpu.GetCoreParam();
                    mapname = coreParam.map_id;
                    isNavi = false;
//                    Navi();
                }

                System.out.println("ActionState:" + actionState.name());
            } catch (Exception e) {
                ExceptionAlert(e);
            }
        }
    }

    public void ExceptionAlert(Exception e )
    {
        isNavi=false;
        isSlam=false;
        isTimeout=true;
        isInited=false;
        getDataHandler1.removeCallbacksAndMessages( null );
        getDataHandler.removeCallbacksAndMessages( null );
        setManualVelHandler.removeCallbacksAndMessages( null );
        toConnectNpu=true;
    }



    public void SendMessageToUI(String a)
    {
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putString("value",a);
        msg.setData(data);
        updateUiHandler.sendMessage(msg);
        msg=null;
        data=null;
    }


}

package com.lynnsion.lmnpuht;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.permission.FloatWindowManager;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.LoopPagerAdapter;
import com.lynnsion.lmnpuht.Lynnsion.FileUtil;
import com.lynnsion.lmnpuht.Lynnsion.MusicBroadCastReceiver;
import com.lynnsion.lmnpuht.Lynnsion.MusicPlayActivity;
import com.lynnsion.lmnpuht.Lynnsion.PlayMusciServices;
import com.wizrobonpu.NpuIceI;
import com.wizrobonpu.WizRoboNpuUdp;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import wizrobo_npu.ActionState;
import wizrobo_npu.CoreParam;
import wizrobo_npu.DiagnosticsStatus;
import wizrobo_npu.ImgLidarScan;
import wizrobo_npu.ImgMap;
import wizrobo_npu.ImgPath;
import wizrobo_npu.ImgPoint;
import wizrobo_npu.ImgPose;
import wizrobo_npu.ImgStation;
import wizrobo_npu.ImgVirtualWall;
import wizrobo_npu.ImuData;
import wizrobo_npu.InitImgPoseArea;
import wizrobo_npu.InitPoseArea;
import wizrobo_npu.LidarScan;
import wizrobo_npu.MapInfo;
import wizrobo_npu.MotorEnc;
import wizrobo_npu.MotorSpd;
import wizrobo_npu.NaviMode;
import wizrobo_npu.NaviState;
import wizrobo_npu.NpuException;
import wizrobo_npu.NpuIcePrx;
import wizrobo_npu.NpuState;
import wizrobo_npu.PathInfo;
import wizrobo_npu.PixelMat;
import wizrobo_npu.Point3D;
import wizrobo_npu.Pose3D;
import wizrobo_npu.SensorState;
import wizrobo_npu.ServerState;
import wizrobo_npu.SlamMode;
import wizrobo_npu.Station;
import wizrobo_npu.StationInfo;
import wizrobo_npu.StationType;
import wizrobo_npu.Task;
import wizrobo_npu.Vel3D;
import wizrobo_npu.VirtualWallInfo;


@SuppressWarnings({"SpellCheckingInspection", "AccessStaticViaInstance", "deprecation", "Convert2Diamond", "ConstantConditions"})
public class WizRoboNpu extends AppCompatActivity implements JoystickView.JoystickListener, OnClickListener {
    private Bitmap bm_background = null;
    private ImageView iv_background;
    private Spinner sp_map_list, sp_path_station_list, sp_virtual_wall_list, sp_path_list, sp_task_list;
    private TextView tv_map_info, tv_act_vel, tv_cmd_vel, tv_act_posex, tv_robot_status, tv_act_motor_spd,
            tv_cmd_motor_spd, tv_ip, tv_wifi_strength, tv_act_enc, tv_alert, tv_npu_version, tv_imu_data;
    private CheckBox cb_lidar_display, cb_path_display, cb_station_display;
    private ScrollView sv_control;
    private ImageButton ibt_delete_map, ibt_pause, ibt_cancel, ibt_save_map;
    private ExpandableListView elv_operate = null, elv_setting = null, elv_function = null, elv_virtual_wall = null, elv_task_manage = null;
    private ExpandableListAdapter adapter_operate = null, adapter_setting = null, adapter_function = null, adapter_virtual_wall = null, adapter_task_manage = null;
    private static CheckBox cb_cruise_control;
    private TextProgressBar pg_my_progressbar;
    public static DisplayMetrics dm = new DisplayMetrics();

    ExpandableListView tempParent;
    View tempView;
    public static ViewGroup track_navi_parent, slam_parent;
    public static View track_navi_view, slam_view;
    JoystickView joystickView;

    public static String strSavePath = "添加路径", strSaveVirtualWall = "添加虚拟墙", strSetInitialPose = "设置初始点",
            strSetInitPoseArea = "设置初始区域", strSetGoalPose = "设置目标点", strSetFreePath = "设置自由路径",
            strSetCoveragePath = "设置清扫区域", strNaviTrack = "开始导航",
            strSlam = "开始建图", strAddCoverageArea = "添加清扫区域";

    public Map<String, List<String>> operateItemData = ExpandableListData.getData("Operate");
    public List<String> operateTitle = new ArrayList<String>(operateItemData.keySet());
    public Map<String, List<String>> settingItemData = ExpandableListData.getData("Setting");
    public List<String> settingTitle = new ArrayList<String>(settingItemData.keySet());
    public Map<String, List<String>> functionItemData = ExpandableListData.getData("Function");
    public List<String> functionTitle = new ArrayList<String>(functionItemData.keySet());
    public Map<String, List<String>> virtualWallItemData = ExpandableListData.getData("VirtualWall");
    public List<String> virtualWallTitle = new ArrayList<String>(virtualWallItemData.keySet());
    public Map<String, List<String>> taskManageItemData = ExpandableListData.getData("TaskManage");
    public List<String> taskManageTitle = new ArrayList<String>(taskManageItemData.keySet());

    private Context mContext;
    private ProgressDialog mProgressDialog;
    ///*****Handler Class******

    private static Thread getDataThread1;
    private static Handler getDataHandler1;

    private final static int MESSAGECODE = 1;
    private static Handler getDataHandler, setManualVelHandler;

    public static String strServerIP = new String();
    public static NpuIceI mynpu = new NpuIceI();
    WizRoboNpuUdp udpSendIp = new WizRoboNpuUdp();
    private SlamMode slamMode = SlamMode.ICP_SLAM;

    ///*****Map Class*****
    static ImgMap imgMap = new ImgMap();
    public static MapInfo mapInfo = new MapInfo();
    MapInfo[] mapInfoList = null;
    Station[] stationList;
    ImgPath[] imgPathList = null;
    ImgVirtualWall[] imgVirtualWallList = null;
    public static ImgStation[] imgStationList = null;
    wizrobo_npu.Path[] pathList;
    Task[] taskList = null;

    static PixelMat pixelMat = new PixelMat();
    Pose3D[] pathPose = new Pose3D[100];        //Limited
    Pose3D[] stationPathPose = new Pose3D[100]; //Limited
    ImgPose[] imgPathPose = new ImgPose[100];   //Limited
    ImgPoint[] imgPathPoint = new ImgPoint[100];//Limited
    wizrobo_npu.Path path2D = new wizrobo_npu.Path();
    static float resolution = 0.07f, redarrowYaw = 0;
    static Pose3D pose3D_test = new Pose3D(0, 0, 0, 0, 0, 0);

    public static ImgPose actImgPose = new ImgPose(0, 0, 0);
    public static ImuData imuData = null;
    public static Pose3D actPose = new Pose3D(0, 0, 0, 0, 0, 0);
    public static Vel3D actVel = new Vel3D(0, 0, 0, 0, 0, 0);
    public static Vel3D cmdVel = new Vel3D(0, 0, 0, 0, 0, 0);
    public static MotorSpd cmdMotorSpd = new MotorSpd();
    public static MotorSpd actMotorSpd = new MotorSpd();
    public static MotorEnc actMotorEnc = null;
    public static NaviState naviState = NaviState.IDLE;
    public static wizrobo_npu.Path cmdPath = new wizrobo_npu.Path();
    public static ImgPath cmdImgPath = new ImgPath();
    public static LidarScan lidarScanData = new LidarScan();
    public static ImgLidarScan imglidarscandata = new ImgLidarScan();
    public static ImgPoint[] FootprintVerticles;
    public static ServerState serverState;

    public static InitPoseArea initPoseArea = new InitPoseArea(pose3D_test, 0, 0);
    public static InitImgPoseArea initImgPoseArea = new InitImgPoseArea(null, 0, 0);

    static NpuException npuException;

    boolean displayOnce = false, wifiIsConnected = false,
            pause = false, toChangeChildView = false, screenIsTouched = false,
            isPoseMode = false, isImgPoseMode = true, isSettingCoveragePath = false,
            actionMove = false, pointerdown = false, excuteUnfinishedTask = false, toExcuteUnfinishedTask = false,
            isReadThumbnail = false, isToDisplayInitPose = false, toStopGetImgMap = false, pointIsSelected = false,
            isGettingSensorstatus = false, toGetSensorData = false;

    public static boolean isJoystickTriggered = false, isTrack = false, isSlam = false, isNavi = false, isSettingStationPath = false,
            toModifyPathXY = false, toModifyPathYaw = false, toRunOnce = true, toConnectNpu = true, isSettingVirtualWall = false,
            isInited = false, toFollowAllpaths = false,
            followOnce = false, isTimeout = false, isSettingPathpose = false, isSettingFreePath = false, isSettingInitialPose = false, isSettingGoalPose = false, isSettingInitPoseArea = false,
            toGetImgMap = false, toGetParam = false, togetFileInfo = false, toGetSensorStatus = true, toReboot = false, toShutDown = false, toSendFile = false,
            toSetmapInfos = false, toGetMapInfos = false, mapInfosChanged = false, toCoveragePathPlanning = false;

    private static float mapEnlargeLevel = 1.0f, mapZoomScare = 1.0f, mapZoomScareTemp = 1.0f, canvasHeight, canvasWidth, initImgPoseU_float,
            initImgPoseV_float, setImgPoseU_float, setImgPoseV_float, rec_left, rec_top, rec_right, rec_bottom;

    private ArrayAdapter<String> adapterMapList, adapterPathStationList, adapterPathList, adapterVirtualWallList, adapterTaskList;
    public static List<String> listMapId, listPathStationId, listVirtualWallId, pathListId, taskListId;
    public static String mapname = "0", str_npu_version = "npu_v1.0";
    private static Matrix currentMatrix = new Matrix(), matrix = new Matrix();
    private PointF midPoint = new PointF(), startPoint = new PointF();
    private static Bitmap resizeBmp;

    Bitmap backgroundbmp_draw;
    Canvas canvas = new Canvas();
    Paint paint = new Paint();

    private MODE mode = MODE.NONE;
    private float startDis, mapZoomdx, mapZoomdy, mapDragdx, mapDragdy;
    private int coveragePathBordersNum, countTime = 0, progress = 0,
            actImgposeU, actImgposeV, initImgposeU, initImgposeV, setImgposeU, setImgposeV, robotInitPoseX, robotInitPoseY;


    private enum MODE {NONE, DRAG, ZOOM}

    ;
    private static int stationPathNum = 0, wifiStrength = 0, actionDownPointNum = 0, pathPoseNum = 0;
    ///pose class
    private float actPoseX, actPoseY, actPoseYaw, actImgposeTheta, initImgposeTheta, setImgposeTheta,
            initPoseX, initPoseY, initPoseYaw, setPoseX, setPoseY, setPoseYaw;
    public static float linScale = 0, angScale = 0, poseYaw;
    private SensorState[] mSensorStatus;
    PowerManager pm = null;
    WakeLock wl = null;
    NaviMode naviMode;

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
                case "SelectMap":
                    SelectMapNotice();
                    break;
                case "ChangeWifiUI":
                    ChangeWifiUI();
                    break;
                case "ChangePathStationUI":
                    ChangePathStationUI();
                    break;
                case "ChangePathUI":
                    ChangePathUI();
                    break;
                case "ChangeTaskUI":
                    ChangeTaskUI();
                    break;
                case "ChangeNaviUI":
                    ChangeNaviUI();
                    break;
                case "ChangeSlamUI":
                    ChangeSlamUI();
                    break;
                case "ChangeVirtualWallUI":
                    ChangeVirtualWallUI();
                    break;
                case "NpuException":
                    NpuExceptionAlert(npuException);
                    break;
                case "CheckSensorStatus":
                    DisPlaySensorStatus();
                    break;
                case "ChangeProgressBar":
                    ChangeProgressBar();
                    break;
                case "GetSensorStatus":
                    toGetSensorStatus = false;
                    GetSensorStatus();
                    break;
                case "SetMapInfos":
                    SetMapInfoSucceed();
                    break;
                case "GetMapInfos":
                    GetMapInfos();
                    break;
                case "GetServerVersion":
                    GetServerVersion();
                    break;

                default:
                    break;
            }

        }
    };


    Runnable updateUiRunnable = new Runnable() {

        public void run() {
            UpdateRobotStatus();
            serverState = mynpu.serverState;
            if (togetFileInfo) {
                SendMessageToUI("GetFile");
            }

            if (toGetSensorStatus) {
                SendMessageToUI("GetSensorStatus");
            }

            if (toReboot) {
                SendMessageToUI("Reboot");
            }

            if (toShutDown) {
                SendMessageToUI("ShutDown");
            }

            if (toSendFile) {
                SendMessageToUI("SendFile");
            }

            if (!wifiIsConnected) {
                getDataHandler1.removeCallbacksAndMessages(null);
                getDataHandler.removeCallbacksAndMessages(null);
                setManualVelHandler.removeCallbacksAndMessages(null);
                updateUiHandler.removeCallbacksAndMessages(null);
                ConnectFailed();
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

            UpdateCmdVel();
            UpdateActVel();
            UpdateCmdMotorSpd();
            UpdateActMotorSpd();
            UpdateActEnc();
            UpdateActPose();
            UpdateImudata();

            if (isNavi || isSlam) {
                try {
                    toGetSensorData = false;
                    if (mynpu.isInited) {
                        Display();
                        UpdateCmdVel();
                        UpdateActVel();
                        UpdateCmdMotorSpd();
                        UpdateActMotorSpd();
                        UpdateActEnc();
                        UpdateActPose();
                        UpdateImudata();
                        if (toChangeChildView) {
                            toChangeChildView = false;
                            adapter_operate.getChildView(0, 1, true, tempView, tempParent);
                        }
                    }
                } catch (Exception e) {
                    ExceptionAlert(e);
                }
            }

            if (toGetSensorData && !isNavi && !isSlam) {
                UpdateActEnc();
                DisplayLidar();

            }

            if (!mynpu.isInited && !displayOnce && wifiIsConnected) {
                countTime++;
                if (countTime == 8) {
                    displayOnce = true;
                    ConnectFailed();
                }
            }

            if (isSlam || isNavi)
                updateUiHandler.postDelayed(updateUiRunnable, 200);
            else
                updateUiHandler.postDelayed(updateUiRunnable, 1000);
        }
    };

    //读写权限
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    //定点导航
    private Button btn2Dingdian, btnNextPose, btnCloseNpu;
    public static int listCount = 0, dingDianlistItem = 0;
    public static boolean isDingdianPlaying = false, isNextPlay = false;
    private Thread dingDianPlayThred, dingDianPlayNpuStateThread;

    private LinearLayout linearLayoutPlay, linearLayoutBtnTest;

    public static NaviState naviStateDingdianPlay = NaviState.IDLE;
    public static NaviState lastNaviState = NaviState.IDLE;

    public static ImgPose currentLXImgPose = new ImgPose(0, 0, 0);

    private static int playcount = 0;


    //图片轮播
    private static List<String> listPicPaths = new ArrayList<>();
    private RollPagerView rollPagerViewDingdianPlay;
    private final String PICPATHS = "/mnt/sdcard/tuPian";
    private Button btnPlayStop, btnPlayNext, btnFinishMusic, btnFinishSong;
    private FileUtil fileUtil = new FileUtil();
    private ImageLoopAdapter imageLoopAdapter;

    //音乐播放

    public static boolean isMusicPlayOnce = false, isMusicPlaying = false;
    private final String MUSICPATH = "/mnt/sdcard/tuPian/";
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private int countId = 1;

    public static final int GOING_POSE = 1;
    public static final int GO_POSE_SUCCESS = 2;
    public static final int MUSICPLAYOVER = 3;
    public static final int TEST = 4;





    private TextView tvTestPlayStatus, tvTestMusicStatus, tvTextNaviStatus, tvtextPlaycount;


    /**
     * Called when the activity is first created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        verifyStoragePermissions(WizRoboNpu.this);



        initLayout();

        //权限判断，如果没有权限就请求权限
        if (ContextCompat.checkSelfPermission(WizRoboNpu.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(WizRoboNpu.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
//            initMediaPlayer();//初始化播放器 MediaPlayer

            setMusicMediaPlayer(countId);
        }

//        receiver = new MusicBroadCastReceiver();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction("com.complete");
//        registerReceiver(receiver, filter);


        // get mapName
        try {
            imgStationList = mynpu.GetImgStations(mapname);
        } catch (NpuException e) {
            e.printStackTrace();
        }

        setWifiDormancy();
        setWifiNeverSleep();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        wl.setReferenceCounted(false);
        wl.acquire();
        InitialUI();
        joystickView = (JoystickView) findViewById(R.id.joystickLeft);
        pg_my_progressbar.setProgress(0);
        pg_my_progressbar.setVisibility(View.INVISIBLE);

        getWindowManager().getDefaultDisplay().getMetrics(dm);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        FloatWindowManager.getInstance().askPermission(this);

        //创建Handler
        getDataHandler = new GetDataHandler(this);
        //创建线程并且启动线程
        new Thread(new GetDataRunnable()).start();
        //创建Handler
        setManualVelHandler = new SetManualVelHandler(this);
        //创建线程并且启动线程
        new Thread(new SetManualVelRunnable()).start();

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

        getDataThread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        CheckWifiStatus();
                        if (toConnectNpu)
                            ConnectNPU();
                        if (toSetmapInfos) {
                            toSetmapInfos = false;
                            mynpu.SetMapInfos(mapInfoList);
                            SendMessageToUI("SetMapInfos");
                        }

                        if (toGetMapInfos) {
                            toGetMapInfos = false;
                            mapInfoList = mynpu.GetMapInfos();
                            SendMessageToUI("GetMapInfos");
                        }

                        if (isInited && toGetSensorData && !isNavi && !isSlam) {
                            lidarScanData = mynpu.GetLidarScan();
                            actMotorEnc = mynpu.GetMotorEnc();
                        }

                        if (isNavi)
                            Thread.currentThread().sleep(100);
                        else
                            Thread.currentThread().sleep(500);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                    }
                }
            }
        });

        getDataThread1.start(); /* 启动线程 */


        iv_background.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // showBig(v);
                float rel = 0;
                if (!toGetSensorData) {
                    if (mapInfo == null || pixelMat == null || mapInfo.offset == null)
                        return false;
                    rel = mapInfo.resolution;
                }
                if (mynpu.isInited) {
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN: //Click select map
                            actionDownPointNum += 1;
                            screenIsTouched = true;
                            mode = MODE.DRAG;
                            startPoint.set(event.getX(), event.getY());
                            currentMatrix.set(iv_background.getImageMatrix());
                            if (!toGetSensorData) {
                                float x = (float) (((event.getX() + mapZoomdx) / mapZoomScare - mapDragdx) / mapEnlargeLevel * rel / pixelMat.ratio + mapInfo.offset.x);
                                initPoseX = x;
                                setPoseX = x;
                                float y = (float) ((pixelMat.height - ((event.getY() + mapZoomdy) / mapZoomScare - mapDragdy) / mapEnlargeLevel) * rel / pixelMat.ratio + mapInfo.offset.y);
                                initPoseY = y;
                                setPoseY = y;
                                int imgposeu = (int) (((event.getX() + mapZoomdx) / mapZoomScare - mapDragdx) / mapEnlargeLevel);
                                initImgPoseU_float = ((event.getX() + mapZoomdx) / mapZoomScare - mapDragdx) / mapEnlargeLevel;   // improve displaying precision
                                setImgPoseU_float = initImgPoseU_float;  // improve displaying precision
                                initImgposeU = imgposeu;
                                setImgposeU = imgposeu;
                                int imgposev = (int) (((event.getY() + mapZoomdy) / mapZoomScare - mapDragdy) / mapEnlargeLevel);
                                initImgPoseV_float = ((event.getY() + mapZoomdy) / mapZoomScare - mapDragdy) / mapEnlargeLevel;
                                setImgPoseV_float = initImgPoseV_float;
                                initImgposeV = imgposev;
                                setImgposeV = imgposev;
                            }

                            if (isSettingPathpose || isSettingFreePath || toCoveragePathPlanning || isSettingVirtualWall) {
                                float yaw = poseYaw;
                                Pose3D newpose = new Pose3D(setPoseX, setPoseY, 0, 0, 0, yaw);
                                pathPose[pathPoseNum] = newpose;
                                ImgPose newimgpose = new ImgPose(setImgposeU, setImgposeV, yaw);
                                imgPathPose[pathPoseNum] = newimgpose;
                                ImgPoint newImgPoint = new ImgPoint(setImgposeU, setImgposeV);
                                imgPathPoint[pathPoseNum] = newImgPoint;
                                pathPoseNum++;
                                newpose = null;
                                newimgpose = null;
                                newImgPoint = null;
                            }
                            break;

                        default:
                            break;
                        case MotionEvent.ACTION_POINTER_DOWN:
                            pointerdown = true;
                            actionDownPointNum++;
                            if (actionDownPointNum >= 3) {
                                if (!isSettingInitialPose && !isSettingGoalPose && !isSettingPathpose && !isSettingFreePath && !isSettingCoveragePath) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "请用两个手指进行缩放！", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }
                            }

                            if (actionDownPointNum >= 2) {
                                if (isSettingInitialPose || isSettingGoalPose || isSettingPathpose || isSettingFreePath || isSettingCoveragePath) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "设置状态下不能平移/缩放！", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }
                            }

                            if (!isSettingInitialPose && !isSettingGoalPose && !isSettingPathpose && !isSettingFreePath && !isSettingCoveragePath && actionDownPointNum == 2) {
                                mode = MODE.ZOOM;
                                actionMove = false;
                                startDis = Distance(event);
                                if (startDis > 1f) {
                                    midPoint = MidPoint(event);
                                }
                            }
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (mode == MODE.DRAG && actionDownPointNum <= 2) {
                                if (isSettingInitialPose || isSettingGoalPose || isSettingPathpose || isSettingFreePath || isSettingCoveragePath || isSettingVirtualWall || isSettingInitPoseArea) {
                                    actionMove = false;
                                    if (isSettingInitialPose) {
                                        double poseyaw = Math.atan2(startPoint.y - event.getY(), event.getX() - startPoint.x);   //返回的就是弧度制 不需要再转换
                                        poseYaw = (float) poseyaw;
                                        initPoseYaw = (float) poseyaw;
                                        initImgposeTheta = (float) poseyaw;
                                        redarrowYaw = initImgposeTheta;
                                    } else if (isSettingGoalPose) {
                                        double goalyaw = Math.atan2(startPoint.y - event.getY(), event.getX() - startPoint.x);
                                        poseYaw = (float) goalyaw;
                                        setPoseYaw = (float) goalyaw;
                                        setImgposeTheta = (float) goalyaw;
                                        redarrowYaw = setImgposeTheta;
                                        if (isSettingFreePath) {
                                            imgPathPose[pathPoseNum - 1].theta = poseYaw;
                                        }
                                    } else if (isSettingPathpose || isSettingFreePath) {
                                        double goalyaw = Math.atan2(startPoint.y - event.getY(), event.getX() - startPoint.x);
                                        poseYaw = (float) goalyaw;
                                        if (pathPoseNum == 0)
                                            return false;
                                        imgPathPose[pathPoseNum - 1].theta = poseYaw;
                                        redarrowYaw = poseYaw;
                                    } else if (isSettingInitPoseArea) {
                                        float left, top, right, bottom;
                                        right = ((event.getX() + mapZoomdx) / mapZoomScare - mapDragdx) / mapEnlargeLevel;
                                        top = ((event.getY() + mapZoomdy) / mapZoomScare - mapDragdy) / mapEnlargeLevel;
                                        left = ((startPoint.x + mapZoomdx) / mapZoomScare - mapDragdx) / mapEnlargeLevel;
                                        bottom = ((startPoint.y + mapZoomdy) / mapZoomScare - mapDragdy) / mapEnlargeLevel;


                                        initImgPoseArea.pose = new ImgPose((int) (left + right) / 2, (int) (top + bottom) / 2, 0);
                                        initImgPoseArea.width = (int) Math.abs(left - right);
                                        initImgPoseArea.height = (int) Math.abs(top - bottom);

                                        rec_left = left;
                                        rec_bottom = bottom;
                                        rec_right = right;
                                        rec_top = top;

                                        right = (float) (((event.getX() + mapZoomdx) / mapZoomScare - mapDragdx) / mapEnlargeLevel * rel / pixelMat.ratio + mapInfo.offset.x);
                                        top = (float) ((pixelMat.height - ((event.getY() + mapZoomdy) / mapZoomScare - mapDragdy) / mapEnlargeLevel) * rel / pixelMat.ratio + mapInfo.offset.y);
                                        left = (float) (((startPoint.x + mapZoomdx) / mapZoomScare - mapDragdx) / mapEnlargeLevel * rel / pixelMat.ratio + mapInfo.offset.x);
                                        bottom = (float) ((pixelMat.height - ((startPoint.y + mapZoomdy) / mapZoomScare - mapDragdy) / mapEnlargeLevel) * rel / pixelMat.ratio + mapInfo.offset.y);
                                        Pose3D pose3D = new Pose3D();

                                        if (right < left) {
                                            float temp = left;
                                            left = right;
                                            right = temp;
                                        }

                                        if (top < bottom) {
                                            float temp = bottom;
                                            bottom = top;
                                            top = temp;
                                        }

                                        pose3D.x = left;
                                        pose3D.y = bottom;
                                        InitPoseArea poseArea = new InitPoseArea();
                                        poseArea.pose = pose3D;
                                        //initPoseArea.pose.y = startPoint.y;
                                        poseArea.height = top - bottom;
                                        poseArea.width = right - left;
                                        initPoseArea = poseArea;
                                    } else {
                                    }
                                } else {

                                    actionMove = true;
                                    float dxtmp = event.getX() - startPoint.x;
                                    float dytmp = event.getY() - startPoint.y;
                                    matrix.set(currentMatrix);
                                    matrix.postTranslate(dxtmp, dytmp);
                                    iv_background.setImageMatrix(matrix);

                                }
                            } else if (mode == MODE.ZOOM && actionDownPointNum == 2) {
                                actionMove = false;
                                float endDis = Distance(event);
                                if (endDis > 1f) {
                                    float scale = endDis / startDis;
                                    float scaretmppre = mapZoomScareTemp;

                                    mapZoomScareTemp = scale;
                                    mapZoomScareTemp = mapZoomScareTemp * mapZoomScare;
                                    if (mapZoomScareTemp < 1) {
                                        mapZoomScareTemp = scaretmppre;
                                        break;
                                    }
                                    matrix.set(currentMatrix);
                                    matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                                    iv_background.setImageMatrix(matrix);

                                }
                            }
                            break;
                        case MotionEvent.ACTION_POINTER_UP:
                            actionDownPointNum--;
                            break;
                        case MotionEvent.ACTION_UP:
                            pointerdown = false;
                            if (mode == MODE.DRAG && actionMove == true && actionDownPointNum == 1) {
                                actionMove = false;
                                mapDragdx = mapDragdx + (event.getX() - startPoint.x) / mapZoomScareTemp;
                                mapDragdy = mapDragdy + (event.getY() - startPoint.y) / mapZoomScareTemp;
                                mode = MODE.NONE;
                            }

                            if (mode == MODE.ZOOM && actionDownPointNum <= 2) {
                                mapZoomScare = mapZoomScareTemp;
                                mapZoomdx = (mapZoomScare - 1) * midPoint.x;
                                mapZoomdy = (mapZoomScare - 1) * midPoint.y;
                                mode = MODE.NONE;
                            }
                            if (actionDownPointNum > 2)
                                actionDownPointNum--;

                            else {
                                actionDownPointNum = 0;
                            }
                            break;
                    }
                }
                Delay(20);
                return false;
            }

        });


        iv_background.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {

                if (isNavi && !pointerdown)  //Standard Navi Mode
                {
                    if (isSettingInitialPose) {
                        strSetInitialPose = "设置初始位";
                        isSettingInitialPose = false;
                        adapter_setting.getChildView(0, 0, true, tempView, tempParent);
                        SettingCancel();
                    }

                    if (isSettingGoalPose) {
                        strSetGoalPose = "设置目标点";
                        isSettingGoalPose = false;
                        adapter_setting.getChildView(0, 1, true, tempView, tempParent);
                        SettingCancel();
                    }

                    if (isSettingFreePath) {
                        strSetFreePath = "设置自由路径";
                        isSettingFreePath = false;
                        adapter_setting.getChildView(0, 3, true, tempView, tempParent);
                        SettingCancel();
                        pathPoseNum = 0;
                    }

                    if (isSettingPathpose) {
                        strSavePath = "添加路径";
                        isSettingPathpose = false;
                        adapter_operate.getChildView(0, 4, false, tempView, tempParent);
                        SettingCancel();
                        pathPoseNum = 0;
                    }

                    if (toCoveragePathPlanning) {
                        toCoveragePathPlanning = false;
                        strSetCoveragePath = "设置清扫区域";
                        adapter_setting.getChildView(0, 2, true, tempView, tempParent);
                        isSettingCoveragePath = false;
                        strAddCoverageArea = "添加清扫区域";
                        adapter_operate.getChildView(0, 6, true, tempView, tempParent);
                        SettingCancel();
                        pathPoseNum = 0;
                    }
                }

                if (isTrack && !pointerdown)      //AGVS Mode
                {
                    pathPoseNum = 0;
                    if (toModifyPathXY || toModifyPathYaw) {
                        for (int i = 0; i < imgPathList.length; i++) {
                            for (int j = 0; j < imgPathList[i].poses.length; j++) {
                                if (Math.abs(setImgposeU - imgPathList[i].poses[j].u) < 10 && Math.abs(setImgposeV - imgPathList[i].poses[j].v) < 10) {
                                    pointIsSelected = true;
                                    String temp = "";
                                    if (toModifyPathXY)
                                        temp = "点选中成功，请点击摇杆上下左右方向进行调节";
                                    if (toModifyPathYaw)
                                        temp = "点选中成功，请推动摇杆调节方向角";
                                    Toast toast = Toast.makeText(getApplicationContext(), temp, Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }
                            }
                        }
                    }
                }


                if (isSettingVirtualWall) {
                    isSettingVirtualWall = false;
                    strSaveVirtualWall = "添加虚拟墙";
                    adapter_virtual_wall.getChildView(0, 0, false, tempView, tempParent);
                    SettingCancel();
                }

                if (!mynpu.isInited) {
                    Toast toast = Toast.makeText(getApplicationContext(), "正在连接NPU...", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    countTime = 0;
                    displayOnce = false;
                    toConnectNpu = true;
                }
                return false;
            }
        });

        sp_map_list.setOnTouchListener(new Spinner.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN: //Click select map
                        if (mynpu.isInited) {
                            try {
                                toGetSensorData = false;
                                if (mapInfoList == null || mapInfosChanged == true) {
                                    mapInfosChanged = false;
                                    listMapId.clear();
                                    toGetMapInfos = true;
                                } else {
                                    listMapId.clear();
                                    GetMapInfos();
                                }
                            } catch (Exception e) {
                            }
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });


        sp_map_list.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                if (mynpu.isInited) {
                    if (mapInfoList == null)
                        return;
                    Spinner spinner = (Spinner) parent;
                    mapname = spinner.getSelectedItem().toString();
                    ReadThumbnail();                  //选择地图时显示缩略图
                    Display();
                    adapterMapList.notifyDataSetChanged();
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sp_path_station_list.setOnTouchListener(new Spinner.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN: //Click select map
                        GetPathStationList();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        sp_path_list.setOnTouchListener(new Spinner.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN: //Click select map
                        GetPathList();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        sp_virtual_wall_list.setOnTouchListener(new Spinner.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN: //Click select map
                        GetVirtualWallList();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });


        sp_task_list.setOnTouchListener(new Spinner.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN: //Click select map
                        try {
                            GetTaskList();
                            if (taskList == null || taskList.length == 0)
                                return false;
                        } catch (Exception e) {
                            NormalException(e);
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        sp_task_list.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                if (WizRoboNpu.isInited) {
                    if (taskList == null || taskList.length == 0)
                        return;
                    adapterTaskList.notifyDataSetChanged();
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // Get the iv_background width and height to use it calculate the scale
        ViewTreeObserver vto = iv_background.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                iv_background.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                canvasWidth = iv_background.getWidth();
                canvasHeight = iv_background.getHeight();
            }
        });
        Resources res = getResources();

        //收缩
        elv_operate.setOnGroupCollapseListener(new OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                setListViewHeightBasedOnChildren(elv_operate);
            }
        });
        //伸展
        elv_operate.setOnGroupExpandListener(new OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                for (int i = 0, count = elv_operate.getExpandableListAdapter().getGroupCount(); i < count; i++) {
                    if (groupPosition != i) {// 关闭其他分组
                        elv_operate.collapseGroup(i);
                    }
                }
                CollapseGroup(elv_virtual_wall);
                CollapseGroup(elv_setting);
                CollapseGroup(elv_function);
                CollapseGroup(elv_task_manage);
                setListViewHeightBasedOnChildren(elv_operate);
            }
        });
        //子条目点击
        elv_operate.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                try {


                    if (isNavi) {
                        if (groupPosition == 0 && childPosition == 0) {
                            SaveStation();
                        }

                        if (groupPosition == 0 && childPosition == 1) {
                            DeleteStation();
                        }

                        if (groupPosition == 0 && childPosition == 2) {

                            tempParent = parent;
                            tempView = v;
                            SavePath();
                            adapter_operate.getChildView(0, 2, true, v, parent);
                        }

                        if (groupPosition == 0 && childPosition == 3) {

                            tempParent = parent;
                            tempView = v;
                            DeletePath();
                            adapter_operate.getChildView(0, 3, true, v, parent);
                        }

                        if (groupPosition == 0 && childPosition == 4) {
                            SetStationPath();
                        }

                        if (groupPosition == 0 && childPosition == 5) {
                            SaveStationPath();
                        }


                        if (groupPosition == 0 && childPosition == 6) {

                            tempParent = parent;
                            tempView = v;
                            AddCoverageArea();
                            adapter_operate.getChildView(0, 6, true, v, parent);
                        }


                        if (groupPosition == 1 && childPosition == 0) {

                            GotoStation();
                        }

                        if (groupPosition == 1 && childPosition == 1) {

                            TrackPath();
                        }


                    }


                } catch (Exception e) {
                    NormalException(e);
                }
                return false;
            }
        });


        //收缩
        elv_setting.setOnGroupCollapseListener(new OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                setListViewHeightBasedOnChildren(elv_setting);
            }
        });
        //伸展
        elv_setting.setOnGroupExpandListener(new OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

                CollapseGroup(elv_virtual_wall);
                CollapseGroup(elv_operate);
                CollapseGroup(elv_function);
                CollapseGroup(elv_task_manage);
                setListViewHeightBasedOnChildren(elv_setting);
            }
        });


        elv_setting.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                if (isNavi) {
                    if (groupPosition == 0 && childPosition == 0) {
                        if (!isSettingGoalPose) {
                            SetInitPose();
                            tempParent = parent;
                            tempView = v;
                            adapter_setting.getChildView(0, 0, true, v, parent);
                        }
                    }

                    if (groupPosition == 0 && childPosition == 1) {
                        if (!isSettingInitialPose) {
                            SetGoalPose();
                            tempParent = parent;
                            tempView = v;
                            adapter_setting.getChildView(0, 1, true, v, parent);
                        }
                    }

                    if (groupPosition == 0 && childPosition == 2) {
                        if (!isSettingInitialPose && !isSettingGoalPose) {
                            SetInitPoseArea();
                            tempParent = parent;
                            tempView = v;
                            adapter_setting.getChildView(0, 2, true, v, parent);
                        }
                    }


                    if (groupPosition == 0 && childPosition == 3) {
                        if (!isSettingInitialPose && !toCoveragePathPlanning) {
                            tempParent = parent;
                            tempView = v;
                            SetFreePath();
                        }
                    }

                    if (groupPosition == 0 && childPosition == 4) {
                        if (!isSettingInitialPose && !isSettingFreePath) {
                            tempParent = parent;
                            tempView = v;
                            SetCoveragePath();
                        }
                    }
                }
                return false;
            }
        });

        elv_function.setOnGroupCollapseListener(new OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                setListViewHeightBasedOnChildren(elv_function);
            }
        });
        //伸展
        elv_function.setOnGroupExpandListener(new OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                setListViewHeightBasedOnChildren(elv_function);
                CollapseGroup(elv_virtual_wall);
                CollapseGroup(elv_setting);
                CollapseGroup(elv_operate);
                CollapseGroup(elv_task_manage);
            }
        });

        elv_function.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {


                if (groupPosition == 0 && childPosition == 0 && !isSlam) {
                    if (isInited) {
                        TrackNavi();
                    }
                }

                if (groupPosition == 0 && childPosition == 1 && !isNavi && !isTrack) {
                    if (isInited) {
                        SlamMap();
                    }
                }

                return false;
            }
        });


        elv_virtual_wall.setOnGroupCollapseListener(new OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                setListViewHeightBasedOnChildren(elv_virtual_wall);
            }
        });
        //伸展
        elv_virtual_wall.setOnGroupExpandListener(new OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                setListViewHeightBasedOnChildren(elv_virtual_wall);
                CollapseGroup(elv_operate);
                CollapseGroup(elv_setting);
                CollapseGroup(elv_function);
                CollapseGroup(elv_task_manage);
            }
        });

        elv_virtual_wall.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                if (groupPosition == 0 && childPosition == 0) {
                    SaveVirtualWall();
                    tempParent = parent;
                    tempView = v;
                    adapter_virtual_wall.getChildView(0, 0, true, v, parent);
                }

                if (groupPosition == 0 && childPosition == 1) {
                    DeleteVirtualWall();
                    tempParent = parent;
                    tempView = v;
                    adapter_virtual_wall.getChildView(0, 1, true, v, parent);
                }

                return false;
            }
        });

        elv_task_manage.setOnGroupCollapseListener(new OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                setListViewHeightBasedOnChildren(elv_task_manage);
            }
        });
        //伸展
        elv_task_manage.setOnGroupExpandListener(new OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                setListViewHeightBasedOnChildren(elv_task_manage);
                CollapseGroup(elv_virtual_wall);
                CollapseGroup(elv_setting);
                CollapseGroup(elv_function);
                CollapseGroup(elv_operate);
            }
        });
        elv_task_manage.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                if (groupPosition == 0 && childPosition == 0) {
                    ExcuteTask();
                }


                if (groupPosition == 0 && childPosition == 1) {

                    Intent intent = new Intent(WizRoboNpu.this, SetTask.class);
                    startActivity(intent);

                }

                return false;
            }
        });


        cb_cruise_control.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                try {
                    // TODO Auto-generated method stub
                    if (isChecked) {
                        Toast toast = Toast.makeText(getApplicationContext(), "开始定速巡航，可控制遥控取消巡航！", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else {
                        if (mynpu.isInited)
                            mynpu.SetManualVel(0, 0);
                    }
                } catch (NpuException e) {
                    npuException = e;
                    SendMessageToUI("NpuException");
                } catch (Exception e) {

                }
            }
        });

        Toast toast = Toast.makeText(getApplicationContext(), "正在连接NPU...", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnDingdianPlay:
                linearLayoutPlay.setVisibility(View.VISIBLE);
                dingDianPlay();
                isMusicPlayOnce = false;
                break;

            case R.id.btnNextPose:
//                if (isNextPlay == false) {
//                    isNextPlay = true;
//                    new Thread(dingDianplayRunnable2).start();
//                }
                gotoNextPose();
                break;


            case R.id.btnFinishMusic:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.reset();
                }
                break;

            case R.id.btnPlayStop:
                if (isDingdianPlaying == true) {
                    stopGoPose();
                    isDingdianPlaying = false;
                    linearLayoutPlay.setVisibility(View.GONE);

                } else {
                    Log.e("btn", "isDingdianPlaying = false");
                }
                break;

            case R.id.btnPlayNext:
                if (isDingdianPlaying == true) {
                    gotoNextPose();
                    isMusicPlayOnce = false;
                } else {
                    Log.e("btn", "isDingdianPlaying = false");
                }

                break;

            case R.id.btnFinishSong:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.reset();
                }
                break;

            case R.id.btnCloseNpu:
                try {
//                    tvTextNaviStatus.setText("GetNpuState="+mynpu.GetNpuState());
                    if(mynpu.GetNpuState() != NpuState.IDLE_STATE){
                        Toast.makeText(WizRoboNpu.this,"请先退出导航或者扫图模式",Toast.LENGTH_SHORT).show();
                    }else{
                        mynpu.Shutdown();
                    }
                } catch (NpuException e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }
    }

    private void initLayout() {

        tvTestMusicStatus = (TextView) findViewById(R.id.textTestMusicStatus);
        tvTestPlayStatus = (TextView) findViewById(R.id.textTestplayStatus);
        tvTextNaviStatus = (TextView) findViewById(R.id.textTestnaviStateus);
        tvtextPlaycount = (TextView) findViewById(R.id.textTestplaycount);

        btn2Dingdian = (Button) findViewById(R.id.btnDingdianPlay);
        btn2Dingdian.setOnClickListener(this);

        btnNextPose = (Button) findViewById(R.id.btnNextPose);
        btnNextPose.setOnClickListener(this);

        btnFinishMusic = (Button) findViewById(R.id.btnFinishMusic);
        btnFinishMusic.setOnClickListener(this);

        btnPlayStop = (Button) findViewById(R.id.btnPlayStop);
        btnPlayStop.setOnClickListener(this);

        btnPlayNext = (Button) findViewById(R.id.btnPlayNext);
        btnPlayNext.setOnClickListener(this);

        btnFinishSong = (Button) findViewById(R.id.btnFinishSong);
        btnFinishSong.setOnClickListener(this);


        btnCloseNpu = (Button) findViewById(R.id.btnCloseNpu);
        btnCloseNpu.setOnClickListener(this);

        linearLayoutPlay = (LinearLayout) findViewById(R.id.linearlayoutPlay);
        linearLayoutBtnTest = (LinearLayout) findViewById(R.id.linearBtnTest);
    }


    @SuppressWarnings("Convert2Diamond")
    private void InitialUI() {

        ibt_delete_map = (ImageButton) findViewById(R.id.ibt_delete_map);
        ibt_delete_map.setVisibility(View.INVISIBLE);
        ibt_save_map = (ImageButton) findViewById(R.id.ibt_savemap);
        ibt_save_map.setVisibility(View.INVISIBLE);
        ibt_pause = (ImageButton) findViewById(R.id.ibt_pause);
        ibt_pause.setVisibility(View.INVISIBLE);
        ibt_cancel = (ImageButton) findViewById(R.id.ibt_cancel);
        ibt_cancel.setVisibility(View.INVISIBLE);

        // Add button listener

        ibt_delete_map.setOnClickListener(Ibt_DeleteMap_OnClick);
        ibt_save_map.setOnClickListener(Ibt_SaveMap_OnClick);

        ibt_pause.setOnClickListener(Ibt_Pause_OnClick);
        ibt_cancel.setOnClickListener(Ibt_Cancel_OnClick);


        cb_lidar_display = (CheckBox) findViewById(R.id.cb_lidar_display);
        cb_path_display = (CheckBox) findViewById(R.id.cb_path_display);
        cb_station_display = (CheckBox) findViewById(R.id.cb_station_display);
        cb_cruise_control = (CheckBox) findViewById(R.id.cb_cruise_control);
        cb_station_display.setVisibility(View.INVISIBLE);
        cb_lidar_display.setVisibility(View.INVISIBLE);
        cb_path_display.setVisibility(View.INVISIBLE);
        cb_cruise_control.setVisibility(View.INVISIBLE);


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
        tv_npu_version = (TextView) findViewById(R.id.tv_npu_version);
        tv_imu_data = (TextView) findViewById(R.id.tv_imu_data);

        //Add imageview
        iv_background = (ImageView) findViewById(R.id.iv_background);

        sp_map_list = (Spinner) findViewById(R.id.Spinnermaplist);
        listMapId = new ArrayList<String>();
        //adapterMapList = new ArrayAdapter<String>(this,R.layout.myspinner, R.id.textview, listMapId);
        adapterMapList = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listMapId);

        sp_map_list.setAdapter(adapterMapList);
        listMapId.add("地图列表");
        adapterMapList.notifyDataSetChanged();

        sp_path_station_list = (Spinner) findViewById(R.id.sp_station_list);
        listPathStationId = new ArrayList<String>();
        adapterPathStationList = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listPathStationId);
        sp_path_station_list.setAdapter(adapterPathStationList);


        sp_path_list = (Spinner) findViewById(R.id.sp_path_list);
        pathListId = new ArrayList<String>();
        pathListId.add("路径列表");
        adapterPathList = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pathListId);
        sp_path_list.setAdapter(adapterPathList);

        sp_virtual_wall_list = (Spinner) findViewById(R.id.sp_virtual_wall);
        listVirtualWallId = new ArrayList<String>();
        adapterVirtualWallList = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listVirtualWallId);
        sp_virtual_wall_list.setAdapter(adapterVirtualWallList);
        listVirtualWallId.add("虚拟墙列表");
        adapterVirtualWallList.notifyDataSetChanged();

        sp_task_list = (Spinner) findViewById(R.id.sp_task_list);
        taskListId = new ArrayList<String>();
        adapterTaskList = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, taskListId);
        sp_task_list.setAdapter(adapterTaskList);
        taskListId.add("任务列表");
        adapterTaskList.notifyDataSetChanged();

        sv_control = (ScrollView) findViewById(R.id.sv_control);
        sv_control.setVisibility(View.INVISIBLE);
        pg_my_progressbar = (TextProgressBar) findViewById(R.id.pg_my_progressbar);


        elv_operate = (CustomExpandableListView) findViewById(R.id.elv);
        adapter_operate = new ExpandableListAdapter(operateTitle, operateItemData, "Operate", this);
        elv_operate.setAdapter(adapter_operate);

        elv_setting = (ExpandableListView) findViewById(R.id.elv_setting);
        adapter_setting = new ExpandableListAdapter(settingTitle, settingItemData, "Setting", this);
        elv_setting.setAdapter(adapter_setting);

        elv_function = (ExpandableListView) findViewById(R.id.elv_function);
        adapter_function = new ExpandableListAdapter(functionTitle, functionItemData, "Function", this);
        elv_function.setAdapter(adapter_function);


        elv_virtual_wall = (ExpandableListView) findViewById(R.id.elv_virtual_wall);
        adapter_virtual_wall = new ExpandableListAdapter(virtualWallTitle, virtualWallItemData, "VirtualWall", this);
        elv_virtual_wall.setAdapter(adapter_virtual_wall);

        elv_task_manage = (ExpandableListView) findViewById(R.id.elv_task_manage);
        adapter_task_manage = new ExpandableListAdapter(taskManageTitle, taskManageItemData, "TaskManage", this);
        elv_task_manage.setAdapter(adapter_task_manage);
    }

    private static class GetDataHandler extends Handler {
        WeakReference<WizRoboNpu> weakReference;

        public GetDataHandler(WizRoboNpu activity) {
            weakReference = new WeakReference<WizRoboNpu>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (weakReference.get() != null) {
                // update android ui
            }
        }
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private static class GetDataRunnable implements Runnable {

        @Override
        public void run() {
            while (true) {
                getDataHandler.sendEmptyMessage(MESSAGECODE);
                try {
                    if (isNavi)
                        Thread.currentThread().sleep(400);
                    else
                        Thread.currentThread().sleep(1000);

                    try {
                        mynpu.CheckAbnormalInfo();
                    } catch (NpuException e) {
                        npuException = e;
                        System.out.println(e.msg);
                    }

                    if (toGetImgMap && !toGetParam) {
                        if (!isTimeout && mynpu.isInited) {
                            try {
                                imgMap = mynpu.GetCurrentImgMap();
                            } catch (Ice.ConnectionLostException e) {
                            }
                        }
                    }

                    cmdVel = mynpu.GetCmdVel();
                    actVel = mynpu.GetActVel();
                    cmdMotorSpd = mynpu.GetCmdMotorSpd();
                    actMotorSpd = mynpu.GetActMotorSpd();
                    actMotorEnc = mynpu.GetMotorEnc();
                    naviState = mynpu.GetNaviState();
                    actPose = mynpu.GetCurrentPose();
                    actImgPose = mynpu.GetCurrentImgPose();
                    FootprintVerticles = mynpu.GetFootprintImgVertices();
                    imglidarscandata = mynpu.GetImgLidarScan();

                    if (isNavi || isSlam) {
                        if (!isTimeout && !toGetParam) {
                            if (mynpu.isInited) {
                                try {
                                    imuData = mynpu.GetImuData();
                                    if (WizRoboNpu.isNavi) {
                                        cmdImgPath = mynpu.GetCmdImgPath();
                                    }
                                } catch (Ice.ConnectionLostException e) {
                                }
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                getDataHandler.sendEmptyMessage(MESSAGECODE);
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    private static class SetManualVelHandler extends Handler {
        WeakReference<WizRoboNpu> weakReference;

        public SetManualVelHandler(WizRoboNpu activity) {
            weakReference = new WeakReference<WizRoboNpu>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (weakReference.get() != null) {
                // update android ui
            }
        }
    }

    private static class SetManualVelRunnable implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.currentThread().sleep(100);
                    if (!isTimeout && !toGetParam) {
                        try {
                            int newTimeout = 30000;
                            NpuIcePrx newProxy = (NpuIcePrx) mynpu.npu.ice_timeout(newTimeout);
                            if (cb_cruise_control.isChecked()) {
                                newProxy.SetManualVel(0.99f, 0);
                            }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wl.release();
        restoreWifiDormancy();
        //如果参数为null的话，会将所有的Callbacks和Messages全部清除掉。

        updateUiHandler.removeCallbacksAndMessages(null);
        getDataHandler1.removeCallbacksAndMessages(null);
        getDataHandler.removeCallbacksAndMessages(null);
        setManualVelHandler.removeCallbacksAndMessages(null);

//        unregisterReceiver(receiver);

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setMusicMediaPlayer(countId);
                } else {
                    Toast.makeText(this, "拒绝权限，将无法使用程序。", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            default:
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {

        if (!isInited) {
            ConnectivityManager manager = (ConnectivityManager) this
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            NetworkInfo wifiNetworkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (wifiNetworkInfo.isConnected()) {
                String wserviceName = Context.WIFI_SERVICE;
                WifiManager wm = (WifiManager) getSystemService(wserviceName);
                WifiInfo info1 = wm.getConnectionInfo();
                wifiIsConnected = true;
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(WizRoboNpu.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
                builder.setTitle("wifi未连接");
                builder.setMessage("请点击确定键进行设置！");
                builder.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent();
                                if (Build.VERSION.SDK_INT >= 11) {
                                    //Honeycomb
                                    i.setClassName("com.android.settings", "com.android.settings.Settings$WifiSettingsActivity");
                                } else {
                                    //other versions
                                    i.setClassName("com.android.settings"
                                            , "com.android.settings.wifi.WifiSettings");
                                }
                                startActivity(i);
                                dialog.cancel();
                            }
                        });

                builder.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                builder.show();
            }
        }
        toGetParam = false;
        super.onStart();
    }

    private void SetInitPose() {
        try {
            if (isNavi) {
                if (!isSettingInitialPose && !isSettingGoalPose && !isSettingInitPoseArea) {
                    screenIsTouched = false;
                    isSettingInitialPose = true;
                    strSetInitialPose = "确定";
                    if (toCoveragePathPlanning) {
                        isSettingCoveragePath = false;
                    }
                    initImgposeU = 0;
                    initImgposeV = 0;
                    initImgposeTheta = 0;
                    redarrowYaw = 0;
                    initImgPoseU_float = 0;
                    initImgPoseV_float = 0;
                    pathPoseNum = 0;
                    isSettingFreePath = false;
                    SettingNotice();

                } else if (isSettingInitialPose && !isSettingGoalPose && !isSettingInitPoseArea) {
                    isSettingInitialPose = false;
                    strSetInitialPose = "设置初始位";
                    if (isPoseMode) {
                        Pose3D pose = new Pose3D(initPoseX, initPoseY, 0, 0, 0, initPoseYaw);
                        if (screenIsTouched)
                            mynpu.SetInitPose(pose);
                    }
                    if (isImgPoseMode) {
                        ImgPose pose = new ImgPose(initImgposeU, initImgposeV, initImgposeTheta);
                        if (screenIsTouched)
                            mynpu.SetInitImgPose(pose);
                    }
                }
            }
        } catch (NpuException e) {
            NpuExceptionAlert(e);
            return;
        }
    }

    private void SetGoalPose() {
        try {
            if (isNavi)  //Standard Navi Mode
            {
                if (!isSettingGoalPose && !isSettingInitialPose && !isSettingInitPoseArea) {
                    screenIsTouched = false;
                    isSettingGoalPose = true;
                    strSetGoalPose = "确定";
                    setImgposeU = 0;
                    setImgposeV = 0;
                    redarrowYaw = 0;

                    setImgposeTheta = 0;
                    setImgPoseU_float = 0;
                    setImgPoseV_float = 0;
                    SettingNotice();

                } else if (isSettingGoalPose && !isSettingInitialPose && !isSettingInitPoseArea) {
                    isSettingGoalPose = false;
                    strSetGoalPose = "设置目标点";
                    final ImgPose imgpose = new ImgPose(setImgposeU, setImgposeV, setImgposeTheta);
                    if (isPoseMode) {
                        Pose3D pose = new Pose3D(setPoseX, setPoseY, 0, 0, 0, setPoseYaw);
                        if (screenIsTouched)
                            mynpu.GotoGoal(pose);
                    }
                    if (isImgPoseMode) {
                        if (screenIsTouched) {
                            final AlertDialog dialog = new AlertDialog.Builder(WizRoboNpu.this)
                                    .setTitle("提示")
                                    // .setIcon(R.drawable.warming)
                                    .setMessage("是否让机器人移动到所设的目标点？")
                                    //.setMessage("不能包含@#￥%&*等特殊字符！")
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .setPositiveButton("确定", null)
                                    .setCancelable(true)
                                    .create();
                            dialog.show();

                            //为了避免点击 positive 按钮后直接关闭 dialog,把点击事件拿出来设置
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {

                                                                                                 public void onClick(View v) {
                                                                                                     ImgPose pose = imgpose;
                                                                                                     try {
                                                                                                         mynpu.GotoImgGoal(pose);
                                                                                                         dialog.dismiss();
                                                                                                     } catch (NpuException e) {
                                                                                                         NpuExceptionAlert(e);
                                                                                                         return;
                                                                                                     }
                                                                                                 }
                                                                                             }
                            );

                            //为了避免点击 positive 按钮后直接关闭 dialog,把点击事件拿出来设置
                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new OnClickListener() {

                                                                                                 @Override
                                                                                                 public void onClick(View v) {
                                                                                                     dialog.dismiss();
                                                                                                 }
                                                                                             }
                            );
                        }
                    }
                }
            }
        } catch (NpuException e) {
            NpuExceptionAlert(e);
            return;
        }
    }


    private void SetInitPoseArea() {
        try {
            if (isNavi) {
                if (!isSettingInitPoseArea && !isSettingGoalPose && !isSettingInitialPose) {
                    screenIsTouched = false;
                    isSettingInitPoseArea = true;
                    strSetInitPoseArea = "确定";
                    initPoseArea = null;

                } else if (isSettingInitPoseArea && !isSettingGoalPose && !isSettingInitialPose) {
                    isSettingInitPoseArea = false;
                    strSetInitPoseArea = "设置初始区域";
                    //mynpu.SetInitPoseArea(initPoseArea);
                    mynpu.SetInitImgPoseArea(initImgPoseArea);
                }
            }
        } catch (NpuException e) {
            NpuExceptionAlert(e);
            return;
        } catch (Exception e) {
            NormalException(e);
        }
    }

    private void SetFreePath() {
        try {
            if (!toFollowAllpaths && !followOnce)      //AGVS Mode
            {
                if (!isSettingFreePath && !isSettingInitialPose && !toCoveragePathPlanning) {
                    isSettingFreePath = true;
                    //isSettingGoalPose = true;
                    strSetFreePath = "确定";
                    setImgposeU = 0;
                    setImgposeV = 0;
                    setImgposeTheta = 0;
                    setImgPoseU_float = 0;
                    setImgPoseV_float = 0;
                    adapter_setting.getChildView(0, 3, true, tempView, tempParent);
                    SettingNotice();
                } else if (isSettingFreePath && !isSettingInitialPose && !toCoveragePathPlanning) {
                    //isSettingGoalPose = false;
                    isSettingFreePath = false;
                    strSetFreePath = "设置自由路径";
                    ImgPose[] newimgpose = new ImgPose[pathPoseNum];
                    for (int i = 0; i < pathPoseNum; i++) {
                        newimgpose[i] = imgPathPose[i];
                        System.out.println("imgpose_theta" + newimgpose[i].theta);
                    }
                    ImgPoint[] newPointList = new ImgPoint[pathPoseNum];
                    for (int i = 0; i < pathPoseNum; i++) {
                        newPointList[i] = imgPathPoint[i];
                    }

                    if (pathPoseNum == 0) {
                        adapter_setting.getChildView(0, 3, true, tempView, tempParent);
                        return;
                    }
                    mynpu.FollowTempImgPath(newimgpose);
                    pathPoseNum = 0;
                    adapter_setting.getChildView(0, 3, true, tempView, tempParent);
                    newimgpose = null;
                    newPointList = null;
                }
            }
        } catch (NpuException e) {
            NpuExceptionAlert(e);
            return;
        }
    }

    private void SetCoveragePath() {
        try {
            if (!toFollowAllpaths && !followOnce)      //AGVS Mode
            {
                if (!isSettingGoalPose && !isSettingInitialPose && !toCoveragePathPlanning) {
                    toCoveragePathPlanning = true;
                    isSettingGoalPose = true;
                    strSetCoveragePath = "确定";
                    setImgposeU = 0;
                    setImgposeV = 0;
                    setImgposeTheta = 0;
                    setImgPoseU_float = 0;
                    setImgPoseV_float = 0;
                    isSettingCoveragePath = false;
                    adapter_setting.getChildView(0, 4, true, tempView, tempParent);
                } else if (isSettingGoalPose && !isSettingInitialPose && toCoveragePathPlanning) {
                    isSettingGoalPose = false;
                    strSetCoveragePath = "设置清扫区域";
                    ImgPose[] newimgpose = new ImgPose[pathPoseNum];
                    for (int i = 0; i < pathPoseNum; i++) {
                        newimgpose[i] = imgPathPose[i];
                    }
                    ImgPoint[] newPointList = new ImgPoint[pathPoseNum];
                    for (int i = 0; i < pathPoseNum; i++) {
                        newPointList[i] = imgPathPoint[i];
                    }

                    if (pathPoseNum == 0) {
                        adapter_setting.getChildView(0, 4, true, tempView, tempParent);
                        return;
                    }

                    mynpu.PlanCoverageImgPath(newPointList);
                    isSettingCoveragePath = false;
                    coveragePathBordersNum = pathPoseNum;
                    pathPoseNum = 0;
                    toCoveragePathPlanning = false;
                    adapter_setting.getChildView(0, 4, true, tempView, tempParent);
                }
            }
        } catch (NpuException e) {
            NpuExceptionAlert(e);
        }
    }

    private void Navi() {
        if (mynpu.isInited) {
            try {
                if (!isNavi) {
                    try {
                        toGetSensorData = false;
                        imgMap = null;
                        if (mynpu.GetActionState() == ActionState.IDLE_ACTION) {
                            mapname = sp_map_list.getSelectedItem().toString();
                            if (mapname.equals("地图列表")) {
                                SendMessageToUI("SelectMap");
                                return;
                            }
                            mynpu.SelectMap(mapname);
                            mynpu.StartNavi(naviMode);
                        }
                        imgStationList = mynpu.GetImgStations(mapname);
                        imgVirtualWallList = mynpu.GetImgVirtualWalls(mapname);
                        imgPathList = mynpu.GetImgPaths(mapname);
                        taskList = mynpu.GetTaskList(mapname);
                        Delay(2000);
                        SendMessageToUI("ChangeNaviUI");
                        SendMessageToUI("ChangePathStationUI");
                        SendMessageToUI("ChangeVirtualWallUI");
                        SendMessageToUI("ChangePathUI");
                        SendMessageToUI("ChangeTaskUI");
                    } catch (NpuException e) {
                        npuException = e;
                        SendMessageToUI("NpuException");
                        return;
                    }

                } else {
                    mynpu.StopNavi();
                    System.out.println("stop navi");
                    Delay(2000);
                    SendMessageToUI("ChangeNaviUI");
                }
            } catch (Exception e) {
                ExceptionAlert(e);
            }


        }
    }

    private void TrackNavi() {
        if (!isNavi) {
            new AlertDialog.Builder(WizRoboNpu.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                    //.setTitle(R.string.str_warming)
                    //.setIcon(R.drawable.warming)
                    .setMessage("请选择导航模式！")
                    .setPositiveButton("           循迹导航",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialoginterface,
                                        int i) {
                                    if (isInited) {
                                        Toast toast = Toast.makeText(getApplicationContext(), "正在启动循迹导航...", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                        naviMode = NaviMode.PF_NAVI;
                                        Navi();
                                    }
                                }
                            })
                    .setNegativeButton("自由导航",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialoginterface,
                                        int i) {
                                    if (isInited) {
                                        Toast toast = Toast.makeText(getApplicationContext(), "正在启动自由导航...", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                        naviMode = NaviMode.P2P_NAVI;
                                        Navi();
                                    }

                                }
                            }).show();
        } else if (isNavi) {

            Navi();
        }
    }

    private void Slam() {
        try {
            if (mynpu.isInited) {
                if (!isSlam) {

                    toGetSensorData = false;
                    imgMap = null;
                    if (mynpu.GetActionState() == ActionState.SLAM_ACTION) {
                    } else {
                        try {
                            mynpu.StartSlam(slamMode);
                        } catch (Exception e) {
                            NormalException(e);
                            return;
                        }
                        Delay(500);
                    }
                    Delay(1000);
                    SendMessageToUI("ChangeSlamUI");
                } else {
                    SendMessageToUI("ChangeSlamUI");
                }
            }
        } catch (NpuException e) {
            NpuExceptionAlert(e);
        }
    }

    private void SlamMap() {

        if (!isSlam) {
            new AlertDialog.Builder(WizRoboNpu.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                    //.setTitle(R.string.str_warming)
                    //.setIcon(R.drawable.warming)
                    .setMessage("请选择建图模式！")
                    .setPositiveButton("图优化",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialoginterface,
                                        int i) {
                                    if (isInited) {
                                        slamMode = SlamMode.ICP_SLAM;
                                        Slam();
                                    }
                                }
                            })

                    .setNeutralButton("图优化Plus",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialoginterface,
                                        int i) {
                                    if (isInited) {
                                        slamMode = SlamMode.GH_SLAM;
                                        Slam();
                                    }
                                }
                            })
                    .setNegativeButton("粒子滤波",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialoginterface,
                                        int i) {
                                    if (isInited) {
                                        slamMode = SlamMode.PF_SLAM;
                                        Slam();
                                    }

                                }
                            }).show();
        } else {
            Slam();
        }
    }

    private void SavePath() {
        if (mynpu.isInited) {
            if (!isSettingPathpose && !isSettingStationPath) {
                isSettingPathpose = true;
                pathPoseNum = 0;
                setImgposeU = 0;
                setImgposeV = 0;
                redarrowYaw = 0;
                setImgposeTheta = 0;
                setImgPoseU_float = 0;
                setImgPoseV_float = 0;
                Toast toast = Toast.makeText(getApplicationContext(), "可通过遥控改变点的朝向角！", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                strSavePath = "保存";
            } else if (isSettingPathpose && !isSettingStationPath) {
                isSettingPathpose = false;
                strSavePath = "添加路径";
                if (pathPoseNum == 0)
                    return;
                final EditText et_pathnamex = new EditText(WizRoboNpu.this);
                final AlertDialog dialog = new AlertDialog.Builder(WizRoboNpu.this)
                        .setTitle("提示")
                        // .setIcon(R.drawable.warming)
                        .setMessage("请输入路径名称！")
                        //.setMessage("不能包含@#￥%&*等特殊字符！")
                        .setView(et_pathnamex)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", null)
                        .setCancelable(true)
                        .create();
                dialog.show();

                //为了避免点击 positive 按钮后直接关闭 dialog,把点击事件拿出来设置
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {

                                                                                     @Override
                                                                                     public void onClick(View v) {
                                                                                         try {
                                                                                             boolean a;
                                                                                             String name = et_pathnamex.getText().toString();
                                                                                             if (name == null || name.length() <= 0) {
                                                                                             } else {
                                                                                                 dialog.dismiss();
                                                                                             }

                                                                                             System.out.println("IS SAVING PATH");
                                                                                             pathListId.clear();
                                                                                             ImgPath[] newImgPathList = imgPathList;
                                                                                             List<ImgPath> imgPaths = new ArrayList<ImgPath>(0);

                                                                                             if (newImgPathList == null) {
                                                                                                 System.out.println("Is Adding");
                                                                                             } else {
                                                                                                 for (int i = 0; i < newImgPathList.length; i++) {
                                                                                                     imgPaths.add(newImgPathList[i]);
                                                                                                 }
                                                                                             }
                                                                                             PathInfo newpathinfo = new PathInfo();
                                                                                             ImgPose[] newimgpose = new ImgPose[pathPoseNum];
                                                                                             ImgPath newpath = new ImgPath();
                                                                                             newpathinfo.map_id = mapname;
                                                                                             newpathinfo.id = name;
                                                                                             newpathinfo.length = 0;
                                                                                             newpathinfo.pose_num = pathPoseNum;

                                                                                             for (int i = 0; i < pathPoseNum; i++) {
                                                                                                 newimgpose[i] = imgPathPose[i];
                                                                                                 System.out.println("imgpathpose:" + imgPathPose[i].theta);
                                                                                             }
                                                                                             newpath.info = newpathinfo;
                                                                                             newpath.poses = newimgpose;
                                                                                             imgPaths.add(newpath);
                                                                                             ImgPath[] newImgPathList1 = new ImgPath[imgPaths.size()];
                                                                                             for (int i = 0; i < imgPaths.size(); i++) {
                                                                                                 newImgPathList1[i] = imgPaths.get(i);
                                                                                             }
                                                                                             mynpu.SetImgPaths(mapname, newImgPathList1);

                                                                                             for (int i = 0; i < newImgPathList1.length; i++) {
                                                                                                 pathListId.add(newImgPathList1[i].info.id);
                                                                                             }


                                                                                             imgPathList = newImgPathList1;
                                                                                             adapterPathList.notifyDataSetChanged();
                                                                                             pathPoseNum = 0;

                                                                                             Toast toast = Toast.makeText(getApplicationContext(), "添加成功！", Toast.LENGTH_LONG);
                                                                                             toast.setGravity(Gravity.CENTER, 0, 0);
                                                                                             toast.show();
                                                                                         } catch (NpuException e) {
                                                                                             NpuExceptionAlert(e);
                                                                                             return;
                                                                                         }
                                                                                     }
                                                                                 }
                );

                //为了避免点击 positive 按钮后直接关闭 dialog,把点击事件拿出来设置
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new OnClickListener() {

                                                                                     @Override
                                                                                     public void onClick(View v) {
                                                                                         pathPoseNum = 0;
                                                                                         dialog.dismiss();

                                                                                     }
                                                                                 }
                );
            }
        }
    }

    private void SaveStation() {
        if (isInited) {
            final EditText et_stationnamex = new EditText(WizRoboNpu.this);
            final AlertDialog dialog = new AlertDialog.Builder(WizRoboNpu.this)
                    .setTitle("提示")
                    // .setIcon(R.drawable.warming)
                    .setMessage("请输入站点名称！")
                    //.setMessage("不能包含@#￥%&*等特殊字符！")
                    .setView(et_stationnamex)
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("确定", null)
                    .setCancelable(true)
                    .create();
            dialog.show();

            //为了避免点击 positive 按钮后直接关闭 dialog,把点击事件拿出来设置
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {

                                                                                 @Override
                                                                                 public void onClick(View v) {
                                                                                     try {
                                                                                         boolean a;
                                                                                         String name = et_stationnamex.getText().toString();
                                                                                         if (name == null || name.length() <= 0) {

                                                                                         } else {
                                                                                             dialog.dismiss();
                                                                                         }

                                                                                         listPathStationId.clear();
                                                                                         ImgStation[] newImgStationList = imgStationList;
                                                                                         List<ImgStation> imgStations = new ArrayList<ImgStation>(0);
                                                                                         if (newImgStationList == null) {
                                                                                         } else {
                                                                                             for (int i = 0; i < newImgStationList.length; i++) {
                                                                                                 imgStations.add(newImgStationList[i]);
                                                                                             }
                                                                                         }

                                                                                         StationInfo newstationinfo = new StationInfo();
                                                                                         ImgPose newimgpose = new ImgPose();
                                                                                         ImgStation newstation = new ImgStation();
                                                                                         newstationinfo.map_id = mapname;
                                                                                         newstationinfo.id = name;
                                                                                         newstationinfo.type = StationType.USER_DEFINED;
                                                                                         newstationinfo.artag_id = 0;
                                                                                         newimgpose = actImgPose;
                                                                                         newstation.info = newstationinfo;
                                                                                         newstation.pose = newimgpose;
                                                                                         imgStations.add(newstation);
                                                                                         ImgStation[] newImgStationList1 = new ImgStation[imgStations.size()];
                                                                                         for (int i = 0; i < imgStations.size(); i++) {
                                                                                             newImgStationList1[i] = imgStations.get(i);
                                                                                         }
                                                                                         mynpu.SetImgStations(mapname, newImgStationList1);

                                                                                         for (int i = 0; i < newImgStationList1.length; i++) {
                                                                                             listPathStationId.add(newImgStationList1[i].info.id);
                                                                                         }
                                                                                         imgStationList = newImgStationList1;
                                                                                         adapterPathStationList.notifyDataSetChanged();
                                                                                         Toast toast = Toast.makeText(getApplicationContext(), "添加成功！", Toast.LENGTH_LONG);
                                                                                         toast.setGravity(Gravity.CENTER, 0, 0);
                                                                                         toast.show();
                                                                                     } catch (NpuException e) {
                                                                                         npuException = e;
                                                                                         SendMessageToUI("NpuException");
                                                                                     }
                                                                                 }
                                                                             }
            );
        }
    }

    private OnClickListener Ibt_DeleteMap_OnClick = new OnClickListener() {
        public void onClick(View v) {
            if (mynpu.isInited) {
                if (mapInfoList == null || mapInfoList.length == 0)
                    return;
                new AlertDialog.Builder(WizRoboNpu.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                        .setTitle(R.string.str_warming)
                        //.setIcon(R.drawable.warming)
                        .setMessage("是否删除地图:" + listMapId.get(sp_map_list.getSelectedItemPosition()))

                        .setPositiveButton(R.string.str_ok,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialoginterface,
                                            int i) {
                                        if (isInited) {
                                            if (mapInfoList.length <= 1) {
                                                Toast toast = Toast.makeText(getApplicationContext(), "NPU内置地图，无法删除！", Toast.LENGTH_LONG);
                                                toast.setGravity(Gravity.CENTER, 0, 0);
                                                toast.show();
                                            } else {
                                                listMapId.clear();
                                                List<MapInfo> mapInfos = new ArrayList<MapInfo>(0);
                                                if (mapInfoList == null || mapInfoList.length == 0)
                                                    return;
                                                for (int j = 0; j < mapInfoList.length; j++) {
                                                    mapInfos.add(mapInfoList[j]);
                                                }

                                                mapInfos.remove(sp_map_list.getSelectedItemPosition());
                                                mapInfoList = new MapInfo[mapInfos.size()];
                                                for (int j = 0; j < mapInfos.size(); j++) {
                                                    mapInfoList[j] = mapInfos.get(j);
                                                }

                                                mapInfos = null;
                                                for (int j = 0; j < mapInfoList.length; j++) {
                                                    listMapId.add(mapInfoList[j].id);
                                                }
                                                adapterMapList.notifyDataSetChanged();

                                                Toast toast = Toast.makeText(getApplicationContext(), "正在删除地图 ...", Toast.LENGTH_SHORT);
                                                toast.setGravity(Gravity.CENTER, 0, 0);
                                                toast.show();
                                                toSetmapInfos = true;

                                            }

                                        }
                                    }
                                })
                        .setNegativeButton(R.string.str_no,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialoginterface,
                                            int i) {
                                    }
                                }).show();

            }
        }
    };

    private OnClickListener Ibt_SaveMap_OnClick = new OnClickListener() {
        public void onClick(View v) {
            if (mynpu.isInited) {
                final EditText et_mapnamex = new EditText(WizRoboNpu.this);
                final AlertDialog dialog = new AlertDialog.Builder(WizRoboNpu.this)
                        .setTitle(R.string.str_notice)
                        // .setIcon(R.drawable.warming)
                        .setMessage(R.string.str_notice_inputmapname)
                        //.setMessage("不能包含@#￥%&*等特殊字符！")
                        .setView(et_mapnamex)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", null)
                        .setCancelable(true)
                        .create();
                dialog.show();

                //为了避免点击 positive 按钮后直接关闭 dialog,把点击事件拿出来设置
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {

                                                                                     @Override
                                                                                     public void onClick(View v) {
                                                                                         try {
                                                                                             boolean a;
                                                                                             String name = et_mapnamex.getText().toString();
                                                                                             if (name == null || name.length() <= 0) {
                                                                                                 mynpu.SaveMapImg("");
                                                                                                 System.out.println(" the mapname is null");
                                                                                             } else {

                                                                                                 mynpu.SaveMapImg(name);
                                                                                                 System.out.println("save map");
                                                                                                 Toast toast = Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT);
                                                                                                 toast.setGravity(Gravity.CENTER, 0, 0);
                                                                                                 toast.show();
                                                                                             }


                                                                                             dialog.dismiss();

                                                                                         } catch (NpuException e) {
                                                                                             NpuExceptionAlert(e);
                                                                                         } catch (Exception e) {
                                                                                             NormalException(e);
                                                                                         }
                                                                                     }
                                                                                 }
                );

            }
        }
    };

    private OnClickListener Ibt_Pause_OnClick = new OnClickListener() {
        public void onClick(View v) {
            try {
                if (mynpu.isInited) {

                    if (!pause) {
                        ibt_pause.setImageResource(R.drawable.continue3);
                        mynpu.PauseTask();
                        Toast toast = Toast.makeText(getApplicationContext(), "已暂停! ", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        pause = true;
                    } else {
                        ibt_pause.setImageResource(R.drawable.stop2);
                        if (toExcuteUnfinishedTask) {
                            toExcuteUnfinishedTask = false;
                            mynpu.FollowPath(mapname, "unfinished_path");
                        } else
                            mynpu.ContinueTask();
                        Toast toast = Toast.makeText(getApplicationContext(), "继续执行任务中... ", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        pause = false;
                    }


                }
            } catch (NpuException e) {
                NpuExceptionAlert(e);
                return;
            }
        }

    };

    private OnClickListener Ibt_Cancel_OnClick = new OnClickListener() {
        public void onClick(View v) {
            try {
                if (mynpu.isInited) {
                    mynpu.CancelTask();

                    isDingdianPlaying = false;

                    Toast toast = Toast.makeText(getApplicationContext(), "已停止执行任务! ", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            } catch (NpuException e) {
                NpuExceptionAlert(e);
                return;
            }
        }
    };


    private void DeletePath() {
        if (isInited) {
            if (imgPathList == null || imgPathList.length == 0)
                return;
            new AlertDialog.Builder(WizRoboNpu.this)
                    .setTitle(R.string.str_warming)
                    // .setIcon(R.drawable.warming)
                    .setMessage("是否删除该路径")
                    .setPositiveButton(R.string.str_ok,
                            new DialogInterface.OnClickListener() {
                                @SuppressWarnings("UnusedAssignment")
                                public void onClick(
                                        DialogInterface dialoginterface,
                                        int i) {
                                    try {
                                        listPathStationId.clear();
                                        ImgPath[] newImgPathList = imgPathList;
                                        List<ImgPath> imgPaths = new ArrayList<ImgPath>(0);
                                        if (newImgPathList == null || newImgPathList.length == 0)
                                            return;
                                        for (int j = 0; j < newImgPathList.length; j++) {
                                            imgPaths.add(newImgPathList[j]);
                                        }
                                        imgPaths.remove(sp_path_station_list.getSelectedItemPosition());
                                        ImgPath[] newImgPathList1 = new ImgPath[imgPaths.size()];
                                        for (int j = 0; j < imgPaths.size(); j++) {
                                            newImgPathList1[j] = imgPaths.get(j);
                                        }
                                        mynpu.SetImgPaths(mapname, newImgPathList1);

                                        for (int j = 0; j < newImgPathList1.length; j++) {
                                            listPathStationId.add(newImgPathList1[j].info.id);
                                        }
                                        imgPathList = newImgPathList1;
                                        GetPathStationList();
                                        newImgPathList1 = null;
                                        imgPaths = null;
                                        Toast toast = Toast.makeText(getApplicationContext(), "删除完成！", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                    } catch (NpuException e) {
                                        NpuExceptionAlert(e);
                                        return;
                                    }
                                }
                            })
                    .setNegativeButton(R.string.str_no,
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialoginterface,
                                        int i) {
                                }
                            }).show();
        }
    }

    private void DeleteVirtualWall() {
        if (isNavi) {

            if (imgVirtualWallList == null || imgVirtualWallList.length == 0)
                return;
            new AlertDialog.Builder(WizRoboNpu.this)
                    .setTitle(R.string.str_warming)
                    // .setIcon(R.drawable.warming)
                    .setMessage("是否删除该虚拟墙")
                    .setPositiveButton(R.string.str_ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialoginterface,
                                        int i) {
                                    try {
                                        listVirtualWallId.clear();
                                        ImgVirtualWall[] newImgVirtualWallList = imgVirtualWallList;
                                        List<ImgVirtualWall> imgVirtualWalls = new ArrayList<ImgVirtualWall>(0);
                                        if (newImgVirtualWallList == null || newImgVirtualWallList.length == 0)
                                            return;
                                        for (int j = 0; j < newImgVirtualWallList.length; j++) {
                                            imgVirtualWalls.add(newImgVirtualWallList[j]);
                                        }
                                        imgVirtualWalls.remove(sp_virtual_wall_list.getSelectedItemPosition());
                                        ImgVirtualWall[] newImgVirtaulWall1 = new ImgVirtualWall[imgVirtualWalls.size()];
                                        for (int j = 0; j < imgVirtualWalls.size(); j++) {
                                            newImgVirtaulWall1[j] = imgVirtualWalls.get(j);
                                        }
                                        mynpu.SetImgVirtualWalls(mapname, newImgVirtaulWall1);

                                        for (int j = 0; j < newImgVirtaulWall1.length; j++) {
                                            listPathStationId.add(newImgVirtaulWall1[j].info.id);
                                        }
                                        imgVirtualWallList = newImgVirtaulWall1;
                                        GetVirtualWallList();
                                        newImgVirtaulWall1 = null;
                                        imgVirtualWalls = null;
                                        Toast toast = Toast.makeText(getApplicationContext(), "删除完成！", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                    } catch (NpuException e) {
                                        npuException = e;
                                        SendMessageToUI("NpuException");
                                    }
                                }
                            })
                    .setNegativeButton(R.string.str_no,
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialoginterface,
                                        int i) {
                                }
                            }).show();
        }
    }

    private void DeleteStation() {
        if (mynpu.isInited) {

            if (imgStationList == null || imgStationList.length == 0)
                return;
            new AlertDialog.Builder(WizRoboNpu.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                    .setTitle(R.string.str_warming)
                    //.setIcon(R.drawable.warming)
                    .setMessage("是否删除" + sp_path_station_list.getSelectedItem().toString() + "站点")
                    .setPositiveButton(R.string.str_ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialoginterface,
                                        int i) {
                                    try {
                                        listPathStationId.clear();
                                        ImgStation[] newImgStationList = imgStationList;
                                        List<ImgStation> imgStations = new ArrayList<ImgStation>(0);
                                        if (newImgStationList == null || newImgStationList.length == 0)
                                            return;
                                        for (int j = 0; j < newImgStationList.length; j++) {
                                            imgStations.add(newImgStationList[j]);
                                        }
                                        imgStations.remove(sp_path_station_list.getSelectedItemPosition());
                                        ImgStation[] newImgStationList1 = new ImgStation[imgStations.size()];
                                        for (int j = 0; j < imgStations.size(); j++) {
                                            newImgStationList1[j] = imgStations.get(j);
                                        }
                                        mynpu.SetImgStations(mapname, newImgStationList1);

                                        for (int j = 0; j < newImgStationList1.length; j++) {
                                            listPathStationId.add(newImgStationList1[j].info.id);
                                        }
                                        imgStationList = newImgStationList1;
                                        GetPathStationList();
                                        newImgStationList1 = null;
                                        imgStations = null;
                                        Toast toast = Toast.makeText(getApplicationContext(), "删除中...", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                    } catch (NpuException e) {
                                        npuException = e;
                                        SendMessageToUI("NpuException");
                                    }
                                }
                            })
                    .setNegativeButton(R.string.str_no,
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialoginterface,
                                        int i) {
                                }
                            }).show();
        }
    }

    private void GotoStation() {
        if (mynpu.isInited) {
            if (imgStationList == null || imgStationList.length == 0)
                return;
            final AlertDialog dialog = new AlertDialog.Builder(WizRoboNpu.this)
                    .setTitle("提示")
                    // .setIcon(R.drawable.warming)
                    .setMessage("是否让机器人移动到站点" + sp_path_station_list.getSelectedItem().toString() + "位置")
                    //.setMessage("不能包含@#￥%&*等特殊字符！")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("确定", null)
                    .setCancelable(true)
                    .create();
            dialog.show();

            //为了避免点击 positive 按钮后直接关闭 dialog,把点击事件拿出来设置
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(new OnClickListener() {

                                            @Override
                                            public void onClick(View v) {
                                                if (imgStationList == null || imgStationList.length == 0)
                                                    return;
                                                try {
                                                    mynpu.GotoStation(mapname, sp_path_station_list.getSelectedItem().toString());
                                                    dialog.dismiss();
                                                } catch (NpuException e) {
                                                    NpuExceptionAlert(e);
                                                }


                                            }
                                        }
                    );

            //为了避免点击 positive 按钮后直接关闭 dialog,把点击事件拿出来设置
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new OnClickListener() {

                                                                                 @Override
                                                                                 public void onClick(View v) {
                                                                                     dialog.dismiss();
                                                                                 }
                                                                             }
            );
        }
    }

    private void SetStationPath() {
        if (mynpu.isInited && !isSettingPathpose) {
            stationPathPose[stationPathNum] = actPose;
            System.out.println("station:" + actPose.x + ", " + actPose.y + ", " + actPose.yaw);
            stationPathNum++;
            Toast toast = Toast.makeText(getApplicationContext(), "已添加" + stationPathNum + "个点！", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            isSettingStationPath = true;
        }
    }

    private void SaveStationPath() {
        if (mynpu.isInited && isSettingStationPath) {

            final EditText et_pathnamex = new EditText(WizRoboNpu.this);
            final AlertDialog dialog = new AlertDialog.Builder(WizRoboNpu.this)
                    .setTitle("提示")
                    // .setIcon(R.drawable.warming)
                    .setMessage("请输入路径名称！")
                    //.setMessage("不能包含@#￥%&*等特殊字符！")
                    .setView(et_pathnamex)
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("确定", null)
                    .setCancelable(true)
                    .create();
            dialog.show();

            //为了避免点击 positive 按钮后直接关闭 dialog,把点击事件拿出来设置
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {

                                                                                 @Override
                                                                                 public void onClick(View v) {
                                                                                     boolean a;
                                                                                     String name = et_pathnamex.getText().toString();
                                                                                     if (name == null || name.length() <= 0) {

                                                                                     } else {
//                 a = SpecialSymbols(name);
//                 if (a) {
//                     Toast toast = Toast.makeText(getApplicationContext(), "不能包含*&%$#@!等特殊字符", Toast.LENGTH_SHORT);
//                     toast.setGravity(Gravity.CENTER, 0, 0);
//                     toast.show();
//                     return;
//                 }
                                                                                         dialog.dismiss();
                                                                                     }
                                                                                     try {

                                                                                         if (stationPathNum == 0)
                                                                                             return;
                                                                                         pathList = mynpu.GetPaths(mapname);
                                                                                         if (pathList == null)
                                                                                             return;
                                                                                         wizrobo_npu.Path[] newPathList = new wizrobo_npu.Path[pathList.length + 1];
                                                                                         for (int i = 0; i < pathList.length; i++) {
                                                                                             newPathList[i] = pathList[i];
                                                                                         }

                                                                                         Pose3D[] poses = new Pose3D[stationPathNum];
                                                                                         for (int i = 0; i < stationPathNum; i++) {
                                                                                             poses[i] = stationPathPose[i];
                                                                                         }
                                                                                         PathInfo newPathInfo = new PathInfo(mapname, name, 0, 0);
                                                                                         wizrobo_npu.Path newpath = new wizrobo_npu.Path(newPathInfo, poses);
                                                                                         newPathList[pathList.length] = newpath;

                                                                                         mynpu.SetPaths(mapname, newPathList);
                                                                                         stationPathNum = 0;
                                                                                         isSettingStationPath = false;
                                                                                         Toast toast = Toast.makeText(getApplicationContext(), "保存成功！", Toast.LENGTH_LONG);
                                                                                         toast.setGravity(Gravity.CENTER, 0, 0);
                                                                                         toast.show();
                                                                                         GetPathStationList();
                                                                                         newPathList = null;
                                                                                         newPathInfo = null;
                                                                                     } catch (NpuException e) {
                                                                                         NpuExceptionAlert(e);
                                                                                     }

                                                                                 }
                                                                             }
            );
        }
    }

    private void TrackPath() {
        if (mynpu.isInited) {

            String name;
            if (imgPathList == null || imgPathList.length == 0)
                return;
            if (imgPathList[sp_path_list.getSelectedItemPosition()].info.id.contains("cov_")) {
                new AlertDialog.Builder(WizRoboNpu.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                        .setTitle("提示")
                        //.setIcon(R.drawable.warming)
                        .setMessage("是否开始清扫选定区域？")
                        .setPositiveButton(R.string.str_ok,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialoginterface,
                                            int i) {
                                        try {
                                            int length = imgPathList[sp_path_list.getSelectedItemPosition()].poses.length;
                                            ImgPose[] imgPoses = imgPathList[sp_path_list.getSelectedItemPosition()].poses;
                                            if (length == 0)
                                                return;
                                            ImgPoint[] newPointList = new ImgPoint[length];
                                            for (int j = 0; j < length; j++) {
                                                ImgPoint point = new ImgPoint();

                                                point.u = imgPoses[j].u;
                                                point.v = imgPoses[j].v;
                                                newPointList[j] = point;
                                            }
                                            mynpu.PlanCoverageImgPath(newPointList);
                                        } catch (NpuException e) {
                                            NpuExceptionAlert(e);
                                        }
                                    }
                                })
                        .setNegativeButton(R.string.str_no,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialoginterface,
                                            int i) {
                                    }
                                }).show();
            } else {
                ImgPose[] imagePoses = imgPathList[sp_path_list.getSelectedItemPosition()].poses;
                ImgPose imgPose = imagePoses[imagePoses.length - 1];
                if (Math.abs(imgPose.u - actImgPose.u) < 5 || Math.abs(imgPose.v - actImgPose.v) < 5) {
                    name = "您当前处于选中路径：" + sp_path_list.getSelectedItem().toString() + "(青色标记)终点附近，是否执行？";
                } else {
                    name = "是否执行路径：" + sp_path_list.getSelectedItem().toString() + "(青色标记)？";
                }

                new AlertDialog.Builder(WizRoboNpu.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                        .setTitle("执行单条路径")
                        //.setIcon(R.drawable.warming)
                        .setMessage(name)
                        .setPositiveButton(R.string.str_ok,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialoginterface,
                                            int i) {
                                        try {
                                            mynpu.FollowPath(mapname, sp_path_list.getSelectedItem().toString());
                                        } catch (NpuException e) {
                                            NpuExceptionAlert(e);
                                        }
                                    }
                                })
                        .setNegativeButton(R.string.str_no,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialoginterface,
                                            int i) {
                                    }
                                }).show();
            }
        }
    }


    // Add function

    /**
     * @brief :init the ice use the thread to get the udp ip
     * send the message when get the ip success
     * @attention : you must use the thread to get the ip
     */
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
        } catch (NpuException e) {

            toConnectNpu = false;
            System.out.println("NpuException:" + e.msg);
            npuException = e;
            SendMessageToUI("NpuException");
        }
    }

    private static void Delay(int ms) {
        try {
            Thread.currentThread();
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    ;

    // Update ui function
    private void UpdateCmdMotorSpd() {
        DecimalFormat fnum = new DecimalFormat("##0");
        if (cmdMotorSpd == null)
            return;
        float[] a = cmdMotorSpd.rpms;
        if (a == null || a.length == 0)
            return;
        tv_cmd_motor_spd.setText("电机指令速度: (" + fnum.format(a[0]).toString() + ", " + fnum.format(a[1]).toString() + ")[rpm]");
    }

    private void UpdateActMotorSpd() {
        DecimalFormat fnum = new DecimalFormat("##0");
        if (actMotorSpd == null)
            return;
        float[] a = actMotorSpd.rpms;
        if (a == null || a.length == 0)
            return;
        tv_act_motor_spd.setText("电机当前速度: (" + fnum.format(a[0]).toString() + ", " + fnum.format(a[1]).toString() + ")[rpm]");
    }

    private void UpdateCmdVel() {
        DecimalFormat fnum = new DecimalFormat("##0.00");
        if (cmdVel == null)
            return;
        tv_cmd_vel.setText("指令速度: (" + fnum.format(cmdVel.v_x).toString() + ", " + fnum.format(cmdVel.v_yaw).toString() + ")[m/s,rad/s]");
    }

    private void UpdateActVel() {
        DecimalFormat fnum = new DecimalFormat("##0.00");
        if (actVel == null)
            return;
        tv_act_vel.setText("当前速度: (" + fnum.format(actVel.v_x).toString() + ", " + fnum.format(actVel.v_yaw).toString() + ")[m/s,rad/s]");
    }

    private void UpdateActPose() {
        if (isSlam || isNavi) {
            if (isPoseMode) {
                DecimalFormat fnum = new DecimalFormat("##0.00");
                actPoseX = actPose.x;
                actPoseY = actPose.y;
                if (actPose.yaw < 10f && actPose.yaw > -10f)
                    actPoseYaw = actPose.yaw;
                tv_act_posex.setText("当前位置:" + fnum.format(actPose.x) + ", " + fnum.format(actPose.y) + ", " + fnum.format(actPose.yaw));
            }

            if (isImgPoseMode) {
                DecimalFormat fnum = new DecimalFormat("##0.00");
                if (actImgPose == null)
                    return;
                actImgposeU = actImgPose.u;
                actImgposeV = actImgPose.v;
                if (actImgPose.theta < 10f && actImgPose.theta > -10f)
                    actImgposeTheta = actImgPose.theta;
                //tv_act_posex.setText("当前位置:" + fnum.format(actpose.u) + ", " + fnum.format(actpose.v) + ", " + fnum.format(actpose.theta));

                if (!isToDisplayInitPose && cmdVel.v_x == 0 && cmdVel.v_yaw == 0) {
                    robotInitPoseX = actImgposeU;
                    robotInitPoseY = actImgposeV;
                    isToDisplayInitPose = true;
                }

                if (actPose == null)
                    return;
                actPoseX = actPose.x;
                actPoseY = actPose.y;
                if (actPose.yaw < 10f && actPose.yaw > -10f)
                    actPoseYaw = actPose.yaw;
                tv_act_posex.setText("当前位置:" + fnum.format(actPose.x) + ", " + fnum.format(actPose.y) + ", " + fnum.format(actPose.yaw));
            }
        }
    }

    private void UpdateImudata() {
        if (isNavi) {
            if (imuData == null)
                return;
            tv_imu_data.setText("IMU:" + imuData.yaw_deg);
        }
    }

    private void UpdateActEnc() {
        if (mynpu.isInited) {
            MotorEnc actenc = actMotorEnc;
            if (actMotorEnc == null || actMotorEnc.motor_num == 0 || actenc.ticks.length == 0)
                return;
            tv_act_enc.setText("码盘数值：" + Integer.toString(actenc.ticks[0]) + ", " + Integer.toString(actenc.ticks[1]));
        }
    }

    private void UpdateRobotStatus() {
        if (mynpu.isInited) {
            tv_robot_status.setTextColor(Color.rgb(255, 116, 0));
            tv_robot_status.setText("NPU状态:" + naviState.name());
            if (npuException != null) {
                tv_robot_status.setTextColor(Color.rgb(207, 42, 48));
                tv_robot_status.setText("NPU状态:" + npuException.msg);
                //tv_robot_status.setTextColor(Color.rgb(255, 116, 0));
                npuException = null;
            }
        }

        if (!mynpu.isInited) {
            tv_robot_status.setTextColor(Color.rgb(207, 42, 48));
            tv_robot_status.setText("NPU状态:" + "未连接");
        }

    }

    private void CheckNpuStatus() {
        if (mynpu.isInited) {
            try {
                isInited = true;
                isTimeout = false;
                str_npu_version = mynpu.GetServerVersion();
                str_npu_version = str_npu_version.substring(0, 23);
                SendMessageToUI("GetServerVersion");
                ActionState actionState = mynpu.GetActionState();
                if (actionState == ActionState.SLAM_ACTION) {
                    isSlam = false;
                    Slam();
                }

                if (actionState == ActionState.NAVI_ACTION) {
                    CoreParam coreParam = mynpu.GetCoreParam();
                    mapname = coreParam.map_id;
                    isNavi = false;
                    Navi();
                }

                System.out.println("ActionState:" + actionState.name());
            } catch (Exception e) {
                ExceptionAlert(e);
            }
        }
    }

    private void Display() {

        try {

            if (toGetImgMap) {
                ReadImgMap();
            }

            if (bm_background == null) {
                if (isSlam || isNavi) {
                    Toast toast = Toast.makeText(getApplicationContext(), "地图刷新中...", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "地图数据异常！", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }


            }

            if (bm_background == null)
                return;
            backgroundbmp_draw = bm_background.copy(Config.RGB_565, true);
            canvas = new Canvas(backgroundbmp_draw);
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);      // To solve the zigzag problem.
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            if (pointIsSelected) {
                paint.setColor(Color.RED);
                paint.setStrokeWidth(1f);
                canvas.drawCircle(setImgposeU, setImgposeV, 4, paint);
            }

            paint.setStyle(Paint.Style.FILL);
            if (isSettingInitialPose) {
                paint.setColor(Color.RED);
                paint.setStrokeWidth(1.0f);
                int desx = (int) (initImgPoseU_float + 10f * Math.cos(redarrowYaw));
                int desy = (int) (initImgPoseV_float - 10f * Math.sin(redarrowYaw));
                paint.setColor(Color.GREEN);
                canvas.drawCircle(initImgposeU, initImgposeV, 2, paint);
                paint.setColor(Color.RED);
                DrawArrow(canvas, paint, initImgposeU, initImgposeV, desx, desy);
            }

            if (isSettingGoalPose || isSettingPathpose || isSettingFreePath) {
                paint.setStrokeWidth(1.0f);
                int desx = (int) (setImgPoseU_float + 10f * Math.cos(redarrowYaw));
                int desy = (int) (setImgPoseV_float - 10f * Math.sin(redarrowYaw));
                paint.setColor(Color.GREEN);
                canvas.drawCircle(setImgposeU, setImgposeV, 2, paint);
                paint.setColor(Color.RED);
                DrawArrow(canvas, paint, setImgposeU, setImgposeV, desx, desy);
            }
            DisplayMapInfoPath(canvas, paint);
            DisplayMapInfoVirtualWall(canvas, paint);
            DisplayMapInfoStation(canvas, paint);
            DisplayFreePath(canvas, paint);
            DisplayStationPath(canvas, paint);
            DisplayCmdPath(canvas, paint);
            //DisplayActPath(canvas,paint);
            DisplayActPose(canvas, paint);
            DisplayFootprintVertices(canvas, paint);
            DisplayLidarPoints(canvas, paint);
            DisplayInitPose(canvas, paint);
            DisplayInitPoseArea(canvas, paint);
            if (toCoveragePathPlanning) {
                DisplayCppBorders(canvas, paint);
            }

            if (pixelMat == null || pixelMat.height <= 0 || pixelMat.width <= 0)
                return;


            float compare = 0;
            compare = canvasHeight / pixelMat.height - canvasWidth / pixelMat.width;
            if (compare > 0)
                mapEnlargeLevel = canvasWidth / pixelMat.width;
            else
                mapEnlargeLevel = canvasHeight / pixelMat.height;
            //System.out.printf("mapEnlargeLevel = %3.2f, canvasHeight = %3.2f,canvasWidth = %3.2f, compare = %3.2f", mapEnlargeLevel, canvasHeight, canvasWidth, compare);
            if (Float.isNaN(mapEnlargeLevel) || mapEnlargeLevel > 30)
                return;
            backgroundbmp_draw = MapBig(backgroundbmp_draw, mapEnlargeLevel);   // this function need to read more detail ,2017.4.27 modify by jeremy
            iv_background.setImageBitmap(backgroundbmp_draw);

            backgroundbmp_draw = null;
            resizeBmp = null;
            canvas = null;
        } catch (Exception e) {
            ExceptionAlert(e);
        }

    }

    private void DisplayLidarPoints(Canvas canvas, Paint paint) {
        if (isSlam || isNavi) {
            if (cb_lidar_display.isChecked()) {
                if (isPoseMode) {
                    LidarScan lidarscandata = new LidarScan();
                    lidarscandata = lidarScanData;
                    Point3D[] points;
                    points = lidarscandata.points;
                    float[] lidarpointdata = new float[2 * points.length];

                    for (int i = 0; i < points.length; i++) {
                        lidarpointdata[2 * i] = points[i].x;
                        lidarpointdata[2 * i + 1] = points[i].y;
                    }

                    for (int j = 0; j < lidarpointdata.length; j++) {
                        lidarpointdata[j] = (float) ((lidarpointdata[j] - mapInfo.offset.x) / resolution * pixelMat.ratio);
                        lidarpointdata[j + 1] = (float) ((pixelMat.height - (lidarpointdata[j + 1] - mapInfo.offset.y) / resolution * pixelMat.ratio));
                        j++;
                    }

                    paint.setColor(Color.RED);
                    paint.setStrokeWidth(1.0f);
                    canvas.drawPoints(lidarpointdata, paint);
                    lidarpointdata = null;
                }

                if (isImgPoseMode) {

                    if (imglidarscandata == null)
                        return;
                    ImgPoint[] points;
                    points = imglidarscandata.points;
                    if (points == null)
                        return;
                    float[] lidarpointdata = new float[2 * points.length];

                    for (int i = 0; i < points.length; i++) {
                        lidarpointdata[2 * i] = (float) points[i].u;
                        lidarpointdata[2 * i + 1] = (float) points[i].v;
                    }


                    paint.setColor(Color.RED);
                    paint.setStrokeWidth(1.0f);
                    canvas.drawPoints(lidarpointdata, paint);
                    lidarpointdata = null;
                    points = null;
                    //imglidarscandata=null;
                }
            }
        }
    }

    private void DisplayFootprintVertices(Canvas canvas, Paint paint) {
        try {
            if (isNavi) {
                if (isPoseMode) {
                    Point3D[] point3Ds = mynpu.GetFootprintVertices();             //Display the path
                    float[] FootprintVerticles = new float[2 * point3Ds.length];
                    for (int i = 0; i < point3Ds.length; i++) {
                        FootprintVerticles[2 * i] = point3Ds[i].x;
                        FootprintVerticles[2 * i + 1] = point3Ds[i].y;
                    }

                    for (int j = 0; j < FootprintVerticles.length; j++) {
                        FootprintVerticles[j] = (float) ((FootprintVerticles[j] - mapInfo.offset.x) / resolution);
                        FootprintVerticles[j + 1] = (float) ((pixelMat.height - (FootprintVerticles[j + 1] - mapInfo.offset.y) / resolution));
                        j++;
                    }
                    paint.setColor(Color.BLUE);
                    paint.setStrokeWidth(2.0f);
                    canvas.drawPoints(FootprintVerticles, paint);
                }

                if (isImgPoseMode) {

                    ImgPoint[] imgPoints = FootprintVerticles;
                    if (imgPoints == null)
                        return;
                    paint.setColor(Color.BLUE);
                    paint.setStrokeWidth(1.0f);
                    for (int i = 0; i < imgPoints.length - 1; i++) {
                        //paint.setStrokeWidth(1.0f);
                        canvas.drawLine(imgPoints[i].u, imgPoints[i].v, imgPoints[i + 1].u, imgPoints[i + 1].v, paint);
                    }
                    if (imgPoints.length != 0)
                        canvas.drawLine(imgPoints[0].u, imgPoints[0].v, imgPoints[imgPoints.length - 1].u, imgPoints[imgPoints.length - 1].v, paint);
                    imgPoints = null;
                }
            }
        } catch (NpuException e) {
            NpuExceptionAlert(e);
        }
    }

    private void DisplayCmdPath(Canvas canvas, Paint paint) {
        //*************************DisplayCmdPath****************************
        if (isNavi) {
            if (isPoseMode) {
                path2D = cmdPath;             //Display the path
                Pose3D[] pose;
                pose = path2D.poses;
                float[] pathData = new float[2 * pose.length];
                for (int i = 0; i < pose.length; i++) {
                    pathData[2 * i] = pose[i].x;
                    pathData[2 * i + 1] = pose[i].y;
                }
                for (int j = 0; j < pathData.length; j++) {
                    pathData[j] = (float) ((pathData[j] - mapInfo.offset.x) / resolution);
                    pathData[j + 1] = (float) ((pixelMat.height - (pathData[j + 1] - mapInfo.offset.y) / resolution));
                    j++;
                }
                paint.setColor(Color.GREEN);
                paint.setStrokeWidth(2.0f);
                canvas.drawPoints(pathData, paint);
                pathData = null;
            }

            if (isImgPoseMode) {
                ImgPath imgPath;
                imgPath = cmdImgPath;             //Display the path
                if (imgPath == null)
                    return;
                ImgPose[] pose;
                pose = imgPath.poses;
                if (pose == null)
                    return;
                float[] pathData = new float[2 * pose.length];
                for (int i = 0; i < pose.length; i++) {
                    pathData[2 * i] = pose[i].u;
                    pathData[2 * i + 1] = pose[i].v;
                }

                for (int j = 0; j < pathData.length; j++) {
                    pathData[j] = (float) (pathData[j]);
                    pathData[j + 1] = (float) (pathData[j + 1]);
                    j++;
                }
                paint.setColor(Color.GREEN);
                paint.setStrokeWidth(2.0f);
                canvas.drawPoints(pathData, paint);
                pathData = null;
                imgPath = null;
            }
        }
    }

    private void DisplayActPath(Canvas canvas, Paint paint) {
        //*************************DisplayActPath****************************
        try {
            if (isNavi) {
                if (isPoseMode) {
                    path2D = mynpu.GetActPath();             //Display the path
                    Pose3D[] pose;
                    pose = path2D.poses;
                    float[] pathData = new float[2 * pose.length];
                    for (int i = 0; i < pose.length; i++) {
                        pathData[2 * i] = pose[i].x;
                        pathData[2 * i + 1] = pose[i].y;
                    }

                    for (int j = 0; j < pathData.length; j++) {
                        pathData[j] = (float) ((pathData[j] - mapInfo.offset.x) / resolution);
                        pathData[j + 1] = (float) ((pixelMat.height - (pathData[j + 1] - mapInfo.offset.y) / resolution));
                        j++;

                    }
                    paint.setColor(Color.GREEN);
                    paint.setStrokeWidth(2.0f);
                    canvas.drawPoints(pathData, paint);
                    System.out.println("Displaying ActPath");
                    pathData = null;
                }

                if (isImgPoseMode) {

                    ImgPath imgPath;
                    imgPath = mynpu.GetActImgPath();             //Display the path
                    ImgPose[] pose;
                    pose = imgPath.poses;
                    float[] pathData = new float[2 * pose.length];
                    for (int i = 0; i < pose.length; i++) {
                        pathData[2 * i] = pose[i].u;
                        pathData[2 * i + 1] = pose[i].v;
                    }

                    for (int j = 0; j < pathData.length; j++) {
                        pathData[j] = (float) (pathData[j]);
                        pathData[j + 1] = (float) (pathData[j + 1]);
                        j++;
                    }
                    paint.setColor(Color.GREEN);
                    paint.setStrokeWidth(4.0f);
                    canvas.drawPoints(pathData, paint);
                    System.out.println("Displaying ActPath");
                    pathData = null;
                }
            }
        } catch (NpuException e) {
            NpuExceptionAlert(e);
        }
    }

    private void DisplayMapInfoPath(Canvas canvas, Paint paint) {
        //*************************DisplayMapInfoPath****************************
        if (cb_path_display.isChecked()) {
            if (isPoseMode) {
                Pose3D[] pose;
                pose = pathList[sp_path_station_list.getSelectedItemPosition()].poses;
                float[] pathData = new float[2 * pose.length];
                for (int i = 0; i < pose.length; i++) {
                    pathData[2 * i] = pose[i].x;
                    pathData[2 * i + 1] = pose[i].y;
                }

                for (int j = 0; j < pathData.length; j = j + 2) {
                    pathData[j] = (float) ((pathData[j] - mapInfo.offset.x) / resolution * pixelMat.ratio);
                    pathData[j + 1] = (float) ((pixelMat.height - (pathData[j + 1] - mapInfo.offset.y) / resolution * pixelMat.ratio));
                }
                paint.setColor(Color.GREEN);
                paint.setStrokeWidth(2.0f);
                canvas.drawPoints(pathData, paint);

                //*******Display Lines********
                paint.setColor(Color.BLUE);
                paint.setStrokeWidth(1.5f);
                if (pathData.length <= 2)
                    return;
                else {
                    for (int i = 0; i < pathData.length / 2; i += 2) {
                        canvas.drawLine(pathData[i], pathData[i + 1], pathData[i + 2], pathData[i + 3], paint);
                    }
                }

                pathData = null;
            }

            if (isImgPoseMode) {
                ImgPose[] pose;
                if (imgPathList == null || imgPathList.length == 0)
                    return;

                for (int k = 0; k < imgPathList.length; k++) {

                    if (imgPathList[k].info.id.contains("unfinished_path") && !excuteUnfinishedTask) {
                        excuteUnfinishedTask = true;
                        DetectUnfinishedTask();
                    }
//                    pose = imgPathList[sp_path_station_list.getSelectedItemPosition()].poses;
                    pose = imgPathList[k].poses;
                    float[] pathData = new float[2 * pose.length];
                    for (int i = 0; i < pose.length; i++) {
                        pathData[2 * i] = pose[i].u;
                        pathData[2 * i + 1] = pose[i].v;
                        paint.setColor(Color.RED);
                        paint.setStrokeWidth(1.0f);
                        int desx = pose[i].u + (int) (10f * Math.cos(pose[i].theta));
                        int desy = pose[i].v - (int) (10f * Math.sin(pose[i].theta));
                        if (i == pose.length - 1)   //dispaly the endpoint arrow
                            DrawArrow(canvas, paint, pose[i].u, pose[i].v, desx, desy);
                    }

                    paint.setColor(Color.GREEN);
                    paint.setStrokeWidth(2.0f);
                    canvas.drawPoints(pathData, paint);

                    //*******Display Lines********
                    paint.setColor(Color.BLUE);
                    paint.setStrokeWidth(1.0f);
                    if (k == sp_path_list.getSelectedItemPosition())
                        paint.setColor(Color.rgb(94, 211, 254));

                    if (pathData.length <= 2)
                        return;
                    else {
                        for (int i = 0; i < pathData.length - 2; i += 2) {
                            canvas.drawLine(pathData[i], pathData[i + 1], pathData[i + 2], pathData[i + 3], paint);
                        }
                    }
                    pathData = null;

                }
            }
        }
    }

    private void DisplayMapInfoVirtualWall(Canvas canvas, Paint paint) {
        //*************************DisplayMapInfoVirtualWall****************************
        if (isNavi) {
            if (isImgPoseMode) {
                ImgPoint[] virtualWallPoints;
                if (imgVirtualWallList == null || imgVirtualWallList.length == 0)
                    return;
                for (int j = 0; j < imgVirtualWallList.length; j++) {
                    virtualWallPoints = imgVirtualWallList[j].points;
                    float[] pointsData = new float[2 * virtualWallPoints.length];

                    for (int i = 0; i < virtualWallPoints.length; i++) {
                        pointsData[2 * i] = virtualWallPoints[i].u;
                        pointsData[2 * i + 1] = virtualWallPoints[i].v;
                    }

                    //*******Display Points********
                    paint.setColor(Color.YELLOW);
                    paint.setStrokeWidth(2.0f);
                    canvas.drawPoints(pointsData, paint);

                    //*******Display Lines********
                    paint.setColor(Color.DKGRAY);
                    paint.setStrokeWidth(1.0f);
                    if (j == sp_virtual_wall_list.getSelectedItemPosition())
                        paint.setColor(Color.rgb(94, 211, 254));
                    if (pointsData.length <= 2)
                        return;
                    else {
                        for (int i = 0; i < pointsData.length - 2; i += 2) {
                            canvas.drawLine(pointsData[i], pointsData[i + 1], pointsData[i + 2], pointsData[i + 3], paint);
                        }
                    }
                    System.out.println("Displaying MapInfoVirtualWall");
                    pointsData = null;
                    virtualWallPoints = null;

                }
            }
        }
    }

    private void DisplayStationPath(Canvas canvas, Paint paint) {
        if (isSettingStationPath) {
            float[] pathData = new float[2 * stationPathNum];

            for (int i = 0; i < stationPathNum; i++) {
                pathData[2 * i] = stationPathPose[i].x;
                pathData[2 * i + 1] = stationPathPose[i].y;
            }
            for (int j = 0; j < pathData.length; j = j + 2) {
                pathData[j] = (float) ((pathData[j] - mapInfo.offset.x) / resolution * pixelMat.ratio);
                pathData[j + 1] = (float) ((pixelMat.height - (pathData[j + 1] - mapInfo.offset.y) / resolution * pixelMat.ratio));

                paint.setColor(Color.RED);
                paint.setStrokeWidth(1.0f);
                int desx = (int) pathData[j] + (int) (10f * Math.cos(stationPathPose[j / 2].yaw));
                int desy = (int) pathData[j + 1] - (int) (10f * Math.sin(stationPathPose[j / 2].yaw));
                DrawArrow(canvas, paint, (int) pathData[j], (int) pathData[j + 1], desx, desy);
            }


            //*******Display points********
            paint.setColor(Color.GREEN);
            paint.setStrokeWidth(3.0f);
            canvas.drawPoints(pathData, paint);

            //*******Display Lines********

            paint.setColor(Color.YELLOW);
            paint.setStrokeWidth(1.5f);
            if (stationPathNum < 2)
                return;
            else {
                for (int i = 0; i < pathData.length / 2; i += 2) {
                    canvas.drawLine(pathData[i], pathData[i + 1], pathData[i + 2], pathData[i + 3], paint);
                }
            }

            pathData = null;

        }
    }

    private void DisplayFreePath(Canvas canvas, Paint paint) {

        //*************************DisplayFreePath****************************
        if (isSettingPathpose || isSettingFreePath || toCoveragePathPlanning || isSettingVirtualWall) {
            float[] pathData = new float[2 * pathPoseNum];
            if (isPoseMode) {
                for (int i = 0; i < pathPoseNum; i++) {
                    pathData[2 * i] = pathPose[i].x;
                    pathData[2 * i + 1] = pathPose[i].y;
                }

                for (int j = 0; j < pathData.length; j = j + 2) {
                    pathData[j] = (float) ((pathData[j] - mapInfo.offset.x) / resolution * pixelMat.ratio);
                    pathData[j + 1] = (float) ((pixelMat.height - (pathData[j + 1] - mapInfo.offset.y) / resolution * pixelMat.ratio));
                }
            }

            if (isImgPoseMode) {
                for (int i = 0; i < pathPoseNum; i++) {
                    pathData[2 * i] = imgPathPose[i].u;
                    pathData[2 * i + 1] = imgPathPose[i].v;
                }

                for (int j = 0; j < pathData.length; j = j + 2) {
                    pathData[j] = (float) ((pathData[j]));
                    pathData[j + 1] = (float) (((pathData[j + 1])));
                }
            }
            //*******Display points********
            paint.setColor(Color.GREEN);
            paint.setStrokeWidth(3.0f);
            canvas.drawPoints(pathData, paint);

            //*******Display Lines********
            paint.setColor(Color.BLUE);
            paint.setStrokeWidth(1.0f);
            if (pathPoseNum < 2)
                return;
            else {
                for (int i = 0; i < pathPoseNum - 1; i++) {
                    //paint.setStrokeWidth(1.0f);
                    canvas.drawLine(imgPathPoint[i].u, imgPathPoint[i].v, imgPathPoint[i + 1].u, imgPathPoint[i + 1].v, paint);
                }
            }

            pathData = null;
        }
    }

    private void DisplayMapInfoStation(Canvas canvas, Paint paint) {
        if (cb_station_display.isChecked() && isNavi) {
            if (isPoseMode) {
                Pose3D pose;
                pose = stationList[sp_path_station_list.getSelectedItemPosition()].pose;
                int stationx, stationy;
                stationx = (int) ((pose.x - mapInfo.offset.x) / resolution * pixelMat.ratio);
                stationy = (int) ((pixelMat.height - (pose.y - mapInfo.offset.y) / resolution * pixelMat.ratio));
                paint.setColor(Color.RED);
                paint.setStrokeWidth(2.0f);
                canvas.drawCircle(stationx, stationy, 2, paint);
            }
            if (isImgPoseMode) {
                ImgPose pose;
                if (imgStationList == null || imgStationList.length == 0)
                    return;
                pose = imgStationList[sp_path_station_list.getSelectedItemPosition()].pose;
                int stationx, stationy;
                stationx = (int) ((pose.u));
                stationy = (int) (((pose.v)));
                paint.setColor(Color.rgb(0, 249, 208));    //青色
                paint.setStrokeWidth(6.0f);
                canvas.drawCircle(stationx, stationy, 2, paint);

            }
        }
    }

    private void DisplayCppBorders(Canvas canvas, Paint paint) {
        ///*****************Display the Borders of Coverage Path***********
        if (isSettingCoveragePath) {
            paint.setColor(Color.BLUE);
            paint.setStrokeWidth(1.5f);
            if (coveragePathBordersNum <= 2)
                return;
            else {
                for (int i = 0; i < coveragePathBordersNum - 1; i++) {
                    canvas.drawLine(imgPathPoint[i].u, imgPathPoint[i].v, imgPathPoint[i + 1].u, imgPathPoint[i + 1].v, paint);
                }
                canvas.drawLine(imgPathPoint[0].u, imgPathPoint[0].v, imgPathPoint[coveragePathBordersNum - 1].u, imgPathPoint[coveragePathBordersNum - 1].v, paint);
            }
        }
    }

    private void DisplayActPose(Canvas canvas, Paint paint) {
        int arrowx = 0, arrowy = 0;
        ///*****************Display CurrentPose***************************
        if (isPoseMode) {
            if (pixelMat.ratio > 0) {
                arrowx = (int) ((actPoseX - mapInfo.offset.x) / resolution * pixelMat.ratio);
                arrowy = (int) ((pixelMat.height - (actPoseY - mapInfo.offset.y) / resolution * pixelMat.ratio));
            } else {
                //System.out.println("the ratio is :"+pixelMat.ratio);
                arrowx = (int) ((actPoseY - mapInfo.offset.y) / resolution * (pixelMat.ratio));
                arrowy = (int) ((pixelMat.height - (actPoseX - mapInfo.offset.x) / resolution * (-pixelMat.ratio)));
            }
            // System.out.println("Displaying ActPose");

        }

        ///********************DisplayImgPose*********************************
        if (isImgPoseMode) {
            if (pixelMat == null)
                return;


            if (pixelMat.ratio > 0) {
                arrowx = (int) ((actImgposeU));
                arrowy = (int) (((actImgposeV)));
            } else {   //System.out.println("the ratio is :"+pixelMat.ratio);
                arrowx = (int) (actImgposeV);
                arrowy = (int) (((actImgposeU) * (-1)));
            }
        }

        paint.setColor(Color.BLUE);
        canvas.drawCircle(arrowx, arrowy, 4, paint);
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(1.5f);
        float rad = 0;
        if (isPoseMode)
            rad = actPoseYaw;
        if (isImgPoseMode)
            rad = actImgposeTheta;
        if (pixelMat.ratio > 0) {
            int desx = arrowx + (int) (10f * Math.cos(rad));
            int desy = arrowy - (int) (10f * Math.sin(rad));
            DrawAL(canvas, paint, arrowx, arrowy, desx, desy);
        } else {
            int desx = arrowx + (int) (10f * Math.cos(rad + 1.57));
            int desy = arrowy - (int) (10f * Math.sin(rad + 1.57));
            DrawAL(canvas, paint, arrowx, arrowy, desx, desy);
        }
    }

    private void DisplayInitPose(Canvas canvas, Paint paint) {
        if (isNavi) {
            if (isToDisplayInitPose) {
                paint.setStrokeWidth(2f);
                paint.setColor(Color.YELLOW);
                canvas.drawCircle(robotInitPoseX, robotInitPoseY, 5, paint);
            }
        }
    }

    private void DisplayInitPoseArea(Canvas canvas, Paint paint) {
        if (isSettingInitPoseArea && initPoseArea != null) {
            paint.setColor(Color.BLUE);
            paint.setStrokeWidth(1.0f);
            paint.setStyle(Paint.Style.STROKE);
            float left = initPoseArea.pose.x;
            float top = initPoseArea.pose.y - initPoseArea.height;
            float right = initPoseArea.pose.x + initPoseArea.width;
            float bottom = initPoseArea.pose.y;

            left = (float) ((left - mapInfo.offset.x) / resolution * pixelMat.ratio);
            top = (float) ((pixelMat.height - (top - mapInfo.offset.y) / resolution * pixelMat.ratio));
            right = (float) ((right - mapInfo.offset.x) / resolution * pixelMat.ratio);
            bottom = (float) ((pixelMat.height - (bottom - mapInfo.offset.y) / resolution * pixelMat.ratio));
            //canvas.drawLine();
            canvas.drawRect(rec_left, rec_top, rec_right, rec_bottom, paint);
        }
    }

    private static Bitmap MapBig(Bitmap bitmap, float scare) {
        Matrix matrix1 = new Matrix();
        matrix1.postScale(scare, scare);
        resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix1, true);
        return resizeBmp;
    }

    Bitmap Convert(Bitmap a, int width, int height) {
        if (a == null)
            return null;
        int w = a.getWidth();
        int h = a.getHeight();
        Bitmap newb = Bitmap.createBitmap(w, h, Config.RGB_565);
        Canvas cv = new Canvas(newb);
        Matrix m = new Matrix();
        Bitmap new2 = Bitmap.createBitmap(a, 0, 0, w, h, m, true);
        cv.drawBitmap(new2, new Rect(0, 0, new2.getWidth(), new2.getHeight()), new Rect(0, 0, w, h), null);
        new2 = null;
        cv = null;
        return newb;
    }

    private Bitmap GetBitmapFromPgm(byte[] decodedString, int width, int height, int dataOffset) {
        // Create pixel array, and expand 8 bit gray to RGB_565
        if (width <= 1 || height <= 1) {
            return null;
        }
        System.out.println("width:" + width + "height:" + height);
        int[] pixels = new int[width * height];
        int i = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int gray = 0xff - decodedString[dataOffset + i] & 0xff;
                if (gray == 50)
                    gray = 205;
                // System.out.println(gray+" ");

                pixels[i] = 0xff000000 | gray << 16 | gray << 8 | gray;
                // pixels[i] = decodedString[i] & 0xff;
                i++;
            }
        }

        if (width <= 0 || height <= 0) {
            return null;
        }


        Bitmap pgm = Bitmap.createBitmap(pixels, width, height, Config.RGB_565);
        pixels = null;
        return pgm;
    }

    private double[] RotateVec(int px, int py, double ang, boolean isChLen, double newLen) {
        double mathstr[] = new double[2];
        double vx = px * Math.cos(ang) - py * Math.sin(ang);
        double vy = px * Math.sin(ang) + py * Math.cos(ang);
        if (isChLen) {
            double d = Math.sqrt(vx * vx + vy * vy);
            vx = vx / d * newLen;
            vy = vy / d * newLen;
            mathstr[0] = vx;
            mathstr[1] = vy;
        }
        return mathstr;
    }

    public void DrawAL(Canvas canvas, Paint paint, int sx, int sy, int ex, int ey) {
        double H = 4;
        double L = 2;
        int x3 = 0;
        int y3 = 0;
        int x4 = 0;
        int y4 = 0;
        double awrad = Math.atan(L / H);
        double arraow_len = Math.sqrt(L * L + H * H);
        double[] arrXY_1 = RotateVec(ex - sx, ey - sy, awrad, true, arraow_len);
        double[] arrXY_2 = RotateVec(ex - sx, ey - sy, -awrad, true, arraow_len);
        double x_3 = ex - arrXY_1[0];
        double y_3 = ey - arrXY_1[1];
        double x_4 = ex - arrXY_2[0];
        double y_4 = ey - arrXY_2[1];
        Double X3 = new Double(x_3);
        x3 = X3.intValue();
        Double Y3 = new Double(y_3);
        y3 = Y3.intValue();
        Double X4 = new Double(x_4);
        x4 = X4.intValue();
        Double Y4 = new Double(y_4);
        y4 = Y4.intValue();

        canvas.drawLine(sx, sy, ex, ey, paint);
        Path triangle = new Path();
        triangle.moveTo(ex, ey);
        triangle.lineTo(x3, y3);
        triangle.lineTo(x4, y4);
        triangle.close();
        //canvas.drawPath(triangle,paint); //draw arrow head
    }

    public void DrawArrow(Canvas canvas, Paint paint, int sx, int sy, int ex, int ey) {
        double H = 4;
        double L = 2;
        int x3 = 0;
        int y3 = 0;
        int x4 = 0;
        int y4 = 0;
        double awrad = Math.atan(L / H);
        double arraow_len = Math.sqrt(L * L + H * H);
        double[] arrXY_1 = RotateVec(ex - sx, ey - sy, awrad, true, arraow_len);
        double[] arrXY_2 = RotateVec(ex - sx, ey - sy, -awrad, true, arraow_len);
        double x_3 = ex - arrXY_1[0];
        double y_3 = ey - arrXY_1[1];
        double x_4 = ex - arrXY_2[0];
        double y_4 = ey - arrXY_2[1];
        Double X3 = new Double(x_3);
        x3 = X3.intValue();
        Double Y3 = new Double(y_3);
        y3 = Y3.intValue();
        Double X4 = new Double(x_4);
        x4 = X4.intValue();
        Double Y4 = new Double(y_4);
        y4 = Y4.intValue();

        canvas.drawLine(sx, sy, ex, ey, paint);
        Path triangle = new Path();
        triangle.moveTo(ex, ey);
        triangle.lineTo(x3, y3);
        triangle.lineTo(x4, y4);
        triangle.close();
        canvas.drawPath(triangle, paint); //draw arrow head
    }

    // Add draw scale
    private float Distance(MotionEvent event) {
        try {
            float dx = event.getX(1) - event.getX(0);
            float dy = event.getY(1) - event.getY(0);
            System.out.println("mapZoom_dxdy:" + dx + "  " + dy);

            return (float) Math.sqrt(dx * dx + dy * dy);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.out.println("mapZoom_Distance Exception");
        }
        return -1f;
    }

    private PointF MidPoint(MotionEvent event) {
        float midX = iv_background.getWidth() / 2;//(event.getX(1) + event.getX(0)) / 2;
        //float midX=(event.getX(1) + event.getX(0)) / 2;
        float midY = iv_background.getHeight() / 2;//(event.getY(1) + event.getY(0)) / 2;
        //float midY = (event.getY(1) + event.getY(0)) / 2;//(event.getY(1) + event.getY(0)) / 2;

        return new PointF(midX, midY);
    }

    private void ReadImgMap() {
        isReadThumbnail = false;
        if (imgMap == null)
            return;
        mapInfo = imgMap.info;
        pixelMat = imgMap.mat;
        if (imgMap == null || pixelMat == null || mapInfo == null)
            return;
        if (pixelMat.height <= 0 || pixelMat.width <= 0 || mapInfo.resolution <= 0)
            return;
        System.out.println("Reading Imgmap,Reading Imgmap********.............");
        resolution = (float) mapInfo.resolution;
        DecimalFormat fnum = new DecimalFormat("##0.00");
        tv_map_info.setText("地图信息:" + Integer.toString(pixelMat.width) + ", " + Integer.toString(pixelMat.height) + ", " + fnum.format(mapInfo.resolution));
        bm_background = GetBitmapFromPgm(pixelMat.data, pixelMat.width, pixelMat.height, 0);
        bm_background = Convert(bm_background, pixelMat.width, pixelMat.height);

        if (isNavi)
            toGetImgMap = false;
    }

    public void ReadThumbnail() {
        isReadThumbnail = true;
        pixelMat = mapInfoList[sp_map_list.getSelectedItemPosition()].thumbnail;
        if (pixelMat.width <= 1 || pixelMat.height <= 1)
            return;
        mapInfo = mapInfoList[sp_map_list.getSelectedItemPosition()];
        resolution = mapInfo.resolution;
        if (pixelMat.width < 0 || pixelMat.height < 0)
            return;
        tv_map_info.setText("MapInfo:" + Integer.toString(pixelMat.width) + ", " + Integer.toString(pixelMat.height) + ", " + Float.toString(mapInfo.resolution));
        bm_background = GetBitmapFromPgm(pixelMat.data, pixelMat.width, pixelMat.height, 0);
        if (bm_background == null)
            return;

        bm_background = Convert(bm_background, pixelMat.width, pixelMat.height);

    }

    //@Override
    public void onJoystickMoved(float xPercent, float yPercent, int id) {
        switch (id) {
            case R.id.joystickLeft:

                cb_cruise_control.setChecked(false);
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
                } else {
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


    private void CheckWifiStatus() {
        ConnectivityManager manager = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        NetworkInfo wifiNetworkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (wifiNetworkInfo.isConnected()) {
            String wserviceName = Context.WIFI_SERVICE;
            WifiManager wm = (WifiManager) getSystemService(wserviceName);
            WifiInfo info1 = wm.getConnectionInfo();
            int strength = info1.getRssi();
            wifiStrength = strength;
            if (strength < -85)
                return;
            wifiIsConnected = true;
            SendMessageToUI("ChangeWifiUI");
        } else {
            isTimeout = true;
            mynpu.isInited = false;
            isInited = false;
            wifiIsConnected = false;
            SendMessageToUI("ChangeWifiUI");
            return;
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
                Settings.System.WIFI_SLEEP_POLICY,
                Settings.System.WIFI_SLEEP_POLICY_DEFAULT);
        System.out.println("---> 修改前的Wifi休眠策略值 WIFI_SLEEP_POLICY=" + wifiSleepPolicy);


        Settings.System.putInt(getContentResolver(),
                Settings.System.WIFI_SLEEP_POLICY,
                Settings.System.WIFI_SLEEP_POLICY_NEVER_WHILE_PLUGGED);


        wifiSleepPolicy = Settings.System.getInt(getContentResolver(),
                Settings.System.WIFI_SLEEP_POLICY,
                Settings.System.WIFI_SLEEP_POLICY_DEFAULT);
        System.out.println("---> 修改后的Wifi休眠策略值 WIFI_SLEEP_POLICY=" + wifiSleepPolicy);
    }

    public void restoreWifiDormancy() {
        final SharedPreferences prefs = getSharedPreferences("wifi_sleep_policy", Context.MODE_PRIVATE);
        int defaultPolicy = prefs.getInt("WIFI_SLEEP_POLICY_DEFAULT", Settings.System.WIFI_SLEEP_POLICY_DEFAULT);
        Settings.System.putInt(getContentResolver(), Settings.System.WIFI_SLEEP_POLICY, defaultPolicy);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)        //按Home键弹出提示框
    {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 创建退出对话框
            AlertDialog isExit = new AlertDialog.Builder(this).create();
            // 设置对话框标题
            isExit.setTitle("系统提示");
            // 设置对话框消息
            isExit.setMessage("确定要退出APP吗");
            // 添加选择按钮并注册监听
            isExit.setButton("确定", listener);
            isExit.setButton2("取消", listener);
            // 显示对话框
            isExit.show();
        }

        // 返回键移除二级悬浮窗
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            return true;
        }
        return false;
    }

    /**
     * 监听对话框里面的button点击事件
     */
    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    updateUiHandler.removeCallbacksAndMessages(null);
                    getDataHandler1.removeCallbacksAndMessages(null);
                    getDataHandler.removeCallbacksAndMessages(null);
                    setManualVelHandler.removeCallbacksAndMessages(null);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(0);
                    finish();
                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onConfigurationChanged(Configuration config)      //防止最小化后重启APP
    {
        super.onConfigurationChanged(config);
    }

    public void NpuExceptionAlert(NpuException e) {
        new AlertDialog.Builder(WizRoboNpu.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                .setTitle(R.string.str_warming)
                //.setIcon(R.drawable.warming)
                .setMessage("异常：" + e.msg)
                .setPositiveButton(R.string.str_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialoginterface,
                                    int i) {


                            }
                        })
                .setNegativeButton(R.string.str_no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialoginterface,
                                    int i) {
                            }
                        }).show();
    }

    public void ExceptionAlert(Exception e) {
        isNavi = false;
        isSlam = false;
        isTimeout = true;
        isInited = false;
        getDataHandler1.removeCallbacksAndMessages(null);
        getDataHandler.removeCallbacksAndMessages(null);
        setManualVelHandler.removeCallbacksAndMessages(null);
        toConnectNpu = true;
    }

    public void NormalException(Exception e) {
        new AlertDialog.Builder(WizRoboNpu.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                .setTitle(R.string.str_warming)
                //.setIcon(R.drawable.warming)
                .setMessage("异常：" + e.toString())
                .setPositiveButton(R.string.str_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialoginterface,
                                    int i) {


                            }
                        })
                .setNegativeButton(R.string.str_no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialoginterface,
                                    int i) {
                            }
                        }).show();
    }

    public void ConnectFailed() {
        new AlertDialog.Builder(WizRoboNpu.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                .setTitle("连接失败")
                //.setIcon(R.drawable.warming)
                .setMessage("请检查NPU及网络连接是否正常，长按屏幕重新连接！")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialoginterface,
                                    int i) {
                                toConnectNpu = true;
                                displayOnce = false;
                            }
                        }).show();
    }

    public void SetInitPoseAlert() {
        new AlertDialog.Builder(WizRoboNpu.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                .setTitle("确认初始位置")
                //.setIcon(R.drawable.warming)
                .setMessage("请先确认地图上的机器人位置与实际位置是否匹配，重设请到#设置#栏点击设置初始位置。")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialoginterface,
                                    int i) {
                            }
                        }).show();
    }

    public static void setListViewHeightBasedOnChildren(ExpandableListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    private void GetPathStationList() {
        try {

            if (isInited && isNavi) {
                imgStationList = mynpu.GetImgStations(mapname);
                SendMessageToUI("ChangePathStationUI");
            }
        } catch (NpuException e) {
            NpuExceptionAlert(e);
        }

    }

    private void GetPathList() {
        try {
            if (isInited) {
                imgPathList = mynpu.GetImgPaths(mapname);
                SendMessageToUI("ChangePathUI");
            }
        } catch (NpuException e) {
            NpuExceptionAlert(e);
        }
    }

    private void GetVirtualWallList() {
        try {
            if (isNavi) {
                imgVirtualWallList = mynpu.GetImgVirtualWalls(mapname);
                SendMessageToUI("ChangeVirtualWallUI");
            }
        } catch (NpuException e) {
            NpuExceptionAlert(e);
        }
    }

    private void SaveVirtualWall() {
        if (mynpu.isInited) {
            if (!isSettingVirtualWall) {
                isSettingVirtualWall = true;
                pathPoseNum = 0;
                setImgposeU = 0;
                setImgposeV = 0;
                setImgposeTheta = 0;
                setImgPoseU_float = 0;
                setImgPoseV_float = 0;
                Toast toast = Toast.makeText(getApplicationContext(), "请在地图上选点,长按2秒可取消设置！", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                strSaveVirtualWall = "保存";
            } else {
                isSettingVirtualWall = false;
                strSaveVirtualWall = "添加虚拟墙";
                if (pathPoseNum == 0)
                    return;
                final EditText et_pathnamex = new EditText(WizRoboNpu.this);
                final AlertDialog dialog = new AlertDialog.Builder(WizRoboNpu.this)
                        .setTitle("提示")
                        // .setIcon(R.drawable.warming)
                        .setMessage("请输入虚拟墙名称！")
                        //.setMessage("不能包含@#￥%&*等特殊字符！")
                        .setView(et_pathnamex)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", null)
                        .setCancelable(true)
                        .create();
                dialog.show();

                //为了避免点击 positive 按钮后直接关闭 dialog,把点击事件拿出来设置
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {

                                                                                     @Override
                                                                                     public void onClick(View v) {
                                                                                         try {

                                                                                             String name = et_pathnamex.getText().toString();
                                                                                             dialog.dismiss();
                                                                                             listVirtualWallId.clear();

                                                                                             ImgVirtualWall[] newImgVirtualWallList = mynpu.GetImgVirtualWalls(mapname);
                                                                                             List<ImgVirtualWall> imgVirtualWalls = new ArrayList<ImgVirtualWall>(0);

                                                                                             if (newImgVirtualWallList == null) {
                                                                                             } else {
                                                                                                 for (int i = 0; i < newImgVirtualWallList.length; i++) {
                                                                                                     imgVirtualWalls.add(newImgVirtualWallList[i]);
                                                                                                 }
                                                                                             }
                                                                                             VirtualWallInfo newVirtualWallInfo = new VirtualWallInfo();
                                                                                             ImgPoint[] newimgpoint = new ImgPoint[pathPoseNum];
                                                                                             ImgVirtualWall newVirtualWall = new ImgVirtualWall();
                                                                                             newVirtualWallInfo.map_id = mapname;
                                                                                             newVirtualWallInfo.id = name;
                                                                                             newVirtualWallInfo.length = 0;
                                                                                             newVirtualWallInfo.closed = false;
                                                                                             for (int i = 0; i < pathPoseNum; i++) {
                                                                                                 newimgpoint[i] = imgPathPoint[i];
                                                                                             }
                                                                                             newVirtualWall.info = newVirtualWallInfo;
                                                                                             newVirtualWall.points = newimgpoint;
                                                                                             imgVirtualWalls.add(newVirtualWall);

                                                                                             ImgVirtualWall[] newImgVirtualWallList1 = new ImgVirtualWall[imgVirtualWalls.size()];
                                                                                             for (int i = 0; i < imgVirtualWalls.size(); i++) {
                                                                                                 newImgVirtualWallList1[i] = imgVirtualWalls.get(i);
                                                                                             }
                                                                                             mynpu.SetImgVirtualWalls(mapname, newImgVirtualWallList1);

                                                                                             for (int i = 0; i < newImgVirtualWallList1.length; i++) {
                                                                                                 listVirtualWallId.add(newImgVirtualWallList1[i].info.id);
                                                                                             }
                                                                                             imgVirtualWallList = newImgVirtualWallList1;
                                                                                             adapterVirtualWallList.notifyDataSetChanged();
                                                                                             pathPoseNum = 0;

                                                                                             Toast toast = Toast.makeText(getApplicationContext(), "添加成功！", Toast.LENGTH_LONG);
                                                                                             toast.setGravity(Gravity.CENTER, 0, 0);
                                                                                             toast.show();
                                                                                         } catch (NpuException e) {
                                                                                             NpuExceptionAlert(e);
                                                                                         }
                                                                                     }
                                                                                 }
                );

                //为了避免点击 positive 按钮后直接关闭 dialog,把点击事件拿出来设置
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new OnClickListener() {

                                                                                     @Override
                                                                                     public void onClick(View v) {
                                                                                         pathPoseNum = 0;
                                                                                         dialog.dismiss();
                                                                                     }
                                                                                 }
                );
            }
        }
    }

    private void ChangeNaviUI() {
        if (mynpu.isInited) {
            if (!isNavi) {
                try {
                    iv_background.setBackgroundResource(R.drawable.graybackground);
                    ibt_delete_map.setVisibility(View.INVISIBLE);
                    ibt_save_map.setVisibility(View.INVISIBLE);

                    cb_station_display.setVisibility(View.VISIBLE);
                    cb_lidar_display.setVisibility(View.VISIBLE);
                    cb_path_display.setVisibility(View.VISIBLE);
                    cb_cruise_control.setVisibility(View.VISIBLE);
                    ibt_pause.setVisibility(View.VISIBLE);
                    ibt_cancel.setVisibility(View.VISIBLE);
                    sp_map_list.setEnabled(false);
                    toGetImgMap = true;
                    toCoveragePathPlanning = false;
                    isNavi = true;
                    strNaviTrack = "停止导航";
                    adapter_function.getChildView(0, 0, true, track_navi_view, track_navi_parent);
                    Map<String, List<String>> itemData1 = ExpandableListData.getData("Operate");
                    List<String> title1 = new ArrayList<String>(itemData1.keySet());
                    adapter_operate = new ExpandableListAdapter(title1, itemData1, "Operate", this);
                    elv_operate.setAdapter(adapter_operate);
                    strSetGoalPose = "设置目标点";
                    Map<String, List<String>> itemData2 = ExpandableListData.getData("Setting");
                    List<String> title2 = new ArrayList<String>(itemData2.keySet());
                    adapter_setting = new ExpandableListAdapter(title2, itemData2, "Setting", this);
                    elv_setting.setAdapter(adapter_setting);
                    sv_control.setVisibility(View.VISIBLE);
                    listPathStationId.clear();
                    adapterPathStationList.notifyDataSetChanged();
                    SetInitPoseAlert();
                    elv_setting.expandGroup(0);
                    elv_function.collapseGroup(0);
                } catch (Exception e) {
                    ExceptionAlert(e);
                }
            } else {
                cb_station_display.setVisibility(View.INVISIBLE);
                cb_lidar_display.setVisibility(View.INVISIBLE);
                cb_path_display.setVisibility(View.INVISIBLE);
                cb_cruise_control.setVisibility(View.INVISIBLE);
                ibt_pause.setVisibility(View.INVISIBLE);
                ibt_cancel.setVisibility(View.INVISIBLE);
                isNavi = false;
                strNaviTrack = "开始导航";
                adapter_function.getChildView(0, 0, true, track_navi_view, track_navi_parent);
                sp_map_list.setEnabled(true);
                sv_control.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void ChangeSlamUI() {
        if (mynpu.isInited) {

            if (!isSlam) {
                isSettingCoveragePath = false;
                iv_background.setBackgroundResource(R.drawable.graybackground);
                sp_map_list.setEnabled(false);
                ibt_delete_map.setVisibility(View.INVISIBLE);
                ibt_save_map.setVisibility(View.VISIBLE);

                cb_cruise_control.setVisibility(View.VISIBLE);
                isSlam = true;
                strSlam = "停止建图";
                adapter_function.getChildView(0, 1, true, slam_view, slam_parent);
                toGetImgMap = true;
            } else {

                final EditText et_mapnamex = new EditText(WizRoboNpu.this);
                final AlertDialog dialog = new AlertDialog.Builder(WizRoboNpu.this)
                        .setTitle(R.string.str_notice)
                        // .setIcon(R.drawable.warming)
                        .setMessage(R.string.str_notice_inputmapname)
                        //.setMessage("不能包含@#￥%&*等特殊字符！")
                        .setView(et_mapnamex)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", null)
                        .setCancelable(true)
                        .create();
                dialog.show();

                //为了避免点击 positive 按钮后直接关闭 dialog,把点击事件拿出来设置
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {

                                                                                     @Override
                                                                                     public void onClick(View v) {
                                                                                         try {
                                                                                             boolean a;
                                                                                             String name = et_mapnamex.getText().toString();
                                                                                             if (name == null || name.length() <= 0) {
                                                                                                 mynpu.StopSlam("");
                                                                                             } else {
                                                                                                 mynpu.StopSlam(name);
                                                                                                 Toast toast = Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT);
                                                                                                 toast.setGravity(Gravity.CENTER, 0, 0);
                                                                                                 toast.show();
                                                                                             }
                                                                                             mapInfosChanged = true;
                                                                                             Delay(1000);
                                                                                             isSlam = false;
                                                                                             strSlam = "开始建图";
                                                                                             toGetImgMap = false;
                                                                                             cb_cruise_control.setVisibility(View.INVISIBLE);
                                                                                             adapter_function.getChildView(0, 1, true, slam_view, slam_parent);
                                                                                             sp_map_list.setEnabled(true);

                                                                                             dialog.dismiss();

                                                                                         } catch (NpuException e) {
                                                                                             NpuExceptionAlert(e);
                                                                                         }

                                                                                     }
                                                                                 }
                );

            }
        }
    }

    private void ChangePathStationUI() {

        if (isInited && isNavi) {
            listPathStationId.clear();

            if (imgStationList == null || imgStationList.length == 0) {
                listPathStationId.add("站点列表:空");
                adapterPathStationList.notifyDataSetChanged();
                return;
            }

            for (int i = 0; i < imgStationList.length; i++) {
                listPathStationId.add(imgStationList[i].info.id);
            }

            adapterPathStationList.notifyDataSetChanged();
        }
    }

    private void ChangePathUI() {
        if (isInited) {
            pathListId.clear();
            if (imgPathList == null || imgPathList.length == 0) {
                pathListId.add("路径列表:空");
                adapterPathList.notifyDataSetChanged();
                return;
            }

            for (int i = 0; i < imgPathList.length; i++) {
                pathListId.add(imgPathList[i].info.id);
            }
            adapterPathList.notifyDataSetChanged();
        }
    }

    private void ChangeTaskUI() {
        taskListId.clear();

        if (taskList == null || taskList.length == 0) {
            taskListId.add("任务列表:空");
            adapterTaskList.notifyDataSetChanged();
            return;
        }

        for (int i = 0; i < taskList.length; i++) {
            taskListId.add(taskList[i].info.task_id);
        }

        adapterTaskList.notifyDataSetChanged();
    }

    @SuppressLint("SetTextI18n")
    private void ChangeWifiUI() {
        tv_wifi_strength.setTextColor(Color.rgb(255, 116, 0));
        tv_wifi_strength.setText("   信号" + Integer.toString(wifiStrength) + " " + "良好");
        Toast toast = Toast.makeText(getApplicationContext(), "Wifi信号弱！", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);

        if (wifiStrength < -69) {
            toStopGetImgMap = true;
            tv_wifi_strength.setTextColor(Color.rgb(207, 42, 48));
            tv_wifi_strength.setText("   信号" + Integer.toString(wifiStrength) + " " + "弱");
        } else {
            if (isSlam == true && toStopGetImgMap == true) {
                toStopGetImgMap = false;
                toast.cancel();
            }
        }

        if (!wifiIsConnected) {
            tv_wifi_strength.setTextColor(Color.rgb(207, 42, 48));
            tv_wifi_strength.setText(" wifi未连接！");
        }
    }

    public void SendMessageToUI(String a) {
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putString("value", a);
        msg.setData(data);
        updateUiHandler.sendMessage(msg);
        msg = null;
        data = null;
    }

    private void SelectMapNotice() {
        Toast toast = Toast.makeText(getApplicationContext(), "请先选择地图！", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void ConnectSuccess() {
        tv_ip.setText(strServerIP);
        Toast toast = Toast.makeText(getApplicationContext(), "连接成功！", Toast.LENGTH_LONG);
        toast.show();
    }

    private void ChangeVirtualWallUI() {
        listVirtualWallId.clear();

        if (imgVirtualWallList == null || imgVirtualWallList.length == 0) {
            listVirtualWallId.add("虚拟墙:空");
            adapterVirtualWallList.notifyDataSetChanged();
            return;
        }

        for (int i = 0; i < imgVirtualWallList.length; i++) {
            listVirtualWallId.add(imgVirtualWallList[i].info.id);
        }
        adapterVirtualWallList.notifyDataSetChanged();
    }

    private void SettingCancel() {
        Toast toast = Toast.makeText(getApplicationContext(), "设置已取消！", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void SettingNotice() {
        Toast toast = Toast.makeText(getApplicationContext(), "可通过摇杆或滑动改变方向，长按2秒取消设置！", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if ("file".equalsIgnoreCase(uri.getScheme())) {//使用第三方应用打开
                Toast.makeText(this, uri.getPath(), Toast.LENGTH_SHORT).show();
                return;
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                String path = getPath(this, uri);
            } else {//4.4一下系统调用方法
                Toast.makeText(WizRoboNpu.this, getRealPathFromURI(uri), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (null != cursor && cursor.moveToFirst()) {
            ;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }

    /**
     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
     */
    @SuppressLint("NewApi")
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private void CheckSensorStatus() {
        if (isInited) {
            if (!isNavi || !isSlam) {
                try {
                    createProgressDialog();
                    mSensorStatus = mynpu.GetSensorStatus();
                    isGettingSensorstatus = false;
                    SendMessageToUI("CheckSensorStatus");
                } catch (Exception e) {
                }
            }
        }
    }

    private void DisPlaySensorStatus() {
        if (mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(WizRoboNpu.this);
        //builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("硬件状态异常！ 请检查硬件连接和配置，点击 '确定'进入参数设置");
        String[] status = new String[mSensorStatus.length * 4];


        List<String> statuslist = new ArrayList<String>(0);


        for (int i = 0; i < mSensorStatus.length; i++) {
            status[i * 4] = "硬件名称：" + mSensorStatus[i].sensor_id;
            status[i * 4 + 1] = "连接状态：" + mSensorStatus[i].hardware_status.name();
            status[i * 4 + 2] = "数据   ：" + mSensorStatus[i].topic_status.name();
            status[i * 4 + 3] = "节点   ：" + mSensorStatus[i].node_status.name();

            if (mSensorStatus[i].hardware_status == DiagnosticsStatus.DISCONNECT || mSensorStatus[i].topic_status == DiagnosticsStatus.DATA_ERROR) {
                for (int j = 0; j < 4; j++) {
                    statuslist.add(status[i * 4 + j]);
                }
            }
        }

        if (statuslist.size() == 0) {
            Toast.makeText(WizRoboNpu.this, "硬件状态正常！", Toast.LENGTH_SHORT).show();
            toGetSensorData = true;
        } else {

            final String[] statusdata = new String[statuslist.size()];
            for (int i = 0; i < statuslist.size(); i++) {
                statusdata[i] = statuslist.get(i);
            }
            //    设置一个下拉的列表选择项
            builder.setItems(statusdata, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Toast.makeText(WizRoboNpu.this, "选择的城市为：" + cities[which], Toast.LENGTH_SHORT).show();
                }
            });

            builder.setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            builder.setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            builder.show();
        }
    }

    private void ChangeProgressBar() {
        pg_my_progressbar.setProgress(progress);
        pg_my_progressbar.setVisibility(View.VISIBLE);

        if (progress == 0) {
            Toast.makeText(WizRoboNpu.this, "开始传输文件...", Toast.LENGTH_SHORT).show();
        }
        if (progress == 100) {
            pg_my_progressbar.setVisibility(View.INVISIBLE);
            Toast.makeText(WizRoboNpu.this, "传输成功！若为导出文件，存于根目录中，名称为：Npu导出文件...", Toast.LENGTH_LONG).show();
        }
    }

    public void GetSensorStatus() {
        if (isInited && !isGettingSensorstatus)
            CheckSensorStatus();
    }

    private void createProgressDialog() {
        mContext = this;
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("硬件自检中，请稍候...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }


    private void SetMapInfoSucceed() {
        mapInfosChanged = true;
        Toast toast = Toast.makeText(getApplicationContext(), "地图删除成功！", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void GetMapInfos() {
        try {
            if (mapInfoList == null || mapInfoList.length == 0)
                return;
            for (int i = 0; i < mapInfoList.length; i++) {
                listMapId.add(mapInfoList[i].id);
            }
            iv_background.setBackgroundResource(R.drawable.graybackground);
            mapname = mapInfoList[0].id;
            ReadThumbnail();                  //选择地图时显示缩略图
            Display();
            adapterMapList.notifyDataSetChanged();
            ibt_delete_map.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            ExceptionAlert(e);
        }
    }

    private void GetServerVersion() {
        tv_npu_version.setText(str_npu_version);
    }


    private void DisplayLidar() {
        iv_background.setBackgroundResource(R.drawable.graybackground);
        int w = 1000, h = 1000;
        Config conf = Config.ARGB_8888; // see other conf types
        Bitmap bmp = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap
        canvas = new Canvas(bmp);
        if (lidarScanData == null)
            return;
        Point3D[] points;
        points = lidarScanData.points;
        if (points == null)
            return;
        float[] lidarpointdata = new float[2 * points.length];

        for (int i = 0; i < points.length; i++) {
            lidarpointdata[2 * i] = (float) points[i].x;
            lidarpointdata[2 * i + 1] = (float) points[i].y;
        }

        paint.setColor(Color.RED);
        paint.setStrokeWidth(3.0f);
        canvas.drawPoints(lidarpointdata, paint);
        paint.setColor(Color.BLUE);
        canvas.drawCircle(500, 500, 4, paint);
        iv_background.setImageBitmap(bmp);
    }


    private void GetTaskList() {
        try {
            if (WizRoboNpu.isInited) {

                taskList = WizRoboNpu.mynpu.GetTaskList(WizRoboNpu.mapname);

                taskListId.clear();

                if (taskList == null || taskList.length == 0) {
                    taskListId.add("任务列表:空");
                    adapterTaskList.notifyDataSetChanged();
                    return;
                }

                for (int i = 0; i < taskList.length; i++) {
                    taskListId.add(taskList[i].info.task_id);
                }

                adapterTaskList.notifyDataSetChanged();

            }
        } catch (NpuException e) {
            NpuExceptionAlert(e);
        }

    }

    private void ExcuteTask() {

        if (mynpu.isInited) {
            if (taskList == null || taskList.length <= 0)
                return;
            final AlertDialog dialog = new AlertDialog.Builder(WizRoboNpu.this)
                    .setTitle("提示")
                    // .setIcon(R.drawable.warming)
                    .setMessage("是否执行任务：" + sp_task_list.getSelectedItem().toString())
                    //.setMessage("不能包含@#￥%&*等特殊字符！")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("确定", null)
                    .setCancelable(true)
                    .create();
            dialog.show();

            //为了避免点击 positive 按钮后直接关闭 dialog,把点击事件拿出来设置
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {

                                                                                 @Override
                                                                                 public void onClick(View v) {

                                                                                     Task[] newtasklist = new Task[1];
                                                                                     newtasklist[0] = taskList[sp_task_list.getSelectedItemPosition()];
                                                                                     try {
                                                                                         mynpu.ExecuteTask(newtasklist);
                                                                                         dialog.dismiss();
                                                                                     } catch (NpuException e) {
                                                                                         NpuExceptionAlert(e);
                                                                                     }


                                                                                 }
                                                                             }
            );

            //为了避免点击 positive 按钮后直接关闭 dialog,把点击事件拿出来设置
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new OnClickListener() {

                                                                                 @Override
                                                                                 public void onClick(View v) {
                                                                                     dialog.dismiss();
                                                                                 }
                                                                             }
            );
        }
    }

    private void CollapseGroup(ExpandableListView view) {
        for (int i = 0, count = view.getExpandableListAdapter().getGroupCount(); i < count; i++) {
            view.collapseGroup(i);
        }
    }

    private void AddCoverageArea() {
        if (mynpu.isInited) {
            if (!isSettingPathpose && !isSettingStationPath) {
                isSettingPathpose = true;
                toCoveragePathPlanning = true;
                pathPoseNum = 0;
                setImgposeU = 0;
                setImgposeV = 0;
                redarrowYaw = 0;
                setImgposeTheta = 0;
                setImgPoseU_float = 0;
                setImgPoseV_float = 0;
                Toast toast = Toast.makeText(getApplicationContext(), "请在地图上划定区域！", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                strAddCoverageArea = "保存";
            } else if (isSettingPathpose && !isSettingStationPath) {
                isSettingPathpose = false;
                toCoveragePathPlanning = false;
                strAddCoverageArea = "添加清扫区域";
                if (pathPoseNum == 0)
                    return;
                final EditText et_pathnamex = new EditText(WizRoboNpu.this);
                et_pathnamex.setText("cov_");
                final AlertDialog dialog = new AlertDialog.Builder(WizRoboNpu.this)
                        .setTitle("提示")
                        // .setIcon(R.drawable.warming)
                        .setMessage("请输入清扫区域名称！")
                        //.setMessage("不能包含@#￥%&*等特殊字符！")
                        .setView(et_pathnamex)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", null)
                        .setCancelable(true)
                        .create();
                dialog.show();

                //为了避免点击 positive 按钮后直接关闭 dialog,把点击事件拿出来设置
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(new OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    try {
                                                        boolean a;
                                                        String name = et_pathnamex.getText().toString();
                                                        if (name == null || name.length() <= 0) {
                                                        } else {
                                                            dialog.dismiss();
                                                        }
                                                        pathListId.clear();
                                                        ImgPath[] newImgPathList = imgPathList;
                                                        List<ImgPath> imgPaths = new ArrayList<ImgPath>(0);
                                                        if (newImgPathList == null) {
                                                        } else {
                                                            for (int i = 0; i < newImgPathList.length; i++) {
                                                                imgPaths.add(newImgPathList[i]);
                                                            }
                                                        }
                                                        PathInfo newpathinfo = new PathInfo();
                                                        ImgPose[] newimgpose = new ImgPose[pathPoseNum];
                                                        ImgPath newpath = new ImgPath();
                                                        newpathinfo.map_id = mapname;
                                                        newpathinfo.id = name;
                                                        newpathinfo.length = 0;
                                                        newpathinfo.pose_num = pathPoseNum;
                                                        for (int i = 0; i < pathPoseNum; i++) {
                                                            newimgpose[i] = imgPathPose[i];
                                                        }
                                                        newpath.info = newpathinfo;
                                                        newpath.poses = newimgpose;
                                                        imgPaths.add(newpath);
                                                        ImgPath[] newImgPathList1 = new ImgPath[imgPaths.size()];
                                                        for (int i = 0; i < imgPaths.size(); i++) {
                                                            newImgPathList1[i] = imgPaths.get(i);
                                                        }
                                                        mynpu.SetImgPaths(mapname, newImgPathList1);
                                                        for (int i = 0; i < newImgPathList1.length; i++) {
                                                            pathListId.add(newImgPathList1[i].info.id);
                                                        }
                                                        imgPathList = newImgPathList1;
                                                        adapterPathList.notifyDataSetChanged();
                                                        //GetPathList();
                                                        pathPoseNum = 0;

                                                        Toast toast = Toast.makeText(getApplicationContext(), "添加成功！", Toast.LENGTH_LONG);
                                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                                        toast.show();
                                                    } catch (NpuException e) {
                                                        NpuExceptionAlert(e);
                                                        return;
                                                    }
                                                }
                                            }
                        );

                //为了避免点击 positive 按钮后直接关闭 dialog,把点击事件拿出来设置
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setOnClickListener(new OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    pathPoseNum = 0;
                                                    dialog.dismiss();

                                                }
                                            }
                        );
            }
        }
    }

    private void DetectUnfinishedTask() {
        final EditText et_pathnamex = new EditText(WizRoboNpu.this);
        final AlertDialog dialog = new AlertDialog.Builder(WizRoboNpu.this)
                .setTitle("检测到未完成任务")
                // .setIcon(R.drawable.warming)
                .setMessage("是否继续执行？(若初始位置准确，执行点击'确定'，不执行点击'取消'；要执行但初始位置不准确，点击'重设初始位置'，到菜单栏'设置初始点'，后点击'继续'按钮执行)")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", null)
                .setNeutralButton("重设初始位置", null)
                .setCancelable(true)
                .create();
        dialog.show();

        //为了避免点击 positive 按钮后直接关闭 dialog,把点击事件拿出来设置
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {

                                                                             @Override
                                                                             public void onClick(View v) {
                                                                                 try {
                                                                                     mynpu.FollowPath(mapname, "unfinished_path");
                                                                                     dialog.dismiss();
                                                                                 } catch (NpuException e) {
                                                                                     NpuExceptionAlert(e);
                                                                                 }

                                                                             }
                                                                         }
        );

        //为了避免点击 positive 按钮后直接关闭 dialog,把点击事件拿出来设置
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new OnClickListener() {

                                                                             @Override
                                                                             public void onClick(View v) {
                                                                                 dialog.dismiss();

                                                                             }
                                                                         }
        );

        //为了避免点击 positive 按钮后直接关闭 dialog,把点击事件拿出来设置
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new OnClickListener() {

                                                                            @Override
                                                                            public void onClick(View v) {
                                                                                ibt_pause.setImageResource(R.drawable.continue3);
                                                                                pause = true;
                                                                                toExcuteUnfinishedTask = true;
                                                                                dialog.dismiss();
                                                                            }
                                                                        }
        );
    }


    private Runnable getDingdianPlayNpuState = new Runnable() {
        @Override
        public void run() {
            int poseCount = 0;

            while (isDingdianPlaying == true) {
                try {
                    naviStateDingdianPlay = mynpu.GetNaviState();
                } catch (NpuException e) {
                    e.printStackTrace();
                }

                if (naviStateDingdianPlay == NaviState.SUCCEEDED) {
                    //检查是否停留在目标点附近

                    if (poseCount == imgStationList.length) {
                        poseCount = 0;
                    }

                    ImgPose imgPose = imgStationList[poseCount].pose;

                    try {
                        currentLXImgPose = mynpu.GetCurrentImgPose();
                    } catch (NpuException e) {
                        e.printStackTrace();
                    }

                    if (Math.abs(imgPose.u - currentLXImgPose.u) < 4 || Math.abs(imgPose.v - currentLXImgPose.v) < 4) {
                        //在目标点附近
                        if (isMusicPlayOnce == false) {
                            poseCount++;

                            isMusicPlayOnce = true;
                            Message msg = new Message();
                            msg.what = GO_POSE_SUCCESS;
                            dingDianPlayHandler.sendMessage(msg);
                        } else {
                            Message msg = new Message();
                            msg.what = 13;
                            dingDianPlayHandler.sendMessage(msg);
                        }
                    } else {
                        //不在目标点附近
                        Message msg = new Message();
                        msg.what = 12;
                        dingDianPlayHandler.sendMessage(msg);
                        lastNaviState = naviStateDingdianPlay;
                    }
                }

                if (isMusicPlaying) {
                    playcount++;
                    Message msg = new Message();
                    msg.what = 11;
                    dingDianPlayHandler.sendMessage(msg);
                    lastNaviState = naviStateDingdianPlay;

                    isMusicPlaying = mediaPlayer.isPlaying();
                    if (isMusicPlaying == false) {
                        //play music over
                        Delay(2500);
                        Message mssg = new Message();
                        mssg.what = 14;
                        dingDianPlayHandler.sendMessage(mssg);
                        lastNaviState = naviStateDingdianPlay;
                    }
                    Delay(2000);


                } else {
                    playcount++;
                    Message msg = new Message();
                    msg.what = 11;
                    dingDianPlayHandler.sendMessage(msg);
                    lastNaviState = naviStateDingdianPlay;
                    Delay(600);
                }

            }
        }
    };

    Handler dingDianPlayHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GOING_POSE:
                    break;

                case GO_POSE_SUCCESS:
                    isMusicPlaying = true;
                    playMusic(dingDianlistItem + 1);
                    tvTextNaviStatus.setText("在附近 dingDianlistItem"+dingDianlistItem);
                    break;

                case MUSICPLAYOVER:
                    tvTestMusicStatus.setText("isMusicPlayOnce = " + isMusicPlayOnce);
                    break;

                case TEST:
                    tvTextNaviStatus.setText("get post =" + naviState);
                    break;

                case 11:
                    tvTestMusicStatus.setText(playcount + ": isMusicPlayOnce = " + isMusicPlayOnce + "  isMusicPlaying = " + isMusicPlaying);
                    tvtextPlaycount.setText("count =" + playcount);
                    break;
                case 12:
                    tvTextNaviStatus.setText("不在附近");
                    break;
                case 13:
                    tvTextNaviStatus.setText("在附近 isMusicPlayOnce = "+ isMusicPlayOnce);
                    break;
                case 14:
                    gotoNextPose();
                    break;

                default:
                    break;

            }
        }
    };

    private void dingDianPlay() {
        if (imgStationList != null) {
            if (isDingdianPlaying == false) {
                isDingdianPlaying = true;
                listCount = imgStationList.length;
                dingDianlistItem = 0;
                if (dingDianPlayNpuStateThread != null && dingDianPlayNpuStateThread.isAlive()) {
                    dingDianPlayNpuStateThread.interrupt();
                }
                dingDianPlayNpuStateThread = new Thread(getDingdianPlayNpuState);
                dingDianPlayNpuStateThread.start();
            }

            gotoPose(dingDianlistItem);
            playFilePic((dingDianlistItem + 1));
        }
    }

    private void gotoPose(int PoseListItemCount) {
        if (isDingdianPlaying == true) {
            try {
                mynpu.GotoStation(mapname, imgStationList[PoseListItemCount].info.id.toString());
                Log.e("play", "dingDianlistItem =" + dingDianlistItem + "listName =" + imgStationList[PoseListItemCount].info.id.toString());
            } catch (NpuException e) {
                e.printStackTrace();
            }
        }
    }

    private void gotoNextPose() {
        if (isDingdianPlaying == true) {
            if (dingDianlistItem < listCount) {
                dingDianlistItem++;
            }
            if (dingDianlistItem == listCount) {
                dingDianlistItem = 0;
            }

            if (isMusicPlayOnce == true) {
                isMusicPlayOnce = false;
            }

            gotoPose(dingDianlistItem);
            playFilePic((dingDianlistItem + 1));

            tvTestPlayStatus.setText("isplaying =" + isDingdianPlaying + "gongint to pose:" + (dingDianlistItem));
        } else {
            tvTestPlayStatus.setText("isDingdianPlaying == false");
        }

    }


    public void playFilePic(int fileId) {
        listPicPaths.clear();
        System.gc();
        listPicPaths = fileUtil.getPicturePathList(PICPATHS + "/" + fileId);

        rollPagerViewDingdianPlay = (RollPagerView) findViewById(R.id.rpvPicturePlay);
        imageLoopAdapter = new ImageLoopAdapter(rollPagerViewDingdianPlay);
        rollPagerViewDingdianPlay.setAdapter(imageLoopAdapter);
        rollPagerViewDingdianPlay.setPlayDelay(2000);
    }

    public class ImageLoopAdapter extends LoopPagerAdapter {
        public ImageLoopAdapter(RollPagerView viewPager) {
            super(viewPager);
        }

        @Override
        public View getView(ViewGroup container, int position) {

            ImageView view = new ImageView(container.getContext());
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            view.setImageBitmap(getDiskBitmap(listPicPaths.get(position)));
            Log.e("getview", "position = " + position);

            return view;
        }

        @Override
        public int getRealCount() {
            if (listPicPaths != null) {
                return listPicPaths.size();
            } else
                return 0;
        }
    }

    private Bitmap getDiskBitmap(String pathString) {
        Bitmap bitmap = null;
        try {
            File file = new File(pathString);
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(pathString);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return bitmap;
    }

    //获取文件读取权限
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

//    private void playMusic(int type) {
//        //启动服务，播放音乐
//        Intent intent = new Intent(this, PlayMusciServices.class);
//        intent.putExtra("type", type);
//        startService(intent);
//        if (type == STOP_MUSIC) {
//            isMusicPlayOver = true;
//            isMusicPlaying = false;
//        }
//    }
//
//
//    private void playMusic(int type, int playItem) {
//        //启动服务，播放音乐
//        Intent intent = new Intent(this, PlayMusciServices.class);
//        intent.putExtra("type", type);
//        intent.putExtra("playItem", playItem);
//        startService(intent);
//    }


    private void setMusicMediaPlayer(int playID) {
        try {
            String strMusicpath = fileUtil.getMusicPath(MUSICPATH + playID);
            File file = new File(strMusicpath);
            mediaPlayer.setDataSource(file.getPath());//指定音频文件路径
            mediaPlayer.setLooping(false);//循环播放
            mediaPlayer.prepare();//初始化播放器MediaPlayer

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playMusic(int playID) {
        mediaPlayer.reset();
        setMusicMediaPlayer(playID);
        mediaPlayer.start();
    }

    private void stopGoPose() {
        try {
            if (mynpu.isInited) {
                mynpu.CancelTask();
                Toast toast = Toast.makeText(getApplicationContext(), "已停止执行任务! ", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        } catch (NpuException e) {
            NpuExceptionAlert(e);
            return;
        }
    }


}



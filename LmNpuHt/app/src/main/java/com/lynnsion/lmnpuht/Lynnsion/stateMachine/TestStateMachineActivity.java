package com.lynnsion.lmnpuht.Lynnsion.stateMachine;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lynnsion.lmnpuht.R;

import java.lang.ref.WeakReference;


public class TestStateMachineActivity extends Activity implements View.OnClickListener, UpdateUIListener {

    private Button btnPlayStart, btnMuisicOver, btnReachPose;

    private static boolean isDingdianPlaying = false;

    private NaviStateMachine naviStateMachine = null;
    private Handler mHandler = null;

    private Thread dingDianPlayNpuStateThread;

    private TextView tv_msg,tv_state;

    private static int count =0;

    private static String macheState = "default";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initLayout();

        naviStateMachine = new NaviStateMachine();
        naviStateMachine.registerListener(this);

        mHandler = new MyHandler(this);
    }

    private void initLayout() {
        btnPlayStart = (Button) findViewById(R.id.btnPlayStart);
        btnPlayStart.setOnClickListener(this);
        btnMuisicOver = (Button) findViewById(R.id.btnPlayMusicOver);
        btnMuisicOver.setOnClickListener(this);
        btnReachPose = (Button) findViewById(R.id.btnReachPose);
        btnReachPose.setOnClickListener(this);

        tv_msg = (TextView) findViewById(R.id.tv_msg);
        tv_state = (TextView) findViewById(R.id.tv_state);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnReachPose:
                //play muisc
                naviStateMachine.reachPose();

                break;
            case R.id.btnPlayStart:
                // start play
                if (isDingdianPlaying == false) {
                    isDingdianPlaying = true;
                    startPlay();
                    btnPlayStart.setText("finish play");
                } else {
                    isDingdianPlaying = false;
                    btnPlayStart.setText("start play");
                }

                break;
            case R.id.btnPlayMusicOver:
                // goto next pose
                naviStateMachine.playMusicOver();
                break;

        }

    }

    private void startPlay() {
        dingDianPlayNpuStateThread = new Thread(getDingdianPlayNpuState);
        dingDianPlayNpuStateThread.start();
    }

    static class MyHandler extends Handler {
        WeakReference<TestStateMachineActivity> mActivity = null;

        public MyHandler(TestStateMachineActivity act) {
            mActivity = new WeakReference<>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            TestStateMachineActivity testStateMachineActivity = mActivity.get();
            testStateMachineActivity.update((String) msg.obj);
        }
    }


    @Override
    public void update(String tip) {
//        tv_state.setText(tip);
        Log.e("test",tip);
        macheState = tip;
    }


    private Runnable getDingdianPlayNpuState = new Runnable() {
        @Override
        public void run() {
            while (isDingdianPlaying == true) {

                Message msg = new Message();
                msg.what = 1;
                dingDianPlayHandler.sendMessage(msg);

                if(macheState.equals("GoingState")){
                    Message mssg = new Message();
                    mssg.what = 2;
                    dingDianPlayHandler.sendMessage(mssg);
                }else if(macheState.equals("PlayingMusicState")){
                    Message mssg = new Message();
                    mssg.what = 3;
                    dingDianPlayHandler.sendMessage(mssg);
                }

                try {
                    Thread.sleep(700);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    Handler dingDianPlayHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    count ++;
                    tv_msg.setText("count = " + count);

                    break;

                case 2:
                    tv_state.setText("going to next play");
                    break;

                case 3:
                    tv_state.setText("playing muisc");
                    break;

                default:
                    break;
            }
        }
    };


}

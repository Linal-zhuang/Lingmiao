package com.lynnsion.lmnpuht;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import wizrobo_npu.NpuIcePrx;
import wizrobo_npu.ServerState;

/**
 * Created by Lynnsion on 2018/4/4.
 */

public class DingdianActivity extends AppCompatActivity implements JoystickView.JoystickListener{

    private JoystickView joystickViewDingdian;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dingdian_layout);

        initLayout();
    }

    private void initLayout() {
        joystickViewDingdian = (JoystickView) findViewById(R.id.joystickDingdian);
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
}

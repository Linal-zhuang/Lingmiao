package com.lynnsion.lmnpuht.Lynnsion;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lynnsion.lmnpuht.R;

import java.io.File;

/**
 * Created by Lynnsion on 2018/4/25.
 */

public class MusicPlayActivity extends Activity implements View.OnClickListener {

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Button btnMusicPlay, btnMusicNext, btnMusicStopAll, btnMusicStop;

    private final String MUSICPATH = "/mnt/sdcard/tuPian/";

    private FileUtil fileUtil = new FileUtil();

    private int countId = 1, playListCount = 5;

    private static boolean isPlay = false;

    private Thread playThread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_musicplay);

        //权限判断，如果没有权限就请求权限
        if (ContextCompat.checkSelfPermission(MusicPlayActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MusicPlayActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
//            initMediaPlayer();//初始化播放器 MediaPlayer

            setMusicMediaPlayer(countId);
        }


        initLayout();


    }

    private void initMediaPlayer() {
        try {
            String strMusicpath = fileUtil.getMusicPath(MUSICPATH + 2);
            File file = new File(strMusicpath);
            mediaPlayer.setDataSource(file.getPath());//指定音频文件路径
            mediaPlayer.setLooping(false);//循环播放
            mediaPlayer.prepare();//初始化播放器MediaPlayer

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    private void initLayout() {
        btnMusicStopAll = (Button) findViewById(R.id.btnMusicStopAll);
        btnMusicStopAll.setOnClickListener(this);

        btnMusicNext = (Button) findViewById(R.id.btnMusicNext);
        btnMusicNext.setOnClickListener(this);

        btnMusicPlay = (Button) findViewById(R.id.btnMusicPlay);
        btnMusicPlay.setOnClickListener(this);

        btnMusicStop = (Button) findViewById(R.id.btnMusicStop);
        btnMusicStop.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnMusicStopAll:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.reset();
                }
                isPlay = false;
                break;
            case R.id.btnMusicNext:
                countId++;
                if (countId >= 6) {
                    countId = 1;
                }
                playMusic(countId);


                break;
            case R.id.btnMusicPlay:
                //如果没在播放中，立刻开始播放。
//                if (!mediaPlayer.isPlaying()) {
//                    mediaPlayer.start();
//                    Log.e("123", "sss=" + countId);
//                }
                playLoop();
                Log.e("123", "loop start countID = " + countId);
                break;
            case R.id.btnMusicStop:
                //如果在播放中，立刻停止。
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.reset();
//                    initMediaPlayer();//初始化播放器 MediaPlayer
//                    setMusicMediaPlayer(countId);
                }

                break;

            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
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
//                    initMediaPlayer();
                    setMusicMediaPlayer(countId);
                } else {
                    Toast.makeText(this, "拒绝权限，将无法使用程序。", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            default:
        }
    }


    private void playLoop() {
        if (isPlay == false) {
            isPlay = true;

            if (playThread != null && playThread.isAlive()) {
                playThread.interrupt();
            }


            playThread = new Thread(playRunnable);
            playThread.start();
        }
    }

    private Runnable playRunnable = new Runnable() {
        @Override
        public void run() {
            playMusic(countId);
            while (isPlay) {
                if (mediaPlayer.isPlaying()) {
                    Log.e("123", "music playing");
                } else {
                    Log.e("123", "music play over");
                    countId++;
                    if (countId >= 6) {
                        countId = 1;
                    }
                    playMusic(countId);
                    Log.e("loop", "loop  countId=" + countId);
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}

package com.lynnsion.lmnpuht.Lynnsion;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;


public class PlayMusciServices extends Service {

    //用于播放音乐等媒体资源
    private MediaPlayer mediaPlayer;

    //标志判断播放歌曲是否是停止之后重新播放，还是继续播放
    private boolean isStop = true;

    private FileUtil fileUtil = new FileUtil();

    private final String MUSICPATH = "/mnt/sdcard/tuPian/";

    private boolean isMusicPlay = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();

            //为播放器添加播放完成时的监听器
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    //发送广播到MainActivity
                    Intent intent = new Intent();
                    intent.setAction("com.complete");
                    sendBroadcast(intent);
                }
            });
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int getPlayItem = intent.getIntExtra("playItem", 0);
        switch (intent.getIntExtra("type", -1)) {
            case LoadPictureAndMusic.PLAY_MUSIC:
                if (isStop) {
                    mediaPlayer.reset();
                    //将需要播放的资源与之绑定
//                mediaPlayer = MediaPlayer.create(this, android.R.raw.birds);
                    try {
                        String strMusicpath = "";
                        if (getPlayItem != 0) {
                            strMusicpath = fileUtil.getMusicPath("/mnt/sdcard/tuPian/" + getPlayItem);
                        } else {
                            strMusicpath = fileUtil.getMusicPath(MUSICPATH + 2);
                        }

                        mediaPlayer.setDataSource(this, Uri.parse("file:/" + strMusicpath));
                        mediaPlayer.prepare();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //开始播放
                    mediaPlayer.start();
                    isMusicPlay = true;
                    //是否循环播放
                    mediaPlayer.setLooping(false);
                    isStop = false;
                } else if (!isStop && mediaPlayer.isPlaying() && mediaPlayer != null) {
                    mediaPlayer.start();
                }

                break;
            case LoadPictureAndMusic.PAUSE_MUSIC:
                //播放器不为空，并且正在播放
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
                break;
            case LoadPictureAndMusic.STOP_MUSIC:
                if (mediaPlayer != null) {
                    //停止之后要开始播放音乐
                    mediaPlayer.stop();
                    isMusicPlay = false;
                    isStop = true;
                }
                break;

            default:
                break;
        }

        return START_NOT_STICKY;
    }


    public boolean getPlayState(){
        if(mediaPlayer.isPlaying()){
            isMusicPlay = true;
        }
        if(mediaPlayer.isPlaying() == false){
            isMusicPlay = false;
        }
        return isMusicPlay;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

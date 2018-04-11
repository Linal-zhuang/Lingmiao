package com.lynnsion.lmnpuht.Lynnsion;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.LoopPagerAdapter;
import com.lynnsion.lmnpuht.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lynnsion on 2018/4/10.
 */

public class LoadPictureAndMusic extends AppCompatActivity implements View.OnClickListener {

    private static List<String> listPath = new ArrayList<>();

    private RollPagerView rollPagerViewPlay;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private Button btnStop, btnNext, btnBack;

    private FileUtil fileUtil = new FileUtil();

    private ImageLoopAdapter imageLoopAdapter;

    private int height, width;

    private MediaPlayer mediaPlayer;

    /**
     * 规定开始音乐、暂停音乐、结束音乐的标志
     */
    public  static final int PLAY_MUSIC=1;
    public  static final int PAUSE_MUSIC=2;
    public  static final int STOP_MUSIC=3;

    private MusicBroadCastReceiver receiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load_pic_layout);

        verifyStoragePermissions(LoadPictureAndMusic.this);

        listPath = fileUtil.getPicturePathList("/mnt/sdcard/tuPian" + "/" + 2);

        rollPagerViewPlay = (RollPagerView) findViewById(R.id.picPagerView);
        imageLoopAdapter = new ImageLoopAdapter(rollPagerViewPlay);
        rollPagerViewPlay.setAdapter(imageLoopAdapter);
        rollPagerViewPlay.setPlayDelay(2000);

        btnStop = (Button) findViewById(R.id.btnPicStop);
        btnStop.setOnClickListener(this);
        btnBack = (Button) findViewById(R.id.btnPicBack);
        btnBack.setOnClickListener(this);
        btnNext = (Button) findViewById(R.id.btnPicNext);
        btnNext.setOnClickListener(this);


        receiver=new MusicBroadCastReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction("com.complete");
        registerReceiver(receiver,filter);


    }

    public void playFilePic(int fileId) {
        listPath.clear();
        System.gc();
        listPath = fileUtil.getPicturePathList("/mnt/sdcard/tuPian" + "/" + fileId);

        rollPagerViewPlay = (RollPagerView) findViewById(R.id.picPagerView);
        imageLoopAdapter = new ImageLoopAdapter(rollPagerViewPlay);
        rollPagerViewPlay.setAdapter(imageLoopAdapter);
        rollPagerViewPlay.setPlayDelay(2000);
    }


    @Override
    public void onClick(View v) {
        IntentFilter filter=new IntentFilter();
        switch (v.getId()) {
            case R.id.btnPicBack:
                playFilePic(1);

                filter.addAction("com.complete");
                registerReceiver(receiver,filter);
                playMusic(PLAY_MUSIC,2);
                break;
            case R.id.btnPicNext:
                playFilePic(3);

                filter.addAction("com.complete");
                registerReceiver(receiver,filter);
                playMusic(PLAY_MUSIC,3);
                break;
            case R.id.btnPicStop:
                if (rollPagerViewPlay.isPlaying()) {
                    rollPagerViewPlay.pause();
                } else {
                    rollPagerViewPlay.resume();
                }
                playMusic(STOP_MUSIC);


                break;
            default:
                break;
        }
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
            view.setImageBitmap(getDiskBitmap(listPath.get(position)));
            Log.e("getview", "position = " + position);

            return view;
        }

        @Override
        public int getRealCount() {
            if (listPath != null) {
                return listPath.size();
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

//                Log.e("pic size", "memory size =" + bytes2kb(bitmap.getAllocationByteCount()));
//                Log.e("pic size", "height =" + bitmap.getHeight() + "width =" + bitmap.getWidth());
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return bitmap;
    }

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


    private void playMusic(int type) {
        //启动服务，播放音乐
        Intent intent=new Intent(this,PlayMusciServices.class);
        intent.putExtra("type",type);
        startService(intent);
    }



    private void playMusic(int type, int playItem) {
        //启动服务，播放音乐
        Intent intent=new Intent(this,PlayMusciServices.class);
        intent.putExtra("type",type);
        intent.putExtra("playItem",playItem);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

//    public void isPlayOrPause() {
//
//        String str = fileUtil.getMusicPath("/mnt/sdcard/tuPian" + "/" + 2);
//        Log.e("music","get path=" + str);
//
//        if (mediaPlayer == null) {
//            mediaPlayer = new MediaPlayer();
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            try {
//                mediaPlayer.setDataSource(this, Uri.parse("file:/"+str));
//                mediaPlayer.prepare();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            mediaPlayer.start();
//            int duration = mediaPlayer.getDuration();
//        } else if (mediaPlayer.isPlaying()) {
//            mediaPlayer.pause();
//
//        } else {
//            mediaPlayer.start();
//
//        }
//    }



//            public int getBitmapSize(Bitmap bitmap) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {     //API 19
//            return bitmap.getAllocationByteCount();
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
//            return bitmap.getByteCount();
//        } else {
//            return bitmap.getRowBytes() * bitmap.getHeight(); //earlier version
//        }
//    }


//    public static String bytes2kb(long bytes) {
//        BigDecimal filesize = new BigDecimal(bytes);
//        BigDecimal megabyte = new BigDecimal(1024 * 1024);
//        float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP)
//                .floatValue();
//        if (returnValue > 1)
//            return (returnValue + "MB");
//        BigDecimal kilobyte = new BigDecimal(1024);
//        returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP)
//                .floatValue();
//        return (returnValue + "KB");
//    }


}

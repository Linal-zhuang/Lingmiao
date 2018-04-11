package com.lynnsion.lmnpuht.Lynnsion;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lynnsion on 2018/4/10.
 */

public class FileUtil {


    public static List<String> listPath = new ArrayList<>();

    public FileUtil() {

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

    public List<String> getPicturePathList(final String strPath) {

        File file = new File(strPath);
        File[] allfiles = file.listFiles();
        if (allfiles == null) {
            return null;
        }
        for (int k = 0; k < allfiles.length; k++) {
            final File fi = allfiles[k];
            if (fi.isFile()) {
                int idx = fi.getPath().lastIndexOf(".");
                if (idx <= 0) {
                    continue;
                }
                String suffix = fi.getPath().substring(idx);
                if (suffix.toLowerCase().equals(".jpg") ||
                        suffix.toLowerCase().equals(".jpeg") ||
                        suffix.toLowerCase().equals(".bmp") ||
                        suffix.toLowerCase().equals(".png") ||
                        suffix.toLowerCase().equals(".gif")) {
                    if (getDiskBitmap(fi.getPath()).getAllocationByteCount() < 32000000) {
                        listPath.add(fi.getPath());
                    }
//                    Log.e("LoadPictureAndMusic","get list path =" + fi.getPath());
                }
            }
        }
        return listPath;
    }


    public String getMusicPath(final String strPath) {
        String musicPath = "";
        File file = new File(strPath);
        File[] allfiles = file.listFiles();
        if (allfiles == null) {
            return null;
        }
        for (int k = 0; k < allfiles.length; k++) {
            final File fi = allfiles[k];
            if (fi.isFile()) {
                int idx = fi.getPath().lastIndexOf(".");
                if (idx <= 0) {
                    continue;
                }
                String suffix = fi.getPath().substring(idx);
                if (suffix.toLowerCase().equals(".mp3")) {
                    musicPath = fi.getPath();
//                    Log.e("LoadPictureAndMusic","get list path =" + fi.getPath());
                }
            }
        }

        return musicPath;
    }

}

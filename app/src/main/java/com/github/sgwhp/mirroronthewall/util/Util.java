package com.github.sgwhp.mirroronthewall.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by robust on 2015/9/23.
 */
public class Util {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public static boolean copyFileFromAsset(Context context, String sourcePath,
                                            String destPath, String fileName) {
        try {
            File dir = new File(destPath);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    return false;
                }
            }
            File toFile = (new File(destPath + "/" + fileName));
            if (toFile.exists()) {
                toFile.delete();
            }
            AssetManager assets = context.getAssets();
            InputStream is = assets.open(sourcePath);
            FileOutputStream fos = new FileOutputStream(destPath + "/"
                    + fileName);
            byte[] buffer = new byte[8192];
            int count;
            while ((count = is.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
            }
            fos.close();
            is.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Date parseDateTime(@Nullable String date){
        if(date == null){
            return null;
        }
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String formatDate(Date date){
        return sdf.format(date);
    }

    public static int[] parseTime(String time){
        int[] result = new int[]{0, 0, 0};
        String[] strs = time.split(":");
        try {
            for (int i = 0; i < result.length; i++) {
                result[i] = Integer.parseInt(strs[i]);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}

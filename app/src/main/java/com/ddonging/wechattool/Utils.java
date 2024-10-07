package com.ddonging.wechattool;

import static android.content.ContentResolver.SCHEME_CONTENT;
import static android.content.ContentResolver.SCHEME_FILE;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;



public class Utils {
    //拷贝APK文件到App内并去除.1后缀
    public static String getFilePathFromURI(Context context, Uri uri){
        String fileName = getFileNameRemoveSuffix(uri);
        if (!TextUtils.isEmpty(fileName)) {
            File copyFile = new File(context.getExternalCacheDir() + File.separator + fileName);
            if (copyApkFile(context, uri, copyFile)) {
                return copyFile.getAbsolutePath();
            }
        }
        return null;
    }

    //获取去掉.1后的文件名
    private static String getFileNameRemoveSuffix(Uri uri){
        String fileName = null;
        if (!uri.getPath().contains(".apk.1")) {
            return null;
        }else
        {
            String path = uri.getPath();
            int cut = path.lastIndexOf('/');
            int end = path.lastIndexOf('k');
            if (cut != -1) {
                fileName = path.substring(cut + 1, end + 1);
            }
        }
        return fileName;
    }

    //读取Uri拷贝Apk
    private static Boolean copyApkFile(Context context, Uri uri, File file) {
        try {
            //解析外部传来的是File:// 还是 Content://
            InputStream inputStream;
            if (SCHEME_FILE.equals(uri.getScheme())) {
                //需要读取存储权限
                inputStream= new FileInputStream(new File(uri.getPath()));
            } else if (SCHEME_CONTENT.equals(uri.getScheme())) {
                inputStream= context.getContentResolver().openInputStream(uri);
            } else {
                return false;
            }
            FileOutputStream outputStream = new FileOutputStream(file);
            int BUFFER_SIZE = 1024 * 2;
            int n = 0;
            byte[] buffer =new byte[BUFFER_SIZE];
            BufferedInputStream bis = new BufferedInputStream(inputStream, BUFFER_SIZE);
            BufferedOutputStream bos = new BufferedOutputStream(outputStream, BUFFER_SIZE);
            while ((n=bis.read(buffer))!=-1) {
                bos.write(buffer, 0, n);
            }
            bos.flush();
            bos.close();
            bis.close();
            inputStream.close();
            outputStream.close();
            return true;
        } catch ( Exception e) {
            e.printStackTrace();

            return false;
        }

    }
}
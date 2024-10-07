package com.ddonging.wechattool;


import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.util.Objects;



public class ShareActivity extends AppCompatActivity {
    File file;
    public int  EXTERNAL_STORAGE_PERMISSION = 1001;
    public int GET_UNKNOWN_APP_SOURCES = 1002;
    public int INSTALL_APP = 1003;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        finishAffinity();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences shares = getSharedPreferences("data", MODE_PRIVATE);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sendIntent.addCategory("android.intent.category.DEFAULT");
        Uri uri = getIntent().getData();
        Boolean isShare=shares.getBoolean("share",false);
        Boolean isInstall=shares.getBoolean("install",false);
            if (uri.getScheme().equals("file")||uri.getScheme().equals("content")) {
                if (uri.getPath().contains(".apk.1")) {
                    new MaterialAlertDialogBuilder(this)
                            .setTitle(R.string.dialog_title)
                            .setMessage(R.string.dialog_message)
                            .setNegativeButton(R.string.ok_share,
                                    (dialog, which) -> {
                                        if (isShare){
                                            //API24以上系统分享支持file:///开头
                                            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                                            StrictMode.setVmPolicy(builder.build());
                                            builder.detectFileUriExposure();
                                            sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                            sendIntent.setType(getIntent().getType());
                                            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                            startActivityForResult(Intent.createChooser(sendIntent, getString(R.string.share)), 1);

                                        }else{
                                            Toast.makeText(getApplicationContext(), R.string.no_share, Toast.LENGTH_LONG).show();
                                            finishAffinity();
                                        }

                                    })
                            .setPositiveButton(R.string.ok_install, (dialog, which) -> {
                                if (isInstall)
                                {
                                    if (uri != null) {
                                        if(creatApkCopyFile(uri) != null){
                                            installApk(file);
                                        }
                                    } else {
                                        finish();
                                    }
                                }else
                                {
                                    Toast.makeText(getApplicationContext(), R.string.no_install, Toast.LENGTH_LONG).show();
                                    finishAffinity();
                                }

                            })
                            .create()
                            .show();
                }else {
                    if (isShare){
                        //API24以上系统分享支持file:///开头
                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder.build());
                        builder.detectFileUriExposure();
                        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        sendIntent.setType(getIntent().getType());
                        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivityForResult(Intent.createChooser(sendIntent, getString(R.string.share)), 1);
                    }else{
                        Toast.makeText(getApplicationContext(), R.string.no_share, Toast.LENGTH_LONG).show();
                        finishAffinity();
                    }
                }

            }

    }
    public File creatApkCopyFile(Uri uri) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
//            //ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION);
//        }
        String filePath = Utils.getFilePathFromURI(this, uri);
        if (filePath != null) {
            file = new File(filePath);
            return file;
        }
        return null;
    }
    private void installApk(File file) {
        Uri apkUri;
        Intent intent =new  Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            apkUri= FileProvider.getUriForFile(this, "com.ddonging.wechattool.FileProvider", file);
        } else {
            apkUri=Uri.fromFile(file);
        }
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        startActivityForResult(intent, INSTALL_APP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_UNKNOWN_APP_SOURCES) {
            installApk(file);
        } else if (requestCode == INSTALL_APP) {
           // file.delete();
            finish();
        }else {
            finish();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTERNAL_STORAGE_PERMISSION) {
            if (Objects.equals(permissions[0], READ_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                installApk(file);
            } else {
                finish();
            }
        }
    }
}

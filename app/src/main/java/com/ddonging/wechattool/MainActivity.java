package com.ddonging.wechattool;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.materialswitch.MaterialSwitch;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE}, 100);

        SharedPreferences shares=getSharedPreferences("data",MODE_PRIVATE);
        Boolean isShare=shares.getBoolean("share",false);
        Boolean isInstall=shares.getBoolean("install",false);
        Boolean isHide=shares.getBoolean("hide",false);

        MaterialSwitch share=(MaterialSwitch) findViewById(R.id.share);
        MaterialSwitch install=(MaterialSwitch) findViewById(R.id.install);
        MaterialSwitch hide=(MaterialSwitch) findViewById(R.id.hide);
        hide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // 隐藏桌面图标
                if(b)
                {
                    PackageManager p = getPackageManager();
                    p.setComponentEnabledSetting(getComponentName(), PackageManager.COMPONENT_ENABLED_STATE_DISABLED , PackageManager.DONT_KILL_APP);

                }else
                {
                    PackageManager p = getPackageManager();
                    p.setComponentEnabledSetting(getComponentName(), PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP);
                }
                SharedPreferences.Editor editor=shares.edit();
                editor.putBoolean("hide",b).commit();
            }
        });


        MaterialCardView p=(MaterialCardView) findViewById(R.id.permission);
        p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Uri packageURI = Uri.parse("package:" + getPackageName());
            Intent intent =new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
            startActivityForResult(intent, 1000);
        } else {
            }
            }
        });
        if(isShare)
        {
            share.setChecked(true);
        }else {
            share.setChecked(false);
        }
        if(isInstall)
        {
            install.setChecked(true);
        }else {
            install.setChecked(false);
        }
        if(isHide)
        {
            hide.setChecked(true);
        }else {
            hide.setChecked(false);
        }
        install.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor=shares.edit();
                editor.putBoolean("install",b).commit();

            }
        });
        share.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor=shares.edit();
                editor.putBoolean("share",b).commit();

            }
        });
    }

}
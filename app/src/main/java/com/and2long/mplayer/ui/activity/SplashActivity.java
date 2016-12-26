package com.and2long.mplayer.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.and2long.mplayer.R;

/**
 * Created by L on 2016/12/3.
 */

public class SplashActivity extends AppCompatActivity {

    //所需权限
    private String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //权限申请码
    private static int REQUEST_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置无标题栏（必须在setContentView方法之前调用）
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !hasAllPermissions()) {
            //动态申请权限
            requestPermissions(permissions, REQUEST_PERMISSION_CODE);
        } else {
            //进入主界面
            intoHome();
        }*/

        //进入主界面
        intoHome();
    }


    /**
     * 检查是否具有全部所需权限
     *
     * @return
     */
    /*private boolean hasAllPermissions() {
        //检查权限
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }*/

    /**
     * 进入主界面。
     */
    private void intoHome() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);

    }

    /**
     * 权限回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    /*@Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE && hasAllPermissons(grantResults)) {
            //进入主界面
            intoHome();
        } else {
            //显示权限提示对话框
            showPermissionDialog();
        }
    }*/

    /**
     * 提示对话框
     */
    /*private void showPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.need_permission);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //退出.
                finish();
            }
        });
        builder.setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //进入设置界面手动授予权限
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        });
        builder.setCancelable(false);
        builder.show();
    }*/

    /**
     * 判断是否具有所有权限
     *
     * @param grantResults
     * @return
     */
    private boolean hasAllPermissons(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}

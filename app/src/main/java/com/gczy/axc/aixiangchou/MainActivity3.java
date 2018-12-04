package com.gczy.axc.aixiangchou;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;

public class MainActivity3 extends AppCompatActivity {

    private Fragment currentFragment;
    private RelativeLayout relativeLayout;
    private FragmentManager manager;
    private Fragment fragmentGongYi, fragmentLaunch, fragmentCenter;

    public static final String URL_1 = "https://yglian.qschou.com/gongyi/publicSite/index?ChannelId=gczy";
    public static final String URL_2 = "https://yglian.qschou.com/launch/index.html?ChannelId=gczy";
    public static final String URL_3 = "https://yglian.qschou.com/center/index.html?ChannelId=gczy";
//    public static final String URL_1 = "file:///android_asset/aaa.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStateBarColor();
        setContentView(R.layout.activity_main3);
        relativeLayout = (RelativeLayout) findViewById(R.id.rl_content);
        permission();
        initView();
    }

    private void setStateBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();

            //设置修改状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            //设置状态栏的颜色，和你的app主题或者标题栏颜色设置一致就ok了
            window.setStatusBarColor(getResources().getColor(R.color.red));
        }
    }

    private void initView() {
        currentFragment = new Fragment();
        manager = getSupportFragmentManager();

        Bundle bundle1 = new Bundle();
        bundle1.putString("tag", URL_1);
        fragmentGongYi = new HomeFragment();
        fragmentGongYi.setArguments(bundle1);

        Bundle bundle2 = new Bundle();
        bundle2.putString("tag", URL_2);
        fragmentLaunch = new HomeFragment();
        fragmentLaunch.setArguments(bundle2);

        Bundle bundle3 = new Bundle();
        bundle3.putString("tag", URL_3);
        fragmentCenter = new HomeFragment();
        fragmentCenter.setArguments(bundle3);

        //默认展示
        showFragment(fragmentGongYi);

    }

    public void show2(){
        showFragment(fragmentLaunch);
    }

    public void show3(){
        showFragment(fragmentCenter);
    }

    /**
     * 展示Fragment
     */
    private void showFragment(Fragment fragment) {
        if (currentFragment != fragment) {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.hide(currentFragment);
            currentFragment = fragment;
            if (!fragment.isAdded()) {
                transaction.add(R.id.rl_content, fragment).show(fragment).commit();
            } else {
                transaction.show(fragment).commit();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //这是一个监听用的按键的方法，keyCode 监听用户的动作，如果是按了返回键，同时Webview要返回的话，WebView执行回退操作，因为mWebView.canGoBack()返回的是一个Boolean类型，所以我们把它返回为true

        if (keyCode == KeyEvent.KEYCODE_BACK && (currentFragment == fragmentLaunch || currentFragment == fragmentCenter)){
            showFragment(fragmentGongYi);
            return true;
        }
        if (NetWorkUtils.isNetworkConnected(this) && currentFragment == fragmentGongYi){
            return ((HomeFragment)fragmentGongYi).goBack(keyCode, event);
        } else if (NetWorkUtils.isNetworkConnected(this) && currentFragment == fragmentLaunch){
            return ((HomeFragment)fragmentLaunch).goBack(keyCode, event);
        } else if (NetWorkUtils.isNetworkConnected(this) && currentFragment == fragmentCenter){
            return ((HomeFragment)fragmentCenter).goBack(keyCode, event);
        }

        return super.onKeyDown(keyCode, event);
    }

    private void permission() {
        ActivityCompat.requestPermissions(MainActivity3.this, new String[]{android
                .Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //创建文件夹
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        File file = new File(Environment.getExternalStorageDirectory() + "/aa/bb/");
                        if (!file.exists()) {
                            Log.d("jim", "path1 create:" + file.mkdirs());
                        }
                    }
                    break;
                }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
//                Toast.makeText(this,"内容为空",Toast.LENGTH_LONG).show();
            } else {
//                ToastUtil.show(R.string.scan_success);
                // ScanResult 为 获取到的字符串
                String ScanResult = intentResult.getContents();

                Toast.makeText(MainActivity3.this, ScanResult, Toast.LENGTH_LONG).show();

                ((HomeFragment)currentFragment).reLoadQrUrl(ScanResult);
            }
        }
    }

}

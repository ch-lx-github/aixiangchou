package com.gczy.axc.aixiangchou;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

/**
 * Created by lixin on 18/11/21.
 */

public class SplashActivity extends AppCompatActivity implements Handler.Callback {

    private final Handler handler = new Handler(this);

    private boolean isAllowDestory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//        View root = findViewById(R.id.root);
//        root.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha_in));

//        EventBus.getDefault().register(this);

        isAllowDestory = false;

        handler.sendEmptyMessageDelayed(0, TimeUnit.SECONDS.toMillis(2));
    }

    @Override
    protected void onDestroy() {
        isAllowDestory = true;
        handler.removeMessages(0);
//        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public boolean handleMessage(Message msg) {
//        LogUtil.logD("handleMessage begin:" + Initializer.isInitialized() + "/" + isAllowDestory);

        if (!isAllowDestory) {
            onComplete();
        }
        isAllowDestory = true;

        return true;
    }

    private void onComplete() {
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        boolean firstStart = sharedPreferences.getBoolean("firstStart", true);
        if (firstStart){
            startActivity(new Intent(this, GuideActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity2.class));
        }
        finish();
    }

//    public void onEvent(Initializer.InitializeEvent event) {
//        if (isAllowDestory) {
//            onComplete();
//        }
//    }
}

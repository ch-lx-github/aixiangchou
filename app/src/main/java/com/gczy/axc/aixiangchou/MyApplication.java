package com.gczy.axc.aixiangchou;

import android.app.Application;
import android.webkit.WebView;

/**
 * Created by lixin on 18/12/14.
 */

public class MyApplication extends Application {

    private static Application mInstance;
    private  WebView mWebView;
    public static final String URL_1 = "https://yglian.qschou.com/gongyi/publicSite/index?ChannelId=gczy";

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mWebView = new WebView(this);
        mWebView.loadUrl(URL_1);
    }

    public WebView getWebView(){
        return mWebView;
    }

    public static Application getInitialize (){
        return mInstance;
    }
}

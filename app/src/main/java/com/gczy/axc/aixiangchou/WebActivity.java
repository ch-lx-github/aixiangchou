package com.gczy.axc.aixiangchou;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * Created by lixin on 18/11/26.
 */

public class WebActivity extends AppCompatActivity {

    private WebView webView;
    private String url;

    private RelativeLayout relativeLayout;
    private ImageView ivReload;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStateBarColor();
        setContentView(R.layout.activity_main);
        webView = (WebView) findViewById(R.id.web_content);
        relativeLayout = (RelativeLayout) findViewById(R.id.rl_net_error);
        ivReload = (ImageView) findViewById(R.id.iv_reload);
        initData();
        initView();
    }

    private void initData() {
        url = getIntent().getStringExtra("url");
        if (TextUtils.isEmpty(url)){
            finish();
        }
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
        webView.clearHistory();
        webView.clearCache(false);
        webView.clearFormData();
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);// 隐藏滚动条webView.requestFocus();
        webView.requestFocusFromTouch();

        WebSettings mWebSettings = webView.getSettings();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mWebSettings.setJavaScriptEnabled(true);// 支持JS
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);//支持通过js打开新的窗口
        mWebSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);//提高渲染等级
        mWebSettings.setBuiltInZoomControls(false);// 设置支持缩放
        mWebSettings.setDomStorageEnabled(true);//使用localStorage则必须打开
        mWebSettings.setBlockNetworkImage(true);// 首先阻塞图片，让图片不显示
        mWebSettings.setBlockNetworkImage(true);//  页面加载好以后，在放开图片：
        mWebSettings.setSupportMultipleWindows(false);// 设置同一个界面
        mWebSettings.setBlockNetworkImage(false);
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebSettings.setNeedInitialFocus(false);// 禁止webview上面控件获取焦点(黄色边框)
        //设置可以访问文件
//        mWebSettings.setAllowFileAccess(true);
//        mWebSettings.setDatabaseEnabled(true);
//        mWebSettings.setAppCacheEnabled(true);
//        webView.setWebChromeClient(new WebChromeClient());
//        mWebSettings.setSupportZoom(true);//是否可以缩放，默认true
//        mWebSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
//        mWebSettings.setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
//        mWebSettings.setLoadsImagesAutomatically(true); // 加载图片
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            mWebSettings.setMediaPlaybackRequiresUserGesture(false);//播放音频，多媒体需要用户手动？设置为false为可自动播放
//        }

        webView.setWebViewClient(webViewClient);

        ivReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetWorkUtils.isNetworkConnected(WebActivity.this)){
                    webView.reload();
                } else {
                    Toast.makeText(WebActivity.this,"请检查网络",Toast.LENGTH_LONG).show();
                }
            }
        });

        if (NetWorkUtils.isNetworkConnected(this)){
            webView.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);
        } else {
            webView.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.VISIBLE);
        }
        webView.loadUrl(url);

    }

    protected WebViewClient webViewClient = new WebViewClient() {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            return super.shouldOverrideUrlLoading(view, url);
            view.loadUrl(url);
//            view.setWebChromeClient(new WebChromeClientProgress());
            return true;
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            //6.0以上执行
            showErrorPage();
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            //6.0以下执行
            showErrorPage();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            webView.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);
        }
    };

    private void showErrorPage(){
        relativeLayout.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);
    }

//    private class WebChromeClientProgress extends WebChromeClient {
//        @Override
//        public void onProgressChanged(WebView view, int progress) {
//            if (mLoadingProgress != null) {
//                mLoadingProgress.setProgress(progress);
//                if (progress == 100) mLoadingProgress.setVisibility(View.GONE);
//            }
//            super.onProgressChanged(view, progress);
//        }
//    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

package com.gczy.axc.aixiangchou;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private String url = "https://yglian.qschou.com/gongyi/publicSite/index?ChannelId=gczy";
//    private String url = "file:///android_asset/aaa.html";
//    private String url = "https://www.baidu.com/";
//    private String url = "https://yglian.qschou.com/gongyi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,  WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setStateBarColor();
        setContentView(R.layout.activity_main);
        webView = (WebView) findViewById(R.id.web_content);
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
//        if (!TextUtils.isEmpty(u)){
//            url = u;
//        }

//        if (NetWorkUtils.isNetworkConnected(this)) {
            webView.loadUrl(url);
//        } else {
//            unZip();
//            webView.loadUrl("file:///android_asset/v1/index.html");
//            webView.loadUrl("file:///" + path + "/index.html");
//        }

    }

    protected WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            Intent intent = new Intent(MainActivity.this, MainActivity.class);
//            intent.putExtra("url",url);
//            startActivity(intent);
//            return true;//返回true表明点击网页里面的连接还是在当前的webview里跳转,不跳到浏览器

            // 如下方案可在非微信内部WebView的H5页面中调出微信支付

            if (url.startsWith("weixin://wap/pay?")) {
                if (!uninstallSoftware(MainActivity.this, "com.tencent.mm")) {
//没有安装
                    Toast.makeText(MainActivity.this, "请先安装微信!", Toast.LENGTH_LONG).show();
                    return true;
                } else {
                    //已经安装，可以操作接下来的操作

                    Intent intent = new Intent();

                    intent.setAction(Intent.ACTION_VIEW);

                    intent.setData(Uri.parse(url));

                    startActivity(intent);

                    return true;
                }

            } else if (url.startsWith("axc://axc.qrcode.camera")) {
                new IntentIntegrator(MainActivity.this)
                        .setOrientationLocked(false)
                        .setCaptureActivity(ScannerActivity.class) // 设置自定义的activity是ScanActivity
                        .initiateScan();
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            //网页在webView中打开
            if(Build.VERSION.SDK_INT <=  Build.VERSION_CODES.LOLLIPOP){//安卓5.0的加载方法
                view.loadUrl(request.toString());
            }else {//5.0以上的加载方法
                view.loadUrl(request.getUrl().toString());
            }
            return true;
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//            super.onReceivedError(view, request, error);
            unZip();
            view.loadUrl("file:///" + path + "/index.html");
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //这是一个监听用的按键的方法，keyCode 监听用户的动作，如果是按了返回键，同时Webview要返回的话，WebView执行回退操作，因为mWebView.canGoBack()返回的是一个Boolean类型，所以我们把它返回为true
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    //    private static String path = "file:///android_asset/v1";
    private static String path = Environment.getExternalStorageDirectory() + "/qsgy/web";

    private void unZip() {
        try {
            UnpackZip.unZip(this, "v1.zip", path, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void permission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android
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


    // com.tencent.mm

    private boolean uninstallSoftware(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
            if (packageInfo != null) {
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
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
//                if (EosUtil.checkEosURI(ScanResult)) {
//                    try {
//                        ScanResult = URLDecoder.decode(ScanResult, "utf-8");
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
//                    IntentUtil.intentToUri(getActivity(), Uri.parse(ScanResult));
//                } else {
//                    ToastUtil.show(R.string.tx_eos_account_specification);
//                }
                Toast.makeText(MainActivity.this, ScanResult,Toast.LENGTH_LONG).show();
            }
        }
//        else if (resultCode == RESULT_OK && requestCode == 100) {
//            if (!TextUtils.isEmpty(data.getStringExtra(AppConstants.EXTRA_ACCOUNT))) {
//                current = getAccountInfoByName(data.getStringExtra(AppConstants.EXTRA_ACCOUNT));
//                ((MainActivity) getActivity()).hmsWalletAccount = current;
//                initData();
//            } else {
//                ToastUtil.show(getString(R.string.error_switch_account));
//            }
//        }
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        webView.reload();//退出时关闭声音
//        Log.d("on", "onStart=="+"onPause");
//        webView.loadUrl("about:blank");
//    }
}

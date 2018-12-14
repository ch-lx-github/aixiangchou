package com.gczy.axc.aixiangchou;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import static com.yalantis.ucrop.util.FileUtils.getPath;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
      public static final String URL_1 = "https://yglian.qschou.com/gongyi/publicSite/index?ChannelId=gczy";
//    public static final String URL_1 = "https://yglian.qschou.com/gongyi/activity";
//    public static final String URL_1 = "file:///android_asset/aaa.html";
//    private String url = "https://www.baidu.com/";
//    private String url = "https://yglian.qschou.com/gongyi";

    private String qrUrl;

    private SwipeRefreshLayout swipeRefreshLayout;

    private RelativeLayout relativeLayout;
    private ImageView ivReload;

    private ValueCallback<Uri[]> mUploadCallbackAboveL;
    private ValueCallback<Uri> mUploadMessage;
    private List<LocalMedia> selectList;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStateBarColor();
        setContentView(R.layout.activity_main);
        startActivity(new Intent(MainActivity.this, SplashActivity.class));
        webView = (WebView) findViewById(R.id.web_content);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.main_srl);
        relativeLayout = (RelativeLayout) findViewById(R.id.rl_net_error);
        ivReload = (ImageView) findViewById(R.id.iv_reload);
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

//    private void preload(){
//        if (getIntent().getBooleanExtra("first", true)){
//            startActivity(new Intent(MainActivity.this, SplashActivity.class));
//        } else {
//            startActivity(new Intent(MainActivity.this, GuideActivity.class));
//        }
//    }

    private void initView() {
        webView.clearHistory();
        webView.clearCache(true);
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
        mWebSettings.setSupportMultipleWindows(false);// 设置同一个界面
        mWebSettings.setBlockNetworkImage(false);
//        mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//先加载缓存再访问网络
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//不使用缓存
        mWebSettings.setNeedInitialFocus(false);// 禁止webview上面控件获取焦点(黄色边框)
        mWebSettings.setUserAgentString(mWebSettings.getUserAgentString() + ";YGLian/Android/1.0.1");

        webView.setWebViewClient(webViewClient);

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                webView.reload();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webView.evaluateJavascript("javascript:reloadData()", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    webView.reload();
                }
            }
        });

        ivReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetWorkUtils.isNetworkConnected(MainActivity.this)) {
                    webView.reload();
                } else {
                    Toast.makeText(MainActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
                }
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            // For Android 5.0+
            @Override
            public boolean onShowFileChooser(WebView webView,
                                             ValueCallback<Uri[]> filePathCallback,
                                             FileChooserParams fileChooserParams) {
                mUploadCallbackAboveL = filePathCallback;
                openImageChooserActivity();
//                take();
                return true;
            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
                openImageChooserActivity();
            }

            //3.0--版本
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;
                openImageChooserActivity();
            }

            // For Android 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                openImageChooserActivity();
            }
        });

        webView.addJavascriptInterface(new AndroidToJs(), "jsBrige");

        if (NetWorkUtils.isNetworkConnected(this)) {
            webView.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);
        } else {
            webView.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.VISIBLE);
        }

        webView.loadUrl(URL_1);

    }

    private void openImageChooserActivity() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);
    }

    protected WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            errorPage = false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (!errorPage) {
                webView.setVisibility(View.VISIBLE);
                relativeLayout.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!NetWorkUtils.isNetworkConnected(MainActivity.this)) {
                Toast.makeText(MainActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
                return true;
            }
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
                qrUrl = Uri.parse(url).getQueryParameter("url");
                try {
                    qrUrl = URLDecoder.decode(qrUrl, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                new IntentIntegrator(MainActivity.this)
                        .setOrientationLocked(false)
                        .setCaptureActivity(ScannerActivity.class) // 设置自定义的activity是ScanActivity
                        .initiateScan();
                return true;
            }
            else if (url.startsWith("aaa://aaa.aaa.aaa")){
                message();
                return true;
            }
            else if (url.startsWith("axc://axc.clipboard.text")) {
                String content = Uri.parse(url).getQueryParameter("text");
                copyToClipboard("axc", content);
                return true;
            } else {
                view.loadUrl(url);
                return true;
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            //网页在webView中打开
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {//安卓5.0的加载方法
                shouldOverrideUrlLoading(view, request.toString());
            } else {//5.0以上的加载方法
                shouldOverrideUrlLoading(view, request.getUrl().toString());
            }
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
    };

    private boolean errorPage;

    private void showErrorPage() {
        relativeLayout.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);
        errorPage = true;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();
                return true;
            } else {
                moveTaskToBack(true);
                return true;
            }
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
                String scanResult = intentResult.getContents();

                Toast.makeText(MainActivity.this, scanResult, Toast.LENGTH_LONG).show();
                if (!TextUtils.isEmpty(qrUrl) && qrUrl.startsWith("http")) {
                    reLoadQrUrl(scanResult);
                } else {
                    //js qr code
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        webView.evaluateJavascript("javascript:scanResult('" + scanResult + "')", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String s) {
                                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        } else if (requestCode == FILE_CHOOSER_RESULT_CODE) {

            if (null == mUploadMessage && null == mUploadCallbackAboveL) return;
            selectList = PictureSelector.obtainMultipleResult(data);
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (mUploadCallbackAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (mUploadMessage != null) {
                if (result != null) {
                    String path = getPath(this.getApplicationContext(),
                            result);
                    Uri uri = Uri.fromFile(new File(path));
                    mUploadMessage.onReceiveValue(uri);
                } else {
                    mUploadMessage.onReceiveValue(null);
                }
                mUploadMessage = null;


            }
        }

    }

    @SuppressWarnings("null")
    @TargetApi(Build.VERSION_CODES.BASE)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent data) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE || mUploadCallbackAboveL == null) {
            return;
        }

        Uri[] results = null;
        String dataString = null;
        if (data == null) {
//            results = new Uri[]{imageUri};
//            Log.e("results==null", "results" + results.toString());
        } else {
            selectList = PictureSelector.obtainMultipleResult(data);
            if (selectList != null && selectList.size() > 0) {
                results = new Uri[selectList.size()];
                String path = "";
                for (int i = 0; i < selectList.size(); i++) {
                    LocalMedia localMedia = selectList.get(i);
                    if (localMedia.isCut() && !localMedia.isCompressed()) {
//                    // 裁剪过
                        path = localMedia.getCutPath();
                    } else if (localMedia.isCompressed() || (localMedia.isCut() && localMedia.isCompressed())) {
                        // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                        path = localMedia.getCompressPath();
                    } else {
                        // 原图
                        path = localMedia.getPath();
                    }
                    Uri uri = Uri.fromFile(new File(path));
                    results[i] = uri;
                }
            }
            dataString = data.getDataString();
            ClipData clipData = data.getClipData();
            if (clipData != null) {
                results = new Uri[clipData.getItemCount()];
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    results[i] = item.getUri();
                }
            }

        }
        if (results != null) {
            mUploadCallbackAboveL.onReceiveValue(results);
            mUploadCallbackAboveL = null;
        } else {
            results = new Uri[]{Uri.parse(dataString == null ? "" : dataString)};
            mUploadCallbackAboveL.onReceiveValue(results);
            mUploadCallbackAboveL = null;
        }

        return;
    }

    private void copyToClipboard(String label, String content) {
        final ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        final ClipData clip = ClipData.newPlainText(label, content);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(MainActivity.this, getString(R.string.copied_to_clipboard), Toast.LENGTH_LONG).show();
    }

    public void reLoadQrUrl(String scanResult) {
        String pr = "";
        try {
            pr = URLEncoder.encode(scanResult, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        webView.loadUrl(qrUrl + "?qrinfo=" + pr);
        qrUrl = "";
    }

    public class AndroidToJs {

        // 定义JS需要调用的方法
        // 被JS调用的方法必须加入@JavascriptInterface注解
        @JavascriptInterface
        public void toast(String msg) {
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void finish() {
            Toast.makeText(MainActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });

        }

        //扫码
        @JavascriptInterface
        public void scanCode() {
            new IntentIntegrator(MainActivity.this)
                    .setOrientationLocked(false)
                    .setCaptureActivity(ScannerActivity.class) // 设置自定义的activity是ScanActivity
                    .initiateScan();
        }

        //重新加载网页
        @JavascriptInterface
        public void reload() {
            webView.post(new Runnable() {
                @Override
                public void run() {
                    webView.reload();
                }
            });
        }

        //系统声音
        @JavascriptInterface
        public void prompt() {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        }
    }

    private void message() {
        BadgeUtilDeskTop.setBadgeCount(this,0);
    }

    /**
     * Retrieve launcher activity name of the application from the context
     *
     * @param context The context of the application package.
     * @return launcher activity name of this application. From the
     *         "android:name" attribute.
     */
    private static String getLauncherClassName(Context context) {
        PackageManager packageManager = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        // To limit the components this Intent will resolve to, by setting an
        // explicit package name.
        intent.setPackage(context.getPackageName());
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        // All Application must have 1 Activity at least.
        // Launcher activity must be found!
        ResolveInfo info = packageManager
                .resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

        // get a ResolveInfo containing ACTION_MAIN, CATEGORY_LAUNCHER
        // if there is no Activity which has filtered by CATEGORY_DEFAULT
        if (info == null) {
            info = packageManager.resolveActivity(intent, 0);
        }

        return info.activityInfo.name;
    }


}

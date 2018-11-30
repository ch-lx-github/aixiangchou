package com.gczy.axc.aixiangchou;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by lixin on 18/11/26.
 */

public class HomeFragment extends Fragment {

    private View mView;
    private WebView mWebView;
    private String url;
    private String qrUrl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_layout_home, null);
        }
        Bundle arguments = getArguments();
        url = arguments.getString("tag");
        mWebView = mView.findViewById(R.id.web_content);
        initWebView();
        return mView;
    }

    private void initWebView() {
        mWebView.clearHistory();
        mWebView.clearCache(false);
        mWebView.clearFormData();
        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);// 隐藏滚动条webView.requestFocus();
        mWebView.requestFocusFromTouch();

        WebSettings mWebSettings = mWebView.getSettings();

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
        mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebSettings.setNeedInitialFocus(false);// 禁止webview上面控件获取焦点(黄色边框)
        mWebSettings.setUserAgentString(mWebSettings.getUserAgentString() + ";YGLian/Android/1.0.1");

        mWebView.setWebViewClient(webViewClient);

        mWebView.loadUrl(url);
    }

    protected WebViewClient webViewClient = new WebViewClient() {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            super.onPageStarted(view, url, favicon);
//            mProgressDialog.show();

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
//            mProgressDialog.hide();

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("weixin://wap/pay?")) {
                if (!uninstallSoftware(getActivity(), "com.tencent.mm")) {
//没有安装
                    Toast.makeText(getActivity(), "请先安装微信!", Toast.LENGTH_LONG).show();
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
                new IntentIntegrator(getActivity())
                        .setOrientationLocked(false)
                        .setCaptureActivity(ScannerActivity.class) // 设置自定义的activity是ScanActivity
                        .initiateScan();
                return true;
            }
            if (url.equals(MainActivity3.URL_2)) {
                ((MainActivity3) getActivity()).show2();
            } else if (url.equals(MainActivity3.URL_3)) {
                ((MainActivity3) getActivity()).show3();
            } else {
                Intent intent = new Intent(getActivity(), WebActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);
            }
            return true;
//            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                shouldOverrideUrlLoading(view, request.getUrl().toString());
            } else {
                shouldOverrideUrlLoading(view, request.toString());
            }
            return true;
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            return super.shouldInterceptRequest(view, request);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
        }
    };

    public boolean goBack(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return false;
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

    public void reLoadQrUrl(String scanResult){
        String pr = "";
        try {
            pr = URLEncoder.encode(scanResult, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mWebView.loadUrl(qrUrl + "?qrinfo=" + pr);
    }
}

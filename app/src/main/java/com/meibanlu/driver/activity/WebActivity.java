package com.meibanlu.driver.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.meibanlu.driver.R;
import com.meibanlu.driver.application.DriverApplication;
import com.meibanlu.driver.base.BaseActivity;
import com.meibanlu.driver.tool.AndroidToJs;


//意见反馈WebView

public class WebActivity extends BaseActivity {
    private WebView webView;
    private ProgressBar loadProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        initView();
        initData();
    }

    private void initView() {
        setTitle(getString(R.string.itinerary));
        webView = (WebView) findViewById(R.id.web);
        loadProgressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    private void initData() {
        String url = getIntent().getStringExtra("url");
        if (!TextUtils.isEmpty(url)) {
            setData(url);
        }
    }

    /**
     * 设置webView的参数
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void setData(String url) {
        //支持javaScript脚本
        webView.getSettings().setJavaScriptEnabled(true);
        // 通过addJavascriptInterface()将Java对象映射到JS对象
        //参数1：Javascript对象名
        //参数2：Java对象名
        webView.addJavascriptInterface(new AndroidToJs(), "android");//AndroidToJS类对象映射到js的test对象
        //需要加载的网站
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });


        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                //想在页面开始加载时有操作，在这添加
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                //想在页面加载结束时有操作，在这添加
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候WebView我打开，为false则系统浏览器或第三方浏览器打开。如果要下载页面中的游戏或者继续点击网页中的链接进入下一个网页的话，重写此方法下，不然就会跳到手机自带的浏览器了，而不继续在你这个webview里面展现了
                return true;
            }

            @Override

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

                //想在收到错误信息的时候，执行一些操作，走此方法

            }
        });
        //网页加载进度条
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) {
                    loadProgressBar.setVisibility(View.GONE);//加载完网页进度条消失
                } else {
                    loadProgressBar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    loadProgressBar.setProgress(progress);//设置进度值
                }
            }
        });
    }

    /**
     * 重写点击返回键的方法
     *
     * @param keyCode keyCode
     * @param event   event
     * @return return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();
                return true;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清空所有Cookie
        CookieSyncManager.createInstance(DriverApplication.getApplication());  //Create a singleton CookieSyncManager within a context
        CookieManager cookieManager = CookieManager.getInstance(); // the singleton CookieManager instance
        cookieManager.removeAllCookie();// Removes all cookies.
        CookieSyncManager.getInstance().sync(); // forces sync manager to sync now
        webView.setWebChromeClient(null);
        webView.setWebViewClient(null);
        webView.getSettings().setJavaScriptEnabled(false);
        webView.clearCache(true);
    }
}

package com.dongxl.oppo.component;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


import com.dongxl.oppo.util.LogUtil;
import com.example.jpushdemo.R;

import java.net.URISyntaxException;

/**
 * <p>Title:${Title} </p>
 * <p>Description: WebActivity</p>
 * <p>Copyright (c) 2016 www.oppo.com Inc. All rights reserved.</p>
 * <p>Company: OPPO</p>
 *
 * @author QuWanxin
 * @version 1.0
 * @date 2017/7/27
 */

public class WebActivity extends AppCompatActivity {

    public static final String KEY_TITLE = "title";
    public static final String KEY_URL = "url";
    public static final String KEY_MENU = "menu";

    private String mUrl = "";
    private String mTitle = "";
    private WebView mWebView;


    public static void start(Context context, String url, String title) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        if (!TextUtils.isEmpty(title)) {
            intent.putExtra(KEY_TITLE, title);
        }
        if (!TextUtils.isEmpty(url)) {
            intent.putExtra(KEY_URL, url);
        }
        context.startActivity(intent);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        parseIntent();
        initWebviewSettings(getWebView());
        LogUtil.d("start to show:" + mUrl);
        getWebView().loadUrl(mUrl);
        setTitle(mTitle);
    }

    private void parseIntent() throws IllegalArgumentException {
        LogUtil.d("parseIntent");
        Intent intent = getIntent();
        if (intent == null) throw new IllegalArgumentException("无法获取到Intent参数");
        if (intent.hasExtra(KEY_TITLE))
            mTitle = intent.getStringExtra(KEY_TITLE);
        if (intent.hasExtra(KEY_URL))
            mUrl = intent.getStringExtra(KEY_URL);
        LogUtil.d("param url:" + mUrl);
    }

    private WebView getWebView() {
        if (mWebView == null) {
            mWebView = (WebView) findViewById(R.id.webview);
        }
        return mWebView;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflate = getMenuInflater();
        menuInflate.inflate(R.menu.menu_web_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        String currentUrl = getWebView().getUrl();//当前正在浏览的页面的Url
        LogUtil.d("webview currentUrl:" + currentUrl);
//        switch (item.getItemId()) {
//            case R.id.menu_open_by_browser://在浏览器中打开当前页面
//                try {
//                    Intent intent = Intent.parseUri(currentUrl, Intent.URI_INTENT_SCHEME);
//                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
//                    intent.setAction(Intent.ACTION_VIEW);
//                    intent.setComponent(null);
//                    intent.setSelector(null);
//                    startActivityIfNeeded(intent, -1);
//                } catch (URISyntaxException e) {
//                    e.printStackTrace();
//                }
//                break;
//            case R.id.menu_copy_url://复制url地址到剪切板
//                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//                clipboardManager.setPrimaryClip(ClipData.newPlainText("url", currentUrl));
//                Toast.makeText(this, "已成功复制url到剪切板", Toast.LENGTH_LONG).show();
//                break;
//        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("SetJavaScriptEnabled")
    void initWebviewSettings(WebView webView) {
        webView.setOnLongClickListener(new View.OnLongClickListener() {// 禁用长安编辑模式

            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                WebActivity.this.setTitle(title);
            }
        });
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);// 禁止OverScroll时候的阴影效果
        if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        WebSettings webSettings = webView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setUseWideViewPort(true);// 设置此属性，可任意比例缩放
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptEnabled(true); // enable js
    }

}

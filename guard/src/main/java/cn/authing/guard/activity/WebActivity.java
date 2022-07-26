package cn.authing.guard.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import cn.authing.guard.R;
import cn.authing.guard.util.Util;

public class WebActivity extends BaseAuthActivity {

    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.setStatusBarColor(this, R.color.authing_status_bar_bg);
        setContentView(R.layout.authing_activity_web);
        Intent intent = getIntent();
        String title = "";
        String url = "";
        if (intent != null) {
            if (intent.hasExtra("title")) {
                title = intent.getStringExtra("title");
            }
            if (intent.hasExtra("url")) {
                url = intent.getStringExtra("url");
            }
        }

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        TextView textView = findViewById(R.id.text_title);
        textView.setText(title);

        webView = findViewById(R.id.web_view);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setAppCacheEnabled(false);
        if (!Util.isNull(url)){
            webView.loadUrl(url);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null){
            webView.clearHistory();
            webView.destroy();
        }
    }
}

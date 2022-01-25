package cn.authing.webview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.authing.MainActivity;
import cn.authing.R;
import cn.authing.guard.WebAuthView;
import cn.authing.guard.activity.BaseAuthActivity;
import cn.authing.guard.data.UserInfo;

public class AuthingWebViewActivity extends BaseAuthActivity {

    WebAuthView webView;
    LinearLayout llRes;
    TextView tvIdToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authing_web_view);

        llRes = findViewById(R.id.ll_res);
        tvIdToken = findViewById(R.id.tv_idtoken);

        webView = findViewById(R.id.wv_auth);
        webView.setOnLoginCallback(this::setResult);
    }

    private void setResult(UserInfo userInfo) {
        runOnUiThread(()->{
            webView.setVisibility(View.GONE);
            llRes.setVisibility(View.VISIBLE);
            if (userInfo != null) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("user", userInfo);
                startActivity(intent);
                finish();
            } else
                tvIdToken.setText("Auth failed");
        });
    }
}
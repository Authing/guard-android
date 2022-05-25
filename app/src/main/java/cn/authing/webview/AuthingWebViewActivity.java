package cn.authing.webview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.authing.MainActivity;
import cn.authing.R;
import cn.authing.guard.WebAuthView;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.activity.BaseAuthActivity;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;

public class AuthingWebViewActivity extends AuthActivity {

    WebAuthView webView;
    LinearLayout llRes;
    TextView tvIdToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final AuthFlow flow = new AuthFlow();
        flow.setSkipConsent(true);
        getIntent().putExtra(AuthActivity.AUTH_FLOW, flow);

        setContentView(R.layout.activity_authing_web_view);

        llRes = findViewById(R.id.ll_res);
        tvIdToken = findViewById(R.id.tv_idtoken);

        webView = findViewById(R.id.wv_auth);
        FrameLayout flLoading = findViewById(R.id.fl_loading);
        webView.setListener(()->{
            flLoading.setVisibility(View.GONE);
        });
        webView.setOnLoginCallback(this::setResult);

//        webView.getAuthRequest().setClientSecret("92bc59a64f793d3a2bfcf6a75db2d207");
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
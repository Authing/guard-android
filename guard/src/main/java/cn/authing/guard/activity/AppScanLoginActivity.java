package cn.authing.guard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import cn.authing.guard.Authing;
import cn.authing.guard.R;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.util.DarkModeManager;
import cn.authing.guard.util.ToastUtil;
import cn.authing.guard.util.Util;

public class AppScanLoginActivity extends BaseAuthActivity {

    private String random = "";;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authing_scan_login);
        DarkModeManager.getInstance().setDarkMode(this);

        Intent intent = getIntent();
        if (intent.hasExtra("random")){
            random = intent.getStringExtra("random");
        }
        String appName = "";
        if (intent.hasExtra("appName")){
            appName = intent.getStringExtra("appName");
        }

        findViewById(R.id.btn_confirm).setOnClickListener(v -> AuthClient.loginByScannedTicket(random, (code, message, data) -> {
            if (code == 200) {
                finish();
            } else {
                runOnUiThread(() -> ToastUtil.showCenter(this, message));
            }
        }));
        findViewById(R.id.btn_cancel).setOnClickListener(v -> AuthClient.cancelByScannedTicket(random, (code, message, data) -> {
            if (code != 200) {
                runOnUiThread(() -> ToastUtil.showCenter(this, message));
            }
            finish();
        }));
        UserInfo userInfo = Authing.getCurrentUser();
        TextView userNameText = findViewById(R.id.user_name);
        userNameText.setText(Util.getUserName(userInfo));
        TextView appNameText = findViewById(R.id.app_name);
        appNameText.setText(appName);
    }
}

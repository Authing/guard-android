package cn.authing.push;

import android.os.Bundle;

import cn.authing.guard.activity.AuthActivity;
import cn.authing.R;

public class LoginByPushNotificationActivity extends AuthActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_by_push_notification);
    }
}
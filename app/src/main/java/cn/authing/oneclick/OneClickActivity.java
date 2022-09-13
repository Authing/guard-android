package cn.authing.oneclick;

import android.os.Bundle;
import cn.authing.R;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.oneclick.OneClickAuthButton;

public class OneClickActivity extends AuthActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_click);


        // If you want to return accessToken，do like this
        OneClickAuthButton oneClickAuthButton = findViewById(R.id.one_click_btn);
    }
}
package cn.authing.guard.social.callback.ddauth;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import cn.authing.guard.social.handler.DingTalk;

public class DDCallBackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DingTalk.getInstance().onActivityResult(getIntent());
        finish();
    }
}

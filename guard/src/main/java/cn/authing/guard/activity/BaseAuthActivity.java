package cn.authing.guard.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import cn.authing.guard.GlobalStyle;
import cn.authing.guard.R;
import cn.authing.guard.util.Util;

public class BaseAuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        Util.setStatusBarColor(this, R.color.authing_status_bar_bg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalStyle.clear();
    }
}
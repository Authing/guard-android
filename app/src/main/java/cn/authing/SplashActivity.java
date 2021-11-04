package cn.authing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.util.Objects;

import cn.authing.guard.Authing;

public class SplashActivity extends AppCompatActivity {

    private int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> gotoLogin(1), 2000);

        Authing.requestPublicConfig((ok, data) -> gotoLogin(2));
    }

    private void gotoLogin(int flag) {
        this.flag |= flag;
        if (3 == this.flag) {
            Intent intent = new Intent(this, SampleListActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
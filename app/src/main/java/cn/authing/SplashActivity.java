package cn.authing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(this::gotoLogin, 1000);
    }

    private void gotoLogin() {
        Intent intent = new Intent(this, SampleListActivity.class);
        startActivity(intent);
        finish();
    }
}
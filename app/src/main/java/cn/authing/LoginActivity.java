package cn.authing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.util.Objects;

import cn.authing.guard.LoginButton;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_default_login);
        LoginButton btn = findViewById(R.id.btn_login);
        btn.setOnLoginListener((ok, data) -> {
            if (ok) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("user", data);
                startActivity(intent);
                finish();
            }
        });
    }
}
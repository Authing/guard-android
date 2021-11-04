package cn.authing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.util.Objects;

import cn.authing.guard.GlobalStyle;
import cn.authing.guard.LoginButton;
import cn.authing.guard.SocialLoginListView;

public class AuthingLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();

        GlobalStyle.setsEditTextBackground(0);
        GlobalStyle.setsEditTextLayoutBackground((R.drawable.authing_edit_text_layout_background));

        setContentView(R.layout.activity_login_authing);

        LoginButton btn = findViewById(R.id.btn_login);
        if (btn != null) {
            btn.setOnLoginListener((ok, data) -> {
                if (ok) {
                    Intent intent = new Intent(AuthingLoginActivity.this, MainActivity.class);
                    intent.putExtra("user", data);
                    startActivity(intent);
                    finish();
                }
            });
        }

        SocialLoginListView lv = findViewById(R.id.lv_social);
        if (lv != null) {
            lv.setOnLoginListener((ok, data) -> {
                if (ok) {
                    Intent intent = new Intent(AuthingLoginActivity.this, MainActivity.class);
                    intent.putExtra("user", data);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
}
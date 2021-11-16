package cn.authing;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import cn.authing.guard.LoginButton;
import cn.authing.guard.activity.AuthingLoginActivity;
import cn.authing.guard.social.SocialLoginListView;

public class AuthingDemoLoginActivity extends AuthingLoginActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView tvRegister = findViewById(R.id.tv_register);
        tvRegister.setOnClickListener((v)->{
            Intent intent = new Intent(AuthingDemoLoginActivity.this, AuthingDemoRegisterActivity.class);
            startActivity(intent);
        });

        LoginButton btn = findViewById(R.id.btn_login);
        if (btn != null) {
            btn.setOnLoginListener((code, message, data) -> {
                if (code == 200) {
                    Intent intent = new Intent(AuthingDemoLoginActivity.this, MainActivity.class);
                    intent.putExtra("user", data);
                    startActivity(intent);
                    finish();
                }
            });
        }

        SocialLoginListView lv = findViewById(R.id.lv_social);
        if (lv != null) {
            lv.setOnLoginListener((code, message, data) -> {
                if (code == 200) {
                    Intent intent = new Intent(AuthingDemoLoginActivity.this, MainActivity.class);
                    intent.putExtra("user", data);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
}

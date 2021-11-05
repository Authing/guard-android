package cn.authing;

import android.content.Intent;
import android.os.Bundle;

import cn.authing.guard.GlobalStyle;
import cn.authing.guard.LoginButton;
import cn.authing.guard.activity.BaseLoginActivity;
import cn.authing.guard.social.SocialLoginListView;

public class AndroidLoginActivity extends BaseLoginActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GlobalStyle.clear();

        setContentView(R.layout.activity_login_android);

        LoginButton btn = findViewById(R.id.btn_login);
        if (btn != null) {
            btn.setOnLoginListener((ok, data) -> {
                if (ok) {
                    Intent intent = new Intent(AndroidLoginActivity.this, MainActivity.class);
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
                    Intent intent = new Intent(AndroidLoginActivity.this, MainActivity.class);
                    intent.putExtra("user", data);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
}

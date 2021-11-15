package cn.authing.theragun;

import android.content.Intent;
import android.os.Bundle;

import cn.authing.MainActivity;
import cn.authing.R;
import cn.authing.guard.LoginButton;
import cn.authing.guard.activity.BaseLoginActivity;

public class TheragunVerifyCodeActivity extends BaseLoginActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.theragun_login_verify_code);

        String phone = getIntent().getStringExtra("phone");
        LoginButton btn = findViewById(R.id.btn_login);
        btn.setPhoneNumber(phone);

        btn.setOnLoginListener((code, message, data) -> {
            if (code == 200) {
                Intent intent = new Intent(TheragunVerifyCodeActivity.this, MainActivity.class);
                intent.putExtra("user", data);
                startActivity(intent);
                finish();
            }
        });
    }
}
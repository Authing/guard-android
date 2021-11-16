package cn.authing;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import cn.authing.guard.RegisterButton;
import cn.authing.guard.activity.AuthingRegisterActivity;

public class AuthingDemoRegisterActivity extends AuthingRegisterActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView tvRegister = findViewById(R.id.tv_login);
        tvRegister.setOnClickListener((v)->{
            Intent intent = new Intent(AuthingDemoRegisterActivity.this, AuthingDemoLoginActivity.class);
            startActivity(intent);
        });

        RegisterButton btn = findViewById(R.id.btn_register);
        btn.setOnRegisterListener((code, message, data)->{
            if (code == 200) {
                Intent intent = new Intent(AuthingDemoRegisterActivity.this, MainActivity.class);
                intent.putExtra("user", data);
                startActivity(intent);
                finish();
            }
        });
    }
}
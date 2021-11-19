package cn.authing.guard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import cn.authing.guard.GlobalStyle;
import cn.authing.guard.LoginButton;
import cn.authing.guard.R;
import cn.authing.guard.data.UserInfo;

public class AuthingLoginActivity extends BaseLoginActivity {

    public static final int RC_LOGIN = 1024;

    public static final int OK = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GlobalStyle.setEditTextBackground(0);
        GlobalStyle.setEditTextLayoutBackground((R.drawable.authing_edit_text_layout_background));

        setContentView(R.layout.activity_login_authing);

        TextView tvForgotPassword = findViewById(R.id.tv_reset_password);
        tvForgotPassword.setOnClickListener((v)->{
            Intent intent = new Intent(AuthingLoginActivity.this, AuthingForgotPasswordActivity.class);
            startActivity(intent);
        });

        TextView tvRegister = findViewById(R.id.tv_register);
        tvRegister.setOnClickListener((v)->{
            Intent intent = new Intent(AuthingLoginActivity.this, AuthingRegisterActivity.class);
            startActivityForResult(intent, RC_LOGIN);
        });

        LoginButton btn = findViewById(R.id.btn_login);
        btn.setOnLoginListener((code, message, user)-> {
            if (code == 200 && user != null) {
                Intent intent = new Intent();
                intent.putExtra("user", user);
                setResult(OK, intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_LOGIN && resultCode == OK) {
            UserInfo userInfo = (UserInfo)data.getSerializableExtra("user");
            Intent intent = new Intent();
            intent.putExtra("user", userInfo);
            setResult(OK, intent);
            finish();
        }
    }
}
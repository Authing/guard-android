package cn.authing.guard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import cn.authing.guard.GlobalStyle;
import cn.authing.guard.R;
import cn.authing.guard.RegisterButton;

public class AuthingRegisterActivity extends BaseLoginActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GlobalStyle.setEditTextBackground(0);
        GlobalStyle.setEditTextLayoutBackground((R.drawable.authing_edit_text_layout_background));

        setContentView(R.layout.activity_register_authing);

        TextView tvRegister = findViewById(R.id.tv_login);
        tvRegister.setOnClickListener((v)->{
            Intent intent = new Intent(AuthingRegisterActivity.this, AuthingLoginActivity.class);
            startActivity(intent);
        });

        RegisterButton btn = findViewById(R.id.btn_register);
        btn.setOnRegisterListener((code, message, user)->{
            if (code == 200 && user != null) {
                Intent intent = new Intent();
                intent.putExtra("user", user);
                setResult(AuthActivity.OK, intent);
                finish();
            }
        });
    }
}
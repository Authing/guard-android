package cn.authing.nissan;

import android.content.Intent;
import android.os.Bundle;

import cn.authing.MainActivity;
import cn.authing.R;
import cn.authing.guard.GlobalStyle;
import cn.authing.guard.LoginButton;
import cn.authing.guard.activity.BaseLoginActivity;

public class NissanVirtualKeyLoginActivity extends BaseLoginActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove EditText's default underline
        GlobalStyle.setEditTextBackground(0);

        setContentView(R.layout.activity_nissan_virtual_key_login);

        LoginButton btn = findViewById(R.id.btn_login);
        btn.setOnLoginListener((code, message, data) -> {
            if (code == 200) {
                Intent intent = new Intent(NissanVirtualKeyLoginActivity.this, MainActivity.class);
                intent.putExtra("user", data);
                startActivity(intent);
                finish();
            }
        });
    }
}
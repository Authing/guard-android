package cn.authing.abao;

import android.content.Intent;
import android.os.Bundle;

import cn.authing.MainActivity;
import cn.authing.R;
import cn.authing.guard.GetVerifyCodeButton;
import cn.authing.guard.GlobalStyle;
import cn.authing.guard.LoginButton;
import cn.authing.guard.PhoneNumberEditText;
import cn.authing.guard.activity.BaseAuthActivity;

public class AbaoVerifyCodeActivity extends BaseAuthActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove EditText's default underline
        GlobalStyle.setEditTextBackground(0);

        setContentView(R.layout.abao_login_verify_code);

        String phone = getIntent().getStringExtra("phone");

        PhoneNumberEditText phoneNumberEditText = findViewById(R.id.pet);
        phoneNumberEditText.getEditText().setEnabled(false);
        phoneNumberEditText.getEditText().setText(phone);
        phoneNumberEditText.getEditText().setTextColor(0xff000000);

        GetVerifyCodeButton gcb = findViewById(R.id.gcb);
        gcb.setCountDownTip("%1$d");

        LoginButton btn = findViewById(R.id.btn_login);
        btn.setPhoneNumber(phone);
        btn.setOnLoginListener((code, message, data) -> {
            if (code == 200) {
                Intent intent = new Intent(AbaoVerifyCodeActivity.this, MainActivity.class);
                intent.putExtra("user", data);
                startActivity(intent);
                finish();
            }
        });
    }
}
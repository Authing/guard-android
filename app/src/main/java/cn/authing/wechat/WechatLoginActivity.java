package cn.authing.wechat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import cn.authing.AuthingLoginActivity;
import cn.authing.MainActivity;
import cn.authing.R;
import cn.authing.guard.CountryCodePicker;
import cn.authing.guard.GlobalStyle;
import cn.authing.guard.LoginButton;
import cn.authing.guard.PhoneNumberEditText;
import cn.authing.guard.activity.BaseLoginActivity;
import cn.authing.guard.data.Country;

public class WechatLoginActivity extends BaseLoginActivity {

    private PhoneNumberEditText input;
    private CountryCodePicker countryCodePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove EditText's default underline
        GlobalStyle.setsEditTextBackground(0);

        setContentView(R.layout.wechat_login);

        countryCodePicker = findViewById(R.id.ccp);
        input = findViewById(R.id.pnet_input);

        Button next = findViewById(R.id.btn_next);
        if (next != null) {
            next.setOnClickListener((v) -> {
                setContentView(R.layout.wechat_login_verify_code);
                gotoVerifyCode();
            });
        }
    }

    private void gotoVerifyCode() {
        Country country = countryCodePicker.getCountry();
        String code = "+" + country.getCode();
        PhoneNumberEditText phoneNumberEditText = findViewById(R.id.pnet);
        phoneNumberEditText.getEditText().setText(code + input.getText());
        phoneNumberEditText.getEditText().setEnabled(false);

        LoginButton btn = findViewById(R.id.btn_login);
        btn.setOnLoginListener((ok, data) -> {
            if (ok) {
                Intent intent = new Intent(WechatLoginActivity.this, MainActivity.class);
                intent.putExtra("user", data);
                startActivity(intent);
                finish();
            }
        });
    }
}
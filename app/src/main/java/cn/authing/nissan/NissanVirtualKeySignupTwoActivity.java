package cn.authing.nissan;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.TextView;

import cn.authing.MainActivity;
import cn.authing.R;
import cn.authing.guard.GlobalStyle;
import cn.authing.guard.PasswordConfirmEditText;
import cn.authing.guard.PasswordEditText;
import cn.authing.guard.PrivacyConfirmBox;
import cn.authing.guard.RegisterButton;
import cn.authing.guard.activity.BaseLoginActivity;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.util.Validator;

public class NissanVirtualKeySignupTwoActivity extends BaseLoginActivity implements TextWatcher {

    private static final int COLOR_OK = 0xffffffff;
    private static final int COLOR_ERR = 0xff831827;

    PasswordEditText passwordEditText;
    PasswordConfirmEditText passwordConfirmEditText;

    TextView tvRule1;
    TextView tvRule2;
    TextView tvRule3;
    TextView tvRule4;
    TextView tvRule5;
    TextView tvRule6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove EditText's default underline
        GlobalStyle.setEditTextBackground(0);

        setContentView(R.layout.activity_nissan_virtual_key_signup2);

        tvRule1 = findViewById(R.id.tv_rule1);
        tvRule2 = findViewById(R.id.tv_rule2);
        tvRule3 = findViewById(R.id.tv_rule3);
        tvRule4 = findViewById(R.id.tv_rule4);
        tvRule5 = findViewById(R.id.tv_rule5);
        tvRule6 = findViewById(R.id.tv_rule6);

        passwordEditText = findViewById(R.id.nvk_et_pwd);
        passwordEditText.getEditText().addTextChangedListener(this);
        passwordConfirmEditText = findViewById(R.id.nvk_et_pwd_confirm);
        passwordConfirmEditText.getEditText().addTextChangedListener(this);

        String email = getIntent().getStringExtra("email");

        RegisterButton btn = findViewById(R.id.btn_signup);
        btn.setEmail(email);

        btn.setOnRegisterListener((code, message, data)->{
            runOnUiThread(()->{
                btn.stopLoadingVisualEffect();
                if (code == 200) {
                    Intent intent = new Intent(NissanVirtualKeySignupTwoActivity.this, MainActivity.class);
                    intent.putExtra("user", data);
                    startActivity(intent);
                    finish();
                }
            });
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String t1 = passwordEditText.getText().toString();
        String t2 = passwordConfirmEditText.getText().toString();
        if (t1.equals(t2)) {
            tvRule1.setTextColor(COLOR_OK);
        } else {
            tvRule1.setTextColor(COLOR_ERR);
        }

        if (t1.length() >= 8) {
            tvRule2.setTextColor(COLOR_OK);
        } else {
            tvRule2.setTextColor(COLOR_ERR);
        }

        if (Validator.hasLowerCase(t1)) {
            tvRule3.setTextColor(COLOR_OK);
        } else {
            tvRule3.setTextColor(COLOR_ERR);
        }

        if (Validator.hasUpperCase(t1)) {
            tvRule4.setTextColor(COLOR_OK);
        } else {
            tvRule4.setTextColor(COLOR_ERR);
        }

        if (Validator.hasNumber(t1)) {
            tvRule5.setTextColor(COLOR_OK);
        } else {
            tvRule5.setTextColor(COLOR_ERR);
        }

        if (Validator.hasSpecialCharacter(t1)) {
            tvRule6.setTextColor(COLOR_OK);
        } else {
            tvRule6.setTextColor(COLOR_ERR);
        }
    }
}
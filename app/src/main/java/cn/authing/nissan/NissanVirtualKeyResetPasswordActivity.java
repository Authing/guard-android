package cn.authing.nissan;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;
import android.widget.Toast;

import cn.authing.R;
import cn.authing.guard.GlobalStyle;
import cn.authing.guard.PasswordConfirmEditText;
import cn.authing.guard.PasswordEditText;
import cn.authing.guard.VerifyCodeEditText;
import cn.authing.guard.activity.BaseLoginActivity;
import cn.authing.guard.internal.LoadingButton;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.util.Validator;

public class NissanVirtualKeyResetPasswordActivity extends BaseLoginActivity implements TextWatcher  {

    private static final int COLOR_OK = 0xffffffff;
    private static final int COLOR_ERR = 0xff831827;

    VerifyCodeEditText codeEditText;
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

        setContentView(R.layout.activity_nissan_virtual_key_reset_password);

        String email = getIntent().getStringExtra("email");
        String s = String.format(getString(R.string.nvk_reset_pwd_title), email);

        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(s);

        tvRule1 = findViewById(R.id.tv_rule1);
        tvRule2 = findViewById(R.id.tv_rule2);
        tvRule3 = findViewById(R.id.tv_rule3);
        tvRule4 = findViewById(R.id.tv_rule4);
        tvRule5 = findViewById(R.id.tv_rule5);
        tvRule6 = findViewById(R.id.tv_rule6);

        codeEditText = findViewById(R.id.nvk_et_code);
        passwordEditText = findViewById(R.id.nvk_et_pwd);
        passwordEditText.getEditText().addTextChangedListener(this);
        passwordConfirmEditText = findViewById(R.id.nvk_et_pwd_confirm);
        passwordConfirmEditText.getEditText().addTextChangedListener(this);

        LoadingButton btnSendCode = findViewById(R.id.btn_send_code);
        btnSendCode.setOnClickListener(v -> {
            btnSendCode.startLoadingVisualEffect();

            AuthClient.resetPasswordByEmail(email, (code, message, data)->{
                runOnUiThread(()->{
                    if (code == 200) {
                        Toast.makeText(getApplicationContext(), "Password reset code sent", Toast.LENGTH_LONG).show();
                    }
                    btnSendCode.stopLoadingVisualEffect();
                });
            });
        });

        LoadingButton btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(v -> {
            btnSubmit.startLoadingVisualEffect();

            AuthClient.resetPasswordByEmailCode(email, codeEditText.getText().toString(), passwordEditText.getText().toString(), (code, message, data)->{
                runOnUiThread(()->{
                    if (code == 200) {
                        Toast.makeText(getApplicationContext(), "Password reset success. Now go to sign in", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(NissanVirtualKeyResetPasswordActivity.this, NissanVirtualKeyLoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    btnSubmit.stopLoadingVisualEffect();
                });
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
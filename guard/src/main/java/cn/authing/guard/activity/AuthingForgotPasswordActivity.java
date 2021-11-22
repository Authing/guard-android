package cn.authing.guard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import cn.authing.guard.AccountEditText;
import cn.authing.guard.GlobalStyle;
import cn.authing.guard.R;
import cn.authing.guard.internal.LoadingButton;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.util.Validator;

public class AuthingForgotPasswordActivity extends BaseLoginActivity {

    private AccountEditText editText;
    private TextView tvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GlobalStyle.setEditTextBackground(0);
        GlobalStyle.setEditTextLayoutBackground((R.drawable.authing_edit_text_layout_background));

        setContentView(R.layout.activity_authing_forgot_password);

        editText = findViewById(R.id.et_phone_or_email);
        tvError = findViewById(R.id.tv_error);

        LoadingButton btn = findViewById(R.id.btn_reset);
        btn.setOnClickListener((v)->{
            String s = editText.getText().toString();
            if (Validator.isValidEmail(s)) {
                btn.setEnabled(false);
                btn.startLoadingVisualEffect();
                AuthClient.sendResetPasswordEmail(s, (code, message, data)->{
                    runOnUiThread(()->{
                        btn.stopLoadingVisualEffect();
                        btn.setEnabled(true);
                        if (code == 200) {
                            next(true);
                        } else {
                            tvError.setText(message);
                        }
                    });
                });
            } else if (Validator.isValidPhoneNumber(s)) {
                btn.setEnabled(false);
                btn.startLoadingVisualEffect();
                next(false);
            } else {
                tvError.setText(R.string.authing_invalid_phone_or_email);
            }
        });
    }

    private void next(boolean isEmail) {
        Intent intent = new Intent();
        if (isEmail) {
            intent.setClass(this, AuthingResetPasswordByEmailActivity.class);
        } else {
            intent.setClass(this, AuthingResetPasswordByPhoneActivity.class);
        }
        intent.putExtra("account", editText.getText().toString());
        startActivity(intent);
        finish();
    }
}
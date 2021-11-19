package cn.authing.guard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import cn.authing.guard.PasswordConfirmEditText;
import cn.authing.guard.PasswordEditText;
import cn.authing.guard.R;
import cn.authing.guard.VerifyCodeEditText;
import cn.authing.guard.internal.LoadingButton;
import cn.authing.guard.network.AuthClient;

public class AuthingResetPasswordByEmailActivity extends BaseLoginActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authing_reset_password_by_email);

        Intent intent = getIntent();
        String account = intent.getStringExtra("account");

        TextView tip = findViewById(R.id.tv_tip);
        String s = String.format(getString(R.string.authing_reset_password_by_email_tip), account);
        tip.setText(s);

        VerifyCodeEditText codeEditText = findViewById(R.id.et_code);
        PasswordEditText passwordEditText = findViewById(R.id.et_password);
        PasswordConfirmEditText confirmEditText = findViewById(R.id.et_password_confirm);

        TextView tvError = findViewById(R.id.tv_error);

        LoadingButton btn = findViewById(R.id.btn_reset);
        btn.setOnClickListener((v)->{
            if (!TextUtils.isEmpty(passwordEditText.getErrorText())
            || !TextUtils.isEmpty(confirmEditText.getErrorText())) {
                return;
            }

            AuthClient.resetPasswordByEmailCode(account, codeEditText.getText().toString(), passwordEditText.getText().toString(),
                    (code, message, data)-> runOnUiThread(()->{
                if (code == 200) {
                    finish();
                } else {
                    tvError.setText(message);
                }
                btn.stopLoadingVisualEffect();
            }));
        });
    }
}
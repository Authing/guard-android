package cn.authing.guard.activity;

import android.os.Bundle;

import cn.authing.guard.PasswordConfirmEditText;
import cn.authing.guard.PasswordEditText;
import cn.authing.guard.R;
import cn.authing.guard.internal.BasePasswordEditText;
import cn.authing.guard.internal.LoadingButton;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.util.Util;

public class ChangePasswordActivity extends BaseAuthActivity {

    private BasePasswordEditText oldEditText;
    private PasswordEditText newEditText;
    private PasswordConfirmEditText confirmEditText;
    private LoadingButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authing_change_password);

        oldEditText = findViewById(R.id.pwd_old);
        newEditText = findViewById(R.id.pwd_new);

        confirmEditText = findViewById(R.id.et_confirm);

        button = findViewById(R.id.btn_submit);
        button.setOnClickListener((v)->{
            if (Util.isNull(oldEditText.getErrorText())
                    && Util.isNull(newEditText.getErrorText())
                    && Util.isNull(confirmEditText.getErrorText())
                    && !Util.isNull(newEditText.getText().toString())
                    && !Util.isNull(confirmEditText.getText().toString())) {
                button.startLoadingVisualEffect();
                AuthClient.updatePassword(newEditText.getText().toString(), oldEditText.getText().toString(), (code, message, data) -> {
                    button.stopLoadingVisualEffect();
                    runOnUiThread(()->{
                        if (code == 200) {
                            finish();
                        } else {
                            Util.setErrorText(button, message);
                        }
                    });
                });
            }
        });
    }
}
package cn.authing.guard.handler.register;

import android.text.TextUtils;
import android.view.View;

import cn.authing.guard.EmailEditText;
import cn.authing.guard.PasswordConfirmEditText;
import cn.authing.guard.PasswordEditText;
import cn.authing.guard.R;
import cn.authing.guard.RegisterButton;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Util;

public class EmailRegisterHandler extends AbsRegisterHandler {

    private String email;

    public EmailRegisterHandler(RegisterButton loginButton, IRegisterRequestCallBack callBack) {
        super(loginButton, callBack);
    }

    @Override
    protected boolean register() {
        View emailET = Util.findViewByClass(mRegisterButton, EmailEditText.class);
        View passwordET = Util.findViewByClass(mRegisterButton, PasswordEditText.class);
        if ((email != null || emailET != null && emailET.isShown())
                && passwordET != null && passwordET.isShown()) {
            final String account = email != null ? email : ((EmailEditText) emailET).getText().toString();
            final String password = ((PasswordEditText) passwordET).getText().toString();
            if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
                Util.setErrorText(mRegisterButton, "Account or password is invalid");
                fireCallback("Account or password is invalid");
                return false;
            }

            View v = Util.findViewByClass(mRegisterButton, PasswordConfirmEditText.class);
            if (v != null) {
                PasswordConfirmEditText passwordConfirmEditText = (PasswordConfirmEditText)v;
                if (!password.equals(passwordConfirmEditText.getText().toString())) {
                    Util.setErrorText(mRegisterButton, mContext.getResources().getString(R.string.authing_password_not_match));
                    fireCallback(mContext.getResources().getString(R.string.authing_password_not_match));
                    return false;
                }
            }

            mRegisterButton.startLoadingVisualEffect();
            registerByEmail(account, password);
        }
        return false;
    }

    private void registerByEmail(String email, String password) {
        AuthClient.registerByEmail(email, password, (code, message, data)->{
            if (code == 200) {
                fireCallback(200, "", data);
            } else {
                Util.setErrorText(mRegisterButton, message);
                fireCallback(code, message, null);
            }
        });
        ALog.d(TAG, "register by email");
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

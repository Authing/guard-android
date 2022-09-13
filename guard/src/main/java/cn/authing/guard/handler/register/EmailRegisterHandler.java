package cn.authing.guard.handler.register;

import android.text.TextUtils;
import android.view.View;

import cn.authing.guard.Authing;
import cn.authing.guard.EmailEditText;
import cn.authing.guard.PasswordConfirmEditText;
import cn.authing.guard.PasswordEditText;
import cn.authing.guard.R;
import cn.authing.guard.RegisterButton;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.OIDCClient;
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
            boolean showError = false;
            EmailEditText emailEditText = ((EmailEditText) emailET);
            final String account = email != null ? email : emailEditText.getText().toString();
            if (!emailEditText.isContentValid()) {
                showError(emailEditText, mContext.getString(R.string.authing_email_address_empty));
                showError = true;
            }

            PasswordEditText passwordEditText = ((PasswordEditText) passwordET);
            final String password = passwordEditText.getText().toString();
            if (TextUtils.isEmpty(password)) {
                showError(passwordEditText, mContext.getString(R.string.authing_password_empty));
                showError = true;
            }

            View v = Util.findViewByClass(mRegisterButton, PasswordConfirmEditText.class);
            if (v != null) {
                PasswordConfirmEditText passwordConfirmEditText = (PasswordConfirmEditText)v;
                if (!password.equals(passwordConfirmEditText.getText().toString())) {
                    showError(passwordConfirmEditText, mContext.getString(R.string.authing_password_not_match));
                    showError = true;
                }
            }

            if (showError){
                return false;
            }

            mRegisterButton.startLoadingVisualEffect();
            registerByEmail(account, password);
            return true;
        }
        return false;
    }

    private void registerByEmail(String email, String password) {
        clearError();
        Authing.AuthProtocol authProtocol = getAuthProtocol();
        if (authProtocol == Authing.AuthProtocol.EInHouse) {
            AuthClient.registerByEmail(email, password, this::fireCallback);
        } else if (authProtocol == Authing.AuthProtocol.EOIDC) {
            new OIDCClient().registerByEmail(email, password, this::fireCallback);
        }
        ALog.d(TAG, "register by email");
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

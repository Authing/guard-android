package cn.authing.guard.handler.login;

import android.text.TextUtils;
import android.view.View;

import cn.authing.guard.AccountEditText;
import cn.authing.guard.Authing;
import cn.authing.guard.CaptchaCodeEditText;
import cn.authing.guard.LoginButton;
import cn.authing.guard.PasswordEditText;
import cn.authing.guard.R;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Util;

public class AccountLoginHandler extends AbsLoginHandler {

    public AccountLoginHandler(LoginButton loginButton, ILoginRequestCallBack callback, boolean autoRegister) {
        super(loginButton, callback, autoRegister);
    }

    @Override
    protected boolean login() {
        View accountET = Util.findViewByClass(loginButton, AccountEditText.class);
        View passwordET = Util.findViewByClass(loginButton, PasswordEditText.class);
        if (accountET != null && accountET.isShown()
                && passwordET != null && passwordET.isShown()) {
            boolean showError = false;
            AccountEditText accountEditText = ((AccountEditText) accountET);
            final String account = accountEditText.getText().toString();
            if (TextUtils.isEmpty(account)) {
                showError(accountEditText, mContext.getString(R.string.authing_account_empty));
                showError = true;
            }

            PasswordEditText passwordEditText = ((PasswordEditText) passwordET);
            final String password = passwordEditText.getText().toString();
            if (TextUtils.isEmpty(password)) {
                showError(passwordEditText, mContext.getString(R.string.authing_password_empty));
                showError = true;
            }

            CaptchaCodeEditText captchaCodeET = (CaptchaCodeEditText) Util.findViewByClass(loginButton, CaptchaCodeEditText.class);
            String captchaCode = null;
            if (captchaCodeET != null){
                captchaCode = captchaCodeET.getText().toString();
                if (TextUtils.isEmpty(captchaCode)) {
                    showError(captchaCodeET, mContext.getString(R.string.authing_captcha_code_empty));
                    showError = true;
                }
            }

            if (showError){
                return false;
            }

            loginButton.startLoadingVisualEffect();
            loginByAccount(account, password, captchaCode);
            return true;
        }
        return false;
    }

    private void loginByAccount(String account, String password, String captchaCode) {
        clearError();
        Authing.AuthProtocol authProtocol = getAuthProtocol();
        if (authProtocol == Authing.AuthProtocol.EInHouse) {
            AuthClient.loginByAccount(account, password, isAutoRegister(), null, captchaCode, this::fireCallback);
        } else if (authProtocol == Authing.AuthProtocol.EOIDC) {
            new OIDCClient().loginByAccount(account, password, isAutoRegister(), null, captchaCode, this::fireCallback);
        }
        ALog.d(TAG, "login by account");
    }
}

package cn.authing.guard.handler.login;

import android.text.TextUtils;
import android.view.View;

import cn.authing.guard.AccountEditText;
import cn.authing.guard.LoginButton;
import cn.authing.guard.PasswordEditText;
import cn.authing.guard.R;
import cn.authing.guard.container.AuthContainer;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Util;

public class AccountLoginHandler extends AbsLoginHandler {

    public AccountLoginHandler(LoginButton loginButton, ILoginRequestCallBack callback) {
        super(loginButton, callback);
    }

    @Override
    protected boolean login() {
        View accountET = Util.findViewByClass(loginButton, AccountEditText.class);
        View passwordET = Util.findViewByClass(loginButton, PasswordEditText.class);
        if (accountET != null && accountET.isShown()
                && passwordET != null && passwordET.isShown()) {
            final String account = ((AccountEditText) accountET).getText().toString();
            final String password = ((PasswordEditText) passwordET).getText().toString();
            if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
                fireCallback(accountET.getContext().getString(R.string.authing_account_or_password_empty));
                return false;
            }

            loginButton.startLoadingVisualEffect();
            loginByAccount(account, password);
            return true;
        }
        return false;
    }

    private void loginByAccount(String account, String password) {
        if (getAuthProtocol() == AuthContainer.AuthProtocol.EInHouse) {
            AuthClient.loginByAccount(account, password, this::fireCallback);
        } else if (getAuthProtocol() == AuthContainer.AuthProtocol.EOIDC) {
            OIDCClient.loginByAccount(account, password, this::fireCallback);
        }
        ALog.d(TAG, "login by account");
    }
}

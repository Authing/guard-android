package cn.authing.guard.handler.login;

import android.text.TextUtils;
import android.view.View;

import cn.authing.guard.AccountEditText;
import cn.authing.guard.Authing;
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
        Authing.AuthProtocol authProtocol = getAuthProtocol();
        if (authProtocol == Authing.AuthProtocol.EInHouse) {
            AuthClient.loginByAccount(account, password, isAutoRegister(), null, this::fireCallback);
        } else if (authProtocol == Authing.AuthProtocol.EOIDC) {
            new OIDCClient().loginByAccount(account, password, isAutoRegister(), null, this::fireCallback);
        }
        ALog.d(TAG, "login by account");
    }
}

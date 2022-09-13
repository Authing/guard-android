package cn.authing.guard.handler.login;

import android.text.TextUtils;
import android.view.View;

import cn.authing.guard.Authing;
import cn.authing.guard.EmailEditText;
import cn.authing.guard.LoginButton;
import cn.authing.guard.R;
import cn.authing.guard.VerifyCodeEditText;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Util;

public class EmailCodeLoginHandler extends AbsLoginHandler{

    private String emailNumber;
    private String emailCode;

    public EmailCodeLoginHandler(LoginButton loginButton, ILoginRequestCallBack callback) {
        super(loginButton, callback);
    }

    @Override
    protected boolean login() {
        View emailNumberET = Util.findViewByClass(loginButton, EmailEditText.class);
        View emailCodeET = Util.findViewByClass(loginButton, VerifyCodeEditText.class);
        if (emailNumberET != null && emailNumberET.isShown()) {
            EmailEditText emailNumberEditText = (EmailEditText)emailNumberET;
            emailNumber = emailNumberEditText.getText().toString();
        }
        if (emailCodeET != null && emailCodeET.isShown()) {
            VerifyCodeEditText verifyCodeEditText = (VerifyCodeEditText)emailCodeET;
            emailCode = verifyCodeEditText.getText().toString();
        }
        if (!TextUtils.isEmpty(emailNumber) && !TextUtils.isEmpty(emailCode)) {
            loginButton.startLoadingVisualEffect();
            loginByEmailCode(emailNumber, emailCode);
            return true;
        }

        if (emailNumberET != null && emailNumberET.isShown()
                && emailCodeET != null && emailCodeET.isShown()) {
            boolean showError = false;
            EmailEditText emailNumberEditText = (EmailEditText)emailNumberET;
            if (!emailNumberEditText.isContentValid()) {
                showError(emailNumberEditText, mContext.getString(R.string.authing_email_address_empty));
                showError = true;
            }

            final String email = emailNumberEditText.getText().toString();
            VerifyCodeEditText verifyCodeEditText = ((VerifyCodeEditText) emailCodeET);
            final String code = verifyCodeEditText.getText().toString();
            if (TextUtils.isEmpty(code)) {
                showError(verifyCodeEditText, mContext.getString(R.string.authing_verify_code_empty));
                showError = true;
            }

            if (showError){
                return false;
            }

            loginButton.startLoadingVisualEffect();
            loginByEmailCode(email, code);
            return true;
        }
        return false;
    }

    private void loginByEmailCode(String email, String verifyCode) {
        clearError();
        Authing.AuthProtocol authProtocol = getAuthProtocol();
        if (authProtocol == Authing.AuthProtocol.EInHouse) {
            AuthClient.loginByEmailCode(email, verifyCode, this::fireCallback);
        } else if (authProtocol == Authing.AuthProtocol.EOIDC) {
            new OIDCClient().loginByEmailCode(email, verifyCode, this::fireCallback);
        }
        ALog.d(TAG, "login by email code");
    }
}

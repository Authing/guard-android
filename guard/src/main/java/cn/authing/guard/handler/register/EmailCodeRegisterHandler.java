package cn.authing.guard.handler.register;

import android.text.TextUtils;
import android.view.View;

import cn.authing.guard.EmailEditText;
import cn.authing.guard.R;
import cn.authing.guard.RegisterButton;
import cn.authing.guard.VerifyCodeEditText;
import cn.authing.guard.container.AuthContainer;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Util;

public class EmailCodeRegisterHandler extends AbsRegisterHandler {


    public EmailCodeRegisterHandler(RegisterButton loginButton, IRegisterRequestCallBack callBack) {
        super(loginButton, callBack);
    }

    @Override
    protected boolean register() {
        View emailET = Util.findViewByClass(mRegisterButton, EmailEditText.class);
        View verifyCodeET = Util.findViewByClass(mRegisterButton, VerifyCodeEditText.class);
        if (emailET != null && emailET.isShown()
                && verifyCodeET != null && verifyCodeET.isShown()) {
            boolean showError = false;
            EmailEditText emailEditText = (EmailEditText)emailET;
            if (!emailEditText.isContentValid()) {
                showError(emailEditText, mContext.getString(R.string.authing_email_address_empty));
                showError = true;
            }

            final String email = emailEditText.getText().toString();
            VerifyCodeEditText verifyCodeEditText = ((VerifyCodeEditText) verifyCodeET);
            final String code = verifyCodeEditText.getText().toString();
            if (TextUtils.isEmpty(code)) {
                showError(verifyCodeEditText, mContext.getString(R.string.authing_verify_code_empty));
                showError = true;
            }

            if (showError){
                return false;
            }

            mRegisterButton.startLoadingVisualEffect();
            registerByEmailCode(email, code);
            return true;
        }
        return false;
    }

    private void registerByEmailCode(String email, String verifyCode) {
        clearError();
        if (getAuthProtocol() == AuthContainer.AuthProtocol.EInHouse) {
            AuthClient.registerByEmailCode(email, verifyCode, this::fireCallback);
        } else if (getAuthProtocol() == AuthContainer.AuthProtocol.EOIDC) {
            new OIDCClient().registerByEmailCode(email, verifyCode, this::fireCallback);
        }
        ALog.d(TAG, "register by email code");
    }

}

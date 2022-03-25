package cn.authing.guard.handler.login;

import android.text.TextUtils;
import android.view.View;

import cn.authing.guard.LoginButton;
import cn.authing.guard.PhoneNumberEditText;
import cn.authing.guard.R;
import cn.authing.guard.VerifyCodeEditText;
import cn.authing.guard.container.AuthContainer;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Util;

public class PhoneCodeLoginHandler extends AbsLoginHandler{

    private String phoneNumber;
    private String phoneCode;

    public PhoneCodeLoginHandler(LoginButton loginButton, ILoginRequestCallBack callback) {
        super(loginButton, callback);
    }

    @Override
    protected boolean login() {
        View phoneNumberET = Util.findViewByClass(loginButton, PhoneNumberEditText.class);
        View phoneCodeET = Util.findViewByClass(loginButton, VerifyCodeEditText.class);
        if (phoneNumberET != null && phoneNumberET.isShown()) {
            PhoneNumberEditText phoneNumberEditText = (PhoneNumberEditText)phoneNumberET;
            phoneNumber = phoneNumberEditText.getText().toString();
        }
        if (phoneCodeET != null && phoneCodeET.isShown()) {
            VerifyCodeEditText verifyCodeEditText = (VerifyCodeEditText)phoneCodeET;
            phoneCode = verifyCodeEditText.getText().toString();
        }
        if (!TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(phoneCode)) {
            loginButton.startLoadingVisualEffect();
            loginByPhoneCode(phoneNumber, phoneCode);
            return true;
        }

        if (phoneNumberET != null && phoneNumberET.isShown()
                && phoneCodeET != null && phoneCodeET.isShown()) {
            PhoneNumberEditText phoneNumberEditText = (PhoneNumberEditText)phoneNumberET;
            if (!phoneNumberEditText.isContentValid()) {
                fireCallback(mContext.getString(R.string.authing_invalid_phone_number));
                return false;
            }

            final String phone = phoneNumberEditText.getText().toString();
            final String code = ((VerifyCodeEditText) phoneCodeET).getText().toString();
            if (TextUtils.isEmpty(code)) {
                fireCallback(mContext.getString(R.string.authing_incorrect_verify_code));
                return false;
            }

            loginButton.startLoadingVisualEffect();
            loginByPhoneCode(phone, code);
            return true;
        }

        return false;
    }

    private void loginByPhoneCode(String phone, String verifyCode) {
        if (getAuthProtocol() == AuthContainer.AuthProtocol.EInHouse) {
            AuthClient.loginByPhoneCode(phone, verifyCode, this::fireCallback);
        } else if (getAuthProtocol() == AuthContainer.AuthProtocol.EOIDC) {
            OIDCClient.loginByPhoneCode(phone, verifyCode, this::fireCallback);
        }
        ALog.d(TAG, "login by phone code");
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

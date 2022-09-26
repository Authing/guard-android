package cn.authing.guard.handler.login;

import android.text.TextUtils;
import android.view.View;

import cn.authing.guard.Authing;
import cn.authing.guard.CountryCodePicker;
import cn.authing.guard.LoginButton;
import cn.authing.guard.PhoneNumberEditText;
import cn.authing.guard.R;
import cn.authing.guard.VerifyCodeEditText;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Util;

public class PhoneCodeLoginHandler extends AbsLoginHandler{

    private String phoneCountryCode;
    private String phoneNumber;
    private String phoneCode;

    public PhoneCodeLoginHandler(LoginButton loginButton, ILoginRequestCallBack callback, boolean autoRegister) {
        super(loginButton, callback, autoRegister);
    }

    @Override
    protected boolean login() {
        View phoneCountryCodeET = Util.findViewByClass(loginButton, CountryCodePicker.class);
        View phoneNumberET = Util.findViewByClass(loginButton, PhoneNumberEditText.class);
        View phoneCodeET = Util.findViewByClass(loginButton, VerifyCodeEditText.class);
        if (phoneCountryCodeET != null && phoneCountryCodeET.isShown()){
            CountryCodePicker countryCodePicker = (CountryCodePicker)phoneCountryCodeET;
            phoneCountryCode = countryCodePicker.getCountryCode();
        }
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
            loginByPhoneCode(phoneCountryCode, phoneNumber, phoneCode);
            return true;
        }

        if (phoneNumberET != null && phoneNumberET.isShown()
                && phoneCodeET != null && phoneCodeET.isShown()) {
            String countryCode = "";
            boolean showError = false;
            PhoneNumberEditText phoneNumberEditText = (PhoneNumberEditText)phoneNumberET;
            if (phoneCountryCodeET != null && phoneCountryCodeET.isShown()){
                CountryCodePicker countryCodePicker = (CountryCodePicker)phoneCountryCodeET;
                countryCode = countryCodePicker.getCountryCode();
                if (TextUtils.isEmpty(countryCode)) {
                    showError(phoneNumberEditText, mContext.getString(R.string.authing_phone_country_code_empty));
                    showError = true;
                }
            }

            if (!phoneNumberEditText.isContentValid()) {
                showError(phoneNumberEditText, mContext.getString(R.string.authing_phone_number_empty));
                showError = true;
            }

            final String phone = phoneNumberEditText.getText().toString();
            VerifyCodeEditText verifyCodeEditText = ((VerifyCodeEditText) phoneCodeET);
            final String code = verifyCodeEditText.getText().toString();
            if (TextUtils.isEmpty(code)) {
                showError(verifyCodeEditText, mContext.getString(R.string.authing_verify_code_empty));
                showError = true;
            }

            if (showError){
                return false;
            }

            loginButton.startLoadingVisualEffect();
            loginByPhoneCode(countryCode, phone, code);
            return true;
        }

        return false;
    }

    private void loginByPhoneCode(String phoneCountryCode, String phone, String verifyCode) {
        clearError();
        Authing.AuthProtocol authProtocol = getAuthProtocol();
        if (authProtocol == Authing.AuthProtocol.EInHouse) {
            AuthClient.loginByPhoneCode(phoneCountryCode, phone, verifyCode, isAutoRegister(), this::fireCallback);
        } else if (authProtocol == Authing.AuthProtocol.EOIDC) {
            new OIDCClient().loginByPhoneCode(phoneCountryCode, phone, verifyCode, isAutoRegister(), this::fireCallback);
        }
        ALog.d(TAG, "login by phone code");
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

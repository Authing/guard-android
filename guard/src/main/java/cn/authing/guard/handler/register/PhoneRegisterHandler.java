package cn.authing.guard.handler.register;

import android.text.TextUtils;
import android.view.View;

import cn.authing.guard.Authing;
import cn.authing.guard.CountryCodePicker;
import cn.authing.guard.PasswordConfirmEditText;
import cn.authing.guard.PasswordEditText;
import cn.authing.guard.PhoneNumberEditText;
import cn.authing.guard.R;
import cn.authing.guard.RegisterButton;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Util;

public class PhoneRegisterHandler extends AbsRegisterHandler {


    public PhoneRegisterHandler(RegisterButton loginButton, IRegisterRequestCallBack callBack) {
        super(loginButton, callBack);
    }

    @Override
    protected boolean register() {
        View phoneCountryCodeET = Util.findViewByClass(mRegisterButton, CountryCodePicker.class);
        View phoneET = Util.findViewByClass(mRegisterButton, PhoneNumberEditText.class);
        View passwordET = Util.findViewByClass(mRegisterButton, PasswordEditText.class);
        if ((phoneET != null && phoneET.isShown())
                && passwordET != null && passwordET.isShown()) {

            boolean showError = false;
            String phoneCountryCode = "";
            PhoneNumberEditText phoneNumberEditText = (PhoneNumberEditText)phoneET;
            if (phoneCountryCodeET != null && phoneCountryCodeET.isShown()){
                CountryCodePicker countryCodePicker = (CountryCodePicker)phoneCountryCodeET;
                phoneCountryCode = countryCodePicker.getCountryCode();
                if (TextUtils.isEmpty(phoneCountryCode)) {
                    showError(phoneNumberEditText, mContext.getString(R.string.authing_phone_country_code_empty));
                    showError = true;
                }
            }

            if (!phoneNumberEditText.isContentValid()) {
                showError(phoneNumberEditText, mContext.getString(R.string.authing_phone_number_empty));
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

            final String phone = phoneNumberEditText.getText().toString();
            mRegisterButton.startLoadingVisualEffect();
            registerByPhone(phoneCountryCode, phone, password);
            return true;
        }
        return false;
    }

    private void registerByPhone(String phoneCountryCode, String phone, String password) {
        Authing.AuthProtocol authProtocol = getAuthProtocol();
        if (authProtocol == Authing.AuthProtocol.EInHouse) {
            AuthClient.registerByPhonePassword(phoneCountryCode, phone, password, null, this::fireCallback);
        } else if (authProtocol == Authing.AuthProtocol.EOIDC) {
            new OIDCClient().registerByPhonePassword(phoneCountryCode, phone, password, null, this::fireCallback);
        }
        ALog.e(TAG, "register by phone password");
    }

}

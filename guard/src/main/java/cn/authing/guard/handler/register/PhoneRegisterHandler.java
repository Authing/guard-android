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

            String phoneCountryCode = "";
            if (phoneCountryCodeET != null && phoneCountryCodeET.isShown()){
                CountryCodePicker countryCodePicker = (CountryCodePicker)phoneCountryCodeET;
                phoneCountryCode = countryCodePicker.getCountryCode();
                if (TextUtils.isEmpty(phoneCountryCode)) {
                    Util.setErrorText(mRegisterButton, mContext.getString(R.string.authing_invalid_phone_country_code));
                    fireCallback(mContext.getString(R.string.authing_invalid_phone_country_code));
                    return false;
                }
            }

            final String phone = ((PhoneNumberEditText) phoneET).getText().toString();
            final String password = ((PasswordEditText) passwordET).getText().toString();
            if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
                Util.setErrorText(mRegisterButton, "Phone or password is invalid");
                fireCallback("Phone or password is invalid");
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

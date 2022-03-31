package cn.authing.guard.handler.register;

import android.text.TextUtils;
import android.view.View;

import cn.authing.guard.CountryCodePicker;
import cn.authing.guard.PasswordEditText;
import cn.authing.guard.PhoneNumberEditText;
import cn.authing.guard.R;
import cn.authing.guard.RegisterButton;
import cn.authing.guard.VerifyCodeEditText;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.util.Util;

public class PhoneCodeRegisterHandler extends AbsRegisterHandler {


    public PhoneCodeRegisterHandler(RegisterButton loginButton, IRegisterRequestCallBack callback) {
        super(loginButton, callback);
    }

    @Override
    protected boolean register() {
        View phoneCountryCodeET = Util.findViewByClass(mRegisterButton, CountryCodePicker.class);
        View phoneNumberET = Util.findViewByClass(mRegisterButton, PhoneNumberEditText.class);
        View passwordET = Util.findViewByClass(mRegisterButton, PasswordEditText.class);
        View phoneCodeET = Util.findViewByClass(mRegisterButton, VerifyCodeEditText.class);

        if (phoneCountryCodeET != null && phoneCountryCodeET.isShown()
                && phoneNumberET != null && phoneNumberET.isShown()
                && passwordET != null && passwordET.isShown()
                && phoneCodeET != null && phoneCodeET.isShown()) {
            CountryCodePicker countryCodePicker = (CountryCodePicker)phoneCountryCodeET;
            final String phoneCountryCode = countryCodePicker.getCountryCode();
            if (TextUtils.isEmpty(phoneCountryCode)) {
                Util.setErrorText(mRegisterButton, mContext.getString(R.string.authing_invalid_phone_country_code));
                fireCallback(mContext.getString(R.string.authing_invalid_phone_country_code));
                return false;
            }

            PhoneNumberEditText phoneNumberEditText = (PhoneNumberEditText)phoneNumberET;
            if (!phoneNumberEditText.isContentValid()) {
                Util.setErrorText(mRegisterButton, mContext.getString(R.string.authing_invalid_phone_number));
                fireCallback(mContext.getString(R.string.authing_invalid_phone_number));
                return false;
            }

            final String phone = phoneNumberEditText.getText().toString();
            final String code = ((VerifyCodeEditText) phoneCodeET).getText().toString();
            if (TextUtils.isEmpty(code)) {
                Util.setErrorText(mRegisterButton, mContext.getString(R.string.authing_incorrect_verify_code));
                fireCallback(mContext.getString(R.string.authing_incorrect_verify_code));
                return false;
            }

            final String password = ((PasswordEditText) passwordET).getText().toString();
            if (TextUtils.isEmpty(password)) {
                fireCallback("Password is invalid");
                return false;
            }

            mRegisterButton.startLoadingVisualEffect();
            registerByPhoneCode(phoneCountryCode, phone, code, password);
            return true;
        }
        return false;
    }


    private void registerByPhoneCode(String phoneCountryCode, String phone, String phoneCode, String password) {
        AuthClient.registerByPhoneCode(phoneCountryCode, phone, phoneCode, password, (code, message, data)->{
            if (code == 200) {
                fireCallback(200, "", data);
            } else {
                Util.setErrorText(mRegisterButton, message);
                fireCallback(code, message, null);
            }
        });
    }
}

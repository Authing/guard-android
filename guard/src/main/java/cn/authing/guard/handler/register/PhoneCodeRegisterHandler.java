package cn.authing.guard.handler.register;

import android.text.TextUtils;
import android.view.View;

import cn.authing.guard.CountryCodePicker;
import cn.authing.guard.PasswordEditText;
import cn.authing.guard.PhoneNumberEditText;
import cn.authing.guard.R;
import cn.authing.guard.RegisterButton;
import cn.authing.guard.VerifyCodeEditText;
import cn.authing.guard.container.AuthContainer;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Util;

public class PhoneCodeRegisterHandler extends AbsRegisterHandler {


    public PhoneCodeRegisterHandler(RegisterButton loginButton, IRegisterRequestCallBack callback) {
        super(loginButton, callback);
    }

    @Override
    protected boolean register() {
        View phoneCountryCodeET = Util.findViewByClass(mRegisterButton, CountryCodePicker.class);
        View phoneNumberET = Util.findViewByClass(mRegisterButton, PhoneNumberEditText.class);
        View phoneCodeET = Util.findViewByClass(mRegisterButton, VerifyCodeEditText.class);

        if (phoneNumberET != null && phoneNumberET.isShown()
                && phoneCodeET != null && phoneCodeET.isShown()) {

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

            mRegisterButton.startLoadingVisualEffect();
            registerByPhoneCode(phoneCountryCode, phone, code, "");
            return true;
        }
        return false;
    }


    private void registerByPhoneCode(String phoneCountryCode, String phone, String phoneCode, String password) {
        if (getAuthProtocol() == AuthContainer.AuthProtocol.EInHouse) {
            AuthClient.registerByPhoneCode(phoneCountryCode, phone, phoneCode, password, this::fireCallback);
        } else if (getAuthProtocol() == AuthContainer.AuthProtocol.EOIDC) {
            new OIDCClient().registerByPhoneCode(phoneCountryCode, phone, phoneCode, password, this::fireCallback);
        }
        ALog.d(TAG, "register by phone code");
    }

}

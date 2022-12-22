package cn.authing.guard.handler.bind;

import android.text.TextUtils;
import android.view.View;

import cn.authing.guard.CountryCodePicker;
import cn.authing.guard.PhoneNumberEditText;
import cn.authing.guard.R;
import cn.authing.guard.VerifyCodeEditText;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.data.SocialBindData;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.social.bind.SocialBindButton;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Util;

public class PhoneCodeBindHandler extends AbsBindHandler {

    private String phoneCountryCode;
    private String phoneNumber;
    private String phoneCode;

    public PhoneCodeBindHandler(SocialBindButton socialBindButton, IBindRequestCallBack callback) {
        super(socialBindButton, callback);
    }

    @Override
    protected boolean bind() {
        View phoneCountryCodeET = Util.findViewByClass(socialBindButton, CountryCodePicker.class);
        View phoneNumberET = Util.findViewByClass(socialBindButton, PhoneNumberEditText.class);
        View phoneCodeET = Util.findViewByClass(socialBindButton, VerifyCodeEditText.class);
        if (phoneCountryCodeET != null && phoneCountryCodeET.isShown()) {
            CountryCodePicker countryCodePicker = (CountryCodePicker) phoneCountryCodeET;
            phoneCountryCode = countryCodePicker.getCountryCode();
        }
        if (phoneNumberET != null && phoneNumberET.isShown()) {
            PhoneNumberEditText phoneNumberEditText = (PhoneNumberEditText) phoneNumberET;
            phoneNumber = phoneNumberEditText.getText().toString();
        }
        if (phoneCodeET != null && phoneCodeET.isShown()) {
            VerifyCodeEditText verifyCodeEditText = (VerifyCodeEditText) phoneCodeET;
            phoneCode = verifyCodeEditText.getText().toString();
        }
        if (!TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(phoneCode)) {
            socialBindButton.startLoadingVisualEffect();
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

            socialBindButton.startLoadingVisualEffect();
            loginByPhoneCode(countryCode, phone, code);
            return true;
        }

        return false;
    }

    private void loginByPhoneCode(String phoneCountryCode, String phone, String verifyCode) {
        if (mContext instanceof AuthActivity) {
            AuthActivity activity = (AuthActivity) mContext;
            AuthFlow flow = activity.getFlow();
            UserInfo userInfo = (UserInfo) flow.getData().get(AuthFlow.KEY_USER_INFO);
            if (userInfo != null && userInfo.getSocialBindData() != null) {
                SocialBindData socialBindData = userInfo.getSocialBindData();
                AuthClient.bindWechatByPhoneCode(socialBindData.getKey(), phoneCountryCode, phone, verifyCode, this::fireCallback);
                ALog.d(TAG, "bind by phone code");
            }
        }
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

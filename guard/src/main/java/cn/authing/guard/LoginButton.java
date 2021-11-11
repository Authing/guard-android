package cn.authing.guard;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import cn.authing.guard.data.Config;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.internal.LoadingButton;
import cn.authing.guard.network.Guardian;
import cn.authing.guard.network.Response;
import cn.authing.guard.util.Const;
import cn.authing.guard.util.Util;

public class LoginButton extends LoadingButton {

    private String phoneNumber;
    private String phoneCode;
    private String identifier;
    protected Callback<UserInfo> callback;

    public LoginButton(@NonNull Context context) {
        this(context, null);
    }

    public LoginButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public LoginButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
            setText(R.string.authing_login);
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textColor") == null) {
            setTextColor(0xffffffff);
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "background") == null) {
            setBackgroundResource(R.drawable.authing_button_background);
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textSize") == null) {
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        }

        PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(Color.WHITE,
                PorterDuff.Mode.SRC_ATOP);
        loading.setColorFilter(porterDuffColorFilter);

        setOnClickListener((v -> login()));
    }

    public void setOnLoginListener(Callback<UserInfo> callback) {
        this.callback = callback;
    }

    // manually set phone number. in case of 2 step login
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void login() {
        if (showLoading) {
            return;
        }

        if (requiresAgreement()) {
            return;
        }

        Authing.getPublicConfig((this::_login));
    }

    public void _login(Config config) {
        if (config == null) {
            fireCallback(null);
            return;
        }

        identifier = config.getIdentifier();

        View phoneNumberET = Util.findViewByClass(this, PhoneNumberEditText.class);
        View phoneCodeET = Util.findViewByClass(this, VerifyCodeEditText.class);
        if (phoneNumberET != null && phoneNumberET.isShown()) {
            PhoneNumberEditText phoneNumberEditText = (PhoneNumberEditText)phoneNumberET;
            phoneNumber = phoneNumberEditText.getText().toString();
        }
        if (phoneCodeET != null && phoneCodeET.isShown()) {
            VerifyCodeEditText verifyCodeEditText = (VerifyCodeEditText)phoneCodeET;
            phoneCode = verifyCodeEditText.getText().toString();
        }
        if (!TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(phoneCode)) {
            startLoadingVisualEffect();
            loginByPhoneCode(phoneNumber, phoneCode);
            return;
        }

        if (phoneNumberET != null && phoneNumberET.isShown()
                && phoneCodeET != null && phoneCodeET.isShown()) {
            PhoneNumberEditText phoneNumberEditText = (PhoneNumberEditText)phoneNumberET;
            if (!phoneNumberEditText.isContentValid()) {
                Util.setErrorText(this, getContext().getString(R.string.authing_invalid_phone_number));
                fireCallback(null);
                return;
            }

            final String phone = phoneNumberEditText.getText().toString();
            final String code = ((VerifyCodeEditText) phoneCodeET).getText().toString();
            if (TextUtils.isEmpty(code)) {
                Util.setErrorText(this, getContext().getString(R.string.authing_incorrect_verify_code));
                fireCallback(null);
                return;
            }

            startLoadingVisualEffect();
            loginByPhoneCode(phone, code);
        } else {
            View accountET = Util.findViewByClass(this, AccountEditText.class);
            View passwordET = Util.findViewByClass(this, PasswordEditText.class);
            if (accountET != null && accountET.isShown()
                    && passwordET != null && passwordET.isShown()) {
                final String account = ((AccountEditText) accountET).getText().toString();
                final String password = ((PasswordEditText) passwordET).getText().toString();
                if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
                    fireCallback(null);
                    return;
                }

                startLoadingVisualEffect();
                loginByAccount(account, password);
            }
        }
    }

    private boolean requiresAgreement() {
        View box = Util.findViewByClass(this, PrivacyConfirmBox.class);
        if (box == null) {
            return false;
        }

        return ((PrivacyConfirmBox)box).require(true);
    }

    private void loginByPhoneCode(String phone, String code) {
        try {
            JSONObject body = new JSONObject();
            body.put("phone", phone);
            body.put("code", code);
            String url = "https://" + identifier + ".authing.cn/api/v2/login/phone-code";
            getUserInfo(url, body);
        } catch (Exception e) {
            e.printStackTrace();
            fireCallback(null);
        }
    }

    private void loginByAccount(String account, String password) {
        try {
            password = Util.encryptPassword(password);
            JSONObject body = new JSONObject();
            body.put("account", account);
            body.put("password", password);
            String url = "https://" + identifier + ".authing.cn/api/v2/login/account";
            getUserInfo(url, body);
        } catch (Exception e) {
            e.printStackTrace();
            fireCallback(null);
        }
    }

    private void getUserInfo(String url, JSONObject body) {
        Guardian.post(url, body, (data)->{
            if (data == null) {
                fireCallback(null);
                return;
            }

            if (data.getCode() != 200) {
                handleError(data);
                fireCallback(null);
                return;
            }

            UserInfo userInfo;
            try {
                userInfo = UserInfo.createUserInfo(data.getData());
                fireCallback(userInfo);
            } catch (JSONException e) {
                e.printStackTrace();
                fireCallback(null);
            }
        });
    }

    private void handleError(Response response) {
        int code = response.getCode();
        if (code == Const.EC_INCORRECT_VERIFY_CODE) {
            Util.setErrorText(this, getContext().getString(R.string.authing_incorrect_verify_code));
        } else if (code == Const.EC_INCORRECT_CREDENTIAL) {
            Util.setErrorText(this, getContext().getString(R.string.authing_incorrect_credential));
        } else {
            Util.setErrorText(this, response.getMessage());
        }
    }

    private void fireCallback(UserInfo info) {
        stopLoadingVisualEffect();

        if (callback != null) {
            if (info == null) {
                post(()-> callback.call(false, null));
            } else {
                post(()-> callback.call(true, info));
            }
        }
    }
}

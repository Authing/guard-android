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

import cn.authing.guard.data.Config;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.internal.LoadingButton;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.Response;
import cn.authing.guard.util.Const;
import cn.authing.guard.util.Util;

public class RegisterButton extends LoadingButton {

    private String phoneNumber;
    private String phoneCode;
    private String identifier;
    private String email;
    protected AuthCallback<UserInfo> callback;

    public RegisterButton(@NonNull Context context) {
        this(context, null);
    }

    public RegisterButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public RegisterButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
            setText(R.string.authing_register);
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

        setOnClickListener((v -> register()));
    }

    public void setOnRegisterListener(AuthCallback<UserInfo> callback) {
        this.callback = callback;
    }

    // manually set phone number. in case of 2 step login
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void register() {
        if (showLoading) {
            return;
        }

        if (requiresAgreement()) {
            return;
        }

        Authing.getPublicConfig((this::_register));
    }

    public void _register(Config config) {
        if (config == null) {
            fireCallback("Public Config is null");
            return;
        }

        identifier = config.getIdentifier();

        View phoneNumberET = Util.findViewByClass(this, PhoneNumberEditText.class);
        View passwordET = Util.findViewByClass(this, PasswordEditText.class);
        View phoneCodeET = Util.findViewByClass(this, VerifyCodeEditText.class);
//        if (phoneNumberET != null && phoneNumberET.isShown()) {
//            PhoneNumberEditText phoneNumberEditText = (PhoneNumberEditText)phoneNumberET;
//            phoneNumber = phoneNumberEditText.getText().toString();
//        }
//        if (phoneCodeET != null && phoneCodeET.isShown()) {
//            VerifyCodeEditText verifyCodeEditText = (VerifyCodeEditText)phoneCodeET;
//            phoneCode = verifyCodeEditText.getText().toString();
//        }
//        if (!TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(phoneCode)) {
//            startLoadingVisualEffect();
//            registerByPhoneCode(phoneNumber, phoneCode);
//            return;
//        }

        if (phoneNumberET != null && phoneNumberET.isShown()
                && passwordET != null && passwordET.isShown()
                && phoneCodeET != null && phoneCodeET.isShown()) {
            PhoneNumberEditText phoneNumberEditText = (PhoneNumberEditText)phoneNumberET;
            if (!phoneNumberEditText.isContentValid()) {
                Util.setErrorText(this, getContext().getString(R.string.authing_invalid_phone_number));
                fireCallback(getContext().getString(R.string.authing_invalid_phone_number));
                return;
            }

            final String phone = phoneNumberEditText.getText().toString();
            final String code = ((VerifyCodeEditText) phoneCodeET).getText().toString();
            if (TextUtils.isEmpty(code)) {
                Util.setErrorText(this, getContext().getString(R.string.authing_incorrect_verify_code));
                fireCallback(getContext().getString(R.string.authing_incorrect_verify_code));
                return;
            }

            final String password = ((PasswordEditText) passwordET).getText().toString();
            if (TextUtils.isEmpty(password)) {
                fireCallback("Password is invalid");
                return;
            }

            startLoadingVisualEffect();
            registerByPhoneCode(phone, password, code);
        } else {
            View accountET = Util.findViewByClass(this, AccountEditText.class);
            if ((email != null || accountET != null && accountET.isShown())
                    && passwordET != null && passwordET.isShown()) {
                final String account = email != null ? email : ((AccountEditText) accountET).getText().toString();
                final String password = ((PasswordEditText) passwordET).getText().toString();
                if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
                    Util.setErrorText(this, "Account or password is invalid");
                    fireCallback("Account or password is invalid");
                    return;
                }

                View v = Util.findViewByClass(this, PasswordConfirmEditText.class);
                if (v != null) {
                    PasswordConfirmEditText passwordConfirmEditText = (PasswordConfirmEditText)v;
                    if (!password.equals(passwordConfirmEditText.getText().toString())) {
                        Util.setErrorText(this, getResources().getString(R.string.authing_password_not_match));
                        fireCallback(getResources().getString(R.string.authing_password_not_match));
                        return;
                    }
                }

                startLoadingVisualEffect();
                registerByEmail(account, password);
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

    private void registerByPhoneCode(String phone, String password, String phoneCode) {
        AuthClient.registerByPhoneCode(phone, password, phoneCode, (code, message, data)->{
            if (code == 200) {
                fireCallback(200, "", data);
            } else {
                Util.setErrorText(this, message);
                fireCallback(code, message, null);
            }
        });
    }

    private void registerByEmail(String email, String password) {
        AuthClient.registerByEmail(email, password, (code, message, data)->{
            if (code == 200) {
                fireCallback(200, "", data);
            } else {
                Util.setErrorText(this, message);
                fireCallback(code, message, null);
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

    private void fireCallback(String message) {
        fireCallback(500, message, null);
    }

    private void fireCallback(int code, String message, UserInfo info) {
        stopLoadingVisualEffect();

        if (callback != null) {
            post(()-> callback.call(code, message, info));
        }
    }
}

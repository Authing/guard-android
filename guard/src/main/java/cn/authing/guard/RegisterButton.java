package cn.authing.guard;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.data.Config;
import cn.authing.guard.data.ExtendedField;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.flow.FlowHelper;
import cn.authing.guard.internal.LoadingButton;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.util.Util;

public class RegisterButton extends LoadingButton {

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

        Analyzer.report("RegisterButton");

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

        View phoneCountryCodeET = Util.findViewByClass(this, CountryCodePicker.class);
        View phoneNumberET = Util.findViewByClass(this, PhoneNumberEditText.class);
        View passwordET = Util.findViewByClass(this, PasswordEditText.class);
        View phoneCodeET = Util.findViewByClass(this, VerifyCodeEditText.class);

        if (phoneCountryCodeET != null && phoneCountryCodeET.isShown()
                && phoneNumberET != null && phoneNumberET.isShown()
                && passwordET != null && passwordET.isShown()
                && phoneCodeET != null && phoneCodeET.isShown()) {
            CountryCodePicker countryCodePicker = (CountryCodePicker)phoneCountryCodeET;
            final String phoneCountryCode = countryCodePicker.getCountryCode();
            if (TextUtils.isEmpty(phoneCountryCode)) {
                Util.setErrorText(this, getContext().getString(R.string.authing_invalid_phone_country_code));
                fireCallback(getContext().getString(R.string.authing_invalid_phone_country_code));
                return;
            }

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
            registerByPhoneCode(phoneCountryCode, phone, code, password);
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

    private void registerByPhoneCode(String phoneCountryCode, String phone, String phoneCode, String password) {
        AuthClient.registerByPhoneCode(phoneCountryCode, phone, phoneCode, password, (code, message, data)->{
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

    private void fireCallback(String message) {
        fireCallback(500, message, null);
    }

    private void fireCallback(int code, String message, UserInfo userInfo) {
        stopLoadingVisualEffect();

        if (callback != null) {
            post(()-> callback.call(code, message, userInfo));
        } else if (code == 200) {
            Authing.getPublicConfig((config)-> {
                if (getContext() instanceof AuthActivity) {
                    AuthActivity activity = (AuthActivity) getContext();
                    AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
                    List<ExtendedField> missingFields = FlowHelper.missingFields(config, userInfo);
                    if (shouldCompleteAfterRegister(config) && missingFields.size() > 0) {
                        flow.getData().put(AuthFlow.KEY_USER_INFO, userInfo);
                        FlowHelper.handleUserInfoComplete(this, missingFields);
                    } else {
                        AuthFlow.Callback<UserInfo> cb = flow.getAuthCallback();
                        if (cb != null) {
                            cb.call(getContext(), code, message, userInfo);
                        }

                        Intent intent = new Intent();
                        intent.putExtra("user", userInfo);
                        activity.setResult(AuthActivity.OK, intent);
                        activity.finish();
                    }
                }
            });
        }
    }

    private boolean shouldCompleteAfterRegister(Config config) {
        List<String> complete = config.getCompleteFieldsPlace();
        return complete != null && complete.contains("register");
    }
}

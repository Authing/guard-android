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

import java.util.ArrayList;
import java.util.List;

import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.data.Config;
import cn.authing.guard.data.ExtendedField;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.flow.FlowHelper;
import cn.authing.guard.internal.LoadingButton;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.util.Const;
import cn.authing.guard.util.Util;

public class LoginButton extends LoadingButton {

    private String phoneNumber;
    private String phoneCode;
    protected AuthCallback<UserInfo> callback;

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

    public void setOnLoginListener(AuthCallback<UserInfo> callback) {
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
            fireCallback("Public Config is null");
            return;
        }

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
                fireCallback(getContext().getString(R.string.authing_invalid_phone_number));
                return;
            }

            final String phone = phoneNumberEditText.getText().toString();
            final String code = ((VerifyCodeEditText) phoneCodeET).getText().toString();
            if (TextUtils.isEmpty(code)) {
                fireCallback(getContext().getString(R.string.authing_incorrect_verify_code));
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
                    fireCallback("Account or password is invalid");
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

    private void loginByPhoneCode(String phone, String verifyCode) {
        AuthClient.loginByPhoneCode(phone, verifyCode, this::fireCallback);
    }

    private void loginByAccount(String account, String password) {
        AuthClient.loginByAccount(account, password, this::fireCallback);
    }

//    private void handleError(Response response) {
//        int code = response.getCode();
//        if (code == Const.EC_INCORRECT_VERIFY_CODE) {
//            Util.setErrorText(this, getContext().getString(R.string.authing_incorrect_verify_code));
//        } else if (code == Const.EC_INCORRECT_CREDENTIAL) {
//            Util.setErrorText(this, getContext().getString(R.string.authing_incorrect_credential));
//        } else {
//            Util.setErrorText(this, response.getMessage());
//        }
//    }

    private void fireCallback(String message) {
        fireCallback(500, message, null);
    }

    private void fireCallback(int code, String message, UserInfo userInfo) {
        stopLoadingVisualEffect();

        if (callback != null) {
            post(()-> callback.call(code, message, userInfo));
        } else if (code == 200) {
            Authing.getPublicConfig((config)->{
                if (getContext() instanceof AuthActivity) {
                    AuthActivity activity = (AuthActivity) getContext();
                    AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
                    List<ExtendedField> missingFields = FlowHelper.missingFields(config, userInfo);
                    if (shouldCompleteAfterLogin(config) && (missingFields != null && missingFields.size() > 0)) {
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
        } else if (code == Const.EC_MFA_REQUIRED) {
            if (getContext() instanceof AuthActivity) {
                AuthActivity activity = (AuthActivity) getContext();
                AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
                flow.getData().put(AuthFlow.KEY_USER_INFO, userInfo);
            }
            FlowHelper.handleMFA(this, userInfo.getMfaData());
        }  else if (code == Const.EC_FIRST_TIME_LOGIN) {
            FlowHelper.handleFirstTimeLogin(this, userInfo);
        } else {
            Util.setErrorText(this, message);
        }
    }

    private boolean shouldCompleteAfterLogin(Config config) {
        List<String> complete = config.getCompleteFieldsPlace();
        return complete != null && complete.contains("login");
    }
}

package cn.authing.guard;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONObject;

import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.internal.LoadingButton;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.util.GlobalCountDown;
import cn.authing.guard.util.ToastUtil;
import cn.authing.guard.util.Util;
import cn.authing.guard.util.Validator;

public class GetVerifyCodeButton extends LoadingButton {

    private String countDownTip;
    private String scene = "VERIFY_CODE";
    private final int verifyCodeType;
    private final int verifyAuthType;
    private String phoneNumber = "";
    private String email = "";
    private boolean autoRegister;
    private int textColor;
    private final int verifyEnableTextColor;

    private static final int VERIFY_CODE_NONE = 0;
    private static final int VERIFY_CODE_PHONE = 1;
    private static final int VERIFY_CODE_EMAIL = 2;

    private static final int VERIFY_AUTH_NONE = 0;
    private static final int VERIFY_AUTH_LOGIN = 1;
    private static final int VERIFY_AUTH_REGISTER = 2;
    private static final int VERIFY_AUTH_RESET_PASSWORD = 3;
    private static final int VERIFY_AUTH_MFA_VERIFY = 4;
    private static final int VERIFY_AUTH_MFA_BIND = 5;


    public GetVerifyCodeButton(@NonNull Context context) {
        this(context, null);
    }

    public GetVerifyCodeButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public GetVerifyCodeButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("GetVerifyCodeButton");

        loadingLocation = OVER; // over on top since this button is usually small

        textColor = context.getColor(R.color.authing_main);
        if (attrs != null && attrs.getAttributeValue(NS_ANDROID, "textColor") != null) {
            textColor = getCurrentTextColor();
        }

        setSingleLine();
        setEllipsize(TextUtils.TruncateAt.END);
        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
            String text = getContext().getString(R.string.authing_get_verify_code);
            setText(text);
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textSize") == null) {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "background") == null) {
            setBackgroundResource(R.drawable.authing_get_code_button_background_normal);
        }

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.GetVerifyCodeButton);
        verifyCodeType = array.getInt(R.styleable.GetVerifyCodeButton_verifyCodeType, 1);
        verifyAuthType = array.getInt(R.styleable.GetVerifyCodeButton_verifyAuthType, 0);
        countDownTip = array.getString(R.styleable.GetVerifyCodeButton_verifyCountDownTip);
        boolean verifyAutoGetCode = array.getBoolean(R.styleable.GetVerifyCodeButton_verifyAutoGetCode, false);
        verifyEnableTextColor = array.getColor(R.styleable.GetVerifyCodeButton_verifyEnableTextColor, getContext().getColor(R.color.authing_text_black));
        if (TextUtils.isEmpty(countDownTip)) {
            countDownTip = context.getString(R.string.authing_resend_after);
        }
        array.recycle();

        if (verifyAuthType == VERIFY_AUTH_RESET_PASSWORD){
            scene = "RESET_PASSWORD";
        } else if (verifyAuthType == VERIFY_AUTH_MFA_VERIFY || verifyAuthType == VERIFY_AUTH_MFA_BIND){
            scene = "MFA_VERIFY";
        }
        post(this::checkCountDown);

        Authing.getPublicConfig(config -> {
            if (config == null) {
                return;
            }
            if (config.isAutoRegisterThenLoginHintInfo()) {
                this.autoRegister = true;
            }
        });

        setOnClickListener((v -> getVerifyCode()));
        if (verifyAutoGetCode) {
            performClick();
        }
    }

    private void getVerifyCode() {
        if (verifyCodeType == VERIFY_CODE_NONE) {
            checkAccount();
        } else if (verifyCodeType == VERIFY_CODE_PHONE) {
            checkPhone();
        } else if (verifyCodeType == VERIFY_CODE_EMAIL) {
            checkEmail();
        }
    }

    private void checkAccount() {
        String account = "";
        View v = Util.findViewByClass(this, AccountEditText.class);
        if (v instanceof AccountEditText) {
            AccountEditText editText = (AccountEditText) v;
            account = editText.getText().toString();
        }
        if (Util.isNull(account)) {
            showAccountMessage(getContext().getString(R.string.authing_phone_or_email_empty));
        } else if (Validator.isValidPhoneNumber(account)) {
            phoneNumber = account;
            checkAccountByPhone();
        } else if (Validator.isValidEmail(account)) {
            email = account;
            checkAccountByEmail();
        } else {
            showAccountMessage(getContext().getString(R.string.authing_invalid_phone_or_email));
        }
    }

    private void checkPhone() {
        phoneNumber = Util.getPhoneNumber(this);
        if (Util.isNull(phoneNumber)) {
            showPhoneAccountMessage(getContext().getString(R.string.authing_phone_number_empty));
        } else if (Validator.isValidPhoneNumber(phoneNumber)) {
            checkAccountByPhone();
        } else {
            showPhoneAccountMessage(getContext().getString(R.string.authing_invalid_phone));
        }
    }

    private void checkAccountByPhone() {
        if (verifyAuthType == VERIFY_AUTH_NONE || verifyAuthType == VERIFY_AUTH_MFA_VERIFY || autoRegister) {
            getSMSCode(phoneNumber);
            return;
        }
        if (verifyAuthType == VERIFY_AUTH_MFA_BIND){
            mfaCheckByPhone(phoneNumber);
            return;
        }
        AuthClient.checkAccount("phone", phoneNumber, (AuthCallback<JSONObject>) (code, message, data) -> {
            showPhoneAccountMessage("");
            if (code == 200) {
                boolean hasAccount = data.has("result") && data.optBoolean("result");
                if ((verifyAuthType == VERIFY_AUTH_LOGIN || verifyAuthType == VERIFY_AUTH_RESET_PASSWORD) && !hasAccount) {
                    showPhoneAccountMessage(getContext().getString(R.string.authing_phone_account_not_found));
                    return;
                }
                if ((verifyAuthType == VERIFY_AUTH_REGISTER) && hasAccount) {
                    showPhoneAccountMessage(getContext().getString(R.string.authing_phone_account_found));
                    return;
                }
            }
            post(() -> getSMSCode(phoneNumber));
        });
    }

    private void mfaCheckByPhone(String phoneNumber){
        AuthClient.mfaCheck(phoneNumber, null, (code, message, ok) -> {
            if (code == 200) {
                if (ok) {
                    getSMSCode(phoneNumber);
                } else {
                    post(() -> ToastUtil.showCenter(getContext(), getContext().getString(R.string.authing_phone_number_already_bound), R.drawable.ic_authing_fail));
                }
            } else {
                post(() -> ToastUtil.showCenter(getContext(), message, R.drawable.ic_authing_fail));
            }
        });
    }

    private void checkEmail() {
        email = Util.getEmail(this);
        if (Util.isNull(email)) {
            showEmailAccountMessage(getContext().getString(R.string.authing_email_address_empty));
        } else if (Validator.isValidEmail(email)) {
            checkAccountByEmail();
        } else {
            showEmailAccountMessage(getContext().getString(R.string.authing_invalid_email));
        }
    }

    private void checkAccountByEmail() {
        if (verifyAuthType == VERIFY_AUTH_NONE || verifyAuthType == VERIFY_AUTH_MFA_VERIFY || autoRegister) {
            getEmailCode(email);
            return;
        }
        if (verifyAuthType == VERIFY_AUTH_MFA_BIND){
            mfaCheckByEmail(email);
            return;
        }
        AuthClient.checkAccount("email", email, (AuthCallback<JSONObject>) (code, message, data) -> {
            showEmailAccountMessage("");
            if (code == 200) {
                boolean hasAccount = data.has("result") && data.optBoolean("result");
                if ((verifyAuthType == VERIFY_AUTH_LOGIN || verifyAuthType == VERIFY_AUTH_RESET_PASSWORD) && !hasAccount) {
                    showEmailAccountMessage(getContext().getString(R.string.authing_email_account_not_found));
                    return;
                }
                if ((verifyAuthType == VERIFY_AUTH_REGISTER) && hasAccount) {
                    showEmailAccountMessage(getContext().getString(R.string.authing_email_account_found));
                    return;
                }
            }
            post(() -> getEmailCode(email));
        });
    }

    private void mfaCheckByEmail(String email){
        AuthClient.mfaCheck(null, email, (code, message, ok) -> {
            if (code == 200) {
                if (ok) {
                    getEmailCode(email);
                } else {
                    post(() -> ToastUtil.showCenter(getContext(), getContext().getString(R.string.authing_email_already_bound), R.drawable.ic_authing_fail));
                }
            } else {
                post(() -> ToastUtil.showCenter(getContext(), message, R.drawable.ic_authing_fail));
            }
        });
    }

    private void showPhoneAccountMessage(String message) {
        post(() -> {
            PhoneNumberEditText phoneNumberEditText = (PhoneNumberEditText) Util.findViewByClass(
                    GetVerifyCodeButton.this, PhoneNumberEditText.class);
            if (phoneNumberEditText != null && phoneNumberEditText.isErrorEnabled()) {
                phoneNumberEditText.showError(message);
                if (!"".equals(message)) {
                    phoneNumberEditText.showErrorBackGround();
                }
                return;
            }
            showAccountMessage(message);
        });
    }

    private void showEmailAccountMessage(String message) {
        post(() -> {
            EmailEditText emailEditText = (EmailEditText) Util.findViewByClass(
                    GetVerifyCodeButton.this, EmailEditText.class);
            if (emailEditText != null && emailEditText.isErrorEnabled()) {
                emailEditText.showError(message);
                if (!"".equals(message)) {
                    emailEditText.showErrorBackGround();
                }
                return;
            }
            showAccountMessage(message);
        });
    }

    private void showAccountMessage(String message) {
        AccountEditText accountEditText = (AccountEditText) Util.findViewByClass(
                GetVerifyCodeButton.this, AccountEditText.class);
        if (accountEditText != null && accountEditText.isErrorEnabled()) {
            accountEditText.showError(message);
            if (!"".equals(message)) {
                accountEditText.showErrorBackGround();
            }
            return;
        }
        Util.setErrorText(GetVerifyCodeButton.this, message);
    }

    private void getSMSCode(String phoneNumber) {
        String phoneCountryCode = Util.getPhoneCountryCode(this);
        beforeSendSmsCode();
        AuthClient.sendSms(phoneCountryCode, phoneNumber, this::handleSMSResult);
    }

    public void beforeSendSmsCode() {
        startLoadingVisualEffect();
        Util.setErrorText(this, null);
        setText("");
    }

    public void handleSMSResult(int code, String message, Object ignore) {
        post(() -> {
            stopLoadingVisualEffect();
            if (code == 200) {
                // in stopLoadingVisualEffect it will setEnabled to true
                setEnabled(false);
                checkCountDown();
                View v = Util.findViewByClass(this, VerifyCodeEditText.class);
                if (v != null) {
                    v.requestFocus();
                }
            } else {
                setText(getContext().getString(R.string.authing_get_verify_code_resend));
                ToastUtil.showCenter(getContext(), getContext().getString(R.string.authing_get_verify_code_error));
            }
        });
    }

    private void checkCountDown() {
        if (verifyCodeType == VERIFY_CODE_PHONE) {
            String phone = Util.getPhoneNumber(this);
            String code = Util.getPhoneCountryCode(this);
            if (GlobalCountDown.isCountingDown(phone + code)) {
                countDown(phone + code);
            }
        } else if (verifyCodeType == VERIFY_CODE_EMAIL) {
            String email = Util.getEmail(this);
            if (GlobalCountDown.isCountingDown(email)) {
                countDown(email);
            }
        } else {
            if (GlobalCountDown.isCountingDown()) {
                countDown();
            }
        }
    }

    private void countDown(String account) {
        if (GlobalCountDown.isCountingDown(account)) {
            updateCountDown(account);
            postDelayed(() -> countDown(account), 1000);
        } else {
            setTextColor(textColor);
            setText(getContext().getString(R.string.authing_get_verify_code_resend));
            setEnabled(true);
        }
    }

    private void countDown() {
        if (GlobalCountDown.isCountingDown()) {
            updateCountDown();
            postDelayed(this::countDown, 1000);
        } else {
            setTextColor(textColor);
            setText(getContext().getString(R.string.authing_get_verify_code_resend));
            setEnabled(true);
        }
    }

    private void updateCountDown(String count) {
        setEnabled(false);
        setTextColor(verifyEnableTextColor);
        setText(String.format(countDownTip, GlobalCountDown.getCountDown(count)));
    }

    private void updateCountDown() {
        setEnabled(false);
        setTextColor(verifyEnableTextColor);
        setText(String.format(countDownTip, GlobalCountDown.getFirstCountDown()));
    }

    public void setCountDownTip(String format) {
        countDownTip = format;
    }


    private void getEmailCode(String email) {
        beforeSendEmailCode();
        AuthClient.sendEmail(email, scene, this::handleEmailResult);
    }

    public void beforeSendEmailCode() {
        startLoadingVisualEffect();
        Util.setErrorText(this, null);
        setText("");
    }

    public void handleEmailResult(int code, String message, Object ignore) {
        post(() -> {
            stopLoadingVisualEffect();
            if (code == 200) {
                // in stopLoadingVisualEffect it will setEnabled to true
                setEnabled(false);
                checkCountDown();
                View v = Util.findViewByClass(this, VerifyCodeEditText.class);
                if (v != null) {
                    v.requestFocus();
                }
            } else {
                setText(getContext().getString(R.string.authing_get_verify_code_resend));
                ToastUtil.showCenter(getContext(), getContext().getString(R.string.authing_get_email_code_failed));
            }
        });
    }

    public void setScene(String scene) {
        this.scene = scene;
    }
}

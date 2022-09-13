package cn.authing.guard;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.content.res.TypedArray;
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
import cn.authing.guard.util.Util;
import cn.authing.guard.util.Validator;

public class GetVerifyCodeButton extends LoadingButton {

    private String countDownTip;
    private String scene = "VERIFY_CODE";
    private final int verifyCodeType;
    private final int verifyAuthType;
    private String phoneNumber = "";
    private String email = "";

    private static final int VERIFY_CODE_NONE = 0;
    private static final int VERIFY_CODE_PHONE = 1;
    private static final int VERIFY_CODE_EMAIL = 2;


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

        countDownTip = context.getString(R.string.authing_resend_after);

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textColor") == null) {
            setTextColor(context.getColor(R.color.authing_main));
        }

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
        array.recycle();

        post(this::checkCountDown);

        setOnClickListener((v -> getVerifyCode()));
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
        if (v != null) {
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
        if (verifyAuthType == 0) {
            getSMSCode(phoneNumber);
            return;
        }
        AuthClient.checkAccount("phone", phoneNumber, (AuthCallback<JSONObject>) (code, message, data) -> {
            showPhoneAccountMessage("");
            if (code == 200) {
                boolean hasAccount = data.has("result") && data.optBoolean("result");
                if ((verifyAuthType == 1 || verifyAuthType == 3) && !hasAccount) {
                    showPhoneAccountMessage(getContext().getString(R.string.authing_phone_account_not_found));
                    return;
                }
                if ((verifyAuthType == 2) && hasAccount) {
                    showPhoneAccountMessage(getContext().getString(R.string.authing_phone_account_found));
                    return;
                }
            }
            getSMSCode(phoneNumber);
        });
    }

    private void checkEmail() {
        View v = Util.findViewByClass(this, EmailEditText.class);
        if (v != null) {
            EmailEditText editText = (EmailEditText) v;
            email = editText.getText().toString();
        }
        if (Util.isNull(email)) {
            showEmailAccountMessage(getContext().getString(R.string.authing_email_address_empty));
        } else if (Validator.isValidEmail(email)) {
            checkAccountByEmail();
        } else {
            showEmailAccountMessage(getContext().getString(R.string.authing_invalid_email));
        }
    }

    private void checkAccountByEmail() {
        if (verifyAuthType == 0) {
            getEmailCode(email);
            return;
        }
        AuthClient.checkAccount("email", email, (AuthCallback<JSONObject>) (code, message, data) -> {
            showEmailAccountMessage("");
            if (code == 200) {
                boolean hasAccount = data.has("result") && data.optBoolean("result");
                if ((verifyAuthType == 1 || verifyAuthType == 3) && !hasAccount) {
                    showEmailAccountMessage(getContext().getString(R.string.authing_email_account_not_found));
                    return;
                }
                if ((verifyAuthType == 2) && hasAccount) {
                    showEmailAccountMessage(getContext().getString(R.string.authing_email_account_found));
                    return;
                }
            }
            getEmailCode(email);
        });
    }

    private void showPhoneAccountMessage(String message) {
        post(() -> {
            PhoneNumberEditText phoneNumberEditText = (PhoneNumberEditText) Util.findViewByClass(
                    GetVerifyCodeButton.this, PhoneNumberEditText.class);
            if (phoneNumberEditText != null && phoneNumberEditText.isErrorEnabled()) {
                phoneNumberEditText.showError(message);
                if (!"".equals(message)){
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
                if (!"".equals(message)){
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
            if (!"".equals(message)){
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
                Util.setErrorText(this, message);
                setText(getContext().getString(R.string.authing_get_verify_code_resend));
            }
        });
    }

    private void checkCountDown(){
        if (verifyCodeType == VERIFY_CODE_PHONE) {
            String phone = Util.getPhoneNumber(this);
            String code = Util.getPhoneCountryCode(this);
            if (GlobalCountDown.isCountingDown(phone+code)){
                countDown(phone+code);
            }
        } else if (verifyCodeType == VERIFY_CODE_EMAIL) {
            View emailView = Util.findViewByClass(GetVerifyCodeButton.this, EmailEditText.class);
            if (emailView != null){
                String email = ((EmailEditText)emailView).getText().toString();
                if (GlobalCountDown.isCountingDown(email)){
                    countDown(email);
                }
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
            setTextColor(getContext().getColor(R.color.authing_main));
            setText(getContext().getString(R.string.authing_get_verify_code_resend));
            setEnabled(true);
        }
    }

    private void countDown() {
        if (GlobalCountDown.isCountingDown()) {
            updateCountDown();
            postDelayed(this::countDown, 1000);
        } else {
            setTextColor(getContext().getColor(R.color.authing_main));
            setText(getContext().getString(R.string.authing_get_verify_code_resend));
            setEnabled(true);
        }
    }

    private void updateCountDown(String count) {
        setEnabled(false);
        setTextColor(getContext().getColor(R.color.authing_text_black));
        setText(String.format(countDownTip, GlobalCountDown.getCountDown(count)));
    }

    private void updateCountDown() {
        setEnabled(false);
        setTextColor(getContext().getColor(R.color.authing_text_black));
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
                Util.setErrorText(this, message);
                setText(getContext().getString(R.string.authing_get_email_code_failed));
            }
        });
    }

    public void setScene(String scene) {
        this.scene = scene;
    }
}

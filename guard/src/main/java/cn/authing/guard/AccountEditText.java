package cn.authing.guard;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;

import java.util.List;

import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.data.Config;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.internal.EditTextLayout;
import cn.authing.guard.util.Util;
import cn.authing.guard.util.Validator;

public class AccountEditText extends EditTextLayout {

    protected final static String LOGIN_METHOD_PHONE = "phone-password";
    protected final static String LOGIN_METHOD_EMAIL = "email-password";
    protected final static String LOGIN_METHOD_UN = "username-password";

    protected final static int PHONE_VALIDATOR = 1;
    protected final static int EMAIL_VALIDATOR = 2;
    protected final static int EXTEND_FILED_VALIDATOR = 3;

    protected int validator;

    public AccountEditText(Context context) {
        this(context, null);
    }

    public AccountEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AccountEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        report();

        Authing.getPublicConfig((config -> {
            CharSequence s = getEditText().getHint();
            if (s == null) {
                getEditText().setHint(getHintByConfig(config, context));
            }

            setup(config);

            if (getContext() instanceof AuthActivity) {
                AuthActivity activity = (AuthActivity) getContext();
                AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
                if (flow != null && flow.isSyncData()) {
                    syncData();
                }
            } else {
                syncData();
            }
        }));
    }

    private String getHintByConfig(Config config, Context context) {
        StringBuilder s = new StringBuilder(context.getString(R.string.authing_account_edit_text_hint));
        String phone = context.getString(R.string.authing_phone);
        String email = context.getString(R.string.authing_email);
        String username = context.getString(R.string.authing_username);

        if (pageType == 1) {
            return s.append(email).toString();
        } else {
            String defaultHint = s + phone + " / " + email + " / " + username;
            if (config == null) {
                return defaultHint;
            }
            List<String> enabledLoginMethods = config.getEnabledLoginMethods();
            if (enabledLoginMethods == null || enabledLoginMethods.size() == 0) {
                return defaultHint;
            }
            boolean addPhone = false;
            if (enabledLoginMethods.contains(LOGIN_METHOD_PHONE)){
                s.append(phone);
                addPhone = true;
                if (enabledLoginMethods.size() == 1){
                    validator |= PHONE_VALIDATOR;
                }
            }
            boolean addEmail = false;
            if (enabledLoginMethods.contains(LOGIN_METHOD_EMAIL)){
                if (addPhone){
                    s.append(" / ");
                }
                s.append(email);
                addEmail = true;
                if (enabledLoginMethods.size() == 1){
                    validator |= EMAIL_VALIDATOR;
                }
            }
            if (enabledLoginMethods.contains(LOGIN_METHOD_UN)){
                if (addEmail){
                    s.append(" / ");
                }
                s.append(username);
            }
            return s.toString();
        }
    }

    private void setup(Config config) {
        // most likely account is in English
        editText.setImeOptions(EditorInfo.IME_FLAG_FORCE_ASCII);

        if (pageType == 1) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            validator = EMAIL_VALIDATOR;
        } else if (config != null) {
            List<String> enabledLoginMethods = config.getEnabledLoginMethods();
            if (enabledLoginMethods.size() == 1) {
                String opt = enabledLoginMethods.get(0);
                switch (opt) {
                    case LOGIN_METHOD_PHONE:
                        editText.setInputType(InputType.TYPE_CLASS_PHONE);
                        break;
                    case LOGIN_METHOD_EMAIL:
                        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                        break;
                    case LOGIN_METHOD_UN:
                        break;
                }
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        super.afterTextChanged(s);

        showError("");

        if (!errorEnabled || validator == 0 || TextUtils.isEmpty(s)) {
            return;
        }

        boolean valid = false;
        if ((validator & PHONE_VALIDATOR) != 0) {
            valid = Validator.isValidPhoneNumber(s.toString());
        }
        if ((validator & EMAIL_VALIDATOR) != 0) {
            valid |= Validator.isValidEmail(s.toString());
        }

        clearErrorText();
        if (!valid) {
            if (validator == PHONE_VALIDATOR) {
                showError(getResources().getString(R.string.authing_invalid_phone));
                showErrorBackGround();
            } else if (validator == EMAIL_VALIDATOR) {
                showError(getResources().getString(R.string.authing_invalid_email));
                showErrorBackGround();
            }
        }
    }

    protected void syncData() {
        String account = Util.getAccount(this);
        if (account != null) {
            getEditText().setText(account);
        }
    }

    protected void report() {
        Analyzer.report("AccountEditText");
    }
}

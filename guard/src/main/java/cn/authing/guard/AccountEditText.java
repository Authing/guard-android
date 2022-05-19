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

    protected final static String LOGIN_METHOD_UN = "username-password";
    protected final static String LOGIN_METHOD_EMAIL = "email-password";
    protected final static String LOGIN_METHOD_PHONE = "phone-password";

    protected final static int EMAIL_VALIDATOR = 1;
    protected final static int PHONE_VALIDATOR = 2;

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
        String username = context.getString(R.string.authing_username);
        String email = context.getString(R.string.authing_email);
        String phone = context.getString(R.string.authing_phone);

        if (pageType == 1) {
            return s.append(email).toString();
        } else {
            String defaultHint = s + username + " / " + email + " / " + phone;
            if (config == null) {
                return defaultHint;
            }
            List<String> enabledLoginMethods = config.getEnabledLoginMethods();
            if (enabledLoginMethods == null || enabledLoginMethods.size() == 0) {
                return defaultHint;
            }
            for (int i = 0, n = enabledLoginMethods.size(); i < n; ++i) {
                String opt = enabledLoginMethods.get(i);
                switch (opt) {
                    case LOGIN_METHOD_UN:
                        s.append(username);
                        break;
                    case LOGIN_METHOD_EMAIL:
                        s.append(email);
                        validator |= EMAIL_VALIDATOR;
                        break;
                    case LOGIN_METHOD_PHONE:
                        s.append(phone);
                        validator |= PHONE_VALIDATOR;
                        break;
                }
                if (i < n - 1) {
                    s.append(" / ");
                }
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
                    case LOGIN_METHOD_UN:
                        break;
                    case LOGIN_METHOD_EMAIL:
                        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                        break;
                    case LOGIN_METHOD_PHONE:
                        editText.setInputType(InputType.TYPE_CLASS_PHONE);
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
        if ((validator & EMAIL_VALIDATOR) != 0) {
            valid = Validator.isValidEmail(s.toString());
        }
        if ((validator & PHONE_VALIDATOR) != 0) {
            valid |= Validator.isValidPhoneNumber(s.toString());
        }

        if (!valid) {
            if (validator == EMAIL_VALIDATOR) {
                showError(getResources().getString(R.string.authing_invalid_email));
            } else if (validator == PHONE_VALIDATOR) {
                showError(getResources().getString(R.string.authing_invalid_phone));
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

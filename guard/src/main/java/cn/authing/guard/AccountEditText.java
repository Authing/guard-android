package cn.authing.guard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;

import java.util.List;

import cn.authing.guard.data.Config;
import cn.authing.guard.internal.EditTextLayout;

public class AccountEditText extends EditTextLayout {

    private final static String LOGIN_METHOD_UN = "username-password";
    private final static String LOGIN_METHOD_EMAIL = "email-password";
    private final static String LOGIN_METHOD_PHONE = "phone-password";

    public AccountEditText(Context context) {
        this(context, null);
    }

    public AccountEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AccountEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        CharSequence s = getEditText().getHint();
        if (s == null) {
            Authing.getPublicConfig((config -> getEditText().setHint(getHintByConfig(config, context))));
        }

        // most likely account is in English
        editText.setImeOptions(EditorInfo.IME_FLAG_FORCE_ASCII);
    }

    private String getHintByConfig(Config config, Context context) {
        StringBuilder s = new StringBuilder(context.getString(R.string.account_edit_text_hint));
        String username = context.getString(R.string.authing_username);
        String email = context.getString(R.string.authing_email);
        String phone = context.getString(R.string.authing_phone);
        String defaultHint = s + username + " / " + email + " / " + phone;

        if (config == null) {
            return defaultHint;
        }
        List<String> enabledLoginMethods = config.getEnabledLoginMethods();
        if (enabledLoginMethods == null || enabledLoginMethods.size() == 0) {
            return defaultHint;
        }
        for (int i = 0, n = enabledLoginMethods.size();i < n;++i) {
            String opt = enabledLoginMethods.get(i);
            switch (opt) {
                case LOGIN_METHOD_UN:
                    s.append(username);
                    break;
                case LOGIN_METHOD_EMAIL:
                    s.append(email);
                    break;
                case LOGIN_METHOD_PHONE:
                    s.append(phone);
                    break;
            }
            if (i < n - 1) {
                s.append(" / ");
            }
        }
        return s.toString();
    }
}

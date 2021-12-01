package cn.authing.guard;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.util.Util;
import cn.authing.guard.util.Validator;

public class EmailEditText extends AccountEditText implements TextWatcher {

    public EmailEditText(@NonNull Context context) {
        this(context, null);
    }

    public EmailEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmailEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        validator = EMAIL_VALIDATOR;

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "hint") == null) {
            getEditText().setHint(context.getString(R.string.authing_account_edit_text_hint) + context.getString(R.string.authing_email));
        }
    }

    @Override
    protected void syncData() {
        String account = Util.getAccount(this);
        if (account != null && Validator.isValidEmail(account)) {
            getEditText().setText(account);
        }
    }
}

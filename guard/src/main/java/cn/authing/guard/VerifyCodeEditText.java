package cn.authing.guard;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.internal.EditTextLayout;
import cn.authing.guard.util.Util;

public class VerifyCodeEditText extends EditTextLayout implements TextWatcher {

    private int maxLength = 6;

    public VerifyCodeEditText(@NonNull Context context) {
        this(context, null);
    }

    public VerifyCodeEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerifyCodeEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        editText.setHint(R.string.verify_code_edit_text_hint);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        if (Authing.getPublicConfig() != null) {
            maxLength = Authing.getPublicConfig().getVerifyCodeLength();
        }
        editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
        editText.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        super.onTextChanged(s, start, before, count);
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() == maxLength) {
            LoginButton button = (LoginButton)Util.findViewByClass(this, LoginButton.class);
            if (button != null) {
                button.login();
            }
        }
    }
}

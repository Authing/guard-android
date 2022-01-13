package cn.authing.guard;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.internal.BasePasswordEditText;
import cn.authing.guard.util.Util;

public class PasswordConfirmEditText extends BasePasswordEditText {
    public PasswordConfirmEditText(@NonNull Context context) {
        this(context, null);
    }

    public PasswordConfirmEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PasswordConfirmEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Analyzer.report("PasswordConfirmEditText");
    }

    protected int getDefaultHintResId() {
        return R.string.authing_password_confirm_edit_text_hint;
    }

    @Override
    public void afterTextChanged(Editable str) {
        super.afterTextChanged(str);

        if (!errorEnabled) {
            return;
        }

        if (TextUtils.isEmpty(errorText)) {
            View v = Util.findViewByClass(this, PasswordEditText.class);
            if (v != null) {
                PasswordEditText et = (PasswordEditText) v;
                Editable s = et.getText();
                Editable s1 = editText.getText();
                if (s != null && s1 != null && !s.toString().equals(s1.toString())) {
                    showError(getContext().getString(R.string.authing_password_not_match));
                }
            }
        }
    }
}

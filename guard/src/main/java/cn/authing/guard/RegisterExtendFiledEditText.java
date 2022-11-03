package cn.authing.guard;

import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.analyze.Analyzer;

public class RegisterExtendFiledEditText extends AccountEditText implements TextWatcher {

    public RegisterExtendFiledEditText(@NonNull Context context) {
        this(context, null);
    }

    public RegisterExtendFiledEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RegisterExtendFiledEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        validator = EXTEND_FILED_VALIDATOR;
    }

    public boolean isContentValid() {
        String text = getText().toString();
        return !TextUtils.isEmpty(text);
    }

    @Override
    protected void syncData() {
    }

    @Override
    protected void report() {
        Analyzer.report("ExtendFiledEditText");
    }
}

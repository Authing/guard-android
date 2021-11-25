package cn.authing.guard;

import android.content.Context;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

        CharSequence s = getEditText().getHint();
        if (s == null) {
            getEditText().setHint(context.getString(R.string.authing_account_edit_text_hint) + context.getString(R.string.authing_email));
        }
    }
}

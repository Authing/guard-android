package cn.authing.guard;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PhoneNumberEditText extends ClearableEditText {
    public PhoneNumberEditText(@NonNull Context context) {
        super(context);
        init(context);
    }

    public PhoneNumberEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PhoneNumberEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setInputType(InputType.TYPE_CLASS_PHONE);

        CharSequence s = getHint();
        if (s == null) {
            setHint(context.getString(R.string.account_edit_text_hint) + context.getString(R.string.authing_phone));
        }
    }
}

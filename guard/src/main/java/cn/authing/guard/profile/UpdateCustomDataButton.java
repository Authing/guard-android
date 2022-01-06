package cn.authing.guard.profile;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.R;
import cn.authing.guard.internal.PrimaryButton;

public class UpdateCustomDataButton extends PrimaryButton {
    public UpdateCustomDataButton(@NonNull Context context) {
        this(context, null);
    }

    public UpdateCustomDataButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public UpdateCustomDataButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setText(context.getString(R.string.authing_submit));
    }
}

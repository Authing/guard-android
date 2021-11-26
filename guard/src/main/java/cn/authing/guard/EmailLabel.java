package cn.authing.guard;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.internal.MaskLabel;

public class EmailLabel extends MaskLabel {
    public EmailLabel(Context context) {
        this(context, null);
    }

    public EmailLabel(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmailLabel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public EmailLabel(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        if (context instanceof AuthActivity) {
            AuthActivity activity = (AuthActivity) context;
            AuthFlow flow = activity.getFlow();
            String email = flow.getData().get(AuthFlow.KEY_MFA_EMAIL);
            if (!TextUtils.isEmpty(email)) {
                setTextWithMask(email);
            }
        }
    }
}

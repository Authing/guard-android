package cn.authing.guard;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.internal.MaskLabel;

public class PhoneLabel extends MaskLabel {
    public PhoneLabel(Context context) {
        this(context, null);
    }

    public PhoneLabel(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PhoneLabel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PhoneLabel(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        if (context instanceof AuthActivity) {
            AuthActivity activity = (AuthActivity) context;
            AuthFlow flow = activity.getFlow();
            String s = (String) flow.getData().get(AuthFlow.KEY_MFA_PHONE);
            if (!TextUtils.isEmpty(s)) {
                setTextWithMask(s);
            }
        }
    }
}

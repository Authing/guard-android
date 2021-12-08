package cn.authing.guard;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.internal.GoSomewhereButton;

public class GoLoginButton extends GoSomewhereButton {

    public GoLoginButton(@NonNull Context context) {
        this(context, null);
    }

    public GoLoginButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public GoLoginButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected String getDefaultText() {
        return getResources().getString(R.string.authing_go_login);
    }

    protected int getTargetLayoutId() {
        if (getContext() instanceof AuthActivity) {
            AuthActivity activity = (AuthActivity) getContext();
            AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
            return flow.getIndexLayoutId();
        }
        return super.getTargetLayoutId();
    }
}

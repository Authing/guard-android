package cn.authing.guard;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.internal.GoSomewhereButton;

public class GoForgotPasswordButton extends GoSomewhereButton {

    public GoForgotPasswordButton(@NonNull Context context) {
        super(context);
    }

    public GoForgotPasswordButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GoForgotPasswordButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Analyzer.report("GoForgotPasswordButton");
    }

    protected String getDefaultText() {
        return getResources().getString(R.string.authing_forgot_password);
    }

    protected int getTargetLayoutId() {
        if (getContext() instanceof AuthActivity) {
            AuthActivity activity = (AuthActivity) getContext();
            AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
            return flow.getForgotPasswordLayoutId();
        }
        return super.getTargetLayoutId();
    }
}


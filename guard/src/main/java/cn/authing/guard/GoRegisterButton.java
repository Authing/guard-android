package cn.authing.guard;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.internal.GoSomewhereButton;

public class GoRegisterButton extends GoSomewhereButton {

    public GoRegisterButton(@NonNull Context context) {
        super(context);
    }

    public GoRegisterButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GoRegisterButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected String getDefaultText() {
        return getResources().getString(R.string.authing_register_now);
    }

    protected int getTargetLayoutId() {
        if (getContext() instanceof AuthActivity) {
            AuthActivity activity = (AuthActivity) getContext();
            AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
            return flow.getRegisterLayoutId();
        }
        return super.getTargetLayoutId();
    }
}

package cn.authing.guard.mfa;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import cn.authing.guard.Authing;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.data.Config;
import cn.authing.guard.data.ExtendedField;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.flow.FlowHelper;
import cn.authing.guard.internal.LoadingButton;
import cn.authing.guard.util.Util;

public class MFABaseButton extends LoadingButton {


    public MFABaseButton(@NonNull Context context) {
        super(context);
    }

    public MFABaseButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MFABaseButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    protected void mfaOk(int code, String message, UserInfo userInfo) {
        Authing.getPublicConfig((config)->{
            if (getContext() instanceof AuthActivity) {
                AuthActivity activity = (AuthActivity) getContext();
                AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
                List<ExtendedField> missingFields = FlowHelper.missingFields(config, userInfo);
                if (shouldCompleteAfterLogin(config) && missingFields.size() > 0) {
                    flow.getData().put(AuthFlow.KEY_USER_INFO, userInfo);
                    FlowHelper.handleUserInfoComplete(this, missingFields);
                } else {
                    AuthFlow.Callback<UserInfo> cb = flow.getAuthCallback();
                    if (cb != null) {
                        cb.call(getContext(), code, message, userInfo);
                    }

                    Intent intent = new Intent();
                    intent.putExtra("user", userInfo);
                    activity.setResult(AuthActivity.OK, intent);
                    activity.finish();
                }
            }
        });
    }


    private boolean shouldCompleteAfterLogin(Config config) {
        List<String> complete = config.getCompleteFieldsPlace();
        return complete != null && complete.contains("login");
    }
}

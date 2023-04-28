package cn.authing.guard.mfa;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import cn.authing.guard.Authing;
import cn.authing.guard.R;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.data.Config;
import cn.authing.guard.data.ExtendedField;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.flow.FlowHelper;
import cn.authing.guard.internal.LoadingButton;
import cn.authing.guard.util.ToastUtil;
import cn.authing.guard.util.Util;

public class MFABaseButton extends LoadingButton {

    public static int MFA_TYPE_BIND = 0;
    public static int MFA_TYPE_VERIFY = 1;
    protected int currentMfaType;

    public MFABaseButton(@NonNull Context context) {
        super(context);
    }

    public MFABaseButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MFABaseButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MFABaseButton);
        currentMfaType = array.getInt(R.styleable.MFABaseButton_mfaType, 0);
        array.recycle();
    }

    protected void mfaVerifyOk(int code, String message, UserInfo userInfo) {
        Authing.getPublicConfig((config) -> {
            if (getContext() instanceof AuthActivity) {
                if (checkBiometricBind((AuthActivity) getContext())){
                    return;
                }
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
                    Util.quitActivity();
                }
            }
        });
    }

    protected boolean checkBiometricBind(AuthActivity activity){
        AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
        Object object = flow.getData().get(AuthFlow.KEY_BIOMETRIC_BIND);
        if (object == null){
            return false;
        }
        boolean isBiometricBind = (Boolean) object;
        if (isBiometricBind){
            activity.setResult(AuthActivity.BIOMETRIC_BIND_OK);
            activity.finish();
            return true;
        }
        return false;
    }

    protected void showToast(int textResId, int imageResId) {
        post(() -> ToastUtil.showCenter(getContext(), getContext().getString(textResId), imageResId));
    }


    private boolean shouldCompleteAfterLogin(Config config) {
        List<String> complete = (config != null ? config.getCompleteFieldsPlace() : null);
        return complete != null && complete.contains("login");
    }
}

package cn.authing.guard.social.bind;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.R;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.data.Config;
import cn.authing.guard.data.ExtendedField;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.flow.FlowHelper;
import cn.authing.guard.handler.bind.BindRequestManager;
import cn.authing.guard.handler.bind.IBindRequestCallBack;
import cn.authing.guard.internal.PrimaryButton;
import cn.authing.guard.util.Const;
import cn.authing.guard.util.ToastUtil;

public class SocialBindButton extends PrimaryButton implements IBindRequestCallBack {

    protected AuthCallback<UserInfo> callback;
    private BindRequestManager mBindRequestManager;

    public SocialBindButton(@NonNull Context context) {
        this(context, null);
    }

    public SocialBindButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public SocialBindButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
            setText(R.string.authing_bind);
        }

        setOnClickListener((v -> bind()));
    }

    public void setOnLoginListener(AuthCallback<UserInfo> callback) {
        this.callback = callback;
    }


    private BindRequestManager getLoginRequestManager() {
        if (null == mBindRequestManager) {
            mBindRequestManager = new BindRequestManager(this, this);
        }
        return mBindRequestManager;
    }

    public void bind() {
        if (showLoading) {
            return;
        }

        Authing.getPublicConfig((this::_bind));
    }

    public void _bind(Config config) {
        if (config == null) {
            fireCallback(Const.ERROR_CODE_10002, "Config not found", null);
            return;
        }
        getLoginRequestManager().requestBind();
    }

    @Override
    public void callback(int code, String message, UserInfo userInfo) {
        fireCallback(code, message, userInfo);
    }

    private void fireCallback(int code, String message, UserInfo userInfo) {
        stopLoadingVisualEffect();

        if (callback != null) {
            if (code != 200 && code != Const.EC_MFA_REQUIRED && code != Const.EC_FIRST_TIME_LOGIN) {
                post(() -> ToastUtil.showCenter(getContext(), message));
            }
            post(() -> callback.call(code, message, userInfo));
            return;
        }

        if (userInfo == null) {
            if (code == Const.EC_VERIFY_EMAIL) {
                if (getContext() instanceof AuthActivity) {
                    AuthActivity activity = (AuthActivity) getContext();
                    AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
                    Intent intent = new Intent(getContext(), AuthActivity.class);
                    intent.putExtra(AuthActivity.AUTH_FLOW, flow);
                    intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, flow.getVerifyEmailSendSuccessLayoutId());
                }
            } else if (code == Const.EC_ACCOUNT_LOCKED) {
                post(() -> ToastUtil.showCenterWarning(getContext(), getContext().getString(R.string.authing_account_locked)));
            } else if (code == Const.EC_NO_DEVICE_PERMISSION_DISABLED) {
                post(() -> ToastUtil.showCenterWarning(getContext(), getContext().getString(R.string.authing_device_deactivated)));
            } else if (code == Const.EC_NO_DEVICE_PERMISSION_SUSPENDED) {
                post(() -> ToastUtil.showCenterWarning(getContext(), message));
            } else if (code == Const.EC_CAPTCHA) {
                FlowHelper.handleCaptcha(this);
                if (!"请输入图形验证码".equals(message)){
                    post(() -> ToastUtil.showCenter(getContext(), message));
                }
            } else {
                post(() -> ToastUtil.showCenter(getContext(), message));
            }
            return;
        }

        if (code == 200) {
            Authing.getPublicConfig((config) -> {
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
        } else if (code == Const.EC_MFA_REQUIRED) {
            if (getContext() instanceof AuthActivity) {
                AuthActivity activity = (AuthActivity) getContext();
                AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
                flow.getData().put(AuthFlow.KEY_USER_INFO, userInfo);
            }
            FlowHelper.handleMFA(this, userInfo.getMfaData());
        } else if (code == Const.EC_FIRST_TIME_LOGIN) {
            FlowHelper.handleFirstTimeLogin(this, userInfo);
        } else {
            post(() -> ToastUtil.showCenter(getContext(), message));
        }
    }

    private boolean shouldCompleteAfterLogin(Config config) {
        List<String> complete = (config != null ? config.getCompleteFieldsPlace() : null);
        return complete != null && complete.contains("login");
    }
}

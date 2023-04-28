package cn.authing.guard.social.bind;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
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
import cn.authing.guard.data.SocialBindData;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.flow.FlowHelper;
import cn.authing.guard.internal.PrimaryButton;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.util.Const;
import cn.authing.guard.util.ToastUtil;

public class GoSocialCreateButton extends PrimaryButton {

    protected AuthCallback<UserInfo> callback;

    public GoSocialCreateButton(@NonNull Context context) {
        this(context, null);
    }

    public GoSocialCreateButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public GoSocialCreateButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
            setText(getResources().getString(R.string.authing_create_new_account));
        }
        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "background") == null) {
            setBackgroundResource(R.drawable.authing_button_background_gray);
        }
        PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(getResources().getColor(R.color.authing_main),
                PorterDuff.Mode.SRC_ATOP);
        loading.setColorFilter(porterDuffColorFilter);

        if (context instanceof AuthActivity) {
            AuthActivity activity = (AuthActivity) context;
            AuthFlow flow = activity.getFlow();
            if (flow != null && flow.getData() != null) {
                String code = (String) flow.getData().get(AuthFlow.KEY_SOCIAL_ACCOUNT_BIND_CODE);
                if (String.valueOf(Const.EC_SOCIAL_BIND_LOGIN).equals(code)) {
                    setVisibility(GONE);
                } else if (String.valueOf(Const.EC_SOCIAL_BIND_REGISTER).equals(code)) {
                    setVisibility(VISIBLE);
                }
            } else {
                return;
            }
            setOnClickListener((v) -> createWechatFederationAccount(flow));
        }
    }

    private void createWechatFederationAccount(AuthFlow flow) {
        UserInfo userInfo = (UserInfo) flow.getData().get(AuthFlow.KEY_USER_INFO);
        if (userInfo != null && userInfo.getSocialBindData() != null) {
            SocialBindData socialBindData = userInfo.getSocialBindData();
            startLoadingVisualEffect();
            AuthClient.bindWechatWithRegister(socialBindData.getKey(), (AuthCallback<UserInfo>) this::loginDone);
        }
    }

    private void loginDone(int code, String message, UserInfo userInfo) {
        stopLoadingVisualEffect();
        if (callback != null) {
            if (code != 200) {
                post(() -> ToastUtil.showCenter(getContext(), message));
            }
            post(() -> callback.call(code, message, userInfo));
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
        } else if (code == Const.EC_VERIFY_EMAIL) {
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
        } else {
            post(() -> ToastUtil.showCenter(getContext(), message));
        }
    }

    private boolean shouldCompleteAfterLogin(Config config) {
        List<String> complete = (config != null ? config.getCompleteFieldsPlace() : null);
        return complete != null && complete.contains("login");
    }

    public void setOnLoginListener(AuthCallback<UserInfo> callback) {
        this.callback = callback;
    }
}

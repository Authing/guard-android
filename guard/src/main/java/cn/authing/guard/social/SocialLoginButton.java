package cn.authing.guard.social;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.PrivacyConfirmBox;
import cn.authing.guard.R;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.data.Config;
import cn.authing.guard.data.ExtendedField;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.flow.FlowHelper;
import cn.authing.guard.util.Const;
import cn.authing.guard.util.Util;

public abstract class SocialLoginButton extends androidx.appcompat.widget.AppCompatImageButton {

    protected SocialAuthenticator authenticator;
    protected AuthCallback<UserInfo> callback;
    protected AnimatedVectorDrawable backgroundDrawable;
    protected String type;

    public SocialLoginButton(Context context) {
        this(context, null);
    }

    protected abstract SocialAuthenticator createAuthenticator();

    private void loginDone(int code, String message, UserInfo userInfo) {
        post(()->{
            backgroundDrawable.stop();
            setBackgroundResource(R.drawable.ic_authing_circle);
        });

        if (callback != null) {
            callback.call(code, message, userInfo);
        } else if (getContext() instanceof AuthActivity) {
            if (code == 200) {
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
            } else if (code == Const.EC_MFA_REQUIRED) {
                if (getContext() instanceof AuthActivity) {
                    AuthActivity activity = (AuthActivity) getContext();
                    AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
                    flow.getData().put(AuthFlow.KEY_USER_INFO, userInfo);
                }
                FlowHelper.handleMFA(this, userInfo.getMfaData());
            } else if (code == Const.EC_SOCIAL_BIND_LOGIN
                    || code == Const.EC_SOCIAL_BIND_REGISTER) {
                if (getContext() instanceof AuthActivity) {
                    AuthActivity activity = (AuthActivity) getContext();
                    AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
                    flow.getData().put(AuthFlow.KEY_USER_INFO, userInfo);
                }
                FlowHelper.handleSocialAccountBind((AuthActivity) getContext(), code);
            } else if (code == Const.EC_SOCIAL_BIND_SELECT) {
                if (getContext() instanceof AuthActivity) {
                    AuthActivity activity = (AuthActivity) getContext();
                    AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
                    flow.getData().put(AuthFlow.KEY_USER_INFO, userInfo);
                }
                FlowHelper.handleSocialAccountSelect((AuthActivity) getContext());
            } else {
                Util.setErrorText(this, message);
            }
        }
    }

    public SocialLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.imageButtonStyle);
    }

    public SocialLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "background") == null) {
            setBackgroundResource(R.drawable.ic_authing_circle);
        }
        backgroundDrawable = (AnimatedVectorDrawable)context.getDrawable(R.drawable.ic_authing_animated_loading_blue);
        setOnClickListener((v -> {
            if (requiresAgreement()) {
                return;
            }
            setBackground(backgroundDrawable);
            backgroundDrawable.start();
            if (authenticator == null){
                authenticator = createAuthenticator();
            }
            authenticator.login(context, this::loginDone);
        }));
    }

    private boolean requiresAgreement() {
        View box = Util.findViewByClass(this, PrivacyConfirmBox.class);
        if (box == null) {
            return false;
        }

        return ((PrivacyConfirmBox)box).require(true);
    }

    public void setOnLoginListener(AuthCallback<UserInfo> callback) {
        this.callback = callback;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (authenticator != null){
            authenticator.onDetachedFromWindow();
        }
    }

    private boolean shouldCompleteAfterLogin(Config config) {
        List<String> complete = (config != null ? config.getCompleteFieldsPlace() : null);
        return complete != null && complete.contains("login");
    }
}

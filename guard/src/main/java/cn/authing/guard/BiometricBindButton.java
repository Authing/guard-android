package cn.authing.guard;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.data.Config;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.dialog.PrivacyConfirmDialog;
import cn.authing.guard.feedback.GoFeedbackImage;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.flow.FlowHelper;
import cn.authing.guard.handler.login.ILoginRequestCallBack;
import cn.authing.guard.handler.login.LoginRequestManager;
import cn.authing.guard.util.Const;
import cn.authing.guard.util.ToastUtil;
import cn.authing.guard.util.Util;

public class BiometricBindButton extends LoginButton implements ILoginRequestCallBack {

    protected AuthCallback<UserInfo> callback;
    private LoginRequestManager mLoginRequestManager;
    private int loginFailCount;
    private boolean autoRegister;
    private boolean useDefaultText;

    public BiometricBindButton(@NonNull Context context) {
        this(context, null);
    }

    public BiometricBindButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public BiometricBindButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("LoginButton");

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
            setText(R.string.authing_bind);
            useDefaultText = true;
        }

        setOnClickListener((v -> login()));
        refreshFeedBackView(false);
    }

    public void setOnLoginListener(AuthCallback<UserInfo> callback) {
        this.callback = callback;
    }

    public void setAutoRegister(boolean autoRegister) {
        this.autoRegister = autoRegister;
        mLoginRequestManager = null;
    }

    private LoginRequestManager getLoginRequestManager() {
        if (null == mLoginRequestManager) {
            mLoginRequestManager = new LoginRequestManager(this, this, autoRegister);
        }
        return mLoginRequestManager;
    }

    // manually set phone number. in case of 2 step login
    public void setPhoneNumber(String phoneNumber) {
        getLoginRequestManager().setPhoneNumber(phoneNumber);
    }

    public void login() {
        if (showLoading) {
            return;
        }

        if (requiresAgreement()) {
            return;
        }

        Authing.getPublicConfig((this::_login));
    }

    public void _login(Config config) {
        if (config == null) {
            fireCallback(Const.ERROR_CODE_10002, "Config not found", null);
            return;
        }
        getLoginRequestManager().requestLogin();
    }

    private boolean requiresAgreement() {
        View box = Util.findViewByClass(this, PrivacyConfirmBox.class);
        if (box == null) {
            return false;
        }

        return ((PrivacyConfirmBox) box).require(new PrivacyConfirmDialog.OnPrivacyListener() {

            @Override
            public void onShow() {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onAgree() {
                performClick();
            }
        });
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
            }
            if (code == Const.EC_ACCOUNT_LOCKED) {
                post(() -> ToastUtil.showCenterWarning(getContext(), getContext().getString(R.string.authing_account_locked)));
            } else {
                post(() -> ToastUtil.showCenter(getContext(), message));
            }
            refreshFeedBackView(true);
            return;
        }

        if (code == 200) {
            Authing.getPublicConfig((config) -> {
                if (getContext() instanceof AuthActivity) {
                    AuthActivity activity = (AuthActivity) getContext();
                    AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
                    AuthFlow.Callback<UserInfo> cb = flow.getAuthCallback();
                    if (cb != null) {
                        cb.call(getContext(), code, message, userInfo);
                    }
                    Util.biometricBind(activity);
                }
            });
        } else if (code == Const.EC_MFA_REQUIRED) {
            if (getContext() instanceof AuthActivity) {
                AuthActivity activity = (AuthActivity) getContext();
                AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
                flow.getData().put(AuthFlow.KEY_USER_INFO, userInfo);
                flow.getData().put(AuthFlow.KEY_BIOMETRIC_BIND, true);
            }
            FlowHelper.handleMFA(this, userInfo.getMfaData());
        } else if (code == Const.EC_FIRST_TIME_LOGIN) {
            FlowHelper.handleFirstTimeLogin(this, userInfo);
        } else {
            post(() -> ToastUtil.showCenter(getContext(), message));
            refreshFeedBackView(true);
        }
    }

    private void refreshFeedBackView(boolean add) {
        post(() -> {
            GoFeedbackImage feedbackImage = (GoFeedbackImage) Util.findViewByClass(BiometricBindButton.this, GoFeedbackImage.class);
            if (feedbackImage != null) {
                if (add) {
                    loginFailCount++;
                }
                feedbackImage.setVisibility(loginFailCount >= 2 ? VISIBLE : GONE);
            }
        });
    }

}
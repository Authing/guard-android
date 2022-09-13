package cn.authing.guard.social;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.PrivacyConfirmBox;
import cn.authing.guard.R;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.dialog.PrivacyConfirmDialog;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.flow.FlowHelper;
import cn.authing.guard.util.Const;
import cn.authing.guard.util.ToastUtil;
import cn.authing.guard.util.Util;

public abstract class SocialLoginButton extends androidx.appcompat.widget.AppCompatImageButton {

    protected SocialAuthenticator authenticator;
    protected AuthCallback<UserInfo> callback;
    protected AnimatedVectorDrawable backgroundDrawable;
    protected String type;
    public static final int AUTH_SUCCESS = 666;

    public SocialLoginButton(Context context) {
        this(context, null);
    }

    protected abstract SocialAuthenticator createAuthenticator();

    protected abstract int getImageRes();

    private void loginDone(int code, String message, UserInfo userInfo) {
        if (code == AUTH_SUCCESS){
            post(this::startLoading);
            return;
        }
        post(this::stopLoading);

        if (callback != null) {
            callback.call(code, message, userInfo);
        } else if (getContext() instanceof AuthActivity) {
            if (code == 200) {
                AuthActivity activity = (AuthActivity) getContext();
                Intent intent = new Intent();
                intent.putExtra("user", userInfo);
                activity.setResult(AuthActivity.OK, intent);
                activity.finish();
            } else if (code == Const.EC_MFA_REQUIRED) {
                if (getContext() instanceof AuthActivity) {
                    AuthActivity activity = (AuthActivity) getContext();
                    AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
                    flow.getData().put(AuthFlow.KEY_USER_INFO, userInfo);
                }
                FlowHelper.handleMFA(this, userInfo.getMfaData());
            } else {
                if (!TextUtils.isEmpty(message)
                        && getContext().getString(R.string.authing_cancelled_by_user).equals(message)){
                    post(() -> ToastUtil.showCenter(getContext(), message));
                }
            }
        }
    }

    public SocialLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.imageButtonStyle);
    }

    public SocialLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setScaleType(ScaleType.CENTER_INSIDE);
        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "background") == null) {
            setBackgroundResource(R.drawable.ic_authing_rectangle);
        }
        setImageResource(getImageRes());

        backgroundDrawable = (AnimatedVectorDrawable)context.getDrawable(R.drawable.ic_authing_animated_loading_blue);
        setOnClickListener((v -> {
            if (requiresAgreement()) {
                return;
            }

            if (authenticator == null){
                authenticator = createAuthenticator();
            }
            authenticator.login(context, this::loginDone);
        }));
    }

    private void startLoading(){
        setImageDrawable(backgroundDrawable);
        backgroundDrawable.start();
    }

    private void stopLoading(){
        backgroundDrawable.stop();
        setImageResource(getImageRes());
    }

    protected boolean requiresAgreement() {
        View box = Util.findViewByClass(this, PrivacyConfirmBox.class);
        if (box == null) {
            return false;
        }

        return ((PrivacyConfirmBox)box).require(new PrivacyConfirmDialog.OnPrivacyListener() {

            @Override
            public void onShow() {
                if (callback != null){
                    callback.call(1000, "", null);
                }
            }

            @Override
            public void onCancel() {
                if (callback != null){
                    callback.call(1001, "", null);
                }
            }

            @Override
            public void onAgree() {
                if (callback != null){
                    callback.call(1001, "", null);
                }
                performClick();
            }
        });
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

}
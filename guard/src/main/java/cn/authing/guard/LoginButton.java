package cn.authing.guard;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.data.Config;
import cn.authing.guard.data.ExtendedField;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.flow.FlowHelper;
import cn.authing.guard.handler.login.ILoginRequestCallBack;
import cn.authing.guard.handler.login.LoginRequestManager;
import cn.authing.guard.internal.PrimaryButton;
import cn.authing.guard.util.Const;
import cn.authing.guard.util.Util;

public class LoginButton extends PrimaryButton implements ILoginRequestCallBack {

    protected AuthCallback<UserInfo> callback;
    private LoginRequestManager mLoginRequestManager;
    private boolean autoRegister;

    public LoginButton(@NonNull Context context) {
        this(context, null);
    }

    public LoginButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public LoginButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("LoginButton");

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
            setText(R.string.authing_login);
        }

        Authing.getPublicConfig(config -> {
            if (config == null){
                return;
            }
            if (config.isAutoRegisterThenLoginHintInfo()){
                if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
                    setText(R.string.authing_login_register);
                }
                this.autoRegister = true;
            }
        });

        setOnClickListener((v -> login()));
    }

    public void setOnLoginListener(AuthCallback<UserInfo> callback) {
        this.callback = callback;
    }

    public void setAutoRegister(boolean autoRegister) {
        this.autoRegister = autoRegister;
    }

    private LoginRequestManager getLoginRequestManager(){
        if (null == mLoginRequestManager){
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

        return ((PrivacyConfirmBox)box).require(true);
    }

    @Override
    public void callback(int code, String message, UserInfo userInfo) {
        fireCallback(code, message, userInfo);
    }

    private void fireCallback(int code, String message, UserInfo userInfo) {
        stopLoadingVisualEffect();

        if (callback != null) {
            if (code != 200 && code != Const.EC_MFA_REQUIRED && code != Const.EC_FIRST_TIME_LOGIN) {
                Util.setErrorText(this, message);
            }
            post(()-> callback.call(code, message, userInfo));
            return;
        }

        if (userInfo == null){
            Util.setErrorText(this, message);
            return;
        }

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
        } else if (code == Const.EC_FIRST_TIME_LOGIN) {
            FlowHelper.handleFirstTimeLogin(this, userInfo);
        } else if (code == Const.EC_CAPTCHA) {
            FlowHelper.handleCaptcha(this);
        } else {
            Util.setErrorText(this, message);
        }
    }

    private boolean shouldCompleteAfterLogin(Config config) {
        List<String> complete = (config != null ? config.getCompleteFieldsPlace() : null);
        return complete != null && complete.contains("login");
    }
}

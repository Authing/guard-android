package cn.authing.guard;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.netease.nis.quicklogin.QuickLogin;

import java.util.List;

import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.data.Config;
import cn.authing.guard.data.ExtendedField;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.dialog.PrivacyConfirmDialog;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.flow.FlowHelper;
import cn.authing.guard.handler.register.IRegisterRequestCallBack;
import cn.authing.guard.handler.register.RegisterRequestManager;
import cn.authing.guard.internal.LoadingButton;
import cn.authing.guard.util.Const;
import cn.authing.guard.util.ToastUtil;
import cn.authing.guard.util.Util;

public class RegisterButton extends LoadingButton implements IRegisterRequestCallBack {

    protected AuthCallback<UserInfo> callback;
    private RegisterRequestManager mRegisterRequestManager;

    public RegisterButton(@NonNull Context context) {
        this(context, null);
    }

    public RegisterButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public RegisterButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("RegisterButton");

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
            setText(R.string.authing_register);
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textColor") == null) {
            setTextColor(0xffffffff);
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "background") == null) {
            setBackgroundResource(R.drawable.authing_button_background);
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textSize") == null) {
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        }

        PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(Color.WHITE,
                PorterDuff.Mode.SRC_ATOP);
        loading.setColorFilter(porterDuffColorFilter);

        setOnClickListener((v -> register()));
    }

    public void setOnRegisterListener(AuthCallback<UserInfo> callback) {
        this.callback = callback;
    }

    private RegisterRequestManager getRegisterRequestManager(){
        if (null == mRegisterRequestManager){
            mRegisterRequestManager = new RegisterRequestManager(this, this);
        }
        return mRegisterRequestManager;
    }

    public void setEmail(String email) {
        getRegisterRequestManager().setEmail(email);
    }

    public void register() {
        if (showLoading) {
            return;
        }

        if (requiresAgreement()) {
            return;
        }

        Authing.getPublicConfig((this::_register));
    }

    public void _register(Config config) {
        if (config == null) {
            fireCallback(Const.ERROR_CODE_10002, "Config not found", null);
            return;
        }

        getRegisterRequestManager().requestRegister();
    }

    private boolean requiresAgreement() {
        View box = Util.findViewByClass(this, PrivacyConfirmBox.class);
        if (box == null) {
            return false;
        }

        return ((PrivacyConfirmBox)box).require(new PrivacyConfirmDialog.OnPrivacyListener() {

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
            post(()-> callback.call(code, message, userInfo));
        }

        if (userInfo == null){
            post(() -> ToastUtil.showCenter(getContext(), message));
            return;
        }

        if (code == 200) {
            Authing.getPublicConfig((config)-> {
                if (getContext() instanceof AuthActivity) {
                    AuthActivity activity = (AuthActivity) getContext();
                    AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
                    List<ExtendedField> missingFields = FlowHelper.missingFields(config, userInfo);
                    if (shouldCompleteAfterRegister(config) && missingFields.size() > 0) {
                        flow.getData().put(AuthFlow.KEY_USER_INFO, userInfo);
                        FlowHelper.handleUserInfoComplete(this, missingFields);
                    } else {
                        AuthFlow.Callback<UserInfo> cb = flow.getAuthCallback();
                        if (cb != null) {
                            cb.call(getContext(), code, message, userInfo);
                        }

                        post(() -> {
                            Intent intent = new Intent();
                            intent.putExtra("user", userInfo);
                            activity.setResult(AuthActivity.OK, intent);
                            activity.finish();
                            QuickLogin.getInstance().quitActivity();
                        });
                    }
                }
            });
        } else {
            post(() -> ToastUtil.showCenter(getContext(), message));
        }
    }

    private boolean shouldCompleteAfterRegister(Config config) {
        List<String> complete = (config != null ? config.getCompleteFieldsPlace() : null);
        return complete != null && complete.contains("register");
    }
}

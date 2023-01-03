package cn.authing.guard.mfa;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.R;
import cn.authing.guard.VerifyCodeEditText;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.util.Util;

public class MFAOTPButton extends MFABaseButton implements AuthActivity.EventListener {

    public MFAOTPButton(@NonNull Context context) {
        this(context, null);
    }

    public MFAOTPButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public MFAOTPButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("MFAOTPButton");

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
            setText(getResources().getString(R.string.authing_bind));
        }

        loading.setTint(Color.WHITE);

        if (context instanceof AuthActivity) {
            //注册 输入完code之后自动进行绑定校验 事件
            AuthActivity activity = (AuthActivity) getContext();
            activity.subscribe(AuthActivity.EVENT_VERIFY_CODE_ENTERED, this);
            setOnClickListener(v -> doMFA());
        }
    }

    private void doMFA() {
        if (!(getContext() instanceof AuthActivity)) {
            return;
        }
        AuthActivity activity = (AuthActivity) getContext();
        View v = Util.findViewByClass(this, VerifyCodeEditText.class);
        if (v instanceof VerifyCodeEditText) {
            VerifyCodeEditText editText = (VerifyCodeEditText) v;
            String verifyCode = editText.getText().toString();
            startLoadingVisualEffect();
            if (currentMfaType == MFA_TYPE_BIND) {
                AuthClient.mfaBindByOtp(verifyCode, (AuthCallback<UserInfo>) (code, message, data) -> activity.runOnUiThread(() -> mfaBindDone(code, message, data)));
            } else if (currentMfaType == MFA_TYPE_VERIFY) {
                AuthClient.mfaVerifyByOTP(verifyCode, (code, message, data) -> activity.runOnUiThread(() -> mfaVerifyDone(code, message, data)));
            }
        }
    }

    private void mfaBindDone(int code, String message, UserInfo userInfo) {
        stopLoadingVisualEffect();
        if (code == 200) {
            showToast(R.string.authing_otp_bind_success, R.drawable.ic_authing_success);
            next();
        } else {
            showToast(R.string.authing_otp_bind_failed, R.drawable.ic_authing_fail);
        }
    }

    protected void next() {
        if (getContext() instanceof AuthActivity){
            if (checkBiometricBind((AuthActivity)getContext())){
                return;
            }
            AuthActivity activity = (AuthActivity) getContext();
            AuthFlow flow = activity.getFlow();
            Intent intent = new Intent(getContext(), AuthActivity.class);
            intent.putExtra(AuthActivity.AUTH_FLOW, flow);
            intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, flow.getMfaOTPLayoutIds()[2]);
            intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            //activity.startActivityForResult(intent, AuthActivity.RC_LOGIN);
            activity.startActivity(intent);
            activity.finish();
        }
    }

    private void mfaVerifyDone(int code, String message, UserInfo userInfo) {
        stopLoadingVisualEffect();
        if (code == 200) {
            showToast(R.string.authing_verify_succeed, R.drawable.ic_authing_success);
            mfaVerifyOk(code, message, userInfo);
        } else {
            showToast(R.string.authing_otp_verify_failed, R.drawable.ic_authing_fail);
        }
    }

    @Override
    public void happened(String what) {
        doMFA();
    }
}

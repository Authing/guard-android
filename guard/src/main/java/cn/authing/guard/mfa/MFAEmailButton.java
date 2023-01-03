package cn.authing.guard.mfa;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.EmailEditText;
import cn.authing.guard.R;
import cn.authing.guard.VerifyCodeEditText;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.util.Util;

public class MFAEmailButton extends MFABaseButton implements AuthActivity.EventListener {

    public MFAEmailButton(@NonNull Context context) {
        this(context, null);
    }

    public MFAEmailButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public MFAEmailButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("MFAEmailButton");

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
            setText(getResources().getString(R.string.authing_bind));
        }

        loading.setTint(Color.WHITE);

        if (context instanceof AuthActivity) {
            setOnClickListener(this::click);
        }
    }

    private void click(View clickedView) {
        if (!(getContext() instanceof AuthActivity)) {
            return;
        }

        doMFA();
    }

    private void doMFA() {
        View v = Util.findViewByClass(this, VerifyCodeEditText.class);
        if (v != null) {
            VerifyCodeEditText editText = (VerifyCodeEditText)v;
            String verifyCode = editText.getText().toString();
            boolean inputEmpty = false;
            if (TextUtils.isEmpty(verifyCode)){
                Util.setErrorText(v, getContext().getString(R.string.authing_verify_code_empty));
                inputEmpty = true;
            }
            startLoadingVisualEffect();
            AuthActivity activity = (AuthActivity) getContext();
            AuthFlow flow = activity.getFlow();
            if (currentMfaType == MFA_TYPE_BIND){
                String email = Util.getEmail(this);
                if (TextUtils.isEmpty(email)){
                    View emailEditText = Util.findViewByClass(this, EmailEditText.class);
                    if (emailEditText instanceof EmailEditText){
                        ((EmailEditText) emailEditText).showError(getContext().getString(R.string.authing_email_address_empty));
                        inputEmpty = true;
                    }
                }
                if (inputEmpty){
                    return;
                }
                AuthClient.mfaVerifyByEmail(email, verifyCode, (code, message, data)-> activity.runOnUiThread(()-> mfaBindDone(code, message, data)));
            } else if (currentMfaType == MFA_TYPE_VERIFY){
                if (inputEmpty){
                    return;
                }
                String email = (String) flow.getData().get(AuthFlow.KEY_MFA_EMAIL);
                AuthClient.mfaVerifyByEmail(email, verifyCode, (code, message, data)-> activity.runOnUiThread(()-> mfaVerifyDone(code, message, data)));
            }
        }
    }

    private void mfaBindDone(int code, String message, UserInfo userInfo){
        stopLoadingVisualEffect();
        if (code == 200) {
            next();
        } else {
            showToast(R.string.authing_otp_bind_failed, R.drawable.ic_authing_fail);
        }
    }

    protected void next(){
        if (getContext() instanceof AuthActivity){
            if (checkBiometricBind((AuthActivity)getContext())){
                return;
            }
            AuthActivity activity = (AuthActivity)getContext();
            AuthFlow flow = activity.getFlow();
            Intent intent = new Intent(getContext(), AuthActivity.class);
            intent.putExtra(AuthActivity.AUTH_FLOW, flow);
            intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, flow.getMfaEmailLayoutIds()[2]);
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
            showToast(R.string.authing_code_verify_failed, R.drawable.ic_authing_fail);
        }
    }

    @Override
    public void happened(String what) {
        doMFA();
    }
}

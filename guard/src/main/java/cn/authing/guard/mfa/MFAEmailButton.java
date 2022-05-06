package cn.authing.guard.mfa;

import static cn.authing.guard.activity.AuthActivity.EVENT_VERIFY_CODE_ENTERED;
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
import cn.authing.guard.GetEmailCodeButton;
import cn.authing.guard.R;
import cn.authing.guard.VerifyCodeEditText;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.internal.LoadingButton;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.util.Util;

public class MFAEmailButton extends LoadingButton implements AuthActivity.EventListener {

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
            AuthActivity activity = (AuthActivity) getContext();
            AuthFlow flow = activity.getFlow();
            String email = (String) flow.getData().get(AuthFlow.KEY_MFA_EMAIL);
            if (!TextUtils.isEmpty(email)) {
                post(() -> {
                    beforeSendEmailCode();
                    AuthClient.sendMFAEmail(email, this::handleSMSResult);
                });
            }

            activity.subscribe(EVENT_VERIFY_CODE_ENTERED, this);
            post(this::initGetEmailCodeButton);
        }
    }

    private void beforeSendEmailCode(){
        GetEmailCodeButton getEmailCodeButton = getEmailCodeButton();
        if (getEmailCodeButton != null){
            getEmailCodeButton.beforeSendEmailCode();
        }
    }

    private void handleSMSResult(int code, String message, Object ignore){
        GetEmailCodeButton getEmailCodeButton = getEmailCodeButton();
        if (getEmailCodeButton != null){
            getEmailCodeButton.handleResult(code, message, ignore);
        }
    }

    private void initGetEmailCodeButton(){
        GetEmailCodeButton getEmailCodeButton = getEmailCodeButton();
        if (getEmailCodeButton != null){
            getEmailCodeButton.setScene("MFA_VERIFY");
        }
    }

    private GetEmailCodeButton getEmailCodeButton(){
        View v = Util.findViewByClass(this, GetEmailCodeButton.class);
        if (v != null) {
            return (GetEmailCodeButton)v;
        }
        return null;
    }

    private void click(View clickedView) {
        if (!(getContext() instanceof AuthActivity)) {
            return;
        }

        AuthActivity activity = (AuthActivity) getContext();
        AuthFlow flow = activity.getFlow();

        View v = Util.findViewByClass(this, VerifyCodeEditText.class);
        if (v != null) {
            doMFA(v);
        } else {
            v = Util.findViewByClass(this, EmailEditText.class);
            if (v != null) {
                EmailEditText editText = (EmailEditText) v;
                String email = editText.getText().toString();
                flow.getData().put(AuthFlow.KEY_MFA_EMAIL, email);
                startLoadingVisualEffect();
                AuthClient.mfaCheck(null, email, (code, message, ok) -> {
                    if (code == 200) {
                        if (ok) {
                            sendEmail(flow, email);
                        } else {
                            stopLoadingVisualEffect();
                            post(()-> editText.showError(activity.getString(R.string.authing_email_already_bound)));
                        }
                    } else {
                        stopLoadingVisualEffect();
                        Util.setErrorText(this, message);
                    }
                });
            }
        }
    }

    private void sendEmail(AuthFlow flow, String email) {
        AuthActivity activity = (AuthActivity) getContext();
        AuthClient.sendMFAEmail(email, (code, message, data)-> activity.runOnUiThread(()->{
            stopLoadingVisualEffect();
            next(flow);
        }));
    }

    private void next(AuthFlow flow) {
        AuthActivity activity = (AuthActivity) getContext();

        int step = flow.getMfaEmailCurrentStep();
        flow.setMfaEmailCurrentStep(step++);

        Intent intent = new Intent(getContext(), AuthActivity.class);
        intent.putExtra(AuthActivity.AUTH_FLOW, flow);
        int[] ids = flow.getMfaEmailLayoutIds();
        if (step < ids.length) {
            intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, ids[step]);
        } else {
            // fallback to our default
            intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, R.layout.authing_mfa_email_1);
        }
        activity.startActivityForResult(intent, AuthActivity.RC_LOGIN);
    }

    private void doMFA(View v) {
        AuthActivity activity = (AuthActivity) getContext();
        AuthFlow flow = activity.getFlow();
        String email = (String) flow.getData().get(AuthFlow.KEY_MFA_EMAIL);
        VerifyCodeEditText editText = (VerifyCodeEditText)v;
        String verifyCode = editText.getText().toString();
        startLoadingVisualEffect();
        AuthClient.mfaVerifyByEmail(email, verifyCode, (code, message, data)-> activity.runOnUiThread(()-> mfaDone(code, message, data)));
    }

    private void mfaDone(int code, String message, UserInfo userInfo) {
        stopLoadingVisualEffect();
        if (code == 200) {
            try {
                AuthActivity activity = (AuthActivity) getContext();
                Intent intent = new Intent();
                intent.putExtra("user", userInfo);
                activity.setResult(AuthActivity.OK, intent);
                activity.finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Util.setErrorText(this, message);
        }
    }

    @Override
    public void happened(String what) {
        View v = Util.findViewByClass(this, VerifyCodeEditText.class);
        doMFA(v);
    }
}

package cn.authing.guard;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONObject;

import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.activity.IndexAuthActivity;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.internal.LoadingButton;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.util.Util;

public class BindEmailButton extends LoadingButton {

    public BindEmailButton(@NonNull Context context) {
        this(context, null);
    }

    public BindEmailButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public BindEmailButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
            setText(getResources().getString(R.string.authing_bind));
        }

        loading.setTint(Color.WHITE);

        setOnClickListener(this::click);
    }

    private void click(View clickedView) {
        if (!(getContext() instanceof AuthActivity)) {
            return;
        }

        AuthActivity activity = (AuthActivity) getContext();
        AuthFlow flow = activity.getFlow();

        View v = Util.findViewByClass(this, VerifyCodeEditText.class);
        if (v != null) {
            String email = flow.getData().get(AuthFlow.KEY_BIND_EMAIL);
            VerifyCodeEditText editText = (VerifyCodeEditText)v;
            String verifyCode = editText.getText().toString();
            AuthClient.mfaVerifyByEmail(email, verifyCode, this::emailBound);
        } else {
            v = Util.findViewByClass(this, EmailEditText.class);
            if (v != null) {
                EmailEditText editText = (EmailEditText) v;
                String email = editText.getText().toString();
                flow.getData().put(AuthFlow.KEY_BIND_EMAIL, email);
                AuthClient.sendMFAEmail(email, (code, message, data)->{
                    activity.runOnUiThread(()->{
                        next(flow);
                    });
                });
            }
        }
    }

    private void next(AuthFlow flow) {
        AuthActivity activity = (AuthActivity) getContext();

        int step = flow.getMfaBindEmailCurrentStep();
        flow.setMfaBindEmailCurrentStep(step++);

        Intent intent = new Intent(getContext(), AuthActivity.class);
        intent.putExtra(AuthActivity.AUTH_FLOW, flow);
        int[] ids = flow.getMfaBindEmailLayoutIds();
        if (step < ids.length) {
            intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, ids[step]);
        } else {
            // fallback to our default
            intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, R.layout.activity_authing_mfa_bind_email_1);
        }
        activity.startActivity(intent);
    }

    private void emailBound(int code, String message, JSONObject data) {
        if (code == 200) {
            AuthActivity activity = (AuthActivity) getContext();
            AuthFlow flow = activity.getFlow();

            if (flow.getData().containsKey(AuthFlow.KEY_MFA_EMAIL)) {
                gotoEmailMFA(flow);
            } else {
                activity.finish();
            }
        } else {
            Util.setErrorText(this, message);
        }
    }

    private void gotoEmailMFA(AuthFlow flow) {
        AuthActivity activity = (AuthActivity) getContext();

        Intent intent = new Intent(getContext(), IndexAuthActivity.class);
        intent.putExtra(AuthActivity.AUTH_FLOW, flow);
        intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, flow.getIndexLayoutId());
        getContext().startActivity(intent);
        activity.finish();
    }
}

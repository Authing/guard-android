package cn.authing.guard.mfa;

import static cn.authing.guard.flow.AuthFlow.KEY_MFA_RECOVERY_CODE;
import static cn.authing.guard.flow.AuthFlow.KEY_USER_INFO;
import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import cn.authing.guard.R;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.internal.LoadingButton;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.util.Util;

public class MFARecoveryButton extends LoadingButton {

    private String recoveryCode;

    public MFARecoveryButton(@NonNull Context context) {
        this(context, null);
    }

    public MFARecoveryButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public MFARecoveryButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("MFARecoveryButton");

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
            setText(getResources().getString(android.R.string.ok));
        }

        loading.setTint(Color.WHITE);

        if (!(context instanceof AuthActivity)) {
            return;
        }

        setOnClickListener(this::click);
        AuthActivity activity = (AuthActivity) getContext();
        AuthFlow flow = activity.getFlow();
        recoveryCode = (String) flow.getData().get(KEY_MFA_RECOVERY_CODE);
        if (!Util.isNull(recoveryCode)) {
            post(()->{
                View v = Util.findViewByClass(this, RecoveryCodeEditText.class);
                if (v != null) {
                    RecoveryCodeEditText editText = (RecoveryCodeEditText) v;
                    editText.getEditText().setText(recoveryCode);
                }
            });
        }
    }

    private void click(View clickedView) {
        if (recoveryCode == null) {
            doMFA();
        } else {
            View v = Util.findViewByClass(this, CheckBox.class);
            if (v == null) {
                done();
            } else {
                CheckBox checkBox = (CheckBox) v;
                if (checkBox.isChecked()) {
                    done();
                }
            }
        }
    }

    private void doMFA() {
        if (!(getContext() instanceof AuthActivity)) {
            return;
        }
        AuthActivity activity = (AuthActivity) getContext();
        View v = Util.findViewByClass(this, RecoveryCodeEditText.class);
        if (v != null) {
            RecoveryCodeEditText editText = (RecoveryCodeEditText) v;
            String recoveryCode = editText.getText().toString();
            startLoadingVisualEffect();
            AuthClient.mfaVerifyByRecoveryCode(recoveryCode, (code, message, data) -> activity.runOnUiThread(() -> mfaDone(code, message, data)));
        }
    }

    private void mfaDone(int code, String message, JSONObject data) {
        stopLoadingVisualEffect();
        if (code == 200) {
            try {
                UserInfo userInfo = UserInfo.createUserInfo(data);
                AuthActivity activity = (AuthActivity) getContext();
                AuthFlow flow = activity.getFlow();
                flow.getData().put(KEY_MFA_RECOVERY_CODE, userInfo.getRecoveryCode());
                int step = flow.getMfaRecoveryCurrentStep();
                flow.setMfaRecoveryCurrentStep(step++);

                Intent intent = new Intent(getContext(), AuthActivity.class);
                intent.putExtra(AuthActivity.AUTH_FLOW, flow);
                int[] ids = flow.getMfaRecoveryLayoutIds();
                if (step < ids.length) {
                    intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, ids[step]);
                } else {
                    // fallback to our default
                    intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, R.layout.authing_mfa_otp_recovery_1);
                }
                activity.startActivityForResult(intent, AuthActivity.RC_LOGIN);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Util.setErrorText(this, message);
        }
    }

    private void done() {
        AuthActivity activity = (AuthActivity) getContext();
        AuthFlow flow = activity.getFlow();
        Intent intent = new Intent();
        intent.putExtra("user", (UserInfo) flow.getData().get(KEY_USER_INFO));
        activity.setResult(AuthActivity.OK, intent);
        activity.finish();
    }
}

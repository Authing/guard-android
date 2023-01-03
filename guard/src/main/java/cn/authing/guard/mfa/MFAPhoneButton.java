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

import cn.authing.guard.PhoneNumberEditText;
import cn.authing.guard.R;
import cn.authing.guard.VerifyCodeEditText;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.util.Util;

public class MFAPhoneButton extends MFABaseButton {

    public MFAPhoneButton(@NonNull Context context) {
        this(context, null);
    }

    public MFAPhoneButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public MFAPhoneButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("MFAPhoneButton");

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

        View v = Util.findViewByClass(this, VerifyCodeEditText.class);
        if (v instanceof VerifyCodeEditText) {
            VerifyCodeEditText editText = (VerifyCodeEditText) v;
            String verifyCode = editText.getText().toString().trim();
            boolean inputEmpty = false;
            if (TextUtils.isEmpty(verifyCode)) {
                Util.setErrorText(v, getContext().getString(R.string.authing_verify_code_empty));
                inputEmpty = true;
            }
            AuthActivity activity = (AuthActivity) getContext();
            AuthFlow flow = activity.getFlow();
            if (currentMfaType == MFA_TYPE_BIND) {
                String phone = Util.getPhoneNumber(this);
                if (TextUtils.isEmpty(phone)) {
                    View phoneNumberEditText = Util.findViewByClass(this, PhoneNumberEditText.class);
                    if (phoneNumberEditText instanceof PhoneNumberEditText) {
                        ((PhoneNumberEditText) phoneNumberEditText).showError(getContext().getString(R.string.authing_phone_number_empty));
                        inputEmpty = true;
                    }
                }
                if (inputEmpty) {
                    return;
                }
                startLoadingVisualEffect();
                String phoneCountryCode = Util.getPhoneCountryCode(this);
                AuthClient.mfaVerifyByPhone(phoneCountryCode, phone, verifyCode, (code, message, data) -> activity.runOnUiThread(() -> mfaBindDone(code, message, data)));
            } else if (currentMfaType == MFA_TYPE_VERIFY) {
                if (inputEmpty) {
                    return;
                }
                startLoadingVisualEffect();
                String phone = (String) flow.getData().get(AuthFlow.KEY_MFA_PHONE);
                String phoneCountryCode = (String) flow.getData().get(AuthFlow.KEY_MFA_PHONE_COUNTRY_CODE);
                AuthClient.mfaVerifyByPhone(phoneCountryCode, phone, verifyCode, (code, message, data) -> activity.runOnUiThread(() -> mfaVerifyDone(code, message, data)));
            }
        }
    }

    private void mfaBindDone(int code, String message, UserInfo userInfo) {
        stopLoadingVisualEffect();
        if (code == 200) {
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
            intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, flow.getMfaPhoneLayoutIds()[2]);
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
        } else if (code == 500 && message.startsWith("duplicate key value violates unique constraint")) {
            showToast(R.string.authing_phone_verify_failed, R.drawable.ic_authing_fail);
        } else {
            showToast(R.string.authing_code_verify_failed, R.drawable.ic_authing_fail);
        }
    }


}

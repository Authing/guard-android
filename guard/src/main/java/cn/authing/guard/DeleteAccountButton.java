package cn.authing.guard;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.igexin.sdk.PushManager;

import org.json.JSONObject;

import cn.authing.guard.activity.DeleteAccountActivity;
import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.internal.LoadingButton;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.util.Const;
import cn.authing.guard.util.ToastUtil;
import cn.authing.guard.util.Util;

public class DeleteAccountButton extends LoadingButton {

    public DeleteAccountButton(@NonNull Context context) {
        this(context, null);
    }

    public DeleteAccountButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public DeleteAccountButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("GetEmailCodeButton");

        loadingLocation = OVER;

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textColor") == null) {
            setTextColor(context.getColor(R.color.authing_main));
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
            String text = getContext().getString(R.string.authing_delete_account);
            setText(text);
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textSize") == null) {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "background") == null) {
            setBackgroundResource(R.drawable.authing_get_code_button_background_normal);
        }

        setOnClickListener((v -> checkAccount()));
    }

    private void checkAccount() {
        UserInfo userInfo = Authing.getCurrentUser();
        if (userInfo != null) {
            String phoneNumber = userInfo.getPhone_number();
            VerifyCodeEditText verifyCodeEditText = (VerifyCodeEditText) Util.findViewByClass(this, VerifyCodeEditText.class);
            if (!Util.isNull(phoneNumber) && verifyCodeEditText != null && verifyCodeEditText.isShown()) {
                final String verifyCode = verifyCodeEditText.getText().toString();
                if (TextUtils.isEmpty(verifyCode)) {
                    Util.setErrorText(this, getContext().getString(R.string.authing_verify_code_empty));
                    return;
                }
                startLoadingVisualEffect();
                AuthClient.loginByPhoneCode(phoneNumber, verifyCode, (AuthCallback<UserInfo>) (code, message, data) -> {
                    if (code == 200 || code == Const.EC_MFA_REQUIRED) {
                        deleteAccount();
                    } else {
                        stopLoadingVisualEffect();
                        Util.setErrorText(this, message);
                    }
                });
                return;
            }

            PasswordEditText passwordEditText = (PasswordEditText) Util.findViewByClass(this, PasswordEditText.class);
            if (passwordEditText != null && passwordEditText.isShown()) {
                final String inputPassword = passwordEditText.getText().toString();
                if (TextUtils.isEmpty(inputPassword)) {
                    Util.setErrorText(this, getContext().getString(R.string.authing_password_empty));
                    return;
                }
                startLoadingVisualEffect();
                AuthClient.checkPassword(inputPassword, (AuthCallback<JSONObject>) (code, message, data) -> {
                    if (code == 200) {
                        deleteAccount();
                    } else {
                        stopLoadingVisualEffect();
                        Util.setErrorText(this, message);
                    }
                });
                return;
            }
        }

        startLoadingVisualEffect();
        deleteAccount();
    }

    private void deleteAccount() {
        //解除推送绑定
        String cid = PushManager.getInstance().getClientid(getContext());
        if (!TextUtils.isEmpty(cid)){
            AuthClient.unBindPushCid(cid, new AuthCallback<JSONObject>() {
                @Override
                public void call(int code, String message, JSONObject data) {
                    delete();
                }
            });
        } else {
            delete();
        }
    }

    private void delete(){
        AuthClient.deleteAccount((code, message, data) -> {
            stopLoadingVisualEffect();
            if (code == 200) {
                ((Activity) getContext()).runOnUiThread(() -> {
                    AuthFlow.start((Activity) getContext());
                    ToastUtil.showTop(getContext(), getContext().getString(R.string.authing_delete_account_success));
                    if (getContext() instanceof DeleteAccountActivity){
                        ((DeleteAccountActivity)getContext()).setResult(DeleteAccountActivity.RC_DELETE);
                    }
                    ((Activity) getContext()).finish();
                });
            } else {
                ((Activity) getContext()).runOnUiThread(() -> ToastUtil.showCenter(getContext(), message));
            }
        });
    }

}

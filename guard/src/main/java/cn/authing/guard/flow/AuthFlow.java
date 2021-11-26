package cn.authing.guard.flow;

import static cn.authing.guard.activity.AuthActivity.RC_LOGIN;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import cn.authing.guard.R;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.activity.IndexAuthActivity;
import cn.authing.guard.data.UserInfo;

public class AuthFlow implements Serializable {

//    public static final String KEY_PHONE_NUMBER = "phoneNumber";
    public static final String KEY_ACCOUNT = "account";

    public static final String KEY_MFA_PHONE = "mfa_phone";
    public static final String KEY_MFA_EMAIL = "mfa_email";

    private Map<String, String> data = new HashMap<>();

    private int indexLayoutId;
    private int registerLayoutId;
    private int forgotPasswordLayoutId;
    private int resetPasswordByEmailLayoutId;
    private int resetPasswordByPhoneLayoutId;

    private int[] mfaPhoneLayoutIds;
    private int mfaPhoneCurrentStep;
    private int[] mfaEmailLayoutIds;
    private int mfaEmailCurrentStep; // index starting from 0

    public interface Callback<T> extends Serializable {
        void call(Context context, int code, String message, T userInfo);
    }
    private Callback<UserInfo> authCallback;

    public static AuthFlow start(Activity context) {
        return start(context, R.layout.activity_authing_login);
    }

    public static AuthFlow start(Activity activity, int layoutId) {
        final AuthFlow flow = new AuthFlow();
        flow.indexLayoutId = layoutId;

        new Thread() {
            public void run() {
                activity.runOnUiThread(()->{
                    Intent intent = new Intent(activity, IndexAuthActivity.class);
                    intent.putExtra(AuthActivity.AUTH_FLOW, flow);
                    activity.startActivityForResult(intent, RC_LOGIN);
                });
            }
        }.start();
        return flow;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public static void put(Context context, String key, String value) {
        if (context instanceof AuthActivity) {
            AuthActivity activity = (AuthActivity) context;
            AuthFlow flow = activity.getFlow();
            flow.data.put(key, value);
        }
    }

    public static String get(Context context, String key) {
        if (context instanceof AuthActivity) {
            AuthActivity activity = (AuthActivity) context;
            AuthFlow flow = activity.getFlow();
            if (flow != null) {
                return flow.data.get(key);
            }
        }
        return null;
    }

    public int getIndexLayoutId() {
        if (indexLayoutId == 0) {
            return R.layout.activity_authing_login;
        } else {
            return indexLayoutId;
        }
    }

    public int getRegisterLayoutId() {
        if (registerLayoutId == 0) {
            return R.layout.activity_authing_register;
        } else {
            return registerLayoutId;
        }
    }

    public AuthFlow setRegisterLayoutId(int registerLayoutId) {
        this.registerLayoutId = registerLayoutId;
        return this;
    }

    public int getForgotPasswordLayoutId() {
        if (forgotPasswordLayoutId == 0) {
            return R.layout.activity_authing_forgot_password;
        } else {
            return forgotPasswordLayoutId;
        }
    }

    public AuthFlow setForgotPasswordLayoutId(int forgotPasswordLayoutId) {
        this.forgotPasswordLayoutId = forgotPasswordLayoutId;
        return this;
    }

    public int getResetPasswordByEmailLayoutId() {
        if (resetPasswordByPhoneLayoutId == 0) {
            return R.layout.activity_authing_reset_password_by_email;
        } else {
            return resetPasswordByEmailLayoutId;
        }
    }

    public AuthFlow setResetPasswordByEmailLayoutId(int resetPasswordByEmailLayoutId) {
        this.resetPasswordByEmailLayoutId = resetPasswordByEmailLayoutId;
        return this;
    }

    public int getResetPasswordByPhoneLayoutId() {
        if (resetPasswordByPhoneLayoutId == 0) {
            return R.layout.activity_authing_reset_password_by_phone;
        } else {
            return resetPasswordByPhoneLayoutId;
        }
    }

    public AuthFlow setResetPasswordByPhoneLayoutId(int resetPasswordByPhoneLayoutId) {
        this.resetPasswordByPhoneLayoutId = resetPasswordByPhoneLayoutId;
        return this;
    }

    public int[] getMfaPhoneLayoutIds() {
        if (mfaPhoneLayoutIds == null) {
            return new int[]{R.layout.activity_authing_mfa_phone_0, R.layout.activity_authing_mfa_phone_1};
        }
        return mfaPhoneLayoutIds;
    }

    public void setMfaPhoneLayoutIds(int[] mfaPhoneLayoutIds) {
        this.mfaPhoneLayoutIds = mfaPhoneLayoutIds;
    }

    public void setMfaPhoneLayoutId(int mfaPhoneLayoutId) {
        this.mfaPhoneLayoutIds = new int[mfaPhoneLayoutId];
    }

    public int getMfaPhoneCurrentStep() {
        return mfaPhoneCurrentStep;
    }

    public void setMfaPhoneCurrentStep(int mfaPhoneCurrentStep) {
        this.mfaPhoneCurrentStep = mfaPhoneCurrentStep;
    }

    public int[] getMfaEmailLayoutIds() {
        if (mfaEmailLayoutIds == null) {
            return new int[]{R.layout.activity_authing_mfa_email_0, R.layout.activity_authing_mfa_email_1};
        }
        return mfaEmailLayoutIds;
    }

    public void setMfaEmailLayoutIds(int[] mfaEmailLayoutIds) {
        this.mfaEmailLayoutIds = mfaEmailLayoutIds;
    }

    public void setMfaBindEmailLayoutId(int mfaBindEmailLayoutId) {
        this.mfaEmailLayoutIds = new int[mfaBindEmailLayoutId];
    }

    public int getMfaEmailCurrentStep() {
        return mfaEmailCurrentStep;
    }

    public void setMfaEmailCurrentStep(int mfaEmailCurrentStep) {
        this.mfaEmailCurrentStep = mfaEmailCurrentStep;
    }

    public Callback<UserInfo> getAuthCallback() {
        return authCallback;
    }

    public AuthFlow setAuthCallback(Callback<UserInfo> authCallback) {
        this.authCallback = authCallback;
        return this;
    }
}

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

    private Map<String, String> data = new HashMap();

    private int indexLayoutId;
    private int registerLayoutId;
    private int forgotPasswordLayoutId;
    private int resetPasswordByEmailLayoutId;
    private int resetPasswordByPhoneLayoutId;

    public interface Callback<T> extends Serializable {
        void call(Context context, int code, String message, T userInfo);
    }
    private Callback<UserInfo> authCallback;

    public static AuthFlow start(Activity context) {
        return start(context, R.layout.activity_login_authing);
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

    public static void put(Context context, String key, String value) {
        if (context instanceof AuthActivity) {
            AuthActivity activity = (AuthActivity) context;
            AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
            flow.data.put(key, value);
        }
    }

    public static String get(Context context, String key) {
        if (context instanceof AuthActivity) {
            AuthActivity activity = (AuthActivity) context;
            AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
            if (flow != null) {
                return flow.data.get(key);
            }
        }
        return null;
    }

    public int getIndexLayoutId() {
        if (indexLayoutId == 0) {
            return R.layout.activity_login_authing;
        } else {
            return indexLayoutId;
        }
    }

    public int getRegisterLayoutId() {
        if (registerLayoutId == 0) {
            return R.layout.activity_register_authing;
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

    public Callback<UserInfo> getAuthCallback() {
        return authCallback;
    }

    public AuthFlow setAuthCallback(Callback<UserInfo> authCallback) {
        this.authCallback = authCallback;
        return this;
    }
}

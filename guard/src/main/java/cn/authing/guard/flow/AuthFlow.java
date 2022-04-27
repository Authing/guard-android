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
import cn.authing.guard.activity.UserProfileActivity;
import cn.authing.guard.container.AuthContainer;
import cn.authing.guard.data.UserInfo;

public class AuthFlow implements Serializable {

//    public static final String KEY_PHONE_NUMBER = "phoneNumber";
    public static final String KEY_ACCOUNT = "account";

    public static final String KEY_USER_INFO = "user_info";

    public static final String KEY_MFA_PHONE = "mfa_phone";
    public static final String KEY_MFA_PHONE_COUNTRY_CODE = "mfa_phoneCountryCode";
    public static final String KEY_MFA_EMAIL = "mfa_email";
    public static final String KEY_MFA_RECOVERY_CODE = "mfa_recovery_code";

    public static final String KEY_EXTENDED_FIELDS = "extended_fields";

    private Map<String, Object> data = new HashMap<>();

    private int indexLayoutId;
    private int registerLayoutId;
    private int forgotPasswordLayoutId;
    private int resetPasswordByEmailLayoutId;
    private int resetPasswordByPhoneLayoutId;
    private int authHelpLayoutId;

    // MFA
    private int[] mfaPhoneLayoutIds;
    private int mfaPhoneCurrentStep;
    private int[] mfaEmailLayoutIds;
    private int mfaEmailCurrentStep; // index starting from 0
    private int mfaOTPLayoutId;
    private int[] mfaRecoveryLayoutIds;
    private int mfaRecoveryCurrentStep;

    // user info complete
    private int[] userInfoCompleteLayoutIds;
    private int userInfoCompleteItemNormal;
    private int userInfoCompleteItemEmail;
    private int userInfoCompleteItemPhone;
    private int userInfoCompleteItemSelect;
    private int userInfoCompleteItemDatePicker;

    private int resetPasswordFirstLoginLayoutId;

    private AuthContainer.AuthProtocol authProtocol = AuthContainer.AuthProtocol.EInHouse;
    private String scope = "openid profile email phone username address offline_access role extended_fields";
    private boolean skipConsent;
    // save confirmed data across Guard. e.g. Phone Number, Email
    private boolean syncData = true;

    public interface Callback<T> extends Serializable {
        void call(Context context, int code, String message, T userInfo);
    }
    private Callback<UserInfo> authCallback;

    public static AuthFlow start(Activity context) {
        return start(context, R.layout.authing_login);
    }

    public static AuthFlow startWeb(Activity context) {
        return start(context, R.layout.authing_login_web);
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

    public static void showUserProfile(Activity activity) {
        Intent intent = new Intent(activity, UserProfileActivity.class);
        activity.startActivity(intent);
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public static void put(Context context, String key, String value) {
        if (context instanceof AuthActivity) {
            AuthActivity activity = (AuthActivity) context;
            AuthFlow flow = activity.getFlow();
            flow.data.put(key, value);
        }
    }

    public static Object get(Context context, String key) {
        if (context instanceof AuthActivity) {
            AuthActivity activity = (AuthActivity) context;
            AuthFlow flow = activity.getFlow();
            if (flow != null) {
                return flow.data.get(key);
            }
        }
        return null;
    }

    public static String getAccount(Context context) {
        return (String)get(context, KEY_ACCOUNT);
    }

    public int getIndexLayoutId() {
        if (indexLayoutId == 0) {
            return R.layout.authing_login;
        } else {
            return indexLayoutId;
        }
    }

    public int getRegisterLayoutId() {
        if (registerLayoutId == 0) {
            return R.layout.authing_register;
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
            return R.layout.authing_forgot_password;
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
            return R.layout.authing_reset_password_by_email;
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
            return R.layout.authing_reset_password_by_phone;
        } else {
            return resetPasswordByPhoneLayoutId;
        }
    }

    public AuthFlow setResetPasswordByPhoneLayoutId(int resetPasswordByPhoneLayoutId) {
        this.resetPasswordByPhoneLayoutId = resetPasswordByPhoneLayoutId;
        return this;
    }

    public int getAuthHelpLayoutId() {
        if (authHelpLayoutId == 0) {
            return R.layout.authing_feedback;
        } else {
            return authHelpLayoutId;
        }
    }

    public AuthFlow setAuthHelpLayoutId(int authHelpLayoutId) {
        this.authHelpLayoutId = authHelpLayoutId;
        return this;
    }

    public int[] getMfaPhoneLayoutIds() {
        if (mfaPhoneLayoutIds == null) {
            return new int[]{R.layout.authing_mfa_phone_0, R.layout.authing_mfa_phone_1};
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
            return new int[]{R.layout.authing_mfa_email_0, R.layout.authing_mfa_email_1};
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

    public int[] getUserInfoCompleteLayoutIds() {
        if (userInfoCompleteLayoutIds == null) {
            return new int[]{R.layout.authing_userinfo_complete};
        }
        return userInfoCompleteLayoutIds;
    }

    public int getMfaOTPLayoutId() {
        if (mfaOTPLayoutId == 0) {
            return R.layout.authing_mfa_otp;
        }
        return mfaOTPLayoutId;
    }

    public void setMfaOTPLayoutId(int mfaOTPLayoutId) {
        this.mfaOTPLayoutId = mfaOTPLayoutId;
    }

    public int[] getMfaRecoveryLayoutIds() {
        if (mfaRecoveryLayoutIds == null) {
            return new int[]{R.layout.authing_mfa_otp_recovery_0, R.layout.authing_mfa_otp_recovery_1};
        }
        return mfaRecoveryLayoutIds;
    }

    public void setMfaRecoveryLayoutIds(int[] mfaRecoveryLayoutIds) {
        this.mfaRecoveryLayoutIds = mfaRecoveryLayoutIds;
    }

    public int getMfaRecoveryCurrentStep() {
        return mfaRecoveryCurrentStep;
    }

    public void setMfaRecoveryCurrentStep(int mfaRecoveryCurrentStep) {
        this.mfaRecoveryCurrentStep = mfaRecoveryCurrentStep;
    }

    public void setUserInfoCompleteLayoutIds(int[] userInfoCompleteLayoutIds) {
        this.userInfoCompleteLayoutIds = userInfoCompleteLayoutIds;
    }

    public void setUserInfoCompleteLayoutId(int userInfoCompleteLayoutId) {
        this.userInfoCompleteLayoutIds = new int[userInfoCompleteLayoutId];
    }

    public int getUserInfoCompleteItemNormal() {
        if (userInfoCompleteItemNormal == 0) {
            return R.layout.authing_userinfo_complete_item_normal;
        }
        return userInfoCompleteItemNormal;
    }

    public void setUserInfoCompleteItemNormal(int userInfoCompleteItemNormal) {
        this.userInfoCompleteItemNormal = userInfoCompleteItemNormal;
    }

    public int getUserInfoCompleteItemEmail() {
        if (userInfoCompleteItemEmail == 0) {
            return R.layout.authing_userinfo_complete_item_email;
        }
        return userInfoCompleteItemEmail;
    }

    public void setUserInfoCompleteItemEmail(int userInfoCompleteItemEmail) {
        this.userInfoCompleteItemEmail = userInfoCompleteItemEmail;
    }

    public int getUserInfoCompleteItemPhone() {
        if (userInfoCompleteItemPhone == 0) {
            return R.layout.authing_userinfo_complete_item_phone;
        }
        return userInfoCompleteItemPhone;
    }

    public void setUserInfoCompleteItemPhone(int userInfoCompleteItemPhone) {
        this.userInfoCompleteItemPhone = userInfoCompleteItemPhone;
    }

    public int getUserInfoCompleteItemSelect() {
        if (userInfoCompleteItemSelect == 0) {
            return R.layout.authing_userinfo_complete_item_select;
        }
        return userInfoCompleteItemSelect;
    }

    public void setUserInfoCompleteItemSelect(int userInfoCompleteItemSelect) {
        this.userInfoCompleteItemSelect = userInfoCompleteItemSelect;
    }

    public int getUserInfoCompleteItemDatePicker() {
        if (userInfoCompleteItemDatePicker == 0) {
            return R.layout.authing_userinfo_complete_item_datepicker;
        }
        return userInfoCompleteItemDatePicker;
    }

    public void setUserInfoCompleteItemDatePicker(int userInfoCompleteItemDatePicker) {
        this.userInfoCompleteItemDatePicker = userInfoCompleteItemDatePicker;
    }

    public int getResetPasswordFirstLoginLayoutId() {
        if (resetPasswordFirstLoginLayoutId == 0) {
            resetPasswordFirstLoginLayoutId = R.layout.authing_reset_password_first_login;
        }
        return resetPasswordFirstLoginLayoutId;
    }

    public void setResetPasswordFirstLoginLayoutId(int resetPasswordFirstLoginLayoutId) {
        this.resetPasswordFirstLoginLayoutId = resetPasswordFirstLoginLayoutId;
    }

    public Callback<UserInfo> getAuthCallback() {
        return authCallback;
    }

    public AuthFlow setAuthCallback(Callback<UserInfo> authCallback) {
        this.authCallback = authCallback;
        return this;
    }

    public AuthContainer.AuthProtocol getAuthProtocol() {
        return authProtocol;
    }

    public void setAuthProtocol(AuthContainer.AuthProtocol authProtocol) {
        this.authProtocol = authProtocol;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean isSkipConsent() {
        return skipConsent;
    }

    public void setSkipConsent(boolean skipConsent) {
        this.skipConsent = skipConsent;
    }

    public boolean isSyncData() {
        return syncData;
    }

    public void setSyncData(boolean syncData) {
        this.syncData = syncData;
    }
}

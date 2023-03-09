package cn.authing.guard.flow;

import static cn.authing.guard.util.Const.MFA_POLICY_EMAIL;
import static cn.authing.guard.util.Const.MFA_POLICY_FACE;
import static cn.authing.guard.util.Const.MFA_POLICY_OTP;
import static cn.authing.guard.util.Const.MFA_POLICY_SMS;
import static cn.authing.guard.util.Util.isNull;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cn.authing.guard.CaptchaContainer;
import cn.authing.guard.CaptchaImageView;
import cn.authing.guard.R;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.data.Config;
import cn.authing.guard.data.ExtendedField;
import cn.authing.guard.data.MFAData;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.util.Util;

public class FlowHelper {
    public static void handleMFA(View currentView, MFAData data) {
        if (data == null) {
            Util.setErrorText(currentView, "MFA is null");
            return;
        }

        Context context = currentView.getContext();
        if (!(context instanceof AuthActivity)) {
            return;
        }

        List<String> options = data.getApplicationMfa();
        if (options == null || options.size() == 0) {
            return;
        }

        // try to find a more convenient way
        for (String option : options) {
            if (MFA_POLICY_SMS.equals(option) && !isNull(data.getPhone())) {
                handleSMSMFA((AuthActivity) context, currentView, data.getPhoneCountryCode(), data.getPhone(), false);
                return;
            } else if (MFA_POLICY_EMAIL.equals(option) && !isNull(data.getEmail())) {
                handleEmailMFA((AuthActivity) context, currentView, data.getEmail());
                return;
            } else if (MFA_POLICY_OTP.equals(option)) {
                handleOTPMFA((AuthActivity) context, data.isTotpMfaEnabled(), false);
                return;
            } else if (MFA_POLICY_FACE.equals(option)) {
                handleFaceMFA((AuthActivity) context, data.isFaceMfaEnabled(), false);
                return;
            }
        }

        // not found a more convenient way, go for first option
        String firstOption = options.get(0);
        if (MFA_POLICY_SMS.equals(firstOption)) {
            handleSMSMFA((AuthActivity) context, currentView, data.getPhoneCountryCode(), data.getPhone());
        } else if (MFA_POLICY_EMAIL.equals(firstOption)) {
            handleEmailMFA((AuthActivity) context, currentView, data.getEmail());
        } else if (MFA_POLICY_OTP.equals(firstOption)) {
            handleOTPMFA((AuthActivity) context, data.isTotpMfaEnabled(), false);
        } else if (MFA_POLICY_FACE.equals(firstOption)) {
            handleFaceMFA((AuthActivity) context, data.isFaceMfaEnabled(), false);
        }
    }

    public static void handleSMSMFA(AuthActivity activity, View currentView, String countryCode, String phone) {
        handleSMSMFA(activity, currentView, countryCode, phone, false);
    }

    public static void handleSMSMFA(AuthActivity activity, View currentView, String countryCode, String phone, boolean forwardResult) {
        AuthFlow flow = activity.getFlow();
        int[] ids = flow.getMfaPhoneLayoutIds();
        if (ids == null || ids.length == 0) {
            Util.setErrorText(currentView, "MFA by phone has no layout. please call AuthFlow.setMfaPhoneLayoutIds");
            return;
        }

        Intent intent = new Intent(activity, AuthActivity.class);
        if (isNull(phone)) {
            intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, flow.getMfaPhoneLayoutIds()[0]);
        } else {
            flow.getData().put(AuthFlow.KEY_MFA_PHONE, phone);
            flow.getData().put(AuthFlow.KEY_MFA_PHONE_COUNTRY_CODE, countryCode);
            int step = ids.length > 1 ? ids.length - 1 : 0;
            intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, ids[step]);
        }
        intent.putExtra(AuthActivity.AUTH_FLOW, flow);
        if (forwardResult) {
            intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            activity.startActivity(intent);
        } else {
            activity.startActivityForResult(intent, AuthActivity.RC_LOGIN);
        }
    }

    public static void handleEmailMFA(AuthActivity activity, View currentView, String email) {
        handleEmailMFA(activity, currentView, email, false);
    }

    public static void handleEmailMFA(AuthActivity activity, View currentView, String email, boolean forwardResult) {
        AuthFlow flow = activity.getFlow();
        int[] ids = flow.getMfaEmailLayoutIds();
        if (ids == null || ids.length == 0) {
            Util.setErrorText(currentView, "MFA by email has no layout. please call AuthFlow.setMfaEmailLayoutIds");
            return;
        }

        Intent intent = new Intent(activity, AuthActivity.class);
        if (isNull(email)) {
            intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, flow.getMfaEmailLayoutIds()[0]);
        } else {
            flow.getData().put(AuthFlow.KEY_MFA_EMAIL, email);
            int step = ids.length > 1 ? ids.length - 1 : 0;
            intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, ids[step]);
        }
        intent.putExtra(AuthActivity.AUTH_FLOW, flow);
        if (forwardResult) {
            intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            activity.startActivity(intent);
        } else {
            activity.startActivityForResult(intent, AuthActivity.RC_LOGIN);
        }
    }

    public static void handleOTPMFA(AuthActivity activity, boolean totpMfaEnabled, boolean forwardResult) {
        AuthFlow flow = activity.getFlow();
        int[] ids = flow.getMfaOTPLayoutIds();

        Intent intent = new Intent(activity, AuthActivity.class);
        intent.putExtra(AuthActivity.AUTH_FLOW, flow);
        if (!totpMfaEnabled) {
            intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, ids[0]);
        } else {
            int step = ids.length > 1 ? ids.length - 1 : 0;
            intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, ids[step]);
        }
        if (forwardResult) {
            intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            activity.startActivity(intent);
        } else {
            activity.startActivityForResult(intent, AuthActivity.RC_LOGIN);
        }
    }

    public static void handleFaceMFA(AuthActivity activity, boolean faceMfaEnabled, boolean forwardResult) {
        AuthFlow flow = activity.getFlow();
        int[] ids = flow.getMfaFaceLayoutIds();

        Intent intent = new Intent(activity, AuthActivity.class);
        intent.putExtra(AuthActivity.AUTH_FLOW, flow);
        if (!faceMfaEnabled) {
            intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, ids[0]);
        } else {
            intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, R.layout.authing_mfa_face_verify_before);
        }
        if (forwardResult) {
            intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            activity.startActivity(intent);
        } else {
            activity.startActivityForResult(intent, AuthActivity.RC_LOGIN);
        }
    }

    public static void handleSocialAccountBind(AuthActivity activity, int code) {
        AuthFlow flow = activity.getFlow();

        Intent intent = new Intent(activity, AuthActivity.class);
        intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, R.layout.authing_social_account_bind_before);
        flow.getData().put(AuthFlow.KEY_SOCIAL_ACCOUNT_BIND_CODE, String.valueOf(code));
        intent.putExtra(AuthActivity.AUTH_FLOW, flow);
        activity.startActivityForResult(intent, AuthActivity.RC_LOGIN);
    }

    public static void handleSocialAccountSelect(AuthActivity activity) {
        AuthFlow flow = activity.getFlow();

        Intent intent = new Intent(activity, AuthActivity.class);
        intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, R.layout.authing_social_account_select);
        intent.putExtra(AuthActivity.AUTH_FLOW, flow);
        activity.startActivityForResult(intent, AuthActivity.RC_LOGIN);
    }

    public static void handleBiometricAccountBind(AuthActivity activity) {
        AuthFlow flow = activity.getFlow();

        Intent intent = new Intent(activity, AuthActivity.class);
        intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, R.layout.authing_biometric_account_bind);
        intent.putExtra(AuthActivity.AUTH_FLOW, flow);
        activity.startActivityForResult(intent, AuthActivity.RC_LOGIN);
    }

    public static List<ExtendedField> missingFields(Config config, UserInfo userInfo) {
        List<ExtendedField> missingFields = new ArrayList<>();
        if (config != null && userInfo != null) {
            List<ExtendedField> fields = config.getExtendedFields();
            for (ExtendedField field : fields) {
                String value = userInfo.getMappedData(field.getName());
                if (isNull(value)) {
                    missingFields.add(field);
                } else if ("gender".equals(field.getName()) && value.equals("U")) {
                    missingFields.add(field);
                }
            }
        }

        return missingFields;
    }

    public static void handleUserInfoComplete(View currentView, List<ExtendedField> extendedFields) {
        Context context = currentView.getContext();
        if (!(context instanceof AuthActivity)) {
            return;
        }

        AuthActivity activity = (AuthActivity) context;
        AuthFlow flow = activity.getFlow();
        int[] ids = flow.getUserInfoCompleteLayoutIds();
        if (ids == null || ids.length == 0) {
            Util.setErrorText(currentView, "UserInfoCompleteLayoutIds has no layout. please call AuthFlow.setUserInfoCompleteLayoutIds");
            return;
        }

        flow.getData().put(AuthFlow.KEY_EXTENDED_FIELDS, extendedFields);
        Intent intent = new Intent(activity, AuthActivity.class);
        intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, flow.getUserInfoCompleteLayoutIds()[0]);
        intent.putExtra(AuthActivity.AUTH_FLOW, flow);
        activity.startActivityForResult(intent, AuthActivity.RC_LOGIN);
    }

    public static void handleUserInfoComplete(Activity activity, List<ExtendedField> extendedFields) {
        AuthFlow flow = new AuthFlow();
        int[] ids = flow.getUserInfoCompleteLayoutIds();
        if (ids == null || ids.length == 0) {
            return;
        }

        flow.getData().put(AuthFlow.KEY_EXTENDED_FIELDS, extendedFields);
        Intent intent = new Intent(activity, AuthActivity.class);
        intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, flow.getUserInfoCompleteLayoutIds()[0]);
        intent.putExtra(AuthActivity.AUTH_FLOW, flow);
        activity.startActivityForResult(intent, AuthActivity.RC_LOGIN);
    }

    public static void handleFirstTimeLogin(View currentView, UserInfo userInfo) {
        if (userInfo == null || isNull(userInfo.getFirstTimeLoginToken())) {
            Util.setErrorText(currentView, "First time login data is null");
            return;
        }

        Context context = currentView.getContext();
        if (!(context instanceof AuthActivity)) {
            return;
        }

        AuthActivity activity = (AuthActivity) context;
        AuthFlow flow = activity.getFlow();
        flow.getData().put(AuthFlow.KEY_USER_INFO, userInfo);
        Intent intent = new Intent(activity, AuthActivity.class);
        intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, flow.getResetPasswordFirstLoginLayoutId());
        intent.putExtra(AuthActivity.AUTH_FLOW, flow);
        activity.startActivityForResult(intent, AuthActivity.RC_LOGIN);
    }

    public static void handleCaptcha(View currentView) {
        View v = Util.findViewByClass(currentView, CaptchaContainer.class);
        if (v != null) {
            currentView.post(() -> v.setVisibility(View.VISIBLE));
        }

        AuthClient.getCaptchaCode((code, message, data) -> {
            CaptchaImageView imageView = (CaptchaImageView) Util.findViewByClass(currentView, CaptchaImageView.class);
            if (v != null) {
                imageView.post(() -> imageView.setImageDrawable(data));
            }
        });
    }

}

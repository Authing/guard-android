package cn.authing.guard.flow;

import static cn.authing.guard.util.Const.MFA_POLICY_EMAIL;
import static cn.authing.guard.util.Const.MFA_POLICY_OTP;
import static cn.authing.guard.util.Const.MFA_POLICY_SMS;
import static cn.authing.guard.util.Util.isNull;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.CaptchaContainer;
import cn.authing.guard.PasswordEditText;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.data.Config;
import cn.authing.guard.data.ExtendedField;
import cn.authing.guard.data.MFAData;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Util;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

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
            }
        }

        // not found a more convenient way, go for first option
        String firstOption = options.get(0);
        if (MFA_POLICY_SMS.equals(firstOption)) {
            handleSMSMFA((AuthActivity) context, currentView, data.getPhoneCountryCode(), data.getPhone());
        } else if (MFA_POLICY_EMAIL.equals(firstOption)) {
            handleEmailMFA((AuthActivity) context, currentView, data.getEmail());
        } else if (MFA_POLICY_OTP.equals(firstOption)) {
            handleOTPMFA((AuthActivity) context);
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

    public static void handleOTPMFA(AuthActivity activity) {
        AuthFlow flow = activity.getFlow();
        int id = flow.getMfaOTPLayoutId();

        Intent intent = new Intent(activity, AuthActivity.class);
        intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, id);
        intent.putExtra(AuthActivity.AUTH_FLOW, flow);
        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        activity.startActivity(intent);
    }

    public static List<ExtendedField> missingFields(Config config, UserInfo userInfo) {
        List<ExtendedField> missingFields = new ArrayList<>();
        List<ExtendedField> fields = config.getExtendedFields();
        for (ExtendedField field : fields) {
            String value = userInfo.getMappedData(field.getName());
            if (isNull(value)) {
                missingFields.add(field);
            } else if ("gender".equals(field.getName()) && value.equals("U")) {
                missingFields.add(field);
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
        final View editText = Util.findViewByClass(currentView, PasswordEditText.class);
        if (editText != null) {
            currentView.post(()-> ((PasswordEditText)editText).setErrorEnabled(true));
        }

        View v = Util.findViewByClass(currentView, CaptchaContainer.class);
        if (v != null) {
            currentView.post(()-> v.setVisibility(View.VISIBLE));
        }

        getCaptcha((code, message, data) -> {

        });
    }

    private static void getCaptcha(@NotNull AuthCallback<Drawable> callback) {
        Authing.getPublicConfig(config -> {
            if (config == null) {
                callback.call(500, "config is null", null);
                return;
            }
            try {
                String url = Authing.getScheme() + "://" + Util.getHost(config) + "/api/v2/security/captcha?r=" + Util.randomString(10) + "&userpool_id=" + config.getUserPoolId();
                Request.Builder builder = new Request.Builder();
                builder.url(url);
                Request request = builder.build();
                OkHttpClient client = new OkHttpClient().newBuilder().build();
                Call call = client.newCall(request);
                okhttp3.Response response;
                response = call.execute();
                String s = new String(Objects.requireNonNull(response.body()).bytes(), StandardCharsets.UTF_8);
                if (response.code() == 200) {
                    callback.call(500, s,null);
                } else {
                    ALog.w("Guard", "getCaptcha failed. " + response.code() + " message:" + s);
                    callback.call(response.code(), s,null);
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.call(500, "Exception", null);
            }
        });
    }
}

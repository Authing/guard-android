package cn.authing.guard.flow;

import static cn.authing.guard.util.Const.MFA_POLICY_EMAIL;
import static cn.authing.guard.util.Const.MFA_POLICY_SMS;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import java.util.List;

import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.data.MFAData;
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
            if (MFA_POLICY_SMS.equals(option) && !isEmpty(data.getPhone())) {
                handleSMSMFA((AuthActivity) context, currentView, data.getPhone());
                return;
            } else if (MFA_POLICY_EMAIL.equals(option) && !isEmpty(data.getEmail())) {
                handleEmailMFA((AuthActivity) context, currentView, data.getEmail());
                return;
            }
        }

        // not found a more convenient way, go for first option
        String firstOption = options.get(0);
        if (MFA_POLICY_SMS.equals(firstOption)) {
            handleSMSMFA((AuthActivity) context, currentView, data.getPhone());
        } else if (MFA_POLICY_EMAIL.equals(firstOption)) {
            handleEmailMFA((AuthActivity) context, currentView, data.getEmail());
        }
    }

    public static void handleSMSMFA(AuthActivity activity, View currentView, String phone) {
        AuthFlow flow = activity.getFlow();
        int[] ids = flow.getMfaPhoneLayoutIds();
        if (ids == null || ids.length == 0) {
            Util.setErrorText(currentView, "MFA by phone has no layout. please call AuthFlow.setMfaPhoneLayoutIds");
            return;
        }

        Intent intent = new Intent(activity, AuthActivity.class);
        if (isEmpty(phone)) {
            intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, flow.getMfaPhoneLayoutIds()[0]);
        } else {
            flow.getData().put(AuthFlow.KEY_MFA_PHONE, phone);
            int step = ids.length > 1 ? ids.length - 1 : 0;
            intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, ids[step]);
        }
        intent.putExtra(AuthActivity.AUTH_FLOW, flow);
        activity.startActivityForResult(intent, AuthActivity.RC_LOGIN);
    }

    public static void handleEmailMFA(AuthActivity activity, View currentView, String email) {
        AuthFlow flow = activity.getFlow();
        int[] ids = flow.getMfaEmailLayoutIds();
        if (ids == null || ids.length == 0) {
            Util.setErrorText(currentView, "MFA by email has no layout. please call AuthFlow.setMfaEmailLayoutIds");
            return;
        }

        Intent intent = new Intent(activity, AuthActivity.class);
        if (isEmpty(email)) {
            intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, flow.getMfaEmailLayoutIds()[0]);
        } else {
            flow.getData().put(AuthFlow.KEY_MFA_EMAIL, email);
            int step = ids.length > 1 ? ids.length - 1 : 0;
            intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, ids[step]);
        }
        intent.putExtra(AuthActivity.AUTH_FLOW, flow);
        activity.startActivityForResult(intent, AuthActivity.RC_LOGIN);
    }

    private static boolean isEmpty(String s) {
        return TextUtils.isEmpty(s) || s.equals("null");
    }
}
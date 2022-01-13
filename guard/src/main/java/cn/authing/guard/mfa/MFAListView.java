package cn.authing.guard.mfa;

import static cn.authing.guard.util.Const.MFA_POLICY_EMAIL;
import static cn.authing.guard.util.Const.MFA_POLICY_OTP;
import static cn.authing.guard.util.Const.MFA_POLICY_SMS;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.List;

import cn.authing.guard.R;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.data.MFAData;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.flow.FlowHelper;
import cn.authing.guard.util.Util;

public class MFAListView extends LinearLayout implements View.OnClickListener {

    public MFAListView(Context context) {
        this(context, null);
    }

    public MFAListView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MFAListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MFAListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        Analyzer.report("MFAListView");

        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);

        if (!(context instanceof AuthActivity)) {
            return;
        }

        AuthActivity activity = (AuthActivity) context;
        AuthFlow flow = activity.getFlow();

        UserInfo data = (UserInfo) flow.getData().get(AuthFlow.KEY_USER_INFO);
        if (data != null) {
            post(()-> setup(context, data.getMfaData().getApplicationMfa()));
        }
    }

    private void setup(Context context, List<String> options) {
        for (String option : options) {
            TextView tv = new TextView(context, null, android.R.attr.buttonStyle);
            tv.setBackground(null);
            tv.setAllCaps(false);
            tv.setClickable(false);

            ImageView iv = new ImageView(context);
            int iconSize = (int) Util.dp2px(context, 24);
            LayoutParams ivlp = new LayoutParams(iconSize, iconSize);
            iv.setLayoutParams(ivlp);
            switch (option) {
                case MFA_POLICY_SMS:
                    if (Util.findViewByClass(this, MFAPhoneButton.class) != null) {
                        continue;
                    }
                    tv.setText(context.getString(R.string.authing_mfa_verify_phone));
                    iv.setImageResource(R.drawable.ic_authing_cellphone);
                    break;
                case MFA_POLICY_EMAIL:
                    if (Util.findViewByClass(this, MFAEmailButton.class) != null) {
                        continue;
                    }
                    tv.setText(context.getString(R.string.authing_mfa_verify_email));
                    iv.setImageResource(R.drawable.ic_authing_email);
                    break;
                case MFA_POLICY_OTP:
                    if (Util.findViewByClass(this, MFAOTPButton.class) != null) {
                        continue;
                    }
                    tv.setText(context.getString(R.string.authing_mfa_verify_otp));
                    iv.setImageResource(R.drawable.ic_authing_shield_check);
                    break;
                default:
                    continue;
            }

            LinearLayout button = new LinearLayout(context);
            button.setBackgroundColor(0xFFF5F5F5);
            button.setOrientation(HORIZONTAL);
            button.setGravity(Gravity.CENTER);
//            button.addView(iv);
            button.addView(tv);
            button.setTag(option);
            button.setOnClickListener(this);

            int m = (int) Util.dp2px(context, 8);
            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, m, 0, m);
            button.setLayoutParams(lp);
            addView(button);
        }
    }

    @Override
    public void onClick(View v) {
        if (!(getContext() instanceof AuthActivity)) {
            return;
        }

        AuthActivity activity = (AuthActivity) getContext();
        AuthFlow flow = activity.getFlow();
        UserInfo data = (UserInfo) flow.getData().get(AuthFlow.KEY_USER_INFO);
        if (data == null) {
            return;
        }

        MFAData mfaData = data.getMfaData();

        String option = (String) v.getTag();
        switch (option) {
            case MFA_POLICY_SMS:
                FlowHelper.handleSMSMFA(activity, this, mfaData.getPhone(), true);
                break;
            case MFA_POLICY_EMAIL:
                FlowHelper.handleEmailMFA(activity, this, mfaData.getEmail(), true);
                break;
            case MFA_POLICY_OTP:
                FlowHelper.handleOTPMFA(activity);
                break;
            default:
                break;
        }

        activity.finish();
    }
}

package cn.authing.guard.mfa;

import static cn.authing.guard.util.Const.MFA_POLICY_EMAIL;
import static cn.authing.guard.util.Const.MFA_POLICY_FACE;
import static cn.authing.guard.util.Const.MFA_POLICY_OTP;
import static cn.authing.guard.util.Const.MFA_POLICY_SMS;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
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

    private TextView textView;
    private OnMFAListItemClickListener mfaListItemClickListener;
    private String hideType;

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
        if (data != null && data.getMfaData() != null && data.getMfaData().getApplicationMfa() != null
                && !data.getMfaData().getApplicationMfa().isEmpty()) {
            addTitle(attrs);
            post(() -> setup(context, data.getMfaData().getApplicationMfa()));
        }
    }


    private void addTitle(AttributeSet attrs) {
        LinearLayout titleLayout = new LinearLayout(getContext());
        titleLayout.setOrientation(LinearLayout.HORIZONTAL);
        titleLayout.setGravity(Gravity.CENTER);

        ImageView leftView = new ImageView(getContext());
        leftView.setBackgroundResource(R.drawable.authing_social_line_left);
        titleLayout.addView(leftView);

        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.MFAListView);
        String text = array.getString(R.styleable.MFAListView_mfaListTitleText);
        float textSize = array.getDimension(R.styleable.MFAListView_mfaListTitleTextSize, Util.sp2px(getContext(), 12));
        int textColor = array.getColor(R.styleable.MFAListView_mfaListTitleTextColor, getContext().getColor(R.color.authing_text_gray));
        array.recycle();

        textView = new TextView(getContext());
        textView.setText(TextUtils.isEmpty(text) ? getContext().getString(R.string.authing_other_mfa) : text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        textView.setTextColor(textColor);
        textView.setPadding((int) Util.dp2px(getContext(), 8), 0, (int) Util.dp2px(getContext(), 8), 0);
        titleLayout.addView(textView);

        ImageView rightView = new ImageView(getContext());
        rightView.setBackgroundResource(R.drawable.authing_social_line_right);
        titleLayout.addView(rightView);

        addView(titleLayout);
    }

    public void setTitle(String title) {
        if (textView != null) {
            textView.setText(title);
        }
    }

    private void setup(Context context, List<String> options) {
        LinearLayout listView = new LinearLayout(getContext());
        LayoutParams listParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        listParams.topMargin = (int) Util.dp2px(getContext(), 12);
        listView.setLayoutParams(listParams);
        listView.setOrientation(LinearLayout.HORIZONTAL);
        listView.setGravity(Gravity.CENTER);

        int index = 0;
        for (int i = 0; i < options.size(); i++) {
            String option = options.get(i);
            if ((!TextUtils.isEmpty(hideType) && !TextUtils.isEmpty(option) && option.equals(hideType))){
                continue;
            }

            ImageView iv = new ImageView(context);
            int iconSize = (int) Util.dp2px(context, 48);
            LayoutParams ivlp = new LayoutParams(iconSize, iconSize);
            iv.setLayoutParams(ivlp);
            iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            iv.setBackgroundResource(R.drawable.authing_button_background_gray);

            TextView tv = new TextView(context);
            LayoutParams tvlp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            tvlp.topMargin = (int) Util.dp2px(getContext(), 4);
            tv.setLayoutParams(tvlp);
            tv.setBackground(null);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            tv.setTextColor(getContext().getColor(R.color.authing_text_black));
            tv.setAllCaps(false);
            tv.setClickable(false);

            switch (option) {
                case MFA_POLICY_SMS:
                    if (Util.findViewByClass(this, MFAPhoneButton.class) != null) {
                        continue;
                    }
                    tv.setText(context.getString(R.string.authing_mfa_verify_phone));
                    iv.setImageResource(R.drawable.ic_authing_mfa_phone);
                    break;
                case MFA_POLICY_EMAIL:
                    if (Util.findViewByClass(this, MFAEmailButton.class) != null ) {
                        continue;
                    }
                    tv.setText(context.getString(R.string.authing_mfa_verify_email));
                    iv.setImageResource(R.drawable.ic_authing_mfa_email);
                    break;
                case MFA_POLICY_OTP:
                    if (Util.findViewByClass(this, MFAOTPButton.class) != null) {
                        continue;
                    }
                    tv.setText(context.getString(R.string.authing_mfa_verify_otp));
                    iv.setImageResource(R.drawable.ic_authing_mfa_otp);
                    break;
                case MFA_POLICY_FACE:
                    if (Util.findViewByClass(this, MFAFaceView.class) != null) {
                        continue;
                    }
                    tv.setText(context.getString(R.string.authing_mfa_verify_face));
                    iv.setImageResource(R.drawable.ic_authing_mfa_face);
                    break;
                default:
                    continue;
            }

            LinearLayout itemView = new LinearLayout(context);
            itemView.setOrientation(VERTICAL);
            itemView.setGravity(Gravity.CENTER);
            itemView.setTag(option);
            itemView.setOnClickListener(this);
            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (index != 0) {
                lp.leftMargin = (int) Util.dp2px(context, 37);
            }
            itemView.setLayoutParams(lp);

            itemView.addView(iv);
            itemView.addView(tv);
            index++;

            listView.addView(itemView);
        }

        addView(listView);
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
                FlowHelper.handleSMSMFA(activity, this, mfaData.getPhoneCountryCode(), mfaData.getPhone(), true);
                break;
            case MFA_POLICY_EMAIL:
                FlowHelper.handleEmailMFA(activity, this, mfaData.getEmail(), true);
                break;
            case MFA_POLICY_OTP:
                FlowHelper.handleOTPMFA(activity, mfaData.isTotpMfaEnabled(), true);
                break;
            case MFA_POLICY_FACE:
                FlowHelper.handleFaceMFA(activity, mfaData.isFaceMfaEnabled(), true);
                break;
            default:
                break;
        }
        if (mfaListItemClickListener != null) {
            mfaListItemClickListener.onMFAItemClick(option);
        }

        activity.finish();
    }

    public String getHideType() {
        return hideType;
    }

    public void setHideType(String hideType) {
        this.hideType = hideType;
    }

    public void setMfaListItemClickListener(OnMFAListItemClickListener mfaListItemClickListener) {
        this.mfaListItemClickListener = mfaListItemClickListener;
    }

    public interface OnMFAListItemClickListener {
        void onMFAItemClick(String type);
    }

}

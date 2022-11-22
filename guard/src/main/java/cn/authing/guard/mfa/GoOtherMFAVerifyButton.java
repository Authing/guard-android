package cn.authing.guard.mfa;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.R;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.dialog.MFAListDialog;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.internal.GoSomewhereButton;
import cn.authing.guard.util.Const;

public class GoOtherMFAVerifyButton extends GoSomewhereButton {

    private MFAListDialog mfaListDialog;
    private String type = "";

    public GoOtherMFAVerifyButton(@NonNull Context context) {
        this(context, null);
    }

    public GoOtherMFAVerifyButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GoOtherMFAVerifyButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        setVisibility(GONE);
        if (!(context instanceof AuthActivity)) {
            return;
        }

        AuthActivity activity = (AuthActivity) context;
        AuthFlow flow = activity.getFlow();

        UserInfo data = (UserInfo) flow.getData().get(AuthFlow.KEY_USER_INFO);
        if (data == null || data.getMfaData() == null || data.getMfaData().getApplicationMfa() == null
                || data.getMfaData().getApplicationMfa().size() < 2) {
            return;
        }

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.GoOtherMFAVerifyButton);
        int currentMfaVerifyType = array.getInt(R.styleable.GoOtherMFAVerifyButton_currentMfaVerifyType, -1);
        array.recycle();
        if (currentMfaVerifyType == 0) {
            type = Const.MFA_POLICY_SMS;
        } else if (currentMfaVerifyType == 1) {
            type = Const.MFA_POLICY_EMAIL;
        } else if (currentMfaVerifyType == 2) {
            type = Const.MFA_POLICY_OTP;
        } else if (currentMfaVerifyType == 3) {
            type = Const.MFA_POLICY_FACE;
        }

        setVisibility(VISIBLE);
        setOnClickListener(v -> {
            if (mfaListDialog == null) {
                mfaListDialog = new MFAListDialog(activity, type);
            }
            mfaListDialog.show();
        });
    }

    @Override
    protected String getDefaultText() {
        return getContext().getString(R.string.authing_other_mfa);
    }
}

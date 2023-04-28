package cn.authing.guard.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.R;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.data.ExtendedField;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.flow.FlowHelper;
import cn.authing.guard.social.SocialLoginListView;
import cn.authing.guard.util.Const;
import cn.authing.guard.util.ToastUtil;
import cn.authing.guard.util.Util;

public class SocialLoginListDialog extends Dialog {

    private final Context mContext;

    public SocialLoginListDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    public SocialLoginListDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
    }

    public SocialLoginListDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);
        getWindow().setBackgroundDrawableResource(R.drawable.authing_social_more_dialog_background);
        getWindow().setGravity(Gravity.CENTER);
        Window window = getWindow();
        int padding = (int) Util.dp2px(getContext(), 24);
        window.getDecorView().setPadding(padding, padding, padding, padding);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        window.setWindowAnimations(R.style.BottomDialogAnimation);

        setContentView(R.layout.authing_social_more_dialog);
        LinearLayout contentView = findViewById(R.id.authing_social_more_layout);

        SocialLoginListView socialLoginListView = new SocialLoginListView(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        socialLoginListView.setLayoutParams(params);
        socialLoginListView.setShowSocialTitle(true);
        socialLoginListView.setShowSingleLine(false);
        socialLoginListView.initView();
        socialLoginListView.setOnLoginListener((AuthCallback<UserInfo>) (code, message, userInfo) -> {
            if (mContext instanceof AuthActivity) {
                if (code == 200) {
                    Authing.getPublicConfig((config) -> {
                        AuthActivity activity = (AuthActivity) mContext;
                        AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
                        List<ExtendedField> missingFields = FlowHelper.missingFields(config, userInfo);
                        if (Util.shouldCompleteAfterLogin(config) && missingFields.size() > 0) {
                            flow.getData().put(AuthFlow.KEY_USER_INFO, userInfo);
                            FlowHelper.handleUserInfoComplete(activity, missingFields);
                        } else {
                            AuthFlow.Callback<UserInfo> cb = flow.getAuthCallback();
                            if (cb != null) {
                                cb.call(getContext(), code, message, userInfo);
                            }

                            socialLoginListView.post(() -> {
                                Intent intent = new Intent();
                                intent.putExtra("user", userInfo);
                                activity.setResult(AuthActivity.OK, intent);
                                activity.finish();
                            });
                        }
                    });
                } else if (code == Const.EC_MFA_REQUIRED) {
                    if (getContext() instanceof AuthActivity) {
                        AuthActivity activity = (AuthActivity) mContext;
                        AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
                        flow.getData().put(AuthFlow.KEY_USER_INFO, userInfo);
                    }
                    FlowHelper.handleMFA(socialLoginListView, userInfo.getMfaData());
                } else if (code == Const.EC_VERIFY_EMAIL) {
                    if (getContext() instanceof AuthActivity) {
                        AuthActivity activity = (AuthActivity) getContext();
                        AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
                        Intent intent = new Intent(getContext(), AuthActivity.class);
                        intent.putExtra(AuthActivity.AUTH_FLOW, flow);
                        intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, flow.getVerifyEmailSendSuccessLayoutId());
                    }
                } else if (code == Const.EC_ACCOUNT_LOCKED) {
                    socialLoginListView.post(() -> ToastUtil.showCenterWarning(getContext(), getContext().getString(R.string.authing_account_locked)));
                } else if (code == Const.EC_NO_DEVICE_PERMISSION_DISABLED) {
                    socialLoginListView.post(() -> ToastUtil.showCenterWarning(getContext(), getContext().getString(R.string.authing_device_deactivated)));
                } else if (code == Const.EC_NO_DEVICE_PERMISSION_SUSPENDED) {
                    socialLoginListView.post(() -> ToastUtil.showCenterWarning(getContext(), message));
                } else if (code == Const.SOCIAL_DIALOG_DISMISS) {
                    dismiss();
                } else if (code == Const.SOCIAL_DIALOG_SHOW) {
                    show();
                    return;
                } else {
                    socialLoginListView.post(() -> ToastUtil.showCenter(getContext(), message));
                }
            }
            dismiss();
        });

        contentView.addView(socialLoginListView, 0);
        contentView.post(() -> {
            int measureHeight = socialLoginListView.getMeasuredHeight();
            if (measureHeight > Const.SOCIAL_DIALOG_MAX_HEIGHT){
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) socialLoginListView.getLayoutParams();
                layoutParams.height = Const.SOCIAL_DIALOG_MAX_HEIGHT;
                contentView.requestLayout();
            }
        });
    }

}

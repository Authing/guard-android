package cn.authing.guard.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.R;
import cn.authing.guard.activity.AuthActivity;
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
                    AuthActivity activity = (AuthActivity) mContext;
                    Intent intent = new Intent();
                    intent.putExtra("user", userInfo);
                    activity.setResult(AuthActivity.OK, intent);
                    activity.finish();
                } else if (code == Const.EC_MFA_REQUIRED) {
                    if (getContext() instanceof AuthActivity) {
                        AuthActivity activity = (AuthActivity) mContext;
                        AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
                        flow.getData().put(AuthFlow.KEY_USER_INFO, userInfo);
                    }
                    FlowHelper.handleMFA(socialLoginListView, userInfo.getMfaData());
                } else if (code == 1000) {
                    dismiss();
                } else if (code == 1001) {
                    show();
                    return;
                } else {
                    socialLoginListView.post(() -> ToastUtil.showCenter(getContext(), message));
                }
            }
            dismiss();
        });

        contentView.addView(socialLoginListView, 0);
    }

}

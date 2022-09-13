package cn.authing.guard.social;

import android.content.Context;
import android.util.AttributeSet;

import cn.authing.guard.R;
import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.dialog.SocialLoginListDialog;

public class MoreLoginButton extends SocialLoginButton {

    private SocialLoginListDialog socialLoginListDialog;

    public MoreLoginButton(Context context) {
        this(context, null);
    }

    public MoreLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MoreLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("MoreLoginButton");

        setOnClickListener(v -> {
            if (socialLoginListDialog == null) {
                socialLoginListDialog = new SocialLoginListDialog(getContext());
            }
            socialLoginListDialog.show();
        });
    }

    @Override
    public SocialAuthenticator createAuthenticator() {
        return null;
    }

    @Override
    protected int getImageRes() {
        return R.drawable.ic_authing_more;
    }
}

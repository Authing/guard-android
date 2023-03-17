package cn.authing.guard.social.view;

import android.content.Context;
import android.util.AttributeSet;

import cn.authing.guard.R;
import cn.authing.guard.social.handler.Gitee;
import cn.authing.guard.social.handler.SocialAuthenticator;

public class GiteeLoginButton extends SocialLoginButton {
    public GiteeLoginButton(Context context) {
        this(context, null);
    }

    public GiteeLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GiteeLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public SocialAuthenticator createAuthenticator() {
        return Gitee.getInstance();
    }

    @Override
    protected int getImageRes() {
        return R.drawable.ic_authing_gitee;
    }
}

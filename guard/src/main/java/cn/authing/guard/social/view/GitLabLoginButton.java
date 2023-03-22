package cn.authing.guard.social.view;

import android.content.Context;
import android.util.AttributeSet;

import cn.authing.guard.R;
import cn.authing.guard.social.handler.GitLab;
import cn.authing.guard.social.handler.SocialAuthenticator;

public class GitLabLoginButton extends SocialLoginButton {
    public GitLabLoginButton(Context context) {
        this(context, null);
    }

    public GitLabLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GitLabLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public SocialAuthenticator createAuthenticator() {
        return GitLab.getInstance();
    }

    @Override
    protected int getImageRes() {
        return R.drawable.ic_authing_gitlab;
    }
}
